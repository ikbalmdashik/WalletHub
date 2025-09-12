package com.example.wallethub.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.example.wallethub.Controllers.UserController
import com.example.wallethub.databinding.FragmentProfileBinding
import com.example.wallethub.views.WelcomeActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        val sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val currentLoginEmail = sharedPref.getString("current_login_email", null)

        if (!currentLoginEmail.isNullOrBlank()) {
            val userController = UserController()
            userController.getUserByEmail(currentLoginEmail) { user ->
                // Update UI on the main thread
                activity?.runOnUiThread {
                    if (user != null) {
                        binding.name.text = "${user.firstName ?: ""} ${user.lastName ?: ""}"
                        binding.username.text = user.firstName ?: "User"
                        binding.balance.text = "$${user.balance ?: 0}"
                        binding.email.text = user.email ?: ""
                        binding.phone.text = user.phoneNumber ?: ""
                        binding.address.text = user.address ?: "" // Make sure `address` exists in UserModel
                    } else {
                        binding.name.text = "User not found"
                        binding.username.text = "User not found"
                        binding.balance.text = "$0"
                        binding.email.text = ""
                        binding.phone.text = ""
                        binding.address.text = ""
                    }
                }
            }
        }

        binding.tvLogout.setOnClickListener {
            val intent = Intent(requireContext(), WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            sharedPref.edit { remove("current_login_email") }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

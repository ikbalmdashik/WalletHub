package com.example.wallethub

import Database
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wallethub.databinding.FragmentProfileBinding
import androidx.core.content.edit

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        val sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val currentLoginEmail = sharedPref.getString("current_login_email", null)

        val user = Database().getUser(currentLoginEmail)

        binding.name.text = user?.name
        binding.username.text = user?.name
        binding.balance.text = "$${user?.balance ?: 0}"
        binding.email.text = user?.email
        binding.phone.text = user?.phone
        binding.address.text = user?.address

        binding.tvLogout.setOnClickListener {
            val intent = Intent(requireContext(), WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            sharedPref.edit { remove("current_login_email") }
        }

        return view
    }
}

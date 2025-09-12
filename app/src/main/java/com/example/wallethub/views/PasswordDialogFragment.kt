package com.example.wallethub.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.wallethub.Controllers.UserController
import com.example.wallethub.databinding.DialogPasswordBinding

class PasswordDialogFragment : DialogFragment() {
    private var _binding: DialogPasswordBinding? = null
    private val binding get() = _binding!!
    private var username: String? = null

    var onPasswordVerified: ((isVerified: Boolean) -> Unit)? = null
    var onClosed: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        username = arguments?.getString("username")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set username
        username?.let {
            binding.tvUsername.text = it
        }

        // Done button
        binding.btnDone.setOnClickListener {
            val password = binding.tilPassword.editText?.text.toString()
            binding.tvError.visibility = View.GONE
            binding.btnRecoverPassword.visibility = View.GONE

            if (password.isNotBlank()) {
                // This should be a real data
                // base check

                val userController = UserController()
                userController.getUserByEmail(username) {user ->
                    val isVerified = password == user?.password

                    if (isVerified) {
                        onPasswordVerified?.invoke(true)
                        dismiss()
                    } else {
                        binding.tvError.text = "Incorrect password"
                        binding.tvError.visibility = View.VISIBLE
                        binding.btnRecoverPassword.visibility = View.VISIBLE
                        onPasswordVerified?.invoke(false)
                    }
                }
            } else {
                binding.tvError.text = "Password is required"
                binding.tvError.visibility = View.VISIBLE
            }
        }

        // Close button
        binding.btnClose.setOnClickListener {
            onClosed?.invoke()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "PasswordDialogFragment"

        fun newInstance(username: String?): PasswordDialogFragment {
            val fragment = PasswordDialogFragment()
            val args = Bundle()
            args.putString("username", username)
            fragment.arguments = args
            return fragment
        }
    }
}

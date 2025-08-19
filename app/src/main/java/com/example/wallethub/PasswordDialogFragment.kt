package com.example.wallethub

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.wallethub.databinding.DialogPasswordBinding

class PasswordDialogFragment : DialogFragment() {
    private var _binding: DialogPasswordBinding? = null
    private val binding get() = _binding!!
    private var username: String? = null

    private var isPasswordVisible = false

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

        // Toggle password visibility
        binding.etPassword.setOnTouchListener { _, event ->
            val DRAWABLE_END = 2 // index for right drawable
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val drawableEnd = binding.etPassword.compoundDrawables[DRAWABLE_END]
                if (drawableEnd != null &&
                    event.rawX >= (binding.etPassword.right - drawableEnd.bounds.width() - binding.etPassword.paddingEnd)
                ) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

        // Done button
        binding.btnDone.setOnClickListener {
            val password = binding.etPassword.text.toString()
            binding.tvError.visibility = View.GONE
            binding.tvForgotPassword.visibility = View.GONE
            binding.btnRecoverPassword.visibility = View.GONE

            if (password.isNotBlank()) {
                val isVerified = (password == "123") // mock check

                if (isVerified) {
                    onPasswordVerified?.invoke(true)
                    dismiss()
                } else {
                    binding.tvError.text = "Incorrect password"
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvForgotPassword.visibility = View.VISIBLE
                    binding.btnRecoverPassword.visibility = View.VISIBLE
                    onPasswordVerified?.invoke(false)
                }
            } else {
                binding.tvError.text = "Password is required"
                binding.tvError.visibility = View.VISIBLE
            }
        }

        // ❌ Close button
        binding.btnClose.setOnClickListener {
            onClosed?.invoke()
            dismiss()
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        if (isPasswordVisible) {
            binding.etPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(
                null, null,
                resources.getDrawable(R.drawable.ic_visibility, null), null
            )
        } else {
            binding.etPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(
                null, null,
                resources.getDrawable(R.drawable.ic_visibility_off, null), null
            )
        }
        // keep cursor at end
        binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "PasswordDialogFragment"

        fun newInstance(username: String): PasswordDialogFragment {
            val fragment = PasswordDialogFragment()
            val args = Bundle()
            args.putString("username", username)
            fragment.arguments = args
            return fragment
        }
    }
}

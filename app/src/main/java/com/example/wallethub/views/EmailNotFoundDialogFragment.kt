package com.example.wallethub.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.wallethub.databinding.EmailNotFoundDialogBinding

class EmailNotFoundDialogFragment : DialogFragment() {

    // Using a nullable backing property for thread safety and to avoid memory leaks
    private var _binding: EmailNotFoundDialogBinding? = null
    private val binding get() = _binding!!

    // Listener to communicate with the parent Fragment/Activity
    var onCreateAccountClickListener: (() -> Unit)? = null

    companion object {
        const val TAG = "EmailNotFoundDialog"

        fun newInstance(): EmailNotFoundDialogFragment {
            return EmailNotFoundDialogFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Correctly inflate the binding class
        _binding = EmailNotFoundDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set click listeners using the 'binding' property
        binding.btnCreateAccount.setOnClickListener {
            onCreateAccountClickListener?.invoke()
            dismiss()
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Always nullify the binding to prevent memory leaks
        _binding = null
    }
}
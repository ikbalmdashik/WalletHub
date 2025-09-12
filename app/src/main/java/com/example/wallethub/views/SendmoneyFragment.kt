package com.example.wallethub.views

import Database
import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.wallethub.R
import com.example.wallethub.databinding.FragmentSendmoneyBinding

class SendmoneyFragment : Fragment() {

    private var _binding: FragmentSendmoneyBinding? = null
    private val binding get() = _binding!!

    private var currentStep = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSendmoneyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Button click listeners
        binding.actionButton.setOnClickListener { handleNextStep() }
        binding.backButton.setOnClickListener { handlePreviousStep() }

        // Show initial step without animation
        showStep(currentStep, animate = false)
    }

    private fun handleNextStep() {
        val sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val currentLoginEmail = sharedPref.getString("current_login_email", null)
        val user = Database().getUser(currentLoginEmail)

        when (currentStep) {
            1 -> {
                val email = binding.receiverEmailInput.text.toString().trim()
                if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.receiverEmailInput.error = "Please enter a valid email address."
                    return
                }
                val reciver = Database().getUser(email)

                if (reciver?.name == null) {
                    Toast.makeText(context, "Email not found.", Toast.LENGTH_SHORT).show()
                    return
                }

                binding.receiverNameText.text = reciver?.name
                currentStep = 2
                showStep(currentStep, animate = true, forward = true)
            }
            2 -> {
                val amountString = binding.amountInput.text.toString().trim()
                if (amountString.isEmpty() || amountString.toDoubleOrNull() == null) {
                    binding.amountInput.error = "Please enter a valid amount."
                    return
                }
                val amount = amountString.toDouble()

                // Fill confirmation details
                binding.senderDetails.text = "Sender: ${user?.name}\nEmail: ${user?.email}"

                binding.receiverDetails.text =
                    "Receiver: ${binding.receiverNameText.text}\nEmail: ${binding.receiverEmailInput.text}"
                binding.amountDetails.text = "Amount: $${String.format("%.2f", amount)}"

                currentStep = 3
                showStep(currentStep, animate = true, forward = true)
            }
            3 -> {
                // Final confirmation logic here
                val pd = PasswordDialogFragment.newInstance(user?.email)
                pd.onPasswordVerified = { isVerified ->
                    if (isVerified) {
                        Toast.makeText(context, "Sending...", Toast.LENGTH_SHORT).show()
                    }
                }
                pd.show(parentFragmentManager, PasswordDialogFragment.TAG)
            }
        }
    }

    private fun handlePreviousStep() {
        when (currentStep) {
            2 -> {
                currentStep = 1
                showStep(currentStep, animate = true, forward = false)
            }
            3 -> {
                currentStep = 2
                showStep(currentStep, animate = true, forward = false)
            }
        }
    }

    private fun showStep(step: Int, animate: Boolean, forward: Boolean = true) {
        val slideIn = AnimationUtils.loadAnimation(
            requireContext(),
            if (forward) R.anim.slide_in_right else R.anim.slide_in_left
        )
        val slideOut = AnimationUtils.loadAnimation(
            requireContext(),
            if (forward) R.anim.slide_out_left else R.anim.slide_out_right
        )

        // Hide all steps first with optional animation
        if (animate) {
            when (currentStep) {
                1 -> binding.stepOneLayout.startAnimation(slideOut)
                2 -> binding.stepTwoLayout.startAnimation(slideOut)
                3 -> binding.stepThreeLayout.startAnimation(slideOut)
            }
        }

        binding.stepOneLayout.visibility = View.GONE
        binding.stepTwoLayout.visibility = View.GONE
        binding.stepThreeLayout.visibility = View.GONE

        // Show only the current step
        when (step) {
            1 -> binding.stepOneLayout.visibility = View.VISIBLE
            2 -> binding.stepTwoLayout.visibility = View.VISIBLE
            3 -> binding.stepThreeLayout.visibility = View.VISIBLE
        }

        // Animate the step in
        if (animate) {
            when (step) {
                1 -> binding.stepOneLayout.startAnimation(slideIn)
                2 -> binding.stepTwoLayout.startAnimation(slideIn)
                3 -> binding.stepThreeLayout.startAnimation(slideIn)
            }
        }

        // Back button visibility
        binding.backButton.visibility = if (step > 1) View.VISIBLE else View.GONE

        // Action button text
        binding.actionButton.text = if (step == 3) "Confirm" else "Next"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
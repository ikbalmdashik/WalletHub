package com.example.wallethub.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.wallethub.R
import com.example.wallethub.databinding.FragmentCreateAccountBinding
import com.example.wallethub.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CreateAccountFragment : Fragment() {

    // View binding instance to access views from the layout
    private var _binding: FragmentCreateAccountBinding? = null
    private val binding get() = _binding!!

    // Firebase authentication and database references
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    // Keep track of the current step
    private var currentStep = 1
    private var isAnimating = false

    // List of all step layouts and buttons
    private val stepLayouts by lazy {
        listOf(binding.llStepOne, binding.llStepTwo, binding.llStepThree, binding.llStepFour, binding.llStepFive)
    }

    private val stepButtons by lazy {
        mapOf(
            1 to listOf(binding.btnNextStep1),
            2 to listOf(binding.btnNextStep2, binding.btnBackStep2),
            3 to listOf(binding.btnNextStep3, binding.btnBackStep3),
            4 to listOf(binding.btnNextStep4, binding.btnBackStep4),
            5 to listOf(binding.btnDone)
        )
    }

    private val progressViews by lazy {
        listOf(binding.viewStep1, binding.viewStep2, binding.viewStep3, binding.viewStep4, binding.viewStep5)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Set up click listeners for the navigation buttons
        binding.btnNextStep1.setOnClickListener {
            if (!isAnimating) animateToStep(fromStep = 1, toStep = 2, direction = 1) // 1 for next
        }

        binding.btnNextStep2.setOnClickListener {
            if (!isAnimating) {
                // Get the new Address
                val address = binding.etAddress.text.toString().trim()
                if (address.isEmpty()) {
                    Toast.makeText(context, "Please enter your address", Toast.LENGTH_SHORT).show()
                } else {
                    animateToStep(fromStep = 2, toStep = 3, direction = 1)
                }
            }
        }

        binding.btnBackStep2.setOnClickListener {
            if (!isAnimating) animateToStep(fromStep = 2, toStep = 1, direction = -1) // -1 for back
        }

        binding.btnNextStep3.setOnClickListener {
            if (!isAnimating) animateToStep(fromStep = 3, toStep = 4, direction = 1)
        }

        binding.btnBackStep3.setOnClickListener {
            if (!isAnimating) animateToStep(fromStep = 3, toStep = 2, direction = -1)
        }

        binding.btnNextStep4.setOnClickListener {
            if (!isAnimating) {
                // Collect data from input fields and validate
                val firstName = binding.etFirstName.text.toString().trim()
                val lastName = binding.etLastName.text.toString().trim()
                val birthDate = binding.etDateOfBirth.text.toString().trim()
                val nidNumber = binding.etNidNumber.text.toString().trim()
                val address = binding.etAddress.text.toString().trim() // Get the address
                val email = binding.etEmail.text.toString().trim().lowercase()
                val phone = binding.etPhoneNumber.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()
                val confirmPassword = binding.etConfirmPassword.text.toString().trim()

                // Validate all fields
                if (firstName.isEmpty() || lastName.isEmpty() || birthDate.isEmpty() || nidNumber.isEmpty() || address.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (password != confirmPassword) {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Create user with email and password
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val userId = user?.uid

                            if (userId != null) {
                                val userModel = UserModel(
                                    firstName = firstName,
                                    lastName = lastName,
                                    birthDate = birthDate,
                                    nidNumber = nidNumber,
                                    email = email,
                                    phoneNumber = phone,
                                    address = address, // Pass the address here
                                    balance = 0.0
                                )

                                // Save data to the database
                                database.getReference("Users").child(userId).setValue(userModel)
                                    .addOnSuccessListener {
                                        Log.d("Firebase", "User data successfully written!")
                                        Toast.makeText(context, "Account Created Successfully!", Toast.LENGTH_SHORT).show()
                                        animateToStep(fromStep = 4, toStep = 5, direction = 1)
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("Firebase", "Error writing user data", e)
                                        Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Log.w("Firebase", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(context, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        binding.btnBackStep4.setOnClickListener {
            if (!isAnimating) animateToStep(fromStep = 4, toStep = 3, direction = -1)
        }

        binding.btnDone.setOnClickListener {
            // Close the fragment, navigate away, etc.
            val fragmentManager = parentFragmentManager
            if (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStack()
            } else {
                fragmentManager.beginTransaction().remove(this).commit()
            }
        }

        // Initialize the UI for the first step without animation
        updateUiForStep(1)
    }

    // New method to animate the transition between steps
    private fun animateToStep(fromStep: Int, toStep: Int, direction: Int) {
        if (isAnimating || fromStep == toStep) return

        isAnimating = true
        val fromLayout = stepLayouts[fromStep - 1]
        val toLayout = stepLayouts[toStep - 1]

        val screenWidth = resources.displayMetrics.widthPixels.toFloat()

        // 1. Position the new layout off-screen to its entry point
        toLayout.translationX = direction * screenWidth
        toLayout.visibility = View.VISIBLE

        // 2. Animate both layouts simultaneously
        // Animate the old layout to slide away
        fromLayout.animate()
            .translationX(-direction * screenWidth)
            .setDuration(350)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                fromLayout.visibility = View.GONE
                fromLayout.translationX = 0f // Reset position for future use
            }
            .start()

        // Animate the new layout to slide into the center
        toLayout.animate()
            .translationX(0f)
            .setDuration(350)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                currentStep = toStep
                updateUiForStep(currentStep)
                isAnimating = false
            }
            .start()
    }



    // Updated function to handle UI state without animation logic
    private fun updateUiForStep(step: Int) {
        // Hide all views and buttons initially
        stepLayouts.forEach { it.visibility = View.GONE }
        stepButtons.values.forEach { buttons -> buttons.forEach { it.visibility = View.GONE } }
        progressViews.forEach { it.setBackgroundColor(Color.WHITE) }

        // Show the correct views and buttons for the current step
        val currentLayout = stepLayouts[step - 1]
        currentLayout.visibility = View.VISIBLE
        progressViews[step - 1].setBackgroundColor(Color.parseColor("#007AFF"))

        stepButtons[step]?.forEach { it.visibility = View.VISIBLE }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
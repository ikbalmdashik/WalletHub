package com.example.wallethub.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.wallethub.Controllers.UserController
import com.example.wallethub.R
import com.example.wallethub.databinding.OnboardingPageBinding

class OnboardingFragment : Fragment() {
    private lateinit var page: OnboardingPage
    private var _binding: OnboardingPageBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_PAGE = "onboarding_page"

        fun newInstance(page: OnboardingPage): OnboardingFragment {
            val fragment = OnboardingFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_PAGE, page)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        page = arguments?.getParcelable(ARG_PAGE)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = OnboardingPageBinding.inflate(inflater, container, false)
        val view = binding.root

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        // Set content using binding
        binding.title.text = page.title
        binding.subtitle.text = page.subtitle
        binding.illustration.setImageResource(page.imageRes)

        // Last page logic
        if (page.isLastPage) {
            binding.btnSkip.visibility = View.GONE
            binding.emailInputLayout.visibility = View.VISIBLE
            binding.btnContinue.visibility = View.VISIBLE
        } else {
            binding.btnSkip.visibility = View.VISIBLE
            binding.emailInputLayout.visibility = View.GONE
            binding.btnContinue.visibility = View.GONE
        }

        // Skip button
        binding.btnSkip.setOnClickListener {
            viewPager?.currentItem = (viewPager.adapter?.itemCount ?: 1) - 1
        }

        // Continue button
        binding.btnContinue.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()

            if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                val userController = UserController()

                // Check if user exists in Firebase
                userController.getUserByEmail(email) { user ->
                    if (user != null) {
                        // User exists, show password dialog
                        val pd = PasswordDialogFragment.newInstance(email)
                        pd.onPasswordVerified = { isVerified ->
                            if (isVerified) {
                                saveAndNavigateToDashboard(email)
                            }
                        }
                        pd.show(parentFragmentManager, PasswordDialogFragment.TAG)
                    } else {
                        // User not found, show dialog
                        val dialog = EmailNotFoundDialogFragment.newInstance()

                        dialog.onCreateAccountClickListener = {
                            val fragmentContainer = activity?.findViewById<View>(R.id.fragment_container)
                            fragmentContainer?.visibility = View.VISIBLE

                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, CreateAccountFragment())
                                .addToBackStack(null)
                                .commit()

                            dialog.dismiss()
                        }

                        dialog.show(parentFragmentManager, EmailNotFoundDialogFragment.TAG)
                    }
                }

            } else {
                binding.emailInput.error = "Enter valid email"
            }
        }


        return view
    }

    private fun saveAndNavigateToDashboard(email: String) {
        val sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        sharedPref.edit {
            putString("email", email)
            putString("current_login_email", email)
        }

        val intent = Intent(requireContext(), DashboardActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.wallethub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
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

        // Check for saved accounts on the first page
        if (page.isFirstPage) {
            val sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val savedAccounts = sharedPref.getStringSet("saved_accounts", emptySet())
            if (!savedAccounts.isNullOrEmpty()) {
                // Skip to the last page (where email input and saved accounts are)
                viewPager?.post {
                    viewPager.currentItem = (viewPager.adapter?.itemCount ?: 1) - 1
                }
            }
        }

        // Set content using binding
        binding.title.text = page.title
        binding.subtitle.text = page.subtitle
        binding.illustration.setImageResource(page.imageRes)

        // Last page logic
        if (page.isLastPage) {
            binding.btnSkip.visibility = View.GONE
            binding.emailInput.visibility = View.VISIBLE
            binding.btnContinue.visibility = View.VISIBLE

            // Check for saved accounts and display them
            showSavedAccounts()
        }

        // Skip button
        binding.btnSkip.setOnClickListener {
            viewPager?.currentItem = (viewPager.adapter?.itemCount ?: 1) - 1
        }

        // Continue button
        binding.btnContinue.setOnClickListener {
            val email = binding.emailInput.text.toString()
            if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                val emails = setOf("e@g.c", "a@g.c", "i@g.c") // demo emails
                val isEmailExist = email in emails // This should be a real API check

                if (isEmailExist) {
                    // Create the dialog and set the callback
                    val pd = PasswordDialogFragment.newInstance(email)
                    pd.onPasswordVerified = { isVerified ->
                        if (isVerified) {
                            saveAndNavigateToDashboard(email)
                        }
                    }
                    pd.show(parentFragmentManager, PasswordDialogFragment.TAG)

                } else {
                    val dialog = EmailNotFoundDialogFragment.newInstance()

                    // Set up the listener for the button click inside the dialog
                    dialog.onCreateAccountClickListener = {
                        // This code runs when the user clicks "Create Account"
                        Toast.makeText(requireContext(), "Navigating to create account...", Toast.LENGTH_SHORT).show()
                        // Here you would add your navigation code
                    }
                    dialog.show(parentFragmentManager, EmailNotFoundDialogFragment.TAG)
                }
            } else {
                binding.emailInput.error = "Enter valid email"
            }
        }

        return view
    }

    // --- Helper functions ---
    private fun showSavedAccounts() {
        val sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val savedAccounts = sharedPref.getStringSet("saved_accounts", emptySet())?.toList() ?: emptyList()

        if (savedAccounts.isNotEmpty()) {
            binding.savedAccountsSection.visibility = View.VISIBLE
            binding.accountListContainer.removeAllViews()

            for (email in savedAccounts) {
                val accountItemView = createAccountItemView(email)
                binding.accountListContainer.addView(accountItemView)
            }
        } else {
            binding.savedAccountsSection.visibility = View.GONE
        }
    }

    private fun createAccountItemView(email: String): View {
        val accountItemView = LayoutInflater.from(requireContext()).inflate(R.layout.saved_account_item, binding.accountListContainer, false)
        val tvEmail = accountItemView.findViewById<TextView>(R.id.accountEmail)
        val removeIcon = accountItemView.findViewById<ImageView>(R.id.removeIcon)
        tvEmail.text = email

        accountItemView.setOnClickListener {
            binding.emailInput.setText(email)
            binding.emailInput.requestFocus()
        }

        removeIcon.setOnClickListener {
            // 1. Get a reference to SharedPreferences
            val sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

            // 2. Read the current set of saved accounts
            val savedAccounts = sharedPref.getStringSet("saved_accounts", mutableSetOf())?.toMutableSet()

            // 3. Remove the specific email from the set
            savedAccounts?.remove(email)

            // 4. Save the updated set back to SharedPreferences
            sharedPref.edit {
                putStringSet("saved_accounts", savedAccounts)
            }

            // Optional: Refresh the UI to show the change
            refreshAccountList()
        }
        return accountItemView
    }

    private fun refreshAccountList() {
        // 1. Clear all existing views from the container
        binding.accountListContainer.removeAllViews()

        // 2. Get the updated list of accounts from SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val savedAccounts = sharedPref.getStringSet("saved_accounts", mutableSetOf())

        // 3. Loop through the updated list and add a new view for each account
        savedAccounts?.forEach { email ->
            val accountItemView = createAccountItemView(email)
            binding.accountListContainer.addView(accountItemView)
        }
    }

    private fun saveAndNavigateToDashboard(email: String) {
        val sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        val savedAccounts = sharedPref.getStringSet("saved_accounts", mutableSetOf())?.toMutableSet()
        savedAccounts?.add(email)

        sharedPref.edit {
            putString("email", email)
            putStringSet("saved_accounts", savedAccounts)
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
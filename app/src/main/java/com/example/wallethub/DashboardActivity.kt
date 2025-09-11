package com.example.wallethub

import Database
import SendmoneyFragment
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.example.wallethub.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inflate with view binding
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply insets to the root view
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val currentLoginEmail = sharedPref.getString("current_login_email", null)
        val user = Database().getUser(currentLoginEmail)
        binding.tvUsername.text = user?.name
        binding.tvBalance.text = "$${user?.balance ?: 0}"

        // Handle the click on the profile section
        binding.profileSection.setOnClickListener {
            // Hide the main dashboard views
            binding.cardBalance.visibility = View.GONE
            binding.featuresContainer.visibility = View.GONE

            // Show the fragment container
            binding.fragmentContainer.visibility = View.VISIBLE

            // Use the supportFragmentManager to begin a transaction
            supportFragmentManager.commit {
                // The `replace` method swaps the current fragment in the container
                // with a new instance of ProfileFragment.
                setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )

                replace(R.id.fragment_container, ProfileFragment())
                // Add the transaction to the back stack. This allows the user to
                // press the back button to navigate back to the previous screen.
                addToBackStack(null)
            }
        }

        binding.sendMoney.setOnClickListener {
            binding.fragmentContainer.visibility = View.VISIBLE
            binding.featuresContainer.visibility = View.GONE
            binding.cardBalance.visibility = View.GONE

            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,  // enter
                    R.anim.slide_out_left,  // exit
                    R.anim.slide_in_left,   // pop enter
                    R.anim.slide_out_right  // pop exit
                )
                .replace(R.id.fragment_container, SendmoneyFragment())
                .addToBackStack(null)
                .commit()
        }

        // Use OnBackPressedDispatcher for modern back press handling
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    // Check if this is the last fragment on the back stack
                    if (supportFragmentManager.backStackEntryCount == 1) {
                        binding.cardBalance.visibility = View.VISIBLE
                        binding.featuresContainer.visibility = View.VISIBLE
                    }
                    supportFragmentManager.popBackStack()
                } else {
                    // Call the default back button behavior
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }
}

package com.example.wallethub.views

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.example.wallethub.Controllers.UserController
import com.example.wallethub.databinding.ActivityDashboardBinding
import com.example.wallethub.R
import com.example.wallethub.views.ProfileFragment
import com.example.wallethub.views.SendmoneyFragment

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get current logged-in email
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val currentLoginEmail = sharedPref.getString("current_login_email", null)

        if (!currentLoginEmail.isNullOrBlank()) {
            // Fetch user from Firebase
            val userController = UserController()
            userController.getUserByEmail(currentLoginEmail) { user ->
                runOnUiThread {
                    if (user != null) {
                        // Update UI with real data
                        binding.tvUsername.text = "${user.firstName ?: ""} ${user.lastName ?: ""}"
                        binding.tvBalance.text = "$${user.balance ?: 0}" // Make sure `balance` is in UserModel
                    } else {
                        binding.tvUsername.text = "User not found"
                        binding.tvBalance.text = "$0"
                    }
                }
            }
        }

        // Profile click listener
        binding.cardBalance.setOnClickListener {
            binding.cardBalance.visibility = View.GONE
            binding.featuresContainer.visibility = View.GONE
            binding.fragmentContainer.visibility = View.VISIBLE

            supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                replace(R.id.fragment_container, ProfileFragment())
                addToBackStack(null)
            }
        }

        // Send money click listener
        binding.sendMoney.setOnClickListener {
            binding.fragmentContainer.visibility = View.VISIBLE
            binding.featuresContainer.visibility = View.GONE
            binding.cardBalance.visibility = View.GONE

            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, SendmoneyFragment())
                .addToBackStack(null)
                .commit()
        }

        // Back press handling
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    if (supportFragmentManager.backStackEntryCount == 1) {
                        binding.cardBalance.visibility = View.VISIBLE
                        binding.featuresContainer.visibility = View.VISIBLE
                    }
                    supportFragmentManager.popBackStack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }
}

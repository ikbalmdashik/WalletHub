package com.example.wallethub.views

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wallethub.R
import com.example.wallethub.databinding.ActivityWelcomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.FirebaseApp

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        FirebaseApp.initializeApp(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Define the list of OnboardingPage objects here
        val pages = listOf(
            OnboardingPage(
                title = "Welcome to WalletHub!",
                subtitle = "Your personal finance manager.",
                imageRes = R.drawable.ic_add_money
            ),
            OnboardingPage(
                title = "Add Money",
                subtitle = "Add Money From Any Wallets",
                imageRes = R.drawable.ic_add_money
            ),
            OnboardingPage(
                title = "Send Money",
                subtitle = "Send Money To WalletHub Users",
                imageRes = R.drawable.ic_send_money
            ),
            OnboardingPage(
                title = "Make Payment",
                subtitle = "Payment Merchants Quickly",
                imageRes = R.drawable.ic_payment
            ),
            OnboardingPage(
                title = "Secure Your Data",
                subtitle = "Your information is safe with us.",
                imageRes = R.drawable.ic_secure,
            ),
            OnboardingPage(
                title = "Email",
                subtitle = "Enter Your Email",
                imageRes = R.drawable.ic_email,
                isLastPage = true
            )
        )

        // Now you can pass the 'pages' list to the adapter
        binding.viewPager.adapter = OnboardingPagerAdapter(this, pages)

        // Dots
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

        // Optional: swipe animation
        binding.viewPager.setPageTransformer { page, position ->
            page.alpha = 0.5f + (1 - Math.abs(position)) * 0.5f
        }
    }
}
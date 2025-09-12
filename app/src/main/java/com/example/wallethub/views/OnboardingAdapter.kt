package com.example.wallethub.views

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.wallethub.views.OnboardingFragment
import com.example.wallethub.views.OnboardingPage

class OnboardingPagerAdapter(
    fa: FragmentActivity,
    private val pages: List<OnboardingPage>
) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment {
        val page = pages[position]

        // Add this check to identify the first page
        if (position == 0) {
            page.isFirstPage = true
        }

        return OnboardingFragment.Companion.newInstance(page)
    }
}
package com.example.wallethub

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

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

        return OnboardingFragment.newInstance(page)
    }
}
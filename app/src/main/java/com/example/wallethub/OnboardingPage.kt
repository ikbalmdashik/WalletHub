package com.example.wallethub

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val imageRes: Int,
    val isLastPage: Boolean = false,
    var isFirstPage: Boolean = false
) : Parcelable
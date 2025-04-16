package com.greildev.erdmovee.utils

import UserParams

sealed class SplashState<T>{
    data object Onboarding : SplashState<Nothing>()
    data object Login : SplashState<Nothing>()
    data object Main : SplashState<Nothing>()
    data object Profile : SplashState<Nothing>()
}

fun UserParams.toSplashState() = when {
    this.user != null && !this.user.username.isNullOrEmpty() -> {
        SplashState.Main
    }
    this.user?.username.isNullOrEmpty() && !this.user?.email.isNullOrEmpty() -> {
        SplashState.Profile
    }
    this.user == null && !isOnboarding -> SplashState.Login
    this.isOnboarding -> SplashState.Onboarding
    else -> SplashState.Onboarding
}

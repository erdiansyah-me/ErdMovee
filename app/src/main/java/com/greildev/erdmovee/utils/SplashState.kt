package com.greildev.erdmovee.utils

sealed class SplashState<T>{
    data object Onboarding : SplashState<Nothing>()
    data object Login : SplashState<Nothing>()
    data object Main : SplashState<Nothing>()
    data object Profile : SplashState<Nothing>()
}

package com.greildev.erdmovee.utils

sealed class SplashState<T>{
    class Onboarding : SplashState<Nothing>()
    class Login : SplashState<Nothing>()
    class Main : SplashState<Nothing>()
    class Profile : SplashState<Nothing>()
}
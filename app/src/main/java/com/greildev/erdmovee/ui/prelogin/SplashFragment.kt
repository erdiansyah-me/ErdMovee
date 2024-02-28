package com.greildev.erdmovee.ui.prelogin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.greildev.core.base.BaseFragment
import com.greildev.erdmovee.databinding.FragmentSplashBinding
import com.greildev.erdmovee.utils.SplashState
import com.greildev.erdmovee.utils.launchAndCollectIn
import com.greildev.erdmovee.utils.onCreated
import com.greildev.erdmovee.utils.onValue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class SplashFragment :
    BaseFragment<FragmentSplashBinding, PreloginViewModel>(FragmentSplashBinding::inflate) {
    override val viewModel: PreloginViewModel by viewModels()

    override fun initView() {
        viewModel.getUserSplash()
        val splashComponentStart =
            ObjectAnimator.ofFloat(binding.splash, View.ROTATION, ANIMATION_ROTATION_START)
                .setDuration(ANIMATION_DURATION_START)
        val splashComponentAfter =
            ObjectAnimator.ofFloat(binding.splash, View.ROTATION, ANIMATION_ROTATION_AFTER)
                .setDuration(ANIMATION_DURATION_AFTER)
        AnimatorSet().apply {
            playSequentially(splashComponentStart, splashComponentAfter)
            start()
        }
        binding.splashText.text = ""
        binding.splashText.setRunningText("ErdMovee")
    }

    override fun observeData() {
        viewModel.userStateSplash.launchAndCollectIn(viewLifecycleOwner) { state ->
            delay(SPLASH_DURATION)
            state.onCreated { }
                .onValue {
                    when (it) {
                        is SplashState.Main -> {
                            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomePageFragment())
                        }

                        is SplashState.Profile -> {
                            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToProfileFragment())
                        }

                        is SplashState.Login -> {
                            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
                        }

                        is SplashState.Onboarding -> {
                            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToOnboardingFragment())
                        }
                    }
                }
        }
    }

    companion object {
        private const val ANIMATION_ROTATION_START = -25f
        private const val ANIMATION_ROTATION_AFTER = 0f
        private const val ANIMATION_DURATION_START = 500L
        private const val ANIMATION_DURATION_AFTER = 1200L
        private const val SPLASH_DURATION = 2000L
    }
}
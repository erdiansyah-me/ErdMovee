package com.greildev.erdmovee.ui.prelogin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.greildev.core.base.BaseFragment
import com.greildev.erdmovee.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment :
    BaseFragment<FragmentSplashBinding, PreloginViewModel>(FragmentSplashBinding::inflate) {
    override val viewModel: PreloginViewModel by viewModels()

    override fun initView() {

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

        Handler(Looper.getMainLooper()).postDelayed(
            {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToOnboardingFragment())
            },
            SPLASH_DURATION
        )
    }

    override fun observeData() {
    }

    companion object {
        private const val ANIMATION_ROTATION_START = -25f
        private const val ANIMATION_ROTATION_AFTER = 0f
        private const val ANIMATION_DURATION_START = 500L
        private const val ANIMATION_DURATION_AFTER = 1200L
        private const val SPLASH_DURATION = 2000L
    }
}
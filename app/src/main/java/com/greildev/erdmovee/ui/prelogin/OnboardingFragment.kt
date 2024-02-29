package com.greildev.erdmovee.ui.prelogin

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.greildev.core.base.BaseFragment
import com.greildev.erdmovee.databinding.FragmentOnboardingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : BaseFragment<FragmentOnboardingBinding, PreloginViewModel>(FragmentOnboardingBinding::inflate) {
    override val viewModel: PreloginViewModel by viewModels()
    override fun initView() {
    }

    override fun observeData() {
    }

    override fun initListener() {
        binding.btnJoinnow.setOnClickListener {
            findNavController().navigate(OnboardingFragmentDirections.actionOnboardingFragmentToRegisterFragment())
        }
        binding.tvSkip.setOnClickListener {
            viewModel.saveUserOnboardingPreferences(isShowOnboarding = false)
            findNavController().navigate(OnboardingFragmentDirections.actionOnboardingFragmentToLoginFragment())
        }
    }
}
package com.greildev.erdmovee.ui.prelogin

import androidx.fragment.app.viewModels
import com.greildev.core.base.BaseFragment
import com.greildev.erdmovee.databinding.FragmentRegisterBinding

class RegisterFragment : BaseFragment<FragmentRegisterBinding, PreloginViewModel>(FragmentRegisterBinding::inflate) {
    override val viewModel: PreloginViewModel by viewModels()
    override fun initView() {
    }

    override fun observeData() {
    }

}
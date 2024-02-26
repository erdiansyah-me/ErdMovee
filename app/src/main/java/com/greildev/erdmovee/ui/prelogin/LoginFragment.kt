package com.greildev.erdmovee.ui.prelogin

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.greildev.core.base.BaseFragment
import com.greildev.erdmovee.databinding.FragmentLoginBinding

class LoginFragment : BaseFragment<FragmentLoginBinding, PreloginViewModel>(FragmentLoginBinding::inflate) {
    override val viewModel: PreloginViewModel by viewModels()

    override fun initView() {
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomePageFragment())
        }
    }

    override fun observeData() {
    }

}
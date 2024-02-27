package com.greildev.erdmovee.ui.homepage

import androidx.fragment.app.viewModels
import com.greildev.core.base.BaseFragment
import com.greildev.erdmovee.databinding.FragmentMainBinding
import com.greildev.erdmovee.ui.prelogin.PreloginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding, PreloginViewModel>(FragmentMainBinding::inflate) {

    override val viewModel: PreloginViewModel by viewModels()
    override fun initView() {
    }

    override fun observeData() {
    }

}
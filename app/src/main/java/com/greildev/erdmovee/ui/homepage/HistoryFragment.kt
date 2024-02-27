package com.greildev.erdmovee.ui.homepage

import androidx.fragment.app.viewModels
import com.greildev.core.base.BaseFragment
import com.greildev.erdmovee.databinding.FragmentHistoryBinding
import com.greildev.erdmovee.ui.prelogin.PreloginViewModel

class HistoryFragment : BaseFragment<FragmentHistoryBinding, PreloginViewModel>(
    FragmentHistoryBinding::inflate
) {


    override val viewModel: PreloginViewModel by viewModels()
    override fun initView() {

    }

    override fun observeData() {

    }
}
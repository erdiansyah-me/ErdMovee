package com.greildev.erdmovee.ui.homepage

import androidx.fragment.app.viewModels
import com.greildev.core.base.BaseFragment
import com.greildev.erdmovee.databinding.FragmentFavoriteBinding
import com.greildev.erdmovee.ui.prelogin.PreloginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment :
    BaseFragment<FragmentFavoriteBinding, PreloginViewModel>(FragmentFavoriteBinding::inflate) {


    override val viewModel: PreloginViewModel by viewModels()
    override fun initView() {

    }

    override fun observeData() {

    }
}
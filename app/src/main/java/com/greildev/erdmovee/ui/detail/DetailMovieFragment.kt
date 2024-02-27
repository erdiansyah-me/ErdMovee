package com.greildev.erdmovee.ui.detail

import androidx.fragment.app.viewModels
import com.greildev.core.base.BaseFragment
import com.greildev.erdmovee.databinding.FragmentDetailMovieBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailMovieFragment : BaseFragment<FragmentDetailMovieBinding, DetailViewModel>(FragmentDetailMovieBinding::inflate) {

        override val viewModel: DetailViewModel by viewModels()
        override fun initView() {
        }

        override fun observeData() {
        }

}
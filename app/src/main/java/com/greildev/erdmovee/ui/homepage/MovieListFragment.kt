package com.greildev.erdmovee.ui.homepage

import androidx.fragment.app.viewModels
import com.greildev.core.base.BaseFragment
import com.greildev.erdmovee.databinding.FragmentMovieListBinding

class MovieListFragment : BaseFragment<FragmentMovieListBinding,HomeViewModel>(FragmentMovieListBinding::inflate) {
    override val viewModel: HomeViewModel by viewModels()
    override fun initView() {
    }

    override fun observeData() {
    }
}
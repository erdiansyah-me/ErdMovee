package com.greildev.erdmovee.ui.homepage

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.greildev.core.base.BaseFragment
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.FragmentMainBinding
import com.greildev.erdmovee.ui.adapter.MovieListAdapter
import com.greildev.erdmovee.ui.adapter.NowPlayingPagingAdapter
import com.greildev.erdmovee.ui.adapter.PagingLoadStateAdapter
import com.greildev.erdmovee.ui.adapter.PagingLoadStateHorizontalAdapter
import com.greildev.erdmovee.utils.Analytics
import com.greildev.erdmovee.utils.Constant
import com.greildev.erdmovee.utils.launchAndCollectIn
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment :
    BaseFragment<FragmentMainBinding, HomeViewModel>(FragmentMainBinding::inflate) {

    override val viewModel: HomeViewModel by viewModels()

    private val popularAdapter: MovieListAdapter by lazy {
        MovieListAdapter { id, title ->
            val logBundle = Bundle()
            logBundle.putString("movie title", title)
            Analytics.logEvent(Constant.TO_DETAIL_SCREEN_EVENT, logBundle)
            val toDetailMovieFragment =
                HomePageFragmentDirections.actionHomePageFragmentToDetailMovieFragment()
            toDetailMovieFragment.movieId = id
            activity?.supportFragmentManager?.findFragmentById(R.id.fragment_container)
                ?.findNavController()?.navigate(toDetailMovieFragment)
        }
    }

    private val nowPlayingAdapter: NowPlayingPagingAdapter by lazy {
        NowPlayingPagingAdapter { id, title ->
            val logBundle = Bundle()
            logBundle.putString("movie title", title)
            Analytics.logEvent(Constant.TO_DETAIL_SCREEN_EVENT, logBundle)
            val toDetailMovieFragment =
                HomePageFragmentDirections.actionHomePageFragmentToDetailMovieFragment()
            toDetailMovieFragment.movieId = id
            activity?.supportFragmentManager?.findFragmentById(R.id.fragment_container)
                ?.findNavController()?.navigate(toDetailMovieFragment)
        }
    }

    override fun initView() {

        binding.rvNowPlaying.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvNowPlaying.adapter = nowPlayingAdapter
            .withLoadStateFooter(
                footer = PagingLoadStateHorizontalAdapter { nowPlayingAdapter.retry() }
            )
        binding.rvPopular.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvPopular.adapter = popularAdapter.withLoadStateFooter(
            footer = PagingLoadStateAdapter { popularAdapter.retry() }
        )
        binding.svMain.viewTreeObserver.addOnScrollChangedListener {
            val scrollVertical = binding.svMain.scrollY
            val headerTextPopular = binding.llPopular.top
            val scrollThreshold = 50

            if (scrollVertical > headerTextPopular + scrollThreshold) {
                binding.svMain.smoothScrollTo(0, binding.llPopular.top)
            }
        }
    }

    override fun observeData() {
        viewModel.getNowPlayingMovies().launchAndCollectIn(viewLifecycleOwner) {
            nowPlayingAdapter.submitData(it)
        }
        viewModel.getPopularMovies().launchAndCollectIn(viewLifecycleOwner) {
            popularAdapter.submitData(it)
        }

        viewModel.userData.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.getTokenUser(it.uid)
            }
        }
        viewModel.tokenUser.launchAndCollectIn(viewLifecycleOwner) {
            binding.tvCoinsBalance.text = it.toString()
        }
    }

    override fun initListener() {
        binding.chipTopup.setOnClickListener {
            activity?.supportFragmentManager?.findFragmentById(R.id.fragment_container)
                ?.findNavController()
                ?.navigate(HomePageFragmentDirections.actionHomePageFragmentToTopupFragment())
        }
        binding.btnSeeMorePopular.setOnClickListener {
            val logBundle = Bundle()
            logBundle.putString(Constant.TO_SCREEN_NAVIGATE_EVENT, "Movie Popular Fragment")
            Analytics.logEvent(Constant.TO_SCREEN_NAVIGATE_EVENT, logBundle)
            val toMovieListFragment =
                HomePageFragmentDirections.actionHomePageFragmentToMovieListFragment()
            toMovieListFragment.fetch = "popular"
            activity?.supportFragmentManager?.findFragmentById(R.id.fragment_container)
                ?.findNavController()?.navigate(toMovieListFragment)
        }
        binding.btnSeeMoreNowPlaying.setOnClickListener {
            val logBundle = Bundle()
            logBundle.putString(Constant.TO_SCREEN_NAVIGATE_EVENT, "Movie Now Playing Fragment")
            Analytics.logEvent(Constant.TO_SCREEN_NAVIGATE_EVENT, logBundle)
            val toMovieListFragment =
                HomePageFragmentDirections.actionHomePageFragmentToMovieListFragment()
            toMovieListFragment.fetch = "nowplaying"
            activity?.supportFragmentManager?.findFragmentById(R.id.fragment_container)
                ?.findNavController()?.navigate(toMovieListFragment)
        }
        nowPlayingAdapter.addLoadStateListener { state ->
            when (state.refresh) {
                is LoadState.NotLoading -> {
                    if (state.append.endOfPaginationReached && popularAdapter.itemCount < 1) {
                        binding.rvNowPlaying.visibility = View.INVISIBLE
                        binding.tvErrorNowPlaying.isVisible = true
                    } else {
                        binding.rvNowPlaying.isVisible = true
                        binding.tvErrorNowPlaying.isVisible = false
                    }
                }

                is LoadState.Loading -> {
                    if (popularAdapter.itemCount == 0) {
                        binding.rvNowPlaying.visibility = View.INVISIBLE
                        binding.tvErrorNowPlaying.isVisible = false
                    } else {
                        binding.rvNowPlaying.isVisible = true
                        binding.tvErrorNowPlaying.isVisible = false
                    }
                }

                is LoadState.Error -> {
                    binding.rvNowPlaying.visibility = View.INVISIBLE
                    binding.tvErrorNowPlaying.isVisible = true
                    (state.refresh as LoadState.Error).error.localizedMessage?.let {
                        binding.tvErrorNowPlaying.text = it
                    }
                }
            }
        }
        popularAdapter.addLoadStateListener { state ->
            when (state.refresh) {
                is LoadState.NotLoading -> {
                    if (state.append.endOfPaginationReached && popularAdapter.itemCount < 1) {
                        binding.rvPopular.visibility = View.INVISIBLE
                        binding.tvErrorPopular.isVisible = true
                    } else {
                        binding.rvPopular.isVisible = true
                        binding.tvErrorPopular.isVisible = false
                    }
                }

                is LoadState.Loading -> {
                    if (popularAdapter.itemCount == 0) {
                        binding.rvPopular.visibility = View.INVISIBLE
                        binding.tvErrorPopular.isVisible = false
                    } else {
                        binding.rvPopular.isVisible = true
                        binding.tvErrorPopular.isVisible = false
                    }
                }

                is LoadState.Error -> {
                    binding.rvPopular.visibility = View.INVISIBLE
                    binding.tvErrorPopular.isVisible = true
                    (state.refresh as LoadState.Error).error.localizedMessage?.let {
                        binding.tvErrorPopular.text = it
                    }
                }
            }
        }
    }

}
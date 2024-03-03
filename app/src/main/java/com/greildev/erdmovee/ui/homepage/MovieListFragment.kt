package com.greildev.erdmovee.ui.homepage

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.greildev.core.base.BaseFragment
import com.greildev.erdmovee.databinding.FragmentMovieListBinding
import com.greildev.erdmovee.ui.adapter.MovieListAdapter
import com.greildev.erdmovee.ui.adapter.PagingLoadStateAdapter
import com.greildev.erdmovee.utils.Analytics
import com.greildev.erdmovee.utils.Constant
import com.greildev.erdmovee.utils.launchAndCollectIn
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieListFragment :
    BaseFragment<FragmentMovieListBinding, HomeViewModel>(FragmentMovieListBinding::inflate) {
    override val viewModel: HomeViewModel by viewModels()
    private var fetchData: String? = null
    private val movieListAdapter: MovieListAdapter by lazy {
        MovieListAdapter { id, title ->
            val bundle = Bundle()
            bundle.putString("movie_title", title)
            Analytics.logEvent(Constant.TO_DETAIL_SCREEN_EVENT, bundle)
            val toDetailMovieFragment =
                MovieListFragmentDirections.actionMovieListFragmentToDetailMovieFragment()
            toDetailMovieFragment.movieId = id
            findNavController().navigate(toDetailMovieFragment)
        }
    }

    override fun initView() {
        binding.tbMovieList.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        fetchData = MovieListFragmentArgs.fromBundle(arguments as Bundle).fetch
        if (fetchData == "popular") {
            binding.tbMovieList.title = "Popular Movie"
        } else if (fetchData == "nowplaying") {
            binding.tbMovieList.title = "Now Playing Movie"
        }
        binding.rvMovieList.adapter = movieListAdapter.withLoadStateFooter(
            footer = PagingLoadStateAdapter {
                movieListAdapter.retry()
            }
        )
        binding.rvMovieList.layoutManager = LinearLayoutManager(context)
    }

    override fun observeData() {
        if (fetchData == "popular") {
            viewModel.getPopularMovies().launchAndCollectIn(viewLifecycleOwner) {
                movieListAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }

        } else if (fetchData == "nowplaying") {
            viewModel.getNowPlayingMovies().launchAndCollectIn(viewLifecycleOwner) {
                movieListAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
    }
}
package com.greildev.erdmovee.ui.search

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.greildev.core.base.BaseFragment
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.FragmentSearchBinding
import com.greildev.erdmovee.ui.adapter.MovieListAdapter
import com.greildev.erdmovee.ui.adapter.PagingLoadStateAdapter
import com.greildev.erdmovee.ui.component.StatedViewState
import com.greildev.erdmovee.ui.homepage.HomePageFragmentDirections
import com.greildev.erdmovee.utils.Analytics
import com.greildev.erdmovee.utils.launchAndCollectIn
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>(
    FragmentSearchBinding::inflate
) {
    override val viewModel: SearchViewModel by viewModels()
    private val searchAdapter: MovieListAdapter by lazy {
        MovieListAdapter { id, title ->
            val logBundle = Bundle()
            logBundle.putString("movie_title", title)
            Analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, logBundle)
            val toDetailMovieFragment =
                HomePageFragmentDirections.actionHomePageFragmentToDetailMovieFragment()
            toDetailMovieFragment.movieId = id
            activity?.supportFragmentManager?.findFragmentById(R.id.fragment_container)
                ?.findNavController()?.navigate(toDetailMovieFragment)
        }
    }

    override fun initView() {
        binding.rvSearch.isVisible = false
        binding.loading.isVisible = false
        binding.viewStated.isVisible = true
        binding.viewStated.setMessage(
            title = getString(R.string.search_first_state_title),
            description = getString(R.string.search_first_state),
            state = StatedViewState.EMPTY,
        )
        binding.rvSearch.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter.withLoadStateFooter(
                footer = PagingLoadStateAdapter {
                    searchAdapter.retry()
                }
            )
        }
    }

    override fun observeData() {
        binding.searchProductField.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val logBundle = Bundle()
                logBundle.putString("search_movie", textView.text.toString())
                Analytics.logEvent(FirebaseAnalytics.Event.SEARCH, logBundle)
                viewModel.searchMovie(textView.text.toString())
                    .launchAndCollectIn(viewLifecycleOwner) {
                        binding.rvSearch.isVisible = true
                        binding.loading.isVisible = false
                        binding.viewStated.isVisible = false
                        searchAdapter.submitData(it)
                    }
            }
            true
        }
    }

    override fun initListener() {
        searchAdapter.addLoadStateListener { state ->
            when (state.refresh) {
                is LoadState.NotLoading -> {
                    if (state.append.endOfPaginationReached && searchAdapter.itemCount < 1) {
                        binding.rvSearch.isVisible = false
                        binding.loading.isVisible = false
                        binding.viewStated.isVisible = true
                        binding.viewStated.setMessage(
                            title = getString(R.string.empty),
                            description = getString(R.string.movie_not_found),
                            btnTitle = getString(R.string.refresh),
                            state = StatedViewState.EMPTY,
                        )
                    } else {
                        binding.rvSearch.isVisible = true
                        binding.loading.isVisible = false
                        binding.viewStated.isVisible = false
                    }
                }

                is LoadState.Loading -> {
                    if (searchAdapter.itemCount == 0) {
                        binding.rvSearch.isVisible = false
                        binding.loading.isVisible = true
                        binding.viewStated.isVisible = false
                    } else {
                        binding.rvSearch.isVisible = true
                        binding.loading.isVisible = false
                        binding.viewStated.isVisible = false
                    }
                }

                is LoadState.Error -> {
                    binding.rvSearch.isVisible = false
                    binding.loading.isVisible = false
                    binding.viewStated.isVisible = true
                    (state.refresh as LoadState.Error).error.localizedMessage?.let { message ->
                        binding.viewStated.setMessage(
                            title = getString(R.string.error),
                            description = message,
                            btnTitle = getString(R.string.refresh),
                            state = StatedViewState.ERROR,
                            action = {
                                viewModel.searchMovie(
                                    binding.searchProductField.text.toString(),
                                ).launchAndCollectIn(viewLifecycleOwner) {
                                    binding.rvSearch.isVisible = true
                                    binding.loading.isVisible = false
                                    binding.viewStated.isVisible = false
                                    searchAdapter.submitData(it)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
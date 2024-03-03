package com.greildev.erdmovee.ui.favorite

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.greildev.core.base.BaseFragment
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.FragmentFavoriteBinding
import com.greildev.erdmovee.ui.adapter.FavoriteListAdapter
import com.greildev.erdmovee.ui.component.StatedViewState
import com.greildev.erdmovee.ui.homepage.HomePageFragmentDirections
import com.greildev.erdmovee.utils.Analytics
import com.greildev.erdmovee.utils.Constant
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment :
    BaseFragment<FragmentFavoriteBinding, FavoriteViewModel>(FragmentFavoriteBinding::inflate) {

    override val viewModel: FavoriteViewModel by viewModels()

    private val favoriteAdapter: FavoriteListAdapter by lazy {
        FavoriteListAdapter(
            action = { movieId, title ->
                val logBundle = Bundle()
                logBundle.putString("movie title", title)
                Analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, logBundle)
                val toDetailMovieFragment =
                    HomePageFragmentDirections.actionHomePageFragmentToDetailMovieFragment()
                toDetailMovieFragment.movieId = movieId
                activity?.supportFragmentManager?.findFragmentById(R.id.fragment_container)
                    ?.findNavController()?.navigate(toDetailMovieFragment)
            },
            deleteAction = { favoriteId, title ->
                val logBundle = Bundle()
                logBundle.putString(Constant.MOVIE_TITLE, title)
                Analytics.logEvent("remove_from_wishlist", logBundle)
                viewModel.deleteFavoriteMovie(favoriteId)
            }
        )
    }

    override fun initView() {
        binding.svFavoriteMovie.isVisible = false
        binding.rvFavoriteMovie.apply {
            adapter = favoriteAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun observeData() {
        viewModel.getFavoriteMovieList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.svFavoriteMovie.isVisible = false
                binding.rvFavoriteMovie.isVisible = true
                favoriteAdapter.submitList(it)
            } else {
                binding.svFavoriteMovie.isVisible = true
                binding.rvFavoriteMovie.isVisible = false
                binding.svFavoriteMovie.setMessage(
                    title = getString(R.string.empty),
                    description = getString(R.string.mark_your_favorite_movie),
                    state = StatedViewState.EMPTY
                )
            }
        }

    }
}
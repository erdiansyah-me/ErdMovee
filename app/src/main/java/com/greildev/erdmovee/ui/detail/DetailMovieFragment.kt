package com.greildev.erdmovee.ui.detail

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.firebase.analytics.FirebaseAnalytics
import com.greildev.core.base.BaseFragment
import com.greildev.core.utils.UIState
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.FragmentDetailMovieBinding
import com.greildev.erdmovee.ui.adapter.MovieCastsAdapter
import com.greildev.erdmovee.ui.adapter.PagingLoadStateHorizontalAdapter
import com.greildev.erdmovee.ui.adapter.RecommendationMovieAdapter
import com.greildev.erdmovee.ui.component.StatedViewState
import com.greildev.erdmovee.utils.Analytics
import com.greildev.erdmovee.utils.Constant.MOVIE_TITLE
import com.greildev.erdmovee.utils.formatDecimal
import com.greildev.erdmovee.utils.imgUrlFormatter
import com.greildev.erdmovee.utils.launchAndCollectIn
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailMovieFragment :
    BaseFragment<FragmentDetailMovieBinding, DetailViewModel>(FragmentDetailMovieBinding::inflate) {

    override val viewModel: DetailViewModel by viewModels()
    private val movieIdArgs: DetailMovieFragmentArgs by navArgs()
    private val movieId: Int by lazy { movieIdArgs.movieId }
    private val recommendationAdapter by lazy {
        RecommendationMovieAdapter { id, title ->
            val logBundle = Bundle()
            logBundle.putString(MOVIE_TITLE, title)
            Analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, logBundle)
            val toDetailMovieFragment =
                DetailMovieFragmentDirections.actionDetailMovieFragmentSelf()
            toDetailMovieFragment.movieId = id
            findNavController().navigate(toDetailMovieFragment)
        }
    }



    private val castsAdapter by lazy {
        MovieCastsAdapter()
    }

    override fun initView() {
        viewModel.getMovieDetailData(movieId)
        binding.chipBookmark.isChecked = viewModel.checkFavoriteMovieById(movieId)
        binding.chipBookmark.text = if (binding.chipBookmark.isChecked) {
            getString(R.string.remove_from_favorite)
        } else {
            getString(R.string.add_to_favorite)
        }
        binding.btnAddCart.isEnabled = viewModel.checkCartMovieById(movieId).not()
    }

    @SuppressLint("SetTextI18n")
    override fun observeData() {
        viewModel.movieDetail.launchAndCollectIn(viewLifecycleOwner) { state ->
            when (state.movieDetailUI) {
                is UIState.Loading -> {
                    binding.loading.isVisible = true
                    binding.llDetailMovieContent.isVisible = false
                    binding.svDetailMovie.isVisible = false
                }

                is UIState.Success -> {
                    binding.loading.isVisible = false
                    binding.llDetailMovieContent.isVisible = true
                    binding.svDetailMovie.isVisible = false
                    val detailMovie = state.movieDetailUI.data
                    if (detailMovie != null) {
                        context?.let {
                            Glide.with(it)
                                .load(detailMovie.posterPath.imgUrlFormatter())
                                .error(R.drawable.ic_image_error_24)
                                .into(binding.ivMoviePoster)
                            Glide.with(it)
                                .load(detailMovie.backdropPath.imgUrlFormatter())
                                .error(R.drawable.ic_image_error_24)
                                .into(binding.ivMovieBackdrop)
                        }
                        binding.chipPrice.text = detailMovie.price.toString()
                        binding.tvMovieTitle.text = detailMovie.title
                        binding.tvMovieLength.text =
                            getString(R.string.minutes, detailMovie.runtime.toString())
                        binding.tvMovieOverview.text = detailMovie.overview
                        binding.tvMovieTagline.text = detailMovie.tagline
                        binding.tvRating.text =
                            getString(R.string._10, detailMovie.voteAverage.formatDecimal())
                        binding.tvMovieReleaseDate.text = detailMovie.releaseDate
                        binding.cgGenre.chipSpacingVertical = 0
                        detailMovie.genres?.let {
                            for (position in it) {
                                val chip = Chip(context)
                                chip.text = position.name
                                context?.let { it1 ->
                                    chip.setTextColor(
                                        ContextCompat.getColor(
                                            it1,
                                            R.color.md_theme_dark_onPrimary
                                        )
                                    )
                                }
                                chip.isCheckable = false
                                chip.isClickable = false
                                chip.setChipBackgroundColorResource(R.color.md_theme_dark_primary)
                                chip.chipStrokeWidth = 0.0F
                                binding.cgGenre.addView(chip)
                            }
                        }
                        castsAdapter.submitList(detailMovie.credits)
                        binding.rvCasts.adapter = castsAdapter
                        binding.rvRecommendation.adapter =
                            recommendationAdapter.withLoadStateFooter(
                                footer = PagingLoadStateHorizontalAdapter {
                                    recommendationAdapter.retry()
                                }
                            )
                        recommendationAdapter.submitData(state.movieRecomPaging)
                        binding.btnAddCart.setOnClickListener {
                            val logBundle = Bundle()
                            logBundle.putString(MOVIE_TITLE, detailMovie.title)
                            Analytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, logBundle)
                            viewModel.saveCartMovie(detailMovie, isRentNow = false)
                            binding.btnAddCart.isEnabled = false
                        }

                        binding.chipBookmark.setOnCheckedChangeListener { _, isChecked ->
                            val logBundle = Bundle()
                            if (isChecked) {
                                logBundle.putString(MOVIE_TITLE, detailMovie.title)
                                Analytics.logEvent(
                                    FirebaseAnalytics.Event.ADD_TO_WISHLIST,
                                    logBundle
                                )
                                binding.chipBookmark.text = getString(R.string.remove_from_favorite)
                                viewModel.saveFavoriteMovie(detailMovie)
                            } else {
                                logBundle.putString(MOVIE_TITLE, detailMovie.title)
                                Analytics.logEvent("remove_from_wishlist", logBundle)
                                binding.chipBookmark.text = getString(R.string.add_to_favorite)
                                viewModel.deleteFavoriteMovieById(movieId)
                            }
                        }

                        binding.btnBuyNow.setOnClickListener {
                            viewModel.getCartMovies()
                                .launchAndCollectIn(viewLifecycleOwner) { cart ->
                                    cart.forEach {
                                        viewModel.isCheckedByCartId(it.id, false)
                                    }
                                }
                            if (viewModel.checkCartMovieById(movieId)) {
                                viewModel.deleteCartMovie(movieId)
                            }
                            viewModel.saveCartMovie(detailMovie, isRentNow = true)
                            findNavController().navigate(
                                DetailMovieFragmentDirections.actionDetailMovieFragmentToCheckoutFragment()
                            )
                        }
                    }
                }

                is UIState.Error -> {
                    binding.loading.isVisible = false
                    binding.llDetailMovieContent.isVisible = false
                    binding.svDetailMovie.isVisible = true
                    state.movieDetailUI.message?.let {
                        binding.svDetailMovie.setMessage(
                            title = (state.movieDetailUI.code ?: getString(R.string.error)).toString(),
                            description = it,
                            btnTitle = getString(R.string.retry),
                            state = StatedViewState.ERROR,
                            action = {
                                viewModel.getMovieDetailData(movieId)
                            }
                        )
                    }
                }

                is UIState.NoState -> {}
            }
        }
    }

    override fun initListener() {
        binding.toolbarDetailMovie.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }
}

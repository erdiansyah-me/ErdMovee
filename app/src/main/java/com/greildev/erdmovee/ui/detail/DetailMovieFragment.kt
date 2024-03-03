package com.greildev.erdmovee.ui.detail

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
    private var movieId: Int? = null
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
        movieId = DetailMovieFragmentArgs.fromBundle(arguments as Bundle).movieId
        viewModel.getMovieDetail(movieId ?: 0)
        binding.chipBookmark.isChecked = viewModel.checkFavoriteMovieById(movieId ?: 0)
        binding.chipBookmark.text = if (binding.chipBookmark.isChecked) {
            getString(R.string.remove_from_favorite)
        } else {
            getString(R.string.add_to_favorite)
        }
        binding.btnAddCart.isEnabled = viewModel.checkCartMovieById(movieId ?: 0).not()
    }

    override fun observeData() {
        viewModel.movieDetail.launchAndCollectIn(viewLifecycleOwner) { state ->
            when (state) {
                is UIState.Loading -> {
                    binding.loading.isVisible = true
                    binding.llDetailMovieContent.isVisible = false
                    binding.svDetailMovie.isVisible = false
                }

                is UIState.Success -> {
                    binding.loading.isVisible = false
                    binding.llDetailMovieContent.isVisible = true
                    binding.svDetailMovie.isVisible = false
                    val detailMovie = state.data
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
                        viewModel.getRecommendation(movieId ?: 0)
                            .launchAndCollectIn(viewLifecycleOwner) {
                                recommendationAdapter.submitData(it)

                            }
                        binding.btnAddCart.setOnClickListener {
                            val logBundle = Bundle()
                            logBundle.putString(MOVIE_TITLE, detailMovie.title)
                            Analytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, logBundle)
                            viewModel.saveCartMovie(detailMovie)
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
                                viewModel.deleteFavoriteMovieById(movieId ?: 0)
                            }
                        }

                        binding.btnBuyNow.setOnClickListener {
                            viewModel.getCartMovies()
                                .launchAndCollectIn(viewLifecycleOwner) { cart ->
                                    cart.forEach {
                                        viewModel.isCheckedByCartId(it.id, false)
                                    }
                                }
                            viewModel.saveCartMovie(detailMovie)
                            findNavController().navigate(DetailMovieFragmentDirections.actionDetailMovieFragmentToCheckoutFragment())
                        }
                    }
                }

                is UIState.Error -> {
                    binding.loading.isVisible = false
                    binding.llDetailMovieContent.isVisible = false
                    binding.svDetailMovie.isVisible = true
                    state.message?.let {
                        binding.svDetailMovie.setMessage(
                            title = (state.code ?: getString(R.string.error)).toString(),
                            description = it,
                            btnTitle = getString(R.string.retry),
                            state = StatedViewState.ERROR,
                            action = {
                                viewModel.getMovieDetail(movieId ?: 0)
                            }
                        )
                    }
                }

                is UIState.NoState -> {}
            }
        }

        viewModel.getRecommendation(movieId ?: 0).launchAndCollectIn(viewLifecycleOwner) {

        }
    }

    override fun initListener() {
        binding.toolbarDetailMovie.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }
}
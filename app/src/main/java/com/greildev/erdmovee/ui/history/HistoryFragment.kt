package com.greildev.erdmovee.ui.history

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.greildev.core.base.BaseFragment
import com.greildev.core.utils.CoreConstant.EMPTY_CODE
import com.greildev.core.utils.UIState
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.FragmentHistoryBinding
import com.greildev.erdmovee.ui.adapter.HistoryTransactionAdapter
import com.greildev.erdmovee.ui.component.StatedViewState
import com.greildev.erdmovee.utils.launchAndCollectIn
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : BaseFragment<FragmentHistoryBinding, HistoryViewModel>(
    FragmentHistoryBinding::inflate
) {

    override val viewModel: HistoryViewModel by viewModels()

    private val historyAdapter: HistoryTransactionAdapter by lazy {
        HistoryTransactionAdapter(
            itemClickListener = {
            }
        )
    }

    override fun initView() {
        binding.rvHistory.adapter = historyAdapter
        binding.rvHistory.layoutManager = LinearLayoutManager(context)

    }

    override fun observeData() {
        viewModel.userData.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.getAllTransactionHistory(it.uid)
                    .launchAndCollectIn(viewLifecycleOwner) { state ->
                        when (state) {
                            is UIState.Loading -> {
                                binding.loading.isVisible = true
                                binding.rvHistory.isVisible = false
                                binding.svFavoriteMovie.isVisible = false
                            }

                            is UIState.Success -> {
                                binding.loading.isVisible = false
                                binding.rvHistory.isVisible = true
                                binding.svFavoriteMovie.isVisible = false
                                historyAdapter.submitList(state.data)
                            }

                            is UIState.Error -> {
                                binding.loading.isVisible = false
                                binding.rvHistory.isVisible = false
                                binding.svFavoriteMovie.isVisible = true
                                binding.svFavoriteMovie.setMessage(
                                    title = if (state.code == EMPTY_CODE) getString(R.string.empty) else getString(R.string.error),
                                    description = state.message
                                        ?: getString(R.string.something_went_wrong),
                                    state = if (state.code == EMPTY_CODE) StatedViewState.EMPTY else StatedViewState.ERROR
                                )
                            }

                            is UIState.NoState -> {}
                        }
                    }
            }
        }
    }
}
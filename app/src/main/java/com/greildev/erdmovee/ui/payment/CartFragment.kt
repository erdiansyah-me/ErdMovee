package com.greildev.erdmovee.ui.payment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.greildev.core.base.BaseFragment
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.FragmentCartBinding
import com.greildev.erdmovee.ui.adapter.CartListAdapter
import com.greildev.erdmovee.ui.component.StatedViewState
import com.greildev.erdmovee.utils.Analytics
import com.greildev.erdmovee.utils.launchAndCollectIn
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment :
    BaseFragment<FragmentCartBinding, PaymentViewModel>(FragmentCartBinding::inflate) {
    override val viewModel: PaymentViewModel by viewModels()

    private val cartAdapter by lazy {
        CartListAdapter(
            cbIsChecked = { cartId, isChecked ->
                viewModel.isCheckedByCartId(cartId, isChecked)
            },
            onIncrement = { cartId, newQuantity, newQuantityPrice ->
                viewModel.updateQuantity(cartId, newQuantity, newQuantityPrice)
            },
            onDecrement = { cartId, newQuantity, newQuantityPrice ->
                viewModel.updateQuantity(cartId, newQuantity, newQuantityPrice)
            }
        )
    }

    override fun initView() {
        binding.toolbarCart.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.cbSelectAll.isChecked = false
        binding.rvCartItem.adapter = cartAdapter
        binding.rvCartItem.layoutManager = LinearLayoutManager(context)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun observeData() {
        viewModel.getCartMovies().launchAndCollectIn(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.svCartMovie.isVisible = false
                binding.viewCartContent.isVisible = true
                cartAdapter.submitList(it)
                binding.cbSelectAll.isChecked = it.all { cart -> cart.isChecked }
            } else {
                binding.svCartMovie.isVisible = true
                binding.viewCartContent.isVisible = false
                binding.svCartMovie.setMessage(
                    title = getString(R.string.empty),
                    description = getString(R.string.its_empty_lets_pick_some_movie_to_rent),
                    state = StatedViewState.EMPTY,
                )
            }
        }
        viewModel.getCheckedCartByUid(true).launchAndCollectIn(viewLifecycleOwner) { state ->
            binding.tvTotalPrice.text = state.sumOf { it.quantityPrice }.toString()
            if (state.size == cartAdapter.currentList.size) {
                binding.cbSelectAll.isChecked = true
            }
        }
    }

    override fun initListener() {
        binding.btnDeleteSelected.setOnClickListener {
            cartAdapter.currentList.forEach {
                if (it.isChecked) {
                    viewModel.deletedCheckedCartByCartId(it.cartId, true)
                }
            }
        }
        binding.cbSelectAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.btnDeleteSelected.setOnClickListener {
                    viewModel.deleteCheckedCartByUid(true)
                }
                cartAdapter.currentList.forEach {
                    viewModel.isCheckedByCartId(it.cartId, true)
                }
            }
        }
        binding.btnRent.setOnClickListener {
            val logBundle = Bundle()
            logBundle.putString("checkout","${cartAdapter.currentList.filter { it.isChecked }.size} item" )
            Analytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, logBundle)
            val toCheckoutFragment = CartFragmentDirections.actionCartFragmentToCheckoutFragment()
            findNavController().navigate(toCheckoutFragment)
        }
    }
}
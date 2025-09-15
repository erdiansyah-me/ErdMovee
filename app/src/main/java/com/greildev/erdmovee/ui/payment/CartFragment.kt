package com.greildev.erdmovee.ui.payment

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment :
    BaseFragment<FragmentCartBinding, PaymentViewModel>(FragmentCartBinding::inflate) {
    override val viewModel: PaymentViewModel by viewModels()

    override fun initView() {
        binding.cbSelectAll.isChecked = false
        binding.rvCartItem.layoutManager = LinearLayoutManager(context)
    }

    override fun fetchData() {
        viewModel.getCartMovies()
    }

    override fun observeData() {
        viewModel.cartEntitites.observe(viewLifecycleOwner) { listOfCartItem ->
            if (listOfCartItem.isNotEmpty()) {
                val cartAdapter = CartListAdapter(
                    cbIsChecked = { position, isChecked, cartItem ->
                        cartItem.isChecked = isChecked
                        viewModel.updateCartEntitiesAt(position, cartItem)
                    },
                    onIncrement = { position, newQuantity, newQuantityPrice, cartItem ->
                        cartItem.quantityItem = newQuantity
                        cartItem.quantityPrice = newQuantityPrice
                        viewModel.updateCartEntitiesAt(position, cartItem)
                    },
                    onDecrement = { position, newQuantity, newQuantityPrice, cartItem ->
                        cartItem.quantityItem = newQuantity
                        cartItem.quantityPrice = newQuantityPrice
                        viewModel.updateCartEntitiesAt(position, cartItem)
                    }
                )
                binding.rvCartItem.adapter = cartAdapter
                binding.svCartMovie.isVisible = false
                binding.viewCartContent.isVisible = true
                val total = listOfCartItem.filter { it.isChecked }.sumOf { it.quantityPrice }.toString()
                binding.tvTotalPrice.text = total
                cartAdapter.submitList(listOfCartItem)
                binding.cbSelectAll.isChecked = listOfCartItem.all { it.isChecked }
                binding.btnDeleteSelected.setOnClickListener {
                    viewModel.deleteCartEntitiesOnChecked()
                }
            } else {
                binding.svCartMovie.isVisible = true
                binding.viewCartContent.isVisible = false
                binding.svCartMovie.setMessage(
                    title = getString(R.string.empty),
                    description = getString(R.string.its_empty_lets_pick_some_movie_to_rent),
                    state = StatedViewState.EMPTY,
                )
            }
            binding.btnRent.isEnabled = listOfCartItem.any { it.isChecked }
        }
    }

    override fun initListener() {
        binding.toolbarCart.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.replaceCartMovies()
                    // Call super to allow default back navigation behavior
                    findNavController().navigate(CartFragmentDirections.actionCartFragmentToHomePageFragment())
                }
            }
        )
        binding.cbSelectAll.setOnClickListener {
            viewModel.isAllChecked(binding.cbSelectAll.isChecked)
        }
        binding.btnRent.setOnClickListener {
            viewModel.replaceCartMovies()
            viewModel.addItemsToCheckout()
            val logBundle = Bundle()
            logBundle.putString(
                "checkout",
                "${viewModel.cartEntitites.value?.filter { it.isChecked }?.size ?: 0} item"
            )
            Analytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, logBundle)
            val toCheckoutFragment = CartFragmentDirections.actionCartFragmentToCheckoutFragment()
            findNavController().navigate(toCheckoutFragment)
        }
    }
}

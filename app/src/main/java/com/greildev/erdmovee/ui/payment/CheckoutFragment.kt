package com.greildev.erdmovee.ui.payment

import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.greildev.core.base.BaseFragment
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.FragmentCheckoutBinding
import com.greildev.erdmovee.ui.adapter.CheckoutListAdapter
import com.greildev.erdmovee.utils.isVisible
import com.greildev.erdmovee.utils.launchAndCollectIn
import com.greildev.erdmovee.utils.onValue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckoutFragment :
    BaseFragment<FragmentCheckoutBinding, PaymentViewModel>(FragmentCheckoutBinding::inflate) {

    override val viewModel: PaymentViewModel by activityViewModels()

    override fun initView() {
        binding.rvCheckoutMovies.layoutManager = LinearLayoutManager(context)
        binding.toolbarCheckout.setNavigationOnClickListener {
            findNavController().popBackStack()
            viewModel.deleteCheckoutItems()
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
                viewModel.deleteCheckoutItems()
            }
        })
    }

    override fun fetchData() {
        viewModel.getAllCheckoutItems()
        viewModel.getTokenUser()
    }

    override fun observeData() {
        viewModel.checkoutItemList.observe(viewLifecycleOwner) { state ->
            val total = state.sumOf { it.quantityPrice }.toString()
            binding.tvTotalPrice.text = total
            if (state.isNotEmpty()) {
                val checkoutAdapter by lazy {
                    CheckoutListAdapter(
                        onIncrement = { position, newQuantity, newQuantityPrice, item ->
                            item.quantityItem = newQuantity
                            item.quantityPrice = newQuantityPrice
                            viewModel.updateCheckoutItemAt(position, item)
                        },
                        onDecrement = { position, newQuantity, newQuantityPrice, item ->
                            item.quantityItem = newQuantity
                            item.quantityPrice = newQuantityPrice
                            viewModel.updateCheckoutItemAt(position, item)
                        }
                    )
                }
                binding.rvCheckoutMovies.adapter = checkoutAdapter
                checkoutAdapter.submitList(state)
            }
        }
        viewModel.tokenUser.launchAndCollectIn(viewLifecycleOwner) { token ->
            binding.tvCoinsBalance.text = getString(R.string.amount_coins, token.toString())
            if (binding.tvTotalPrice.text == 0.toString()) {
                binding.btnRent.isEnabled = false
                binding.btnRent.text = getString(R.string.insufficient_balance)
                binding.btnTopup.isVisible = true
            } else {
                binding.btnRent.isEnabled = true
                binding.btnTopup.isVisible = false
                binding.btnRent.text = getString(R.string.rent)
            }
            binding.tvTotalPrice.doAfterTextChanged {
                if (token > it.toString().toInt()) {
                    binding.btnRent.isEnabled = true
                    binding.btnTopup.isVisible = false
                    binding.btnRent.text = getString(R.string.rent)
                } else {
                    binding.btnRent.isEnabled = false
                    binding.btnRent.text = getString(R.string.insufficient_balance)
                    binding.btnTopup.isVisible = true
                }
            }
        }

        viewModel.paymentStatusModel.launchAndCollectIn(viewLifecycleOwner) { state ->
            state.onValue {
                if (it.isLoading) {
                    binding.loading.isVisible(true)
                } else {
                    binding.loading.isVisible(false)
                    val toPaymentStatus =
                        CheckoutFragmentDirections.actionCheckoutFragmentToPaymentStatusFragment()
                    toPaymentStatus.paymentStatusModel = it
                    findNavController().navigate(toPaymentStatus)
                }
            }
        }
    }

    override fun initListener() {
        binding.btnTopup.setOnClickListener {
            findNavController().navigate(CheckoutFragmentDirections.actionCheckoutFragmentToTopupFragment())
        }
        binding.btnRent.setOnClickListener {
            viewModel.checkoutMovie()
        }
    }
}

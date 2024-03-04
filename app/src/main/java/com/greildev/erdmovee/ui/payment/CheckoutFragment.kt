package com.greildev.erdmovee.ui.payment

import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.greildev.core.base.BaseFragment
import com.greildev.core.data.model.TransactionDetail
import com.greildev.core.domain.model.PaymentStatusModel
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.FragmentCheckoutBinding
import com.greildev.erdmovee.ui.adapter.CheckoutListAdapter
import com.greildev.erdmovee.utils.getCurrentDateTime
import com.greildev.erdmovee.utils.launchAndCollectIn
import com.greildev.erdmovee.utils.onCreated
import com.greildev.erdmovee.utils.onValue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckoutFragment :
    BaseFragment<FragmentCheckoutBinding, PaymentViewModel>(FragmentCheckoutBinding::inflate) {

    override val viewModel: PaymentViewModel by activityViewModels()

    private val checkoutAdapter by lazy {
        CheckoutListAdapter(
            onIncrement = { cartId, newQuantity, newQuantityPrice ->
                viewModel.updateQuantity(cartId, newQuantity, newQuantityPrice)
            },
            onDecrement = { cartId, newQuantity, newQuantityPrice ->
                viewModel.updateQuantity(cartId, newQuantity, newQuantityPrice)
            }
        )
    }

    override fun initView() {
        binding.rvCheckoutMovies.adapter = checkoutAdapter
        binding.rvCheckoutMovies.layoutManager = LinearLayoutManager(context)
        binding.toolbarCheckout.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun observeData() {
        viewModel.getCheckedCartByUid(true).launchAndCollectIn(viewLifecycleOwner) { state ->
            binding.tvTotalPrice.text = state.sumOf { it.quantityPrice }.toString()
            if (state.isNotEmpty()) {
                checkoutAdapter.submitList(state)
            }
        }
        viewModel.userData.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.getTokenUser(it.uid)
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
    }

    override fun initListener() {
        binding.btnTopup.setOnClickListener {
            findNavController().navigate(CheckoutFragmentDirections.actionCheckoutFragmentToTopupFragment())
        }
        binding.btnRent.setOnClickListener {
            val transactionId = viewModel.generateTransactionId()
            viewModel.userData.observe(viewLifecycleOwner) {
                if (it != null) {
                    viewModel.writeTransactionHistory(
                        it.uid,
                        TransactionDetail(
                            transactionId = transactionId,
                            cartMovieListEntities = checkoutAdapter.currentList,
                            transactionDate = getCurrentDateTime(),
                            amountToken = binding.tvTotalPrice.text.toString().toInt(),
                        )
                    )
                }
            }
            viewModel.isWriteTransactionHistory.launchAndCollectIn(viewLifecycleOwner) { state ->
                state.onCreated { }
                    .onValue {
                        val toPaymentStatus =
                            CheckoutFragmentDirections.actionCheckoutFragmentToPaymentStatusFragment()
                        if (it) {
                            viewModel.userData.observe(viewLifecycleOwner) { user ->

                                if (user != null) {
                                    viewModel.tokenUser.launchAndCollectIn(viewLifecycleOwner) { token ->
                                        viewModel.updateTokenUser(
                                            user.uid,
                                            token = token - binding.tvTotalPrice.text.toString()
                                                .toInt()
                                        )
                                    }
                                }
                                toPaymentStatus.paymentStatusModel = PaymentStatusModel(
                                    isSuccess = true,
                                    transactionDetail = TransactionDetail(
                                        transactionId = transactionId,
                                        cartMovieListEntities = checkoutAdapter.currentList,
                                        transactionDate = getCurrentDateTime(),
                                        amountToken = binding.tvTotalPrice.text.toString().toInt(),
                                    )
                                )
                                findNavController().navigate(toPaymentStatus)
                            }
                        } else {
                            toPaymentStatus.paymentStatusModel =
                                PaymentStatusModel(isSuccess = false)
                            findNavController().navigate(toPaymentStatus)
                        }
                    }
            }
        }
    }
}
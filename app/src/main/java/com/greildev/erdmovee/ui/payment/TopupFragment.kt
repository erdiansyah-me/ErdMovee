package com.greildev.erdmovee.ui.payment

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.greildev.core.base.BaseFragment
import com.greildev.core.data.model.TransactionToken
import com.greildev.core.domain.model.PaymentListInfo
import com.greildev.core.domain.model.PaymentStatusModel
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.FragmentTopupBinding
import com.greildev.erdmovee.ui.component.MoveeSnackbar
import com.greildev.erdmovee.ui.component.StateSnackbar
import com.greildev.erdmovee.utils.TransactionType
import com.greildev.erdmovee.utils.chipGroupSetSelectedChip
import com.greildev.erdmovee.utils.getCurrentDateTime
import com.greildev.erdmovee.utils.launchAndCollectIn
import com.greildev.erdmovee.utils.onCreated
import com.greildev.erdmovee.utils.onValue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import java.util.UUID

@AndroidEntryPoint
class TopupFragment :
    BaseFragment<FragmentTopupBinding, PaymentViewModel>(FragmentTopupBinding::inflate) {
    override val viewModel: PaymentViewModel by viewModels()
    private var paymentMethod: PaymentListInfo? = null
    override fun initView() {
        paymentMethod = TopupFragmentArgs.fromBundle(arguments as Bundle).paymentMethod

        if (paymentMethod != null) {
            binding.tvPaymentName.text =
                paymentMethod?.label ?: getString(R.string.pilih_metode_pembayaran)
            context?.let {
                Glide.with(it)
                    .load(paymentMethod?.image)
                    .error(R.drawable.ic_add_payment_24)
                    .into(binding.ivPaymentVirtualLogo)
            }
        }

        binding.tbTopup.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun observeData() {
        viewModel.userData.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                viewModel.getTokenUser(user.uid)
            }
        }
    }

    override fun initListener() {
        binding.cgTopup.setOnCheckedStateChangeListener { chipGroup, ints ->
            binding.tifPayment.clearFocus()
            val selectedChip = chipGroup.chipGroupSetSelectedChip().filterCoins()
            binding.tifPayment.setText(selectedChip)
        }
        binding.tifPayment.setOnFocusChangeListener { view, b ->
            if (b) {
                binding.cgTopup.clearCheck()
            }
        }
        binding.cvPaymentItem.setOnClickListener {
            findNavController().navigate(TopupFragmentDirections.actionTopupFragmentToPaymentMethodFragment())
        }
        binding.btnPay.setOnClickListener {
            val paymentAmount = binding.tifPayment.text.toString()
            if (paymentMethod != null && paymentAmount.isNotEmpty()) {
                viewModel.userData.observe(viewLifecycleOwner) {
                    if (it != null) {
                        val transactionId = UUID.randomUUID().toString()
                        viewModel.writeTokenTransaction(
                            it.uid, TransactionToken(
                                transactionId = transactionId,
                                transactionDate = getCurrentDateTime(),
                                transactionType = TransactionType.TOPUP.name,
                                amountToken = binding.tifPayment.text.toString().toInt(),
                                transactionMethod = paymentMethod?.label ?: ""
                            )
                        )
                    }
                }
                viewModel.isWriteTokenTransaction.launchAndCollectIn(viewLifecycleOwner) { stateWriteToken ->
                    stateWriteToken.onCreated { }
                        .onValue { writeTokenTransaction ->
                            if (writeTokenTransaction) {
                                val toPaymentStatus =
                                    TopupFragmentDirections.actionTopupFragmentToPaymentStatusFragment()
                                toPaymentStatus.paymentStatusModel = PaymentStatusModel(
                                    isSuccess = true,
                                    transactionToken = TransactionToken(
                                        transactionId = viewModel.generateTransactionId(),
                                        transactionDate = getCurrentDateTime(),
                                        transactionType = TransactionType.TOPUP.name,
                                        amountToken = binding.tifPayment.text.toString().toInt(),
                                        transactionMethod = paymentMethod?.label ?: ""
                                    )
                                )
                                viewModel.userData.observe(viewLifecycleOwner) { user ->
                                    if (user != null) {
                                        viewModel.tokenUser.launchAndCollectIn(viewLifecycleOwner) { tokenUser ->
                                            val timeDelay: Long = 1000
                                            delay(timeDelay)
                                            val totalToken = tokenUser.plus(
                                                binding.tifPayment.text.toString().toInt()
                                            )
                                            viewModel.updateTokenUser(
                                                user.uid,
                                                token = totalToken
                                            )
                                        }
                                    }
                                }
                                viewModel.isUpdateSuccess.launchAndCollectIn(viewLifecycleOwner) { state ->
                                    state.onCreated { }
                                        .onValue {
                                            if (it) {
                                                findNavController().navigate(toPaymentStatus)
                                            } else {
                                                val toPaymentStatus =
                                                    TopupFragmentDirections.actionTopupFragmentToPaymentStatusFragment()
                                                toPaymentStatus.paymentStatusModel =
                                                    PaymentStatusModel(isSuccess = false)
                                                findNavController().navigate(toPaymentStatus)
                                            }
                                        }
                                }
                            } else {
                                val toPaymentStatus =
                                    TopupFragmentDirections.actionTopupFragmentToPaymentStatusFragment()
                                toPaymentStatus.paymentStatusModel =
                                    PaymentStatusModel(isSuccess = false)
                                findNavController().navigate(toPaymentStatus)
                            }
                        }
                }
            } else {
                context?.let { it1 ->
                    MoveeSnackbar.showSnackbarCustom(
                        context = it1,
                        root = binding.root,
                        text = "Please Pick Payment Method and Fill the token amount",
                        state = StateSnackbar.INFO,
                        action = {}
                    )
                }
            }
        }
    }
    private fun String.filterCoins() : String {
        val regex = Regex("""(\d+)""")
        val matchResult = regex.find(this)

        return matchResult?.value ?: 0.toString()
    }
}
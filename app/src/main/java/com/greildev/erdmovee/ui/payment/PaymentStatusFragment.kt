package com.greildev.erdmovee.ui.payment

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.greildev.core.base.BaseFragment
import com.greildev.core.domain.model.PaymentStatusModel
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.FragmentPaymentStatusBinding
import com.greildev.erdmovee.ui.adapter.PaymentStatusAdapter
import com.greildev.erdmovee.utils.doubleBackToExit
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentStatusFragment :
    BaseFragment<FragmentPaymentStatusBinding, PaymentViewModel>(FragmentPaymentStatusBinding::inflate) {

    override val viewModel: PaymentViewModel by viewModels()

    private var paymentStatus: PaymentStatusModel? = null

    private val paymentStatusAdapter: PaymentStatusAdapter by lazy {
        PaymentStatusAdapter()
    }

    override fun initView() {
        paymentStatus = PaymentStatusFragmentArgs.fromBundle(arguments as Bundle).paymentStatusModel
        if (paymentStatus != null) {
            if (paymentStatus?.isSuccess == true) {
                binding.ltAnimStatus.setAnimation(R.raw.anim_process_success)
                binding.ltAnimStatus.playAnimation()
                binding.tvPaymentStatus.text = getString(R.string.payment_success)
                if (paymentStatus?.transactionDetail != null) {
                    paymentStatus?.transactionDetail?.let {
                        binding.apply {
                            tvPaymentStatusDescDateValue.text = it.transactionDate
                            tvPaymentStatusDescIdValue.text = it.transactionId
                            tvPaymentStatusDescNominalValue.text = it.amountToken.toString()
                            rvPaymentStatusItem.adapter = paymentStatusAdapter
                            rvPaymentStatusItem.layoutManager = LinearLayoutManager(context)
                            paymentStatusAdapter.submitList(it.cartMovieListEntities)
                        }
                    }
                } else if (paymentStatus?.transactionToken != null) {
                    paymentStatus?.transactionToken?.let {
                        binding.apply {
                            tvPaymentStatusDescDateValue.text = it.transactionDate
                            tvPaymentStatusDescIdValue.text = it.transactionId
                            tvPaymentStatusDescNominalValue.text = it.amountToken.toString()
                            rvPaymentStatusItem.adapter = paymentStatusAdapter
                        }
                    }
                }
            } else {
                binding.rvPaymentStatusItem.isVisible = false
                binding.ltAnimStatus.setAnimation(R.raw.anim_process_failed)
                binding.ltAnimStatus.playAnimation()
                binding.tvPaymentStatus.text = getString(R.string.payment_failed)
                binding.tvDate.text = "-"
                binding.tvId.text = "-"
                binding.tvPaymentStatusDesc.text =
                    getString(R.string.pembayaran_gagal_silahkan_coba_kembali)
                binding.tvNominal.text = "-"
            }
        }
        context?.let { doubleBackToExit(it, activity, viewLifecycleOwner) }
    }

    override fun observeData() {
    }

    override fun initListener() {
        binding.btnDone.setOnClickListener {
            if (paymentStatus?.transactionDetail != null) {
                viewModel.deleteCheckedCartByUid(isChecked = true)
            }
            findNavController().navigate(PaymentStatusFragmentDirections.actionPaymentStatusFragmentToHomePageFragment())
        }
    }

}
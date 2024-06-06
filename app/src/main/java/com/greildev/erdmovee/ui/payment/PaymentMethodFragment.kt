package com.greildev.erdmovee.ui.payment

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.greildev.core.base.BaseFragment
import com.greildev.core.domain.model.PaymentListInfo
import com.greildev.erdmovee.databinding.FragmentPaymentMethodBinding
import com.greildev.erdmovee.ui.adapter.PaymentCategoryAdapter
import com.greildev.erdmovee.utils.Analytics
import com.greildev.erdmovee.utils.launchAndCollectIn
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentMethodFragment : BaseFragment<FragmentPaymentMethodBinding, PaymentViewModel>(
    FragmentPaymentMethodBinding::inflate
) {
    override val viewModel: PaymentViewModel by viewModels()
    private val paymentAdapter: PaymentCategoryAdapter = PaymentCategoryAdapter()
    override fun initView() {
        binding.rvPaymentCategoryItem.adapter = paymentAdapter
        binding.rvPaymentCategoryItem.layoutManager = LinearLayoutManager(context)
        binding.tbPaymentMethod.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
        paymentListLoad()
    }

    override fun observeData() {
        viewModel.getPaymentListUpdate().launchAndCollectIn(viewLifecycleOwner) {
            binding.rvPaymentCategoryItem.adapter = paymentAdapter
            binding.rvPaymentCategoryItem.layoutManager = LinearLayoutManager(context)
            if (it.isNotEmpty()) {
                paymentListLoad()
            } else {
                paymentAdapter.submitList(it)
            }
        }
    }

    private fun paymentListLoad() {
        viewModel.getPaymentList().launchAndCollectIn(viewLifecycleOwner) {
            paymentAdapter.submitList(it)
            paymentAdapter.setOnItemClickListener(object :
                PaymentCategoryAdapter.OnPaymentCategoryClickListener {
                override fun onItemClicked(data: PaymentListInfo) {
                    val logBundle = Bundle()
                    logBundle.putString("payment_info", data.label)
                    Analytics.logEvent(FirebaseAnalytics.Event.ADD_PAYMENT_INFO, logBundle)
                    val toTopUp =
                        PaymentMethodFragmentDirections.actionPaymentMethodFragmentToTopupFragment()
                    toTopUp.paymentMethod = data
                    findNavController().navigate(toTopUp)
                }
            })
        }
    }
}
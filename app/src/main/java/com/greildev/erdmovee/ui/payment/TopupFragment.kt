package com.greildev.erdmovee.ui.payment

import androidx.fragment.app.viewModels
import com.greildev.core.base.BaseFragment
import com.greildev.erdmovee.databinding.FragmentTopupBinding

class TopupFragment : BaseFragment<FragmentTopupBinding, PaymentViewModel>(FragmentTopupBinding::inflate) {
    override val viewModel: PaymentViewModel by viewModels()
    override fun initView() {
    }

    override fun observeData() {
    }
}
package com.greildev.erdmovee.ui.payment

import androidx.fragment.app.viewModels
import com.greildev.core.base.BaseFragment
import com.greildev.erdmovee.databinding.FragmentCartBinding

class CartFragment : BaseFragment<FragmentCartBinding, PaymentViewModel>(FragmentCartBinding::inflate) {
    override val viewModel: PaymentViewModel by viewModels()
    override fun initView() {
    }

    override fun observeData() {
    }


}
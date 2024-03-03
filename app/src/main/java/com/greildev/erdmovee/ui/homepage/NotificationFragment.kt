package com.greildev.erdmovee.ui.homepage

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.greildev.core.base.BaseFragment
import com.greildev.erdmovee.databinding.FragmentNotificationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment :
    BaseFragment<FragmentNotificationBinding, HomeViewModel>(FragmentNotificationBinding::inflate) {
    override val viewModel: HomeViewModel by viewModels()
    override fun initView() {
        binding.tbNotification.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun observeData() {
    }

}
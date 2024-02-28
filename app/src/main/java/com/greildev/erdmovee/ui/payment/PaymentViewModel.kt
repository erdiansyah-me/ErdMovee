package com.greildev.erdmovee.ui.payment

import androidx.lifecycle.ViewModel
import com.greildev.core.domain.usecase.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(useCase: UseCase): ViewModel() {
}
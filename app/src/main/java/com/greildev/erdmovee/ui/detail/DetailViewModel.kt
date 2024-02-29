package com.greildev.erdmovee.ui.detail

import androidx.lifecycle.ViewModel
import com.greildev.core.domain.usecase.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(useCase: UseCase): ViewModel(){
}
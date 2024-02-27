package com.greildev.core.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding?, VM : ViewModel>(
    private val bindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> VB,
) : Fragment() {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!
    protected abstract val viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: android.os.Bundle?
    ): android.view.View? {
        _binding = bindingFactory.invoke(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        observeData()
    }

    abstract fun initView()

    abstract fun observeData()

    open fun initListener() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
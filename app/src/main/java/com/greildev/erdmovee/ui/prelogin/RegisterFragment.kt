package com.greildev.erdmovee.ui.prelogin

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.greildev.core.base.BaseFragment
import com.greildev.core.utils.UIState
import com.greildev.core.utils.streamData
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.FragmentRegisterBinding
import com.greildev.erdmovee.ui.component.MoveeSnackbar
import com.greildev.erdmovee.ui.component.StateSnackbar
import com.greildev.erdmovee.utils.Analytics
import com.greildev.erdmovee.utils.Constant
import com.greildev.erdmovee.utils.Validate
import com.greildev.erdmovee.utils.doubleBackToExit
import com.greildev.erdmovee.utils.launchAndCollectIn
import com.greildev.erdmovee.utils.onValue
import com.greildev.erdmovee.utils.tncText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment :
    BaseFragment<FragmentRegisterBinding, PreloginViewModel>(FragmentRegisterBinding::inflate) {
    override val viewModel: PreloginViewModel by viewModels()
    override fun initView(): Unit = with(binding) {
        loading.isVisible = false
        tvTnc.movementMethod = LinkMovementMethod.getInstance()
        tvTnc.text = context?.let {
            resources.getString(R.string.tnc_auth)
                .tncText(it, resources.configuration.locales[0].language)
        }
        btnRegister.text = getString(R.string.register)
        context?.let {
            doubleBackToExit(
                context = it,
                activity = activity,
                viewLifecycleOwner = viewLifecycleOwner
            )
        }
    }

    override fun observeData() {
        viewModel.validateRegisterEmail.launchAndCollectIn(viewLifecycleOwner) {
            binding.apply {
                tilEmail.isErrorEnabled = it == Validate.INVALID
                tilEmail.error =
                    if (it == Validate.INVALID) getString(R.string.email_tidak_valid) else null
            }
        }
        viewModel.validateRegisterPassword.launchAndCollectIn(viewLifecycleOwner) {
            binding.apply {
                tilPassword.isErrorEnabled = it == Validate.INVALID
                tilPassword.error =
                    if (it == Validate.INVALID) getString(R.string.password_tidak_valid) else null
            }
        }
        viewModel.validateRegisterField.launchAndCollectIn(viewLifecycleOwner) { state ->
            state.onValue { binding.btnRegister.isEnabled = it }
        }
        viewModel.userRegister.launchAndCollectIn(viewLifecycleOwner) { state ->
            binding.loading.isVisible = state is UIState.Loading
            state.streamData(
                onSuccess = {
                    if (it.data == true) {
                        MoveeSnackbar.showSnackbarCustom(
                            context,
                            binding.root,
                            getString(R.string.selamat_datang, binding.tifEmail.text.toString()),
                            StateSnackbar.SUCCESS
                        ) {
                            binding.loading.cancelAnimation()
                            val logBundle = Bundle()
                            logBundle.putString("email", binding.tifEmail.text.toString())
                            Analytics.logEvent(Constant.REGISTER_USER, logBundle)
                            findNavController().navigate(
                                RegisterFragmentDirections.actionRegisterFragmentToProfileFragment()
                            )
                        }
                    } else {
                        MoveeSnackbar.showSnackbarCustom(
                            context,
                            binding.root,
                            getString(R.string.login_gagal, it.message),
                            StateSnackbar.ERROR
                        )
                    }
                },
                onError = {
                    MoveeSnackbar.showSnackbarCustom(
                        context,
                        binding.root,
                        getString(R.string.login_gagal, it.message),
                        StateSnackbar.ERROR
                    )
                }
            )
        }
    }

    override fun initListener() = with(binding) {

        tifEmail.doAfterTextChanged {
            viewModel.validateRegisterEmail(it.toString())
        }
        tifPassword.doAfterTextChanged {
            viewModel.validateRegisterPassword(it.toString())
        }
        btnRegister.setOnClickListener {
            val email = tifEmail.text.toString().trim()
            val password = tifPassword.text.toString().trim()
            viewModel.registerUser(email = email, password = password)
        }

        btnLogin.setOnClickListener {
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
        }
    }
}

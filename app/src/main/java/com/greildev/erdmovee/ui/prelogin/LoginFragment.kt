package com.greildev.erdmovee.ui.prelogin

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.greildev.core.base.BaseFragment
import com.greildev.core.utils.UIState
import com.greildev.core.utils.streamData
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.FragmentLoginBinding
import com.greildev.erdmovee.ui.component.MoveeSnackbar
import com.greildev.erdmovee.ui.component.StateSnackbar
import com.greildev.erdmovee.utils.Analytics
import com.greildev.erdmovee.utils.Validate
import com.greildev.erdmovee.utils.doubleBackToExit
import com.greildev.erdmovee.utils.launchAndCollectIn
import com.greildev.erdmovee.utils.onValue
import com.greildev.erdmovee.utils.tncText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment :
    BaseFragment<FragmentLoginBinding, PreloginViewModel>(FragmentLoginBinding::inflate) {
    override val viewModel: PreloginViewModel by viewModels()

    override fun initView() {
        binding.loading.isVisible = false
        binding.tvTnc.movementMethod = LinkMovementMethod.getInstance()
        binding.tvTnc.text = context?.let {
            resources.getString(R.string.tnc_auth)
                .tncText(it, resources.configuration.locales[0].language)
        }
        context?.let {
            doubleBackToExit(
                context = it,
                activity = activity,
                viewLifecycleOwner = viewLifecycleOwner
            )
        }
    }

    override fun observeData() {
        viewModel.validateLoginField.launchAndCollectIn(viewLifecycleOwner) { state ->
            state.onValue { binding.btnLogin.isEnabled = it }
        }
        viewModel.validateLoginEmail.launchAndCollectIn(viewLifecycleOwner) {
            binding.apply {
                tilEmail.isErrorEnabled = it == Validate.INVALID
                tilEmail.error =
                    if (it == Validate.INVALID) getString(R.string.email_tidak_valid) else null
            }
        }
        viewModel.validateLoginPassword.launchAndCollectIn(viewLifecycleOwner) {
            binding.apply {
                tilPassword.isErrorEnabled = it == Validate.INVALID
                tilPassword.error =
                    if (it == Validate.INVALID) getString(R.string.password_tidak_valid) else null
            }
        }
        viewModel.userLogin.launchAndCollectIn(viewLifecycleOwner) { state ->
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
                            val logBundle = Bundle()
                            logBundle.putString("email", binding.tifEmail.text.toString())
                            Analytics.logEvent(FirebaseAnalytics.Event.LOGIN, logBundle)
                            binding.loading.cancelAnimation()
                            findNavController().navigate(
                                LoginFragmentDirections.actionLoginFragmentToHomePageFragment()
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
            viewModel.validateLoginEmail(it.toString())
        }
        tifPassword.doAfterTextChanged {
            viewModel.validateLoginPassword(it.toString())
        }

        btnRegister.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        btnLogin.setOnClickListener {
            val email = tifEmail.text.toString().trim()
            val password = tifPassword.text.toString().trim()
            viewModel.loginUser(email, password)
        }
    }
}

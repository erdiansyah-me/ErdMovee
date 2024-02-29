package com.greildev.erdmovee.ui.prelogin

import android.text.method.LinkMovementMethod
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.greildev.core.base.BaseFragment
import com.greildev.core.utils.streamData
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.FragmentLoginBinding
import com.greildev.erdmovee.ui.component.MoveeSnackbar
import com.greildev.erdmovee.ui.component.StateSnackbar
import com.greildev.erdmovee.utils.doubleBackToExit
import com.greildev.erdmovee.utils.launchAndCollectIn
import com.greildev.erdmovee.utils.onCreated
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
        viewModel.validateLoginEmail.launchAndCollectIn(viewLifecycleOwner) { state ->
            state.onCreated { }
                .onValue {
                    binding.apply {
                        btnLogin.isEnabled = it
                        if (!it) {
                            btnLogin.text = getString(R.string.email_tidak_valid)
                        } else {
                            btnLogin.text = getString(R.string.login)
                        }
                        tilEmail.isErrorEnabled = it.not()
                        tilEmail.error =
                            if (it.not()) getString(R.string.email_tidak_valid) else null
                    }
                }
        }

        viewModel.validateLoginPassword.launchAndCollectIn(viewLifecycleOwner) { state ->
            state.onCreated { }
                .onValue {
                    binding.apply {
                        btnLogin.isEnabled = it
                        if (!it) {
                            btnLogin.text = getString(R.string.password_tidak_valid)
                        } else {
                            btnLogin.text = getString(R.string.login)
                        }
                        tilPassword.isErrorEnabled = it.not()
                        tilPassword.error =
                            if (it.not()) getString(R.string.password_tidak_valid) else null
                    }
                }
        }
        viewModel.userLogin.launchAndCollectIn(viewLifecycleOwner) { state ->
            state.streamData(
                onSuccess = {
                    binding.loading.isVisible = false
                    if (it.data == true) {
                        context?.let { it1 ->
                            MoveeSnackbar.showSnackbarCustom(
                                it1,
                                binding.root,
                                getString(R.string.selamat_datang, binding.tifEmail.text.toString()),
                                StateSnackbar.SUCCESS
                            ) {
                                binding.loading.cancelAnimation()
                                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomePageFragment())
                            }
                        }
                    } else {
                        context?.let { it1 ->
                            MoveeSnackbar.showSnackbarCustom(
                                it1,
                                binding.root,
                                getString(R.string.login_gagal, it.message),
                                StateSnackbar.ERROR
                            ) {
                            }
                        }
                    }

                },
                onError = {
                    binding.loading.isVisible = false
                    context?.let { it1 ->
                        MoveeSnackbar.showSnackbarCustom(
                            it1,
                            binding.root,
                            getString(R.string.login_gagal, it.message),
                            StateSnackbar.ERROR
                        ) {
                        }
                    }
                },
                onLoading = {
                    binding.loading.isVisible = true
                },
                onNoState = {}
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
            viewModel.validateLoginField(email, password)
            viewModel.validateLoginField.launchAndCollectIn(viewLifecycleOwner) { state ->
                state.onCreated { }
                    .onValue {
                        if (!it) {
                            context?.let { it1 ->
                                MoveeSnackbar.showSnackbarCustom(
                                    it1,
                                    binding.root,
                                    getString(R.string.field_tidak_boleh_kosong),
                                    StateSnackbar.ERROR,
                                    {}
                                )
                            }
                        } else {
                            viewModel.loginUser(email, password)
                        }
                    }
            }
//            viewModel.loginUser(email , password)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}
package com.greildev.erdmovee.ui.prelogin

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.greildev.core.base.BaseFragment
import com.greildev.core.utils.streamData
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.FragmentRegisterBinding
import com.greildev.erdmovee.ui.component.MoveeSnackbar
import com.greildev.erdmovee.ui.component.StateSnackbar
import com.greildev.erdmovee.utils.Analytics
import com.greildev.erdmovee.utils.Constant
import com.greildev.erdmovee.utils.doubleBackToExit
import com.greildev.erdmovee.utils.launchAndCollectIn
import com.greildev.erdmovee.utils.onCreated
import com.greildev.erdmovee.utils.onValue
import com.greildev.erdmovee.utils.tncText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment :
    BaseFragment<FragmentRegisterBinding, PreloginViewModel>(FragmentRegisterBinding::inflate) {
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
        viewModel.validateRegisterEmail.launchAndCollectIn(viewLifecycleOwner) { state ->
            state.onCreated { }
                .onValue {
                    binding.apply {
                        btnRegister.isEnabled = it
                        if (!it) {
                            btnRegister.text = getString(R.string.email_tidak_valid)
                        } else {
                            btnLogin.text = getString(R.string.register)
                        }
                        tilEmail.isErrorEnabled = it.not()
                        tilEmail.error =
                            if (it.not()) getString(R.string.email_tidak_valid) else null
                    }
                }
        }

        viewModel.validateRegisterPassword.launchAndCollectIn(viewLifecycleOwner) { state ->
            state.onCreated { }
                .onValue {
                    binding.apply {
                        btnRegister.isEnabled = it
                        if (!it) {
                            btnRegister.text = getString(R.string.password_tidak_valid)
                        } else {
                            btnRegister.text = getString(R.string.register)
                        }
                        tilPassword.isErrorEnabled = it.not()
                        tilPassword.error =
                            if (it.not()) getString(R.string.password_tidak_valid) else null
                    }
                }
        }
        viewModel.userRegister.launchAndCollectIn(viewLifecycleOwner) { state ->
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
                                val logBundle = Bundle()
                                logBundle.putString("email", binding.tifEmail.text.toString())
                                Analytics.logEvent(Constant.REGISTER_USER, logBundle)
                                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToProfileFragment())
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
            viewModel.validateRegisterEmail(it.toString())
        }
        tifPassword.doAfterTextChanged {
            viewModel.validateRegisterPassword(it.toString())
        }
        btnRegister.setOnClickListener {
            val email = tifEmail.text.toString().trim()
            val password = tifPassword.text.toString().trim()
            viewModel.validateRegisterField(email, password)
            viewModel.validateRegisterField.launchAndCollectIn(viewLifecycleOwner) { state ->
                state.onCreated { }
                    .onValue {
                        if (!it) {
                            context?.let { it1 ->
                                MoveeSnackbar.showSnackbarCustom(
                                    it1,
                                    binding.root,
                                    getString(R.string.field_tidak_boleh_kosong),
                                    StateSnackbar.ERROR
                                ) {}
                            }
                        } else {
                            viewModel.registerUser(
                                email = email,
                                password = password
                            )
                        }
                    }
            }
//            viewModel.registerUser(
//                email = email,
//                password = password
//            )
        }

        btnLogin.setOnClickListener {
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
        }
    }
}
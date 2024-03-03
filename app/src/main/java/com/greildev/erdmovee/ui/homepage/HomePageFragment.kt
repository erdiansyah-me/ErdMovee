package com.greildev.erdmovee.ui.homepage

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.greildev.core.base.BaseFragment
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.FragmentHomePageBinding
import com.greildev.erdmovee.ui.component.MoveeSnackbar
import com.greildev.erdmovee.ui.component.StateSnackbar
import com.greildev.erdmovee.utils.Analytics
import com.greildev.erdmovee.utils.Constant
import com.greildev.erdmovee.utils.doubleBackToExit
import com.greildev.erdmovee.utils.launchAndCollectIn
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomePageFragment :
    BaseFragment<FragmentHomePageBinding, HomeViewModel>(FragmentHomePageBinding::inflate) {

    override val viewModel: HomeViewModel by viewModels()

    private var navHostFragment: NavHostFragment? = null

    override fun initView() {
        navHostFragment =
            childFragmentManager.findFragmentById(R.id.fragment_container_home) as NavHostFragment
        val navController = navHostFragment?.navController
        if (navController != null) {
            binding.bottomNavbar.setupWithNavController(navController)
        }
        context?.let { doubleBackToExit(it, activity, viewLifecycleOwner) }
    }

    override fun observeData() {
        viewModel.userData.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.getTokenUser(it.uid)
                val photoUri = it.photoUri
                binding.tvUsername.text = getString(R.string.hello_home, it.username ?: it.email)
                if (photoUri != null && photoUri.toString().isNotEmpty()) {
                    context?.let { it1 ->
                        Glide.with(it1)
                            .load(photoUri)
                            .circleCrop()
                            .into(binding.ivUserAvatar)
                    }
                }
            } else {
                findNavController().navigate(HomePageFragmentDirections.actionHomePageFragmentToLoginFragment())
            }
        }
        viewModel.tokenUser.launchAndCollectIn(viewLifecycleOwner) {
            binding.chipBalance.text = it.toString()
        }
    }

    override fun initListener() {
        binding.chipBalance.setOnClickListener {
            context?.let { it1 ->
                MoveeSnackbar.showSnackbarCustom(
                    it1,
                    binding.root,
                    getString(R.string.amount_coins, binding.chipBalance.text.toString()),
                    StateSnackbar.COMMON
                ) {
                    findNavController().navigate(HomePageFragmentDirections.actionHomePageFragmentToTopupFragment())
                }
            }
        }
        binding.bottomNavbar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mainFragment -> {
                    navHostFragment?.findNavController()?.navigate(R.id.mainFragment)
                    // Handle home icon press
                    val logBundle = Bundle()
                    logBundle.putString(Constant.TO_SCREEN_NAVIGATE_EVENT, "Main Fragment")
                    Analytics.logEvent(Constant.TO_SCREEN_NAVIGATE_EVENT, logBundle)
                    true
                }

                R.id.searchFragment -> {
                    navHostFragment?.findNavController()?.navigate(R.id.searchFragment)
                    // Handle search icon press
                    val logBundle = Bundle()
                    logBundle.putString(Constant.TO_SCREEN_NAVIGATE_EVENT, "Search Fragment")
                    Analytics.logEvent(Constant.TO_SCREEN_NAVIGATE_EVENT, logBundle)
                    true
                }

                R.id.favoriteFragment -> {
                    navHostFragment?.findNavController()?.navigate(R.id.favoriteFragment)
                    // Handle favorite icon press
                    val logBundle = Bundle()
                    logBundle.putString(Constant.TO_SCREEN_NAVIGATE_EVENT, "Favorite Fragment")
                    Analytics.logEvent(Constant.TO_SCREEN_NAVIGATE_EVENT, logBundle)
                    true
                }

                R.id.historyFragment -> {
                    navHostFragment?.findNavController()?.navigate(R.id.historyFragment)
                    // Handle history icon press
                    val logBundle = Bundle()
                    logBundle.putString(Constant.TO_SCREEN_NAVIGATE_EVENT, "History Fragment")
                    Analytics.logEvent(Constant.TO_SCREEN_NAVIGATE_EVENT, logBundle)
                    true
                }

                else -> false
            }
        }
        binding.actionMenu.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.appbar_menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.notification -> {
                        // Handle search icon press
                        val logBundle = Bundle()
                        logBundle.putString(
                            Constant.TO_SCREEN_NAVIGATE_EVENT,
                            "Notification Fragment"
                        )
                        Analytics.logEvent(Constant.TO_SCREEN_NAVIGATE_EVENT, logBundle)

                        viewModel.userData.observe(viewLifecycleOwner) {
                            if (it != null) {

                            }
                        }
                        true
                    }

                    R.id.cart -> {
                        val logBundle = Bundle()
                        logBundle.putString(Constant.TO_SCREEN_NAVIGATE_EVENT, "Cart Fragment")
                        Analytics.logEvent(Constant.TO_SCREEN_NAVIGATE_EVENT, logBundle)
                        findNavController().navigate(HomePageFragmentDirections.actionHomePageFragmentToCartFragment())
                        // Handle settings icon press
                        true
                    }

                    R.id.logout -> {
                        val userLogout = viewModel.logout()
                        if (userLogout) {
                            context?.let { it1 ->
                                MoveeSnackbar.showSnackbarCustom(
                                    it1,
                                    binding.root,
                                    getString(R.string.success_to_logout),
                                    StateSnackbar.COMMON
                                ) {}
                            }
                            val logBundle = Bundle()
                            logBundle.putString("Logout", "Logout")
                            Analytics.logEvent(Constant.TO_SCREEN_NAVIGATE_EVENT, logBundle)
                            showLogoutDialog()
                        } else {
                            context?.let { it1 ->
                                MoveeSnackbar.showSnackbarCustom(
                                    it1,
                                    binding.root,
                                    getString(R.string.failed_to_logout),
                                    StateSnackbar.ERROR
                                ) {}
                            }
                        }
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.logout_title_dialog))
            .setMessage(getString(R.string.logout_confirmation_message))
            .setPositiveButton(getString(R.string.ya)) { _, _ ->
                Toast.makeText(
                    context,
                    getString(R.string.success_to_logout),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(HomePageFragmentDirections.actionHomePageFragmentToLoginFragment())
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}
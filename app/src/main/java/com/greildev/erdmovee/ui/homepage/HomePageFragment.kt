package com.greildev.erdmovee.ui.homepage

import android.app.AlertDialog
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
import com.greildev.core.base.BaseFragment
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.FragmentHomePageBinding
import com.greildev.erdmovee.ui.component.MoveeSnackbar
import com.greildev.erdmovee.ui.component.StateSnackbar
import com.greildev.erdmovee.utils.doubleBackToExit
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
                val photoUri = it.photoUri
                println("Photo Uri: $photoUri")
                binding.tvUsername.text = getString(R.string.hello_home, it.username ?: it.email)
                if (photoUri != null && photoUri.toString().isNotEmpty()) {
                    println("photoUri Inside: $photoUri")
                    binding.ivUserAvatar.setImageURI(photoUri)
                }
            } else {
                findNavController().navigate(HomePageFragmentDirections.actionHomePageFragmentToLoginFragment())
            }
        }
    }

    override fun initListener() {
        binding.chipBalance.setOnClickListener {
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
        }
        binding.bottomNavbar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mainFragment -> {
                    navHostFragment?.findNavController()?.navigate(R.id.mainFragment)
                    // Handle home icon press
                    true
                }

                R.id.searchFragment -> {
                    navHostFragment?.findNavController()?.navigate(R.id.searchFragment)
                    // Handle search icon press
                    true
                }

                R.id.favoriteFragment -> {
                    navHostFragment?.findNavController()?.navigate(R.id.favoriteFragment)
                    // Handle favorite icon press
                    true
                }

                R.id.historyFragment -> {
                    navHostFragment?.findNavController()?.navigate(R.id.historyFragment)
                    // Handle history icon press
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
                        true
                    }

                    R.id.cart -> {

                        // Handle settings icon press
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
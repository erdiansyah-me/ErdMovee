package com.greildev.erdmovee.ui.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.FragmentHomePageBinding

class HomePageFragment : Fragment() {
    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.fragment_container_home) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavbar.setupWithNavController(navController)
        binding.bottomNavbar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mainFragment -> {
                    navHostFragment.findNavController()
                        .navigate(R.id.mainFragment)
                    // Handle home icon press
                    true
                }

                R.id.searchFragment -> {
                    navHostFragment.findNavController()
                        .navigate(R.id.searchFragment)
                    // Handle search icon press
                    true
                }

                R.id.favoriteFragment -> {
                    navHostFragment.findNavController()
                        .navigate(R.id.favoriteFragment)
                    // Handle favorite icon press
                    true
                }

                R.id.historyFragment -> {
                    navHostFragment.findNavController()
                        .navigate(R.id.historyFragment)
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

}
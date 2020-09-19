package com.cybershark.linkmanager.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import androidx.navigation.ui.NavigationUI.navigateUp
import com.cybershark.linkmanager.BuildConfig
import com.cybershark.linkmanager.R
import com.cybershark.linkmanager.databinding.ActivityMainBinding
import com.cybershark.linkmanager.databinding.NavHeaderMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val navController by lazy { findNavController(R.id.nav_host_fragment) }
    private lateinit var binding: ActivityMainBinding
    private lateinit var headerBinding: NavHeaderMainBinding
    private val appBarConfiguration by lazy {
        AppBarConfiguration(navController.graph, binding.drawerLayout)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        headerBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0))
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setUpNavigationDrawer()
    }

    private fun setUpNavigationDrawer() {
        binding.navView.setupWithNavController(navController)
        binding.toolbar.setupWithNavController(navController,appBarConfiguration)
        setVersionCodeInNavBar()
    }

    private fun setVersionCodeInNavBar() {
        headerBinding.tvVersionID.text = ("v " + BuildConfig.VERSION_NAME)
    }
}

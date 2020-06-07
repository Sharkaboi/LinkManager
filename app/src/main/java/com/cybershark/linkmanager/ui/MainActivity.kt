package com.cybershark.linkmanager.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import com.cybershark.linkmanager.BuildConfig
import com.cybershark.linkmanager.R
import com.cybershark.linkmanager.repository.constants.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val navController by lazy { findNavController(R.id.nav_host_fragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpNavigationDrawer()
        setSupportActionBar(toolbar)
    }

    private fun setUpNavigationDrawer() {
        setupDrawerLayoutToSyncState()
        setVersionCodeInNavBar()
        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_bug_report -> openGithubIssues()
                R.id.nav_about -> openAboutDialog()
                R.id.nav_home -> openLinksFragment(it)
            }
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setVersionCodeInNavBar() {
        findViewById<NavigationView>(R.id.nav_view).getHeaderView(0)
            .findViewById<TextView>(R.id.tvVersionID).text =
            ("v " + BuildConfig.VERSION_CODE.toFloat())
    }

    private fun openLinksFragment(it: MenuItem) {
        if (navController.currentDestination?.id != it.itemId)
            navController.navigate(R.id.nav_home)
    }

    private fun openAboutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.about))
            .setMessage(getString(R.string.about_detailed))
            .setPositiveButton(getString(R.string.github)) { _, _ ->
                openGithubPage()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openGithubPage() {
        val githubIntent = Intent(Intent.ACTION_VIEW)
        githubIntent.data = Uri.parse(Constants.githubPageURL)
        startActivity(githubIntent)
    }

    private fun openGithubIssues() {
        val githubIssuesIntent = Intent(Intent.ACTION_VIEW)
        githubIssuesIntent.data = Uri.parse(Constants.githubIssuesURL)
        startActivity(githubIssuesIntent)
    }

    private fun setupDrawerLayoutToSyncState() {
        val toggleOptions = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggleOptions)
        toggleOptions.syncState()
    }

}

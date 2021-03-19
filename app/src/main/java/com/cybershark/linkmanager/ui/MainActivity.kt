package com.cybershark.linkmanager.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.cybershark.linkmanager.BuildConfig
import com.cybershark.linkmanager.R
import com.cybershark.linkmanager.databinding.ActivityMainBinding
import com.cybershark.linkmanager.databinding.NavHeaderMainBinding
import com.cybershark.linkmanager.repository.constants.Constants
import com.cybershark.linkmanager.ui.links.viewmodels.LinksViewModel
import com.cybershark.linkmanager.util.UIState
import com.cybershark.linkmanager.util.makeGone
import com.cybershark.linkmanager.util.makeVisible
import com.cybershark.linkmanager.util.showToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private val linksViewModel by viewModels<LinksViewModel>()
    private lateinit var binding: ActivityMainBinding
    private lateinit var headerBinding: NavHeaderMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        navController = findNavController(R.id.nav_host_fragment)
        setUpNavigationDrawer()
        setVersionCodeInNavBar()
        setObservers()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setUpNavigationDrawer() {
        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        binding.navView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_bug_report -> openGithubIssues()
                    R.id.nav_about -> openAboutDialog()
                    R.id.nav_home -> openLinksFragment(it.itemId)
                    R.id.nav_settings -> openSettingsFragment(it.itemId)
                    R.id.action_share -> openShareOrCopyChooserDialog()
                }
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
            }
    }

    private fun setObservers() {
        linksViewModel.uiState.observe(this) { uiState ->
            binding.contentLoadingScreen.makeGone()

            when (uiState) {
                is UIState.LOADING -> {
                    binding.contentLoadingScreen.makeVisible()
                }
                is UIState.ERROR -> {
                    showToast(uiState.message)
                }
                is UIState.SUCCESS -> {
                    when (uiState.taskId) {
                        "COPY" -> {
                            copyLinks(uiState.message)
                        }
                        "SHARE" -> {
                            shareLinks(uiState.message)
                        }
                        else -> {
                            showToast(uiState.message)
                        }
                    }
                }
                else -> {
                    // do nothing
                }
            }
        }
    }


    private fun openShareOrCopyChooserDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.choose_an_option))
            .setItems(arrayOf("Share List as Text", "Copy List to Clipboard")) { _, which ->
                when (which) {
                    0 -> shareAsText()
                    1 -> copyToClipBoard()
                }
            }
            .show()
    }

    private fun copyToClipBoard() {
        binding.contentLoadingScreen.makeVisible()
        if (linksViewModel.linksList.value.isNullOrEmpty()) {
            showToast("No links added!")
            binding.contentLoadingScreen.makeGone()
        } else {
            linksViewModel.getAllLinksAsString("COPY")
        }
    }

    private fun shareAsText() {
        binding.contentLoadingScreen.makeVisible()

        if (linksViewModel.linksList.value.isNullOrEmpty()) {
            showToast("No links added!")
            binding.contentLoadingScreen.makeGone()
        } else {
            linksViewModel.getAllLinksAsString("SHARE")
        }
    }

    private fun copyLinks(message: String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(ClipData.newPlainText("links", message))
        showToast("Links Copied!")
    }

    private fun shareLinks(message: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
    }

    private fun openSettingsFragment(it: Int) {
        if (navController.currentDestination?.id != it)
            navController.navigate(R.id.nav_settings)
    }

    private fun setVersionCodeInNavBar() {
        headerBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0))
        headerBinding.tvVersionID.text =
            ("v" + BuildConfig.VERSION_NAME)
    }

    private fun openLinksFragment(it: Int) {
        if (navController.currentDestination?.id != it)
            navController.navigate(R.id.nav_home)
    }

    private fun openAboutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.about))
            .setMessage(getString(R.string.about_detailed))
            .setPositiveButton(getString(R.string.github)) { _, _ ->
                openGithubPage()
            }
            .setNegativeButton(getString(android.R.string.ok)) { dialog, _ ->
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
}

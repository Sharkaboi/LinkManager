package com.cybershark.linkmanager.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.print.PrintAttributes
import android.print.PrintAttributes.Margins
import android.print.PrintAttributes.Resolution
import android.print.pdf.PrintedPdfDocument
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.cybershark.linkmanager.R
import com.cybershark.linkmanager.repository.constants.Constants
import com.cybershark.linkmanager.repository.room.entities.LinkEntity
import com.cybershark.linkmanager.ui.links.viewmodels.LinksViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val navController by lazy { findNavController(R.id.nav_host_fragment) }
    private var lastClickTime = 0L
    private var message = "Hey Check me out here : \n"
    private val linksViewModel by lazy { ViewModelProvider(this).get(LinksViewModel::class.java) }
    private lateinit var pdfUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setUpNavigationDrawer()
        fabAddLink.setOnClickListener { openAddLinkDialog() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> openSettingsActivity()
            R.id.action_share -> openShareDialog()
        }
        return true
    }

    private fun openShareDialog() {
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
        contentLoadingScreen.visibility = View.VISIBLE
        val list = linksViewModel.linksList.value
        if (list == null) {
            Toast.makeText(this, "No links added!", Toast.LENGTH_SHORT).show()
            contentLoadingScreen.visibility = View.GONE
        } else {
            lifecycleScope.launch {
                val operation = async(Dispatchers.Default) { filterListAndAddToString(list) }
                operation.await()
                val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.setPrimaryClip(ClipData.newPlainText("links",message))
                contentLoadingScreen.visibility = View.GONE
                Toast.makeText(this@MainActivity,"Link Copied!",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun shareAsText() {
        contentLoadingScreen.visibility = View.VISIBLE
        val list = linksViewModel.linksList.value
        if (list == null) {
            Toast.makeText(this, "No links added!", Toast.LENGTH_SHORT).show()
            contentLoadingScreen.visibility = View.GONE
        } else {
            Toast.makeText(this, "Generating List as text...", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                val operation = async(Dispatchers.Default) { filterListAndAddToString(list) }
                operation.await()
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, message)
                contentLoadingScreen.visibility = View.GONE
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
            }
        }
    }

    private fun filterListAndAddToString(list: List<LinkEntity>) {
        list.forEach {
            message += ("${it.linkName} - ${it.linkURL} \n ")
        }
    }

    private fun openSettingsActivity() {
        if (SystemClock.elapsedRealtime() - lastClickTime > 1000) {
            lastClickTime = SystemClock.elapsedRealtime()
            startActivity(Intent(this, SettingsActivity::class.java))
            setCustomAnimations()
        }
    }

    private fun setCustomAnimations() {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun openAddLinkDialog() {
        val customView = LayoutInflater.from(this)
            .inflate(R.layout.custom_dialog_layout, findViewById(android.R.id.content), false)
        val etLinkName = customView.findViewById<TextView>(R.id.etLinkName)
        val etLinkURL = customView.findViewById<TextView>(R.id.etLinkURL)
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.enter_details))
            .setView(customView)
            .setPositiveButton(getString(R.string.add)) { _, _ ->
                val name = etLinkName.text.toString()
                val url = etLinkURL.text.toString()
                contentLoadingScreen.visibility = View.VISIBLE
                Toast.makeText(this, "Adding to list", Toast.LENGTH_SHORT).show()
                //todo add to room with viewmodel
                contentLoadingScreen.visibility = View.GONE
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setUpNavigationDrawer() {
        setupDrawerLayoutToSyncState()
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

    private fun openLinksFragment(it: MenuItem) {
        if (navController.currentDestination?.id != it.itemId)
            navController.navigate(R.id.nav_home)
    }

    private fun openAboutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.about))
            .setMessage("test")
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

//    private fun shareAsPDF() {
//        contentLoadingScreen.visibility = View.VISIBLE
//        val list = linksViewModel.linksList.value
//        if (list == null) {
//            Toast.makeText(this, "No links added!", Toast.LENGTH_SHORT).show()
//            contentLoadingScreen.visibility = View.GONE
//        } else {
//            Toast.makeText(this, "Generating List as PDF...", Toast.LENGTH_SHORT).show()
//            lifecycleScope.launch {
//                val operation = async(Dispatchers.Default) { filterListAndCreatePDF(list) }
//                operation.await()
//                val shareIntent = Intent(Intent.ACTION_SEND)
//                shareIntent.type = "application/pdf"
//                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check me out here!")
//                shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri)
//                contentLoadingScreen.visibility = View.GONE
//                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
//            }
//        }
//    }
//
//    private fun filterListAndCreatePDF(list: List<LinkEntity>) {
//        val pdfAttributes = PrintAttributes.Builder()
//            .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
//            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
//            .setResolution(Resolution("small-dpi", Context.PRINT_SERVICE, 300, 300))
//            .setMinMargins(Margins(10,10,10,10))
//            .build()
//
//        val linksPDF = PrintedPdfDocument(this, pdfAttributes)
//
//        val page = linksPDF.startPage(1)
//
//        list.forEach { messageForPDF+=("[${it.linkName}](${it.linkURL})\n") }
//
//        page.canvas.drawText(messageForPDF,10f,10f, Paint(Color.BLACK))
//
//        //val content = findViewById<View>(R.id.rvLinks)
//        //content.draw(page.canvas)
//
//        linksPDF.finishPage(page)
//
//        try {
//            val pdfDirPath = File(filesDir, "pdf")
//            pdfDirPath.mkdirs()
//            val file = File(pdfDirPath, "links.pdf")
//            pdfUri = FileProvider.getUriForFile(this, "com.cybershark.fileprovider", file)
//            val fOut = FileOutputStream(file)
//            linksPDF.writeTo(fOut)
//            linksPDF.close()
//            fOut.close()
//        } catch (ex: IOException) {
//            Toast.makeText(this,"Error generating PDF, Try again.", Toast.LENGTH_SHORT).show()
//            ex.printStackTrace()
//        }
//    }

}

package com.cybershark.linkmanager.ui.links.views

import android.app.Activity.RESULT_OK
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cybershark.linkmanager.R
import com.cybershark.linkmanager.repository.room.entities.LinkEntity
import com.cybershark.linkmanager.ui.SettingsActivity
import com.cybershark.linkmanager.ui.links.adapters.LinksAdapter
import com.cybershark.linkmanager.ui.links.viewmodels.LinksViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_links.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class LinksFragment : Fragment(), LinksAdapter.EditButtonListener {

    private val linksViewModel by lazy { ViewModelProvider(this).get(LinksViewModel::class.java) }
    private var lastClickTime = 0L
    private var message = "Hey Check me out here : \n"
    private val REQUEST_CODE = 1
    //private lateinit var pdfUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_links, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("fragment", "onCreateView: $linksViewModel")
        setHasOptionsMenu(true)
        fabAddLink.setOnClickListener { openAddLinkDialog() }
        setupRecyclerView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //RESULT_OK result implies delete all was checked
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            linksViewModel.deleteAllLinks()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> openSettingsActivity()
            R.id.action_share -> openShareDialog()
        }
        return true
    }

    override fun onEditClick(position: Int) {
        val customView = LayoutInflater.from(context)
            .inflate(
                R.layout.custom_dialog_layout,
                activity?.findViewById(android.R.id.content),
                false
            )
        val link = linksViewModel.linksList.value?.get(position)
        val etLinkName = customView.findViewById<TextView>(R.id.etLinkName)
        val etLinkURL = customView.findViewById<TextView>(R.id.etLinkURL)
        etLinkName.text = link?.linkName
        etLinkURL.text = link?.linkURL
        MaterialAlertDialogBuilder(context)
            .setTitle(getString(R.string.edit_details))
            .setView(customView)
            .setPositiveButton(getString(R.string.update)) { _, _ ->
                val name = etLinkName.text.toString()
                val url = etLinkURL.text.toString()
                if (name.isBlank() || url.isBlank()) {
                    Toast.makeText(context, "Enter Something!", Toast.LENGTH_SHORT).show()
                } else {
                    contentLoadingScreen.visibility = View.VISIBLE
                    Toast.makeText(context, "Updating item", Toast.LENGTH_SHORT).show()
                    linksViewModel.updateLink(link)
                    contentLoadingScreen.visibility = View.GONE
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private val linkSwipeDeleteHelper =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val link = linksViewModel.linksList.value?.get(viewHolder.adapterPosition)
                if (link != null) {
                    linksViewModel.deleteLink(link)
                    Snackbar.make(rvLinks, "Deleted Link!", Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo) {
                            linksViewModel.addLink(link.linkName, link.linkURL)
                        }
                        .show()
                } else
                    Toast.makeText(context, "An error occurred!", Toast.LENGTH_SHORT).show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addBackgroundColor(Color.RED)
                    .addActionIcon(R.drawable.ic_delete)
                    .create()
                    .decorate()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

    private fun setupRecyclerView() {
        val adapter = LinksAdapter(requireContext(), this)

        rvLinks.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
        }

        linksViewModel.linksList.observe(viewLifecycleOwner, Observer {
            tvNoLinksAdded.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            adapter.setList(it)
        })

        rvLinks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && fabAddLink.visibility == View.VISIBLE) {
                    fabAddLink.hide()
                } else if (dy < 0 && fabAddLink.visibility != View.VISIBLE) {
                    fabAddLink.show()
                }
            }
        })

        ItemTouchHelper(linkSwipeDeleteHelper).attachToRecyclerView(rvLinks)
    }

    private fun openSettingsActivity() {
        if (SystemClock.elapsedRealtime() - lastClickTime > 1000) {
            lastClickTime = SystemClock.elapsedRealtime()
            startActivityForResult(Intent(context, SettingsActivity::class.java), REQUEST_CODE)
            setCustomAnimations()
        }
    }

    private fun setCustomAnimations() {
        activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun openAddLinkDialog() {
        val customView = LayoutInflater.from(context)
            .inflate(
                R.layout.custom_dialog_layout,
                activity?.findViewById(android.R.id.content),
                false
            )
        val etLinkName = customView.findViewById<TextView>(R.id.etLinkName)
        val etLinkURL = customView.findViewById<TextView>(R.id.etLinkURL)
        MaterialAlertDialogBuilder(context)
            .setTitle(getString(R.string.enter_details))
            .setView(customView)
            .setPositiveButton(getString(R.string.add)) { _, _ ->
                val name = etLinkName.text.toString()
                val url = etLinkURL.text.toString()
                if (name.isBlank() || url.isBlank()) {
                    Toast.makeText(context, "Enter Something!", Toast.LENGTH_SHORT).show()
                } else {
                    contentLoadingScreen.visibility = View.VISIBLE
                    Toast.makeText(context, "Adding to list", Toast.LENGTH_SHORT).show()
                    linksViewModel.addLink(name, url)
                    contentLoadingScreen.visibility = View.GONE
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openShareDialog() {
        MaterialAlertDialogBuilder(context)
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
        if (list.isNullOrEmpty()) {
            Toast.makeText(context, "No links added!", Toast.LENGTH_SHORT).show()
            contentLoadingScreen.visibility = View.GONE
        } else {
            lifecycleScope.launch {
                val operation = async(Dispatchers.Default) { filterListAndAddToString(list) }
                operation.await()
                val clipboardManager =
                    activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.setPrimaryClip(ClipData.newPlainText("links", message))
                contentLoadingScreen.visibility = View.GONE
                Toast.makeText(context, "Links Copied!", Toast.LENGTH_SHORT).show()
                message = "Hey Check me out here : \n"
            }
        }
    }

    private fun shareAsText() {
        contentLoadingScreen.visibility = View.VISIBLE
        val list = linksViewModel.linksList.value
        if (list.isNullOrEmpty()) {
            Toast.makeText(context, "No links added!", Toast.LENGTH_SHORT).show()
            contentLoadingScreen.visibility = View.GONE
        } else {
            Toast.makeText(context, "Generating List as text...", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                val operation = async(Dispatchers.Default) { filterListAndAddToString(list) }
                operation.await()
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, message)
                contentLoadingScreen.visibility = View.GONE
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
                message = "Hey Check me out here : \n"
            }
        }
    }

    private fun filterListAndAddToString(list: List<LinkEntity>) {
        list.forEach {
            message += ("${it.linkName} - ${it.linkURL} \n ")
        }
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

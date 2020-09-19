package com.cybershark.linkmanager.ui.links.views

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.*
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.cybershark.linkmanager.R
import com.cybershark.linkmanager.data.room.entities.LinkEntity
import com.cybershark.linkmanager.databinding.FragmentLinksBinding
import com.cybershark.linkmanager.ui.links.adapters.LinksAdapter
import com.cybershark.linkmanager.ui.links.viewmodels.LinksViewModel
import com.cybershark.linkmanager.util.getListAsFriendlyMessage
import com.cybershark.linkmanager.util.shortToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_links.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LinksFragment : Fragment(), LinksAdapter.CustomListeners {

    private val linksViewModel by viewModels<LinksViewModel>()
    private lateinit var binding: FragmentLinksBinding
    private val navController: NavController by lazy { findNavController() }
    private val clipboardManager by lazy { context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLinksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        fabAddLink.setOnClickListener { openAddLinkDialog() }
        setupRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> openShareChooser()
        }
        return true
    }

    override fun onItemEditClick(pk: Int) {
        openEditLinkFragment(pk)
    }

    private fun openEditLinkFragment(pk: Int) {
        val action = LinksFragmentDirections.actionLinksFragmentToEditLinkDialog(pk = pk)
        navController.navigate(action)
    }

    override fun onItemShareClick(pk: Int) {
        val selectedItem = linksViewModel.linksList.value?.first { linkEntity ->
            linkEntity.pk == pk
        } ?: LinkEntity(linkURL = "", linkName = "")
        val message = "Check me out on ${selectedItem.linkName}!\n${selectedItem.linkURL}"
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
    }

    override fun onOpenLink(linkURL: String) {
        if(Patterns.WEB_URL.matcher(linkURL).matches()){
            val browserIntent = Intent(Intent.ACTION_VIEW)
            browserIntent.data = Uri.parse(linkURL)
            startActivity(browserIntent)
            requireContext().shortToast("Opening Link")
        }else{
            requireContext().shortToast("Link is Invalid and can't be opened!")
        }
    }

    override fun onLinkLongPress(linkURL: String) {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("link", linkURL))
        requireContext().shortToast("Link Copied!")
    }

    override fun onItemDeleteClick(deleteItem: LinkEntity) {
        linksViewModel.deleteLink(deleteItem)
        Snackbar.make(binding.fabAddLink, "Deleted Link!", Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) {
                linksViewModel.addLink(deleteItem.linkName, deleteItem.linkURL)
            }
            .setAnchorView(binding.fabAddLink)
            .show()
    }

    private fun setupRecyclerView() {
        val adapter = LinksAdapter(this)
        binding.contentLinks.rvLinks.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
        }

        linksViewModel.linksList.observe(viewLifecycleOwner, Observer {
            binding.contentLinks.tvNoLinksAdded.isVisible = it.isEmpty()
            adapter.setList(it)
        })
    }

    private fun openAddLinkDialog() {
        navController.navigate(R.id.addLinkDialog)
    }

    private fun openShareChooser() {
        MaterialAlertDialogBuilder(requireContext())
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
        binding.contentLinks.contentLoadingScreen.isVisible = true
        val list = linksViewModel.linksList.value ?: emptyList()
        if (list.isEmpty()) {
            requireContext().shortToast("No links added!")
            binding.contentLinks.contentLoadingScreen.isGone = true
        } else {
            lifecycleScope.launch {
                val message = list.getListAsFriendlyMessage()
                clipboardManager.setPrimaryClip(ClipData.newPlainText("links", message))
                binding.contentLinks.contentLoadingScreen.isGone = true
                requireContext().shortToast("Links Copied!")
            }
        }
    }

    private fun shareAsText() {
        binding.contentLinks.contentLoadingScreen.isVisible = true
        val list = linksViewModel.linksList.value ?: emptyList()
        if (list.isEmpty()) {
            requireContext().shortToast("No links added!")
            binding.contentLinks.contentLoadingScreen.isGone = true
        } else {
            lifecycleScope.launch {
                val message = list.getListAsFriendlyMessage()
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, message)
                binding.contentLinks.contentLoadingScreen.isGone = true
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
            }
        }
    }

}

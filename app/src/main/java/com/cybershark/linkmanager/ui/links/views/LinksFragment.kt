package com.cybershark.linkmanager.ui.links.views

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cybershark.linkmanager.R
import com.cybershark.linkmanager.databinding.FragmentLinksBinding
import com.cybershark.linkmanager.repository.room.entities.LinkEntity
import com.cybershark.linkmanager.ui.links.adapters.LinksAdapter
import com.cybershark.linkmanager.ui.links.viewmodels.LinksViewModel
import com.cybershark.linkmanager.util.action
import com.cybershark.linkmanager.util.observe
import com.cybershark.linkmanager.util.shortSnackBar
import com.cybershark.linkmanager.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LinksFragment : Fragment(), LinksAdapter.CustomListeners {

    private val linksViewModel by activityViewModels<LinksViewModel>()
    private var _binding: FragmentLinksBinding? = null
    private val binding get() = _binding!!
    private val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding.fabAddLink.setOnClickListener { openAddLinkDialog() }
        setupRecyclerView()
    }

    private fun openAddLinkDialog() {
        val action = LinksFragmentDirections.openAddEditDialog(isAddDialog = true, linkId = 0)
        navController.navigate(action)
    }

    private fun setupRecyclerView() {
        val adapter = LinksAdapter(this)
        binding.rvLinks.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            itemAnimator = DefaultItemAnimator()
        }

        observe(linksViewModel.linksList) {
            binding.tvNoLinksAdded.isVisible = it.isEmpty()
            adapter.setList(it)
        }

        binding.rvLinks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && binding.fabAddLink.isVisible) {
                    binding.fabAddLink.hide()
                } else if (dy < 0 && binding.fabAddLink.isGone) {
                    binding.fabAddLink.show()
                }
            }
        })
    }

    override fun onShareLink(linkEntity: LinkEntity) {
        val message = "Check me out on ${linkEntity.linkName}!\n${linkEntity.linkURL}"
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        startActivity(Intent.createChooser(shareIntent, context?.getString(R.string.share)))
    }

    override fun onEditClick(linkId: Int) {
        val action = LinksFragmentDirections.openAddEditDialog(isAddDialog = false, linkId = linkId)
        navController.navigate(action)
    }

    override fun onCopyLink(linkURL: String) {
        val clipBoardManager =
            context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipBoardManager.setPrimaryClip(ClipData.newPlainText("link", linkURL))
        showToast("Copied!")
    }

    override fun onOpenLink(linkURL: String) {
        showToast("Link invalid if it's not opening", length = Toast.LENGTH_LONG)
    }

    override fun onDeleteLink(linkEntity: LinkEntity) {
        linksViewModel.deleteLink(linkEntity.pk)
        binding.rvLinks.shortSnackBar("Deleted Link!") {
            action(getString(R.string.undo)) {
                linksViewModel.addLink(linkEntity.linkName, linkEntity.linkURL)
            }
        }
    }

    companion object {
        const val INSERT_ID = "INSERT"
        const val UPDATE_ID = "UPDATE"
        const val DELETE_ID = "DELETE"
        const val DELETE_ALL_ID = "DELETE_ALL"
    }
}

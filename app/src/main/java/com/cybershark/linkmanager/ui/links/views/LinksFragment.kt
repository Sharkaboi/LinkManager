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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cybershark.linkmanager.R
import com.cybershark.linkmanager.databinding.FragmentLinksBinding
import com.cybershark.linkmanager.repository.room.entities.LinkEntity
import com.cybershark.linkmanager.ui.add_edit_links.ui.AddEditLinkBottomSheet
import com.cybershark.linkmanager.ui.links.adapters.LinksAdapter
import com.cybershark.linkmanager.ui.links.viewmodels.LinksViewModel
import com.cybershark.linkmanager.util.showToast
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LinksFragment : Fragment(), LinksAdapter.CustomListeners {

    private val linksViewModel by viewModels<LinksViewModel>()
    private var _binding: FragmentLinksBinding? = null
    private val binding get() = _binding!!

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
        AddEditLinkBottomSheet.getInstance(AddEditLinkBottomSheet.ADD)
            .show(childFragmentManager, AddEditLinkBottomSheet.TAG)
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

        linksViewModel.linksList.observe(viewLifecycleOwner) {
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
        startActivity(
            Intent.createChooser(
                shareIntent,
                context?.getString(R.string.share)
            )
        )
    }

    override fun onEditClick(linkId: Int) {
        AddEditLinkBottomSheet.getInstance(AddEditLinkBottomSheet.EDIT, linkId)
            .show(childFragmentManager, AddEditLinkBottomSheet.TAG)
    }

    override fun onCopyLink(linkURL: String) {
        val clipBoardManager =
            context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipBoardManager.setPrimaryClip(ClipData.newPlainText("link", linkURL))
        Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
    }

    override fun onOpenLink(linkURL: String) {
        showToast("Link invalid if it's not opening", length = Toast.LENGTH_LONG)
    }

    override fun onDeleteLink(linkEntity: LinkEntity) {
        linksViewModel.deleteLink(linkEntity.pk)
        Snackbar.make(binding.rvLinks, "Deleted Link!", Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) {
                linksViewModel.addLink(linkEntity.linkName, linkEntity.linkURL)
            }
            .show()
    }
}

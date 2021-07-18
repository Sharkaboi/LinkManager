package com.cybershark.linkmanager.ui.add_edit_links.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.cybershark.linkmanager.R
import com.cybershark.linkmanager.databinding.AddEditBottomSheetBinding
import com.cybershark.linkmanager.repository.room.entities.LinkEntity
import com.cybershark.linkmanager.ui.links.viewmodels.LinksViewModel
import com.cybershark.linkmanager.util.showToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditLinkBottomSheet : BottomSheetDialogFragment() {
    private val args by navArgs<AddEditLinkBottomSheetArgs>()
    private val linkId by lazy { args.linkId }
    private val isAddDialog by lazy { args.isAddDialog }
    private var currentLink: LinkEntity? = null
    private val linksViewModel by activityViewModels<LinksViewModel>()
    private var _binding: AddEditBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddEditBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDialog()
    }

    private fun setupDialog() {
        if (isAddDialog) {
            setupAddDialog()
        } else {
            setupEditDialog()
        }
    }

    private fun setupAddDialog() {
        binding.btnAddOrEdit.setOnClickListener {
            val name = binding.etLinkName.text.toString()
            val url = binding.etLinkURL.text.toString()
            if (name.isBlank() || url.isBlank()) {
                showToast("Enter Something!")
            } else {
                linksViewModel.addLink(name, url)
                dismiss()
            }
        }
    }

    private fun setupEditDialog() {
        currentLink = linksViewModel.linksList.value?.first { it.pk == linkId }
        currentLink?.let {
            binding.etLinkName.setText(it.linkName)
            binding.etLinkURL.setText(it.linkURL)
            binding.btnAddOrEdit.setText(R.string.edit)
            binding.btnAddOrEdit.setOnClickListener {
                val name = binding.etLinkName.text.toString()
                val url = binding.etLinkURL.text.toString()
                if (name.isBlank() || url.isBlank()) {
                    showToast("Enter Something!")
                } else {
                    linksViewModel.updateLink(linkId, name, url)
                    dismiss()
                }
            }
        } ?: run {
            dismiss()
        }

    }
}
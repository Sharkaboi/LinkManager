package com.cybershark.linkmanager.ui.links.edit

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.cybershark.linkmanager.data.room.entities.LinkEntity
import com.cybershark.linkmanager.databinding.FragmentEditDialogBinding
import com.cybershark.linkmanager.ui.links.viewmodels.LinksViewModel
import com.cybershark.linkmanager.util.shortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditLinkDialog : DialogFragment() {

    private var _binding: FragmentEditDialogBinding? = null
    private val binding get() = _binding!!
    private val linksViewModel by viewModels<LinksViewModel>()
    private val args: EditLinkDialogArgs by navArgs()
    private val pk: Int by lazy { args.pk }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentEditDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()
        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupData() {
        val currentItem = linksViewModel.getLinkByPK(pk)
        if (currentItem != null) {
            binding.etLinkName.setText(currentItem.linkName)
            binding.etLinkURL.setText(currentItem.linkURL)
        }
    }

    private fun setupListeners() {
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnConfirm.setOnClickListener { editLink() }
    }

    private fun editLink() {
        val name = binding.etLinkName.text.toString()
        val url = binding.etLinkURL.text.toString()
        if (name.isBlank() || url.isBlank()) {
            requireContext().shortToast("Enter Something!")
        } else if (!Patterns.WEB_URL.matcher(url).matches()) {
            requireContext().shortToast("Link is Invalid!")
        } else {
            requireContext().shortToast("Updating item")
            linksViewModel.updateLink(LinkEntity(pk, name, url))
            dismiss()
        }
    }

}
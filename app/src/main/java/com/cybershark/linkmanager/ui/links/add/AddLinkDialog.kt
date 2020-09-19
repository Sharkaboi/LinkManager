package com.cybershark.linkmanager.ui.links.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.cybershark.linkmanager.databinding.FragmentAddDialogBinding
import com.cybershark.linkmanager.ui.links.viewmodels.LinksViewModel
import com.cybershark.linkmanager.util.shortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddLinkDialog : DialogFragment() {

    private lateinit var binding: FragmentAddDialogBinding
    private val linksViewModel by viewModels<LinksViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnConfirm.setOnClickListener { addLink() }
    }

    private fun addLink() {
        val name = binding.etLinkName.text.toString()
        val url = binding.etLinkURL.text.toString()
        if (name.isBlank() || url.isBlank()) {
            requireContext().shortToast("Enter Something!")
        } else {
            requireContext().shortToast("Adding item")
            linksViewModel.addLink(name, url)
            dismiss()
        }
    }
}
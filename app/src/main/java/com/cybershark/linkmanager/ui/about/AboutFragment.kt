package com.cybershark.linkmanager.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.cybershark.linkmanager.data.constants.Constants
import com.cybershark.linkmanager.databinding.FragmentAboutBinding

class AboutFragment : DialogFragment() {

    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        binding.btnGithub.setOnClickListener { openGithubPage() }
        binding.btnOk.setOnClickListener { dismiss() }
    }

    private fun openGithubPage() {
        val githubIntent = Intent(Intent.ACTION_VIEW)
        githubIntent.data = Uri.parse(Constants.githubPageURL)
        startActivity(githubIntent)
    }
}
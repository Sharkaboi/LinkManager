package com.cybershark.linkmanager.ui.links.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cybershark.linkmanager.R
import com.cybershark.linkmanager.ui.links.adapters.LinksAdapter
import com.cybershark.linkmanager.ui.links.viewmodels.LinksViewModel
import kotlinx.android.synthetic.main.fragment_links.*

class LinksFragment : Fragment() {

    private val linksViewModel by lazy { ViewModelProvider(this).get(LinksViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_links, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = LinksAdapter(requireContext())

        rvLinks.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            itemAnimator = DefaultItemAnimator()
        }

        linksViewModel.linksList.observe(viewLifecycleOwner, Observer {
            tvNoLinksAdded.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            adapter.setList(it)
        })
    }
}

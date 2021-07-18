package com.cybershark.linkmanager.ui.links.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.cybershark.linkmanager.R
import com.cybershark.linkmanager.databinding.LinkItemBinding
import com.cybershark.linkmanager.repository.room.entities.LinkEntity
import com.cybershark.linkmanager.ui.links.util.LinksDiffUtilItemCallback

class LinksAdapter(
    private val customListeners: CustomListeners
) : RecyclerView.Adapter<LinksAdapter.LinksViewHolder>() {

    class LinksViewHolder(
        private val binding: LinkItemBinding,
        private val customListeners: CustomListeners
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(linkEntity: LinkEntity) {
            binding.tvLinkName.text = linkEntity.linkName
            binding.tvLinkURL.text = linkEntity.linkURL

            binding.ibOpenOptions.setOnClickListener {
                val menu = PopupMenu(binding.ibOpenOptions.context, binding.ibOpenOptions)
                menu.menuInflater.inflate(R.menu.link_item_menu, menu.menu)
                menu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.link_edit -> customListeners.onEditClick(linkEntity.pk)
                        R.id.link_share -> customListeners.onShareLink(linkEntity)
                        R.id.link_delete -> customListeners.onDeleteLink(linkEntity)
                    }
                    true
                }
                menu.show()
            }

            binding.tvLinkURL.setOnClickListener {
                customListeners.onOpenLink(linkEntity.linkURL)
            }
            binding.tvLinkURL.setOnLongClickListener {
                customListeners.onCopyLink(linkEntity.linkURL)
                true
            }
        }
    }

    private lateinit var binding: LinkItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinksViewHolder {
        binding = LinkItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LinksViewHolder(
            binding,
            customListeners
        )
    }

    override fun getItemCount(): Int = listDiffer.currentList.size

    override fun onBindViewHolder(holder: LinksViewHolder, position: Int) {
        holder.bind(listDiffer.currentList[position])
    }

    private val listDiffer = AsyncListDiffer(this, LinksDiffUtilItemCallback)

    fun setList(itemsList: List<LinkEntity>) {
        listDiffer.submitList(itemsList)
    }

    interface CustomListeners {
        fun onEditClick(linkId: Int)
        fun onCopyLink(linkURL: String)
        fun onShareLink(linkEntity: LinkEntity)
        fun onOpenLink(linkURL: String)
        fun onDeleteLink(linkEntity: LinkEntity)
    }
}
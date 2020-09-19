package com.cybershark.linkmanager.ui.links.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.cybershark.linkmanager.R
import com.cybershark.linkmanager.data.room.entities.LinkEntity
import com.cybershark.linkmanager.databinding.LinkItemBinding
import com.cybershark.linkmanager.ui.links.util.LinksDiffUtilItemCallback


class LinksAdapter(
    private val editButtonListener: CustomListeners
) : RecyclerView.Adapter<LinksAdapter.LinksViewHolder>() {

    private lateinit var binding: LinkItemBinding
    private val listDiffer = AsyncListDiffer(this, LinksDiffUtilItemCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinksViewHolder {
        binding = LinkItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LinksViewHolder(binding, editButtonListener)
    }

    override fun getItemCount(): Int {
        return listDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: LinksViewHolder, position: Int) {
        holder.bind(listDiffer.currentList[position])
    }

    fun setList(itemsList: List<LinkEntity>) {
        listDiffer.submitList(itemsList)
    }

    class LinksViewHolder(private val binding: LinkItemBinding, private val customListeners: CustomListeners) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LinkEntity) {
            binding.tvLinkName.text = item.linkName
            binding.tvLinkURL.text = item.linkURL
            binding.ivFavicon.load(item.linkURL){
                error(R.drawable.ic_link)
            }

            // Listeners
            binding.ibOpenOptions.setOnClickListener {
                val popup = PopupMenu(it.context, it)
                popup.menuInflater.inflate(R.menu.popup_menu_options, popup.menu)
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.edit_item -> customListeners.onItemEditClick(item.pk)
                        R.id.delete_item -> customListeners.onItemDeleteClick(item)
                        R.id.share_item -> customListeners.onItemShareClick(item.pk)
                    }
                    return@setOnMenuItemClickListener true
                }
                popup.show()
            }

            binding.tvLinkURL.setOnClickListener {
                customListeners.onOpenLink(item.linkURL)
            }
            binding.tvLinkURL.setOnLongClickListener {
                customListeners.onLinkLongPress(item.linkURL)
                true
            }
        }
    }

    interface CustomListeners {
        fun onItemEditClick(pk: Int)
        fun onItemShareClick(pk: Int)
        fun onItemDeleteClick(deleteItem: LinkEntity)
        fun onOpenLink(linkURL: String)
        fun onLinkLongPress(linkURL: String)
    }
}
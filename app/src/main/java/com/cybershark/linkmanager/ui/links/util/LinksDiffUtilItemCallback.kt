package com.cybershark.linkmanager.ui.links.util

import androidx.recyclerview.widget.DiffUtil
import com.cybershark.linkmanager.repository.room.entities.LinkEntity

object LinksDiffUtilItemCallback : DiffUtil.ItemCallback<LinkEntity>() {
    override fun areItemsTheSame(oldItem: LinkEntity, newItem: LinkEntity): Boolean {
        return oldItem.pk == newItem.pk
    }

    override fun areContentsTheSame(oldItem: LinkEntity, newItem: LinkEntity): Boolean {
        return oldItem == newItem
    }
}

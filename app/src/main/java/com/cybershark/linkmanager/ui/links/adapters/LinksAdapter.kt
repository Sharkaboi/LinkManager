package com.cybershark.linkmanager.ui.links.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cybershark.linkmanager.R
import com.cybershark.linkmanager.repository.room.entities.LinkEntity
import com.cybershark.linkmanager.ui.links.util.LinksDiffUtilItemCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LinksAdapter(private val context: Context) : RecyclerView.Adapter<LinksAdapter.LinksViewHolder>() {

    class LinksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvLinkName = itemView.findViewById<TextView>(R.id.tvLinkName)!!
        private val tvLinkURL = itemView.findViewById<TextView>(R.id.tvLinkURL)!!
        private val ivFavIcon = itemView.findViewById<ImageView>(R.id.ivFavIcon)!!
        private val ibShareURL = itemView.findViewById<ImageButton>(R.id.ibShareURL)!!
        private val ibEditLink = itemView.findViewById<ImageButton>(R.id.ibEditLink)!!

        fun bind(linkEntity: LinkEntity,context: Context){
            tvLinkName.text = linkEntity.linkName
            tvLinkURL.text = linkEntity.linkURL
            Glide.with(ivFavIcon.context).asBitmap().load(linkEntity.linkFavIconURL).error(R.drawable.ic_link).into(ivFavIcon)
            ibShareURL.setOnClickListener {
                val message = "Check me out on ${linkEntity.linkName}!\n${linkEntity.linkURL}"
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT,message)
                context.startActivity(Intent.createChooser(shareIntent,context.getString(R.string.share)))
            }
            ibEditLink.setOnClickListener {
                //todo open dialog

            }
            tvLinkURL.setOnClickListener {
                try {
                    val browserIntent = Intent(Intent.ACTION_VIEW)
                    browserIntent.data = Uri.parse(linkEntity.linkURL)
                    context.startActivity(browserIntent)
                    Toast.makeText(tvLinkURL.context,"Opening Link",Toast.LENGTH_SHORT).show()
                }catch (ex:Exception){
                    Toast.makeText(tvLinkURL.context,"Invalid Link",Toast.LENGTH_SHORT).show()
                    ex.printStackTrace()
                }
            }
            tvLinkURL.setOnLongClickListener {
                val clipBoardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipBoardManager.setPrimaryClip(ClipData.newPlainText("link",linkEntity.linkURL))
                Toast.makeText(tvLinkURL.context,"Link Copied!",Toast.LENGTH_SHORT).show()
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinksViewHolder {
        return LinksViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.link_item, parent, false))
    }

    override fun getItemCount(): Int {
        return listDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: LinksViewHolder, position: Int) {
        holder.bind(listDiffer.currentList[position],context)
    }

    private val listDiffer = AsyncListDiffer(this, LinksDiffUtilItemCallback)

    fun setList(itemsList: List<LinkEntity>) {
        listDiffer.submitList(itemsList)
    }
}
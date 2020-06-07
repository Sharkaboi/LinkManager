package com.cybershark.linkmanager.ui.links.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.cybershark.linkmanager.R
import com.cybershark.linkmanager.repository.room.entities.LinkEntity
import com.cybershark.linkmanager.ui.links.util.LinksDiffUtilItemCallback

class LinksAdapter(
    private val context: Context,
    private val editButtonListener: EditButtonListener
) : RecyclerView.Adapter<LinksAdapter.LinksViewHolder>() {

    class LinksViewHolder(itemView: View, private val editButtonListener: EditButtonListener) :
        RecyclerView.ViewHolder(itemView) {
        private val tvLinkName = itemView.findViewById<TextView>(R.id.tvLinkName)!!
        private val tvLinkURL = itemView.findViewById<TextView>(R.id.tvLinkURL)!!
        private val ibShareURL = itemView.findViewById<Button>(R.id.ibShareURL)!!
        private val ibEditLink = itemView.findViewById<Button>(R.id.ibEditLink)!!
        private val optionsMenu = itemView.findViewById<LinearLayout>(R.id.optionsMenu)!!
        private val ibOpenOptions = itemView.findViewById<ImageButton>(R.id.ibOpenOptions)!!

        fun bind(linkEntity: LinkEntity, context: Context) {
            tvLinkName.text = linkEntity.linkName
            tvLinkURL.text = linkEntity.linkURL
            optionsMenu.visibility = View.GONE
            Glide.with(ibOpenOptions.context)
                .load(R.drawable.ic_arrow_down)
                .into(ibOpenOptions)

            ibOpenOptions.setOnClickListener {
                when (optionsMenu.visibility) {
                    View.VISIBLE -> {
                        TransitionManager.beginDelayedTransition(optionsMenu, AutoTransition())
                        optionsMenu.visibility = View.GONE
                        Glide.with(ibOpenOptions.context)
                            .load(R.drawable.ic_arrow_down)
                            .into(ibOpenOptions)
                    }
                    View.GONE -> {
                        TransitionManager.beginDelayedTransition(optionsMenu, AutoTransition())
                        optionsMenu.visibility = View.VISIBLE
                        Glide.with(ibOpenOptions.context)
                            .load(R.drawable.ic_arrow_up)
                            .into(ibOpenOptions)
                    }
                }
            }
            ibShareURL.setOnClickListener {
                val message = "Check me out on ${linkEntity.linkName}!\n${linkEntity.linkURL}"
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, message)
                context.startActivity(
                    Intent.createChooser(
                        shareIntent,
                        context.getString(R.string.share)
                    )
                )
            }
            ibEditLink.setOnClickListener {
                editButtonListener.onEditClick(adapterPosition)
            }

            tvLinkURL.setOnClickListener {
                try {
                    val browserIntent = Intent(Intent.ACTION_VIEW)
                    browserIntent.data = Uri.parse(linkEntity.linkURL)
                    context.startActivity(browserIntent)
                    Toast.makeText(tvLinkURL.context, "Opening Link", Toast.LENGTH_SHORT).show()
                } catch (ex: Exception) {
                    Toast.makeText(tvLinkURL.context, "Invalid Link", Toast.LENGTH_SHORT).show()
                    ex.printStackTrace()
                }
            }
            tvLinkURL.setOnLongClickListener {
                val clipBoardManager =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipBoardManager.setPrimaryClip(ClipData.newPlainText("link", linkEntity.linkURL))
                Toast.makeText(tvLinkURL.context, "Link Copied!", Toast.LENGTH_SHORT).show()
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinksViewHolder {
        return LinksViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.link_item, parent, false),
            editButtonListener
        )
    }

    override fun getItemCount(): Int {
        return listDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: LinksViewHolder, position: Int) {
        holder.bind(listDiffer.currentList[position], context)
    }

    private val listDiffer = AsyncListDiffer(this, LinksDiffUtilItemCallback)

    fun setList(itemsList: List<LinkEntity>) {
        listDiffer.submitList(itemsList)
    }

    interface EditButtonListener {
        fun onEditClick(position: Int)
    }
}
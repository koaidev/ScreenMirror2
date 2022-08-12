package com.bangbangcoding.screenmirror.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.ItemMediaBinding
import com.bangbangcoding.screenmirror.databinding.TimeLayoutBinding
import com.bangbangcoding.screenmirror.db.model.MediaItem
import com.bangbangcoding.screenmirror.db.model.group.DateItem
import com.bangbangcoding.screenmirror.db.model.group.GeneralItem
import com.bangbangcoding.screenmirror.db.model.group.ListItem
import com.bangbangcoding.screenmirror.db.model.group.ListItem.Companion.TYPE_DATE
import com.bangbangcoding.screenmirror.db.model.group.ListItem.Companion.TYPE_GENERAL


class MediaAdapter(
    val medias: ArrayList<ListItem> = arrayListOf(),
    private val activity: Activity
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class MediaViewHolder(
        val binding: ItemMediaBinding,
        context: Context,
    ) : RecyclerView.ViewHolder(binding.root)

    class DateTimeViewHolder(val binding: TimeLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType != TYPE_DATE) {
            MediaViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_media,
                    parent,
                    false
                ), parent.context
            )
        } else {
            DateTimeViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.time_layout,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return medias[position].type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == TYPE_GENERAL) {
            holder as MediaViewHolder
            holder.binding.media = medias[position] as GeneralItem
            holder.binding.cardMedia.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                val type =
                    if ((medias[position] as GeneralItem).isVideo || (((medias[position] as GeneralItem).duration != null) && ((medias[position] as GeneralItem).duration!! > 0))) {
                        "video/*"
                    } else {
                        "image/*"
                    }
                intent.setDataAndType((medias[position] as GeneralItem).uri, type)
                activity.startActivity(intent)
            }
            println("Uri from adapter: ${(medias[position] as GeneralItem).uri}")
            holder.binding.executePendingBindings()
        } else if (holder.itemViewType == TYPE_DATE) {
            holder as DateTimeViewHolder
            holder.binding.date = (medias[position] as DateItem).dateAdd
            holder.binding.executePendingBindings()
        }
    }

    override fun getItemCount(): Int = medias.size
}

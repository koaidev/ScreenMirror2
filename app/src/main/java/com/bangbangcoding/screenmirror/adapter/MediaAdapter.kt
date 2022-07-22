package com.bangbangcoding.screenmirror.adapter

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.ItemMediaBinding
import com.bangbangcoding.screenmirror.model.MediaItem

class MediaAdapter(val medias: ArrayList<MediaItem> = arrayListOf(), private val activity: Activity) :
    RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    class MediaViewHolder(val binding: ItemMediaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        return MediaViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_media,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.binding.media = medias[position]
        holder.binding.cardMedia.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            val type =
                if (medias[position].isVideo || ((medias[position].duration != null) && (medias[position].duration!! > 0)))
                   {
                       "video/*"
                    }else{
                    "image/*"
                }
            intent.setDataAndType(medias[position].uri, type)
            activity.startActivity(intent)
        }
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = medias.size
}
package com.bangbangcoding.screenmirror.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.ItemShortcutBinding
import com.bangbangcoding.screenmirror.db.model.entity.Shortcut
import com.bangbangcoding.screenmirror.db.viewmodel.WebViewModel

class ShortcutAdapter(
    val webViewModel: WebViewModel,
    val lifecycleOwner: LifecycleOwner,
    val callBack: CallBack
) :
    ListAdapter<Shortcut, ShortcutAdapter.ShortcutVH>(ShortcutComparator()) {
    companion object {

    }

    class ShortcutVH(val binding: ItemShortcutBinding) : RecyclerView.ViewHolder(binding.root)

    class ShortcutComparator : DiffUtil.ItemCallback<Shortcut>() {
        override fun areItemsTheSame(oldItem: Shortcut, newItem: Shortcut): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Shortcut, newItem: Shortcut): Boolean {
            return oldItem.name == newItem.name
        }
    }

    override fun onBindViewHolder(holder: ShortcutVH, position: Int) {
        val current = getItem(position)
        holder.binding.shortcut = current
        webViewModel.visibility.observe(lifecycleOwner) {
            if (position == 0)
                holder.binding.remove = false
            else holder.binding.remove = it
        }
        holder.binding.btnRemove.setOnClickListener {
            callBack.deleteShortcut(current)
        }
        holder.binding.imageView10.setOnClickListener {
            if (position == 0) {
                callBack.addNewShortcut()
            } else {
                callBack.onClickShortcut(current)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShortcutVH {
        return ShortcutVH(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_shortcut,
                parent,
                false
            )
        )
    }
}

interface CallBack {
    fun deleteShortcut(shortcut: Shortcut)
    fun onClickShortcut(shortcut: Shortcut)
    fun addNewShortcut()
}
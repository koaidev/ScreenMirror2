package com.bangbangcoding.screenmirror.adapter

import android.view.LayoutInflater
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

class ShortcutAdapter :
    ListAdapter<Shortcut, ShortcutAdapter.ShortcutVH>(ShortcutComparator()) {
    companion object {
        lateinit var callback: CallBack
        lateinit var webViewModel: WebViewModel
        lateinit var lifecycleOwner: LifecycleOwner
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
            holder.binding.remove = it
        }
        holder.binding.btnRemove.setOnClickListener {
            callback.deleteShortcut(current)
        }
        holder.binding.imageView10.setOnClickListener {
            if (position == 0) {
                callback.addNewShortcut()
            } else {
                callback.onClickShortcut(current)
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
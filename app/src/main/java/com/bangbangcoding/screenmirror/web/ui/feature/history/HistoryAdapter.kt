package com.bangbangcoding.screenmirror.web.ui.feature.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangbangcoding.screenmirror.web.data.local.HistoryEntry
import com.bangbangcoding.screenmirror.databinding.ItemHistoryListBinding

class HistoryAdapter(
    private val onItemLongClickListener: (HistoryEntry, View) -> Unit,
    private val onItemClickListener: (HistoryEntry) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var histories: List<HistoryEntry> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return HistoryViewHolder(binding, onItemLongClickListener, onItemClickListener)
    }

    override fun getItemCount() = histories.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) =
        holder.bind(histories[position])

    fun itemAt(position: Int): HistoryEntry = histories[position]

    fun deleteItem(item: HistoryEntry) {
        val newList = histories - item
        updateItems(newList)
    }

    fun updateItems(newList: List<HistoryEntry>) {
        val oldList = histories
        histories = newList

        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = oldList.size

            override fun getNewListSize() = histories.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                oldList[oldItemPosition] == histories[newItemPosition]

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                oldList[oldItemPosition] == histories[newItemPosition]
        })

        diffResult.dispatchUpdatesTo(this)
    }

    inner class HistoryViewHolder(
        private val binding: ItemHistoryListBinding,
        private val onItemLongClickListener: (HistoryEntry, View) -> Unit,
        private val onItemClickListener: (HistoryEntry) -> Unit
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        var txtTitle: TextView = binding.tvHistory

        init {
            binding.root.setOnClickListener(this)
            binding.ivAction.setOnClickListener {
                val index = absoluteAdapterPosition
                onItemLongClickListener(histories[index], it)
            }
        }

        override fun onClick(v: View) {
            val index = absoluteAdapterPosition
            if (index.toLong() != RecyclerView.NO_ID) {
                onItemClickListener(histories[index])
            }
        }


        fun bind(viewModel: HistoryEntry) {
            txtTitle.text = viewModel.title
            binding.tvUrl.text = viewModel.url
        }
    }
}

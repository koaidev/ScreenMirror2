package com.bangbangcoding.screenmirror.web.ui.progress

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangbangcoding.screenmirror.databinding.ItemTabBinding
import com.bangbangcoding.screenmirror.web.ui.feature.TabViewState

class TabsAdapter(
    private var tabInfoList: List<TabViewState>,
    private val clickItemTab: (Int) -> Unit,
    private val removeTabFunc: (Int) -> Unit,
) : RecyclerView.Adapter<TabsAdapter.TabViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        val binding = ItemTabBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return TabViewHolder(binding, clickItemTab, removeTabFunc)
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        holder.bind(tab = tabInfoList[position])
    }

    fun getTabsList(): List<TabViewState> {
        return tabInfoList
    }

    fun showTabs(tabs: List<TabViewState>) {
        val oldList = tabInfoList
        tabInfoList = ArrayList(tabs)

        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = oldList.size

            override fun getNewListSize() = tabInfoList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                oldList[oldItemPosition] == tabInfoList[newItemPosition]

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldTab = oldList[oldItemPosition]
                val newTab = tabInfoList[newItemPosition]

                return (oldTab.title == newTab.title
                        && oldTab.favicon == newTab.favicon
                        && oldTab.isForegroundTab == newTab.isForegroundTab
                        && oldTab == newTab)
            }
        })

        result.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return tabInfoList.size
    }

    fun remove(pos: Int) {
        (tabInfoList as ArrayList).removeAt(pos)
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean {
        return tabInfoList.isEmpty()
    }

    inner class TabViewHolder(
        private val binding: ItemTabBinding,
        private val clickItemTab: (Int) -> Unit,
        private val removeTabFunc: (Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                clickItemTab.invoke(bindingAdapterPosition)
            }

            binding.ivClose.setOnClickListener {
                removeTabFunc.invoke(bindingAdapterPosition)
            }
        }

        fun bind(tab: TabViewState) {
            binding.tvTitle.text = tab.title
            binding.viewSelected.isSelected = tab.isSelect
            binding.imgIcon.setImageBitmap(tab.screenShot)
//            Glide.with(binding.imgIcon.context).load(videoDownloadModel.thumbnail)
//                .into(binding.imgIcon)
//            binding.root.setOnClickListener {
//                topPagesListener.onItemClicked(pageInfo)
//            }
        }
    }
}
package com.bangbangcoding.screenmirror.web.ui.feature.mange_tab

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bangbangcoding.screenmirror.web.data.local.room.entity.PageInfo
import com.bangbangcoding.screenmirror.databinding.ItemTopPageBinding

class ManagementTabAdapter(
    private var pageInfos: List<PageInfo>,
    private val topPagesListener: TopPagesListener
) : RecyclerView.Adapter<ManagementTabAdapter.TopPageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopPageViewHolder {
        val binding = ItemTopPageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return TopPageViewHolder(binding, topPagesListener)
    }

    override fun getItemCount() = pageInfos.size

    override fun onBindViewHolder(holder: TopPageViewHolder, position: Int) =
        holder.bind(pageInfos[position])

    class TopPageViewHolder(
        private val binding: ItemTopPageBinding,
        private val topPagesListener: TopPagesListener
    ) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(pageInfo: PageInfo) {
            binding.tvName.text = pageInfo.name
            binding.tvLink.text = pageInfo.link
            Glide.with(binding.imgIcon.context).load(pageInfo.icon).into(binding.imgIcon)
            binding.root.setOnClickListener {
                topPagesListener.onItemClicked(pageInfo)
            }
        }
    }

    fun setData(pageInfos: List<PageInfo>) {
        this.pageInfos = pageInfos
        notifyDataSetChanged()
    }

    interface TopPagesListener {
        fun onItemClicked(pageInfo: PageInfo)
    }
}

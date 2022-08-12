package com.bangbangcoding.screenmirror.web.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bangbangcoding.screenmirror.web.data.local.model.DomainAllow
import com.bangbangcoding.screenmirror.databinding.ItemTopPageBinding

class TopPageAdapter(
    private var pageInfos: List<DomainAllow>,
    private val topPagesListener: TopPagesListener,
    private var canEdit: Boolean = false
) : RecyclerView.Adapter<TopPageAdapter.TopPageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopPageViewHolder {
        val binding = ItemTopPageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return TopPageViewHolder(binding, topPagesListener, canEdit)
    }

    override fun getItemCount() = pageInfos.size

    override fun onBindViewHolder(holder: TopPageViewHolder, position: Int) =
        holder.bind(pageInfos[position])

    class TopPageViewHolder(
        private val binding: ItemTopPageBinding,
        private val topPagesListener: TopPagesListener,
        private var canEdit: Boolean = false

    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(domain: DomainAllow) {
            binding.tvLink.text = domain.domain
            binding.btnRemove.visibility = if (domain.canEdit == true && canEdit) {
                View.VISIBLE
            } else {
                View.GONE
            }
            domain.payload?.let { payload ->
                binding.tvName.text = payload.name

                if (payload.icon.isNotEmpty()) {
                    Glide.with(binding.imgIcon.context).load(payload.icon).into(binding.imgIcon)
                } else {
                    Glide.with(binding.imgIcon.context).load(payload.iconRes).into(binding.imgIcon)
                }
                binding.root.setOnClickListener {
                    topPagesListener.onItemClicked(domain)
                }
                binding.btnRemove.setOnClickListener { topPagesListener.onItemDelete(domain) }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(pageInfos: List<DomainAllow>) {
        this.pageInfos = pageInfos
        notifyDataSetChanged()
    }

    interface TopPagesListener {
        fun onItemClicked(pageInfo: DomainAllow)
        fun onItemDelete(pageInfo: DomainAllow)
    }
}

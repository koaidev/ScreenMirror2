package com.bangbangcoding.screenmirror.web.ui.feature.bookmarks

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangbangcoding.screenmirror.web.data.local.Bookmark
import com.bangbangcoding.screenmirror.databinding.BookmarkListItemBinding
import com.bangbangcoding.screenmirror.web.favicon.FaviconModel
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.ConcurrentHashMap

class BookmarksAdapter(
    private val faviconModel: FaviconModel,
    private val folderBitmap: Bitmap,
    private val webPageBitmap: Bitmap,
    private val networkScheduler: Scheduler,
    private val mainScheduler: Scheduler,
    private val onItemLongClickListener: (Bookmark) -> Boolean,
    private val onItemClickListener: (Bookmark) -> Unit
) : RecyclerView.Adapter<BookmarksAdapter.BookmarkViewHolder>() {

    private var bookmarks: List<BookmarkViewModel> = listOf()
    private val faviconFetchSubscriptions = ConcurrentHashMap<String, Disposable>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val binding = BookmarkListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return BookmarkViewHolder(binding, onItemLongClickListener, onItemClickListener)
    }

    fun cleanupSubscriptions() {
        for (subscription in faviconFetchSubscriptions.values) {
            subscription.dispose()
        }
        faviconFetchSubscriptions.clear()
    }

    override fun getItemCount() = bookmarks.size

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) =
        holder.bind(bookmarks[position])

    fun itemAt(position: Int): BookmarkViewModel = bookmarks[position]

    fun deleteItem(item: BookmarkViewModel) {
        val newList = bookmarks - item
        updateItems(newList)
    }

    fun updateItems(newList: List<BookmarkViewModel>) {
        val oldList = bookmarks
        bookmarks = newList

        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = oldList.size

            override fun getNewListSize() = bookmarks.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                oldList[oldItemPosition] == bookmarks[newItemPosition]

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                oldList[oldItemPosition] == bookmarks[newItemPosition]
        })

        diffResult.dispatchUpdatesTo(this)
    }

    inner class BookmarkViewHolder(
        private val binding: BookmarkListItemBinding,
        private val onItemLongClickListener: (Bookmark) -> Boolean,
        private val onItemClickListener: (Bookmark) -> Unit
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

        var txtTitle: TextView = binding.textBookmark
        var favicon: ImageView = binding.faviconBookmark

        init {
            binding.root.setOnLongClickListener(this)
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val index = absoluteAdapterPosition
            if (index.toLong() != RecyclerView.NO_ID) {
                onItemClickListener(bookmarks[index].bookmark)
            }
        }

        override fun onLongClick(v: View): Boolean {
            val index = absoluteAdapterPosition
            return index != RecyclerView.NO_POSITION && onItemLongClickListener(bookmarks[index].bookmark)
        }

        fun bind(viewModel: BookmarkViewModel) {
            itemView.jumpDrawablesToCurrentState()

            txtTitle.text = viewModel.bookmark.title

            val bitmap = viewModel.icon ?: when (viewModel.bookmark) {
                is Bookmark.Folder -> folderBitmap
                is Bookmark.Entry -> webPageBitmap.also {
                    favicon.tag = viewModel.bookmark.url.hashCode()

                    val url = viewModel.bookmark.url

                    faviconFetchSubscriptions[url]?.dispose()
                    faviconFetchSubscriptions[url] =
                        faviconModel.faviconForUrl(url, viewModel.bookmark.title)
                            .subscribeOn(networkScheduler)
                            .observeOn(mainScheduler)
                            .subscribeBy(
                                onSuccess = { bitmap ->
                                    viewModel.icon = bitmap
                                    if (favicon.tag == url.hashCode()) {
                                        favicon.setImageBitmap(bitmap)
                                    }
                                }
                            )
                }
            }

            favicon.setImageBitmap(bitmap)
        }
    }
}

class BookmarkViewModel(
    val bookmark: Bookmark,
    var icon: Bitmap? = null
) {
    override fun equals(other: Any?): Boolean {
        return if (other is BookmarkViewModel) {
            bookmark == other.bookmark
        } else {
            super.equals(other)
        }
    }

    override fun hashCode(): Int = bookmark.hashCode()

    override fun toString(): String = "BookmarkViewModel(bookmark=$bookmark)"

}

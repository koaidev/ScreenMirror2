package com.bangbangcoding.screenmirror.web.list

import com.bangbangcoding.screenmirror.R
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangbangcoding.screenmirror.web.utils.ext.inflater

/**
 * A simple [RecyclerView.Adapter] that displays a [List] of [String].
 */
class RecyclerViewStringAdapter<T>(
    private val listItems: List<T>,
    private val convertToString: T.() -> String
) : RecyclerView.Adapter<SimpleStringViewHolder>() {

    var onItemClickListener: ((T) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleStringViewHolder =
        SimpleStringViewHolder(
            parent.context.inflater.inflate(R.layout.item_simple_list, parent, false)
        )

    override fun getItemCount(): Int = listItems.size

    override fun onBindViewHolder(holder: SimpleStringViewHolder, position: Int) {
        val item = listItems[position]
        holder.title.text = item.convertToString()
        holder.itemView.setOnClickListener { onItemClickListener?.invoke(item) }
    }

}

/**
 * A simple [RecyclerView.ViewHolder] that displays a single text item.
 */
class SimpleStringViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    /**
     * The text to display.
     */
    val title: TextView = view.findViewById(R.id.title_text)

}

package com.bangbangcoding.screenmirror.web.list

import com.bangbangcoding.screenmirror.R
import android.graphics.PorterDuff
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangbangcoding.screenmirror.web.ui.dialog.DialogItem
import com.bangbangcoding.screenmirror.web.utils.ext.inflater

/**
 * A [RecyclerView.Adapter] that displays [DialogItem] with icons.
 */
class RecyclerViewDialogItemAdapter(
    private val listItems: List<DialogItem>
) : RecyclerView.Adapter<DialogItemViewHolder>() {

    var onItemClickListener: ((DialogItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogItemViewHolder =
        DialogItemViewHolder(
            parent.context.inflater.inflate(R.layout.dialog_list_item, parent, false)
        )

    override fun getItemCount(): Int = listItems.size

    override fun onBindViewHolder(holder: DialogItemViewHolder, position: Int) {
        val item = listItems[position]
        holder.icon.setImageDrawable(item.icon)
        holder.icon.setColorFilter(item.colorTint, PorterDuff.Mode.SRC_IN)
        holder.title.setText(item.title)
        holder.itemView.setOnClickListener { onItemClickListener?.invoke(item) }
    }

}

/**
 * A [RecyclerView.ViewHolder] that displays an icon and a title.
 */
class DialogItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    /**
     * The icon to display.
     */
    val icon: ImageView = view.findViewById(R.id.icon)

    /**
     * The title to display.
     */
    val title: TextView = view.findViewById(R.id.title_text)

}

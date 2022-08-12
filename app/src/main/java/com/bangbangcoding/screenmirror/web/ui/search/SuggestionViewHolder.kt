package com.bangbangcoding.screenmirror.web.ui.search

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bangbangcoding.screenmirror.R

class SuggestionViewHolder(view: View) {
    val imageView: ImageView = view.findViewById(R.id.suggestionIcon)
    val titleView: TextView = view.findViewById(R.id.title)
    val urlView: TextView = view.findViewById(R.id.url)
}

package com.bangbangcoding.screenmirror.web.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.data.local.model.Suggestion

class SuggestionAdapter(
    private var suggestions: List<Suggestion>,
    private val isIncognito: Boolean
) : BaseAdapter(), Filterable {

    fun setData(suggestions: List<Suggestion>) {
        this.suggestions = suggestions
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return if (isIncognito) 0 else suggestions.size
    }

    override fun getItem(position: Int): Any? {
        if (position > suggestions.size || position < 0) {
            return null
        }
        return suggestions[position]
    }

    override fun getItemId(p0: Int): Long = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: SuggestionViewHolder
        val finalView: View

        if (convertView == null) {
            val inflater = LayoutInflater.from(parent.context)
            finalView = inflater.inflate(R.layout.item_suggestion, parent, false)

            holder = SuggestionViewHolder(finalView)
            finalView.tag = holder
        } else {
            finalView = convertView
            holder = convertView.tag as SuggestionViewHolder
        }
        val webPage: Suggestion = suggestions[position]

        holder.titleView.text = webPage.title
        holder.urlView.text = webPage.url
        return finalView
    }

    private fun clearSuggestions() {
        (suggestions as ArrayList).clear()
    }

    override fun getFilter(): Filter {
        return  object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (constraint == null || constraint.isEmpty()) {
//                    SuggestionAdapter@this.clearSuggestions()
                    return results
                }
                results.count = 1
                return results
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                //no-op
            }

        }
    }

    private class SuggestionViewHolder(view: View) {
        val imageView: ImageView = view.findViewById(R.id.imgStart)
        val titleView: TextView = view.findViewById(R.id.tvTitle)
        val urlView: TextView = view.findViewById(R.id.tvSuggestion)
    }
}



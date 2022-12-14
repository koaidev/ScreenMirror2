package com.bangbangcoding.screenmirror.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.ItemDocumentBinding
import com.bangbangcoding.screenmirror.db.model.DocumentItem


class DocumentAdapter(

    val activity: Activity
) :
    RecyclerView.Adapter<DocumentAdapter.DocumentVH>() {
    class DocumentVH(val binding: ItemDocumentBinding) : RecyclerView.ViewHolder(binding.root)

    private var documents: ArrayList<DocumentItem> = arrayListOf()
    private var documentsLits: ArrayList<DocumentItem> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentVH {
        return DocumentVH(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_document,
                parent,
                false
            )
        )
    }

    @SuppressLint("IntentReset")
    override fun onBindViewHolder(holder: DocumentVH, position: Int) {
        holder.binding.document = documents[position]
        holder.binding.root.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                val cR: ContentResolver = activity.contentResolver
                val mime = MimeTypeMap.getSingleton()
                val mimeType =
                    mime.getExtensionFromMimeType(documents[position].uriDoc?.let { it1 ->
                        cR.getType(
                            it1
                        )
                    })
                intent.setDataAndType(
                    documents[position].uriDoc,
                    "application/$mimeType"
                )
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                activity.startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Intent.ACTION_VIEW)
                    .setType("*/*")
                    .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    .setData(documents[position].uriDoc)
                activity.startActivity(intent)
            }
        }
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = documents.size

    fun searchView(vararg type: ItemViewType) {
        val listSearch =  documentsLits.filter { documentItem ->
            check(documentItem, *type)
        }
        documents.clear()
        documents.addAll(listSearch)
        notifyDataSetChanged()

    }
    fun updateData(documentsLits : ArrayList<DocumentItem>){
       this.documentsLits = documentsLits
        notifyDataSetChanged()
    }

    private fun check(documentItem: DocumentItem, vararg type: ItemViewType): Boolean{
        return type.filter { it.value == documentItem.viewType }.isNotEmpty()
    }
}

enum class ItemViewType(var value: Int) {
    PDF(1), DOCX(2), DOC(3), XLS(4), PPT(5), TXT(6)
}
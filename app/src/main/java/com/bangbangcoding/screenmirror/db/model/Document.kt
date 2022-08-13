package com.bangbangcoding.screenmirror.db.model

import android.net.Uri
import androidx.annotation.Keep
import androidx.documentfile.provider.DocumentFile
import com.bangbangcoding.screenmirror.adapter.ItemViewType

@Keep
data class DocumentItem(
    @Keep val id: Long,
    @Keep val viewType: Int,
    @Keep val title: String,
    @Keep val dateCreated: Long,
    @Keep val size: Int,
    @Keep val type: String,
    @Keep val uriDoc: Uri
) {
    constructor() : this(0, ItemViewType.TXT.value,"", 0, 0, "", Uri.EMPTY)
}

data class CachingDocumentFile(private val documentFile: DocumentFile) {
    val name: String? by lazy { documentFile.name }
    val type: String? by lazy { documentFile.type }

    val isDirectory: Boolean by lazy { documentFile.isDirectory }

    val uri get() = documentFile.uri

    fun rename(newName: String): CachingDocumentFile {
        documentFile.renameTo(newName)
        return CachingDocumentFile(documentFile)
    }
}

fun Array<DocumentFile>.toCachingList(): List<CachingDocumentFile> {
    val list = mutableListOf<CachingDocumentFile>()
    for (document in this) {
        list += CachingDocumentFile(document)
    }
    return list
}

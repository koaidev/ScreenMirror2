package com.bangbangcoding.screenmirror.viewmodel

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangbangcoding.screenmirror.adapter.ItemViewType
import com.bangbangcoding.screenmirror.model.DocumentItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DocumentViewModel : ViewModel() {
    val isCheckWord = MutableLiveData<Boolean>()
    val isCheckXLS = MutableLiveData<Boolean>()
    val isCheckPDF = MutableLiveData<Boolean>()
    val isCheckPPT = MutableLiveData<Boolean>()
    val isCheckTxt = MutableLiveData<Boolean>()

    private val _documentItems = MutableLiveData<ArrayList<DocumentItem>>()
    val documentItems: LiveData<ArrayList<DocumentItem>>
        get() = _documentItems

//    fun getAllPdfs(contentResolver: ContentResolver) {
//        val mimePdf = "'application/pdf'"
//        val projection = arrayOf(
//            MediaStore.Files.FileColumns._ID,
//            MediaStore.Files.FileColumns.SIZE,
//            MediaStore.Files.FileColumns.DATE_ADDED,
//            MediaStore.Files.FileColumns.MIME_TYPE,
//            MediaStore.Files.FileColumns.TITLE
//        )
//        val selection = (
//                MediaStore.Files.FileColumns.MIME_TYPE + "=" + mimePdf)
//
//        val queryUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
//        viewModelScope.launch(Dispatchers.Main) {
//            contentResolver.query(
//                queryUri!!,
//                projection,
//                selection,
//                null,
//                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
//            )?.use { cursor ->
//                val documentFiles = arrayListOf<DocumentItem>()
//                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
//                val nameColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE)
//                val typeColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
//                val dateAddColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
//                val sizeColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
//                while (cursor.moveToNext()) {
//                    val id = cursor.getLong(idColumn)
//                    val name = cursor.getString(nameColumn)
//                    val size = cursor.getInt(sizeColumn)
//                    val type = cursor.getString(typeColumn)
//                    val dateAdd = cursor.getLong(dateAddColumn)
//                    val contentUri: Uri = ContentUris.withAppendedId(
//                        MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
//                        id
//                    )
//                    println("Document: $name")
//                    // Stores column values and the contentUri in a local object
//                    // that represents the media file.
//                    documentFiles.add(DocumentItem(id, name, dateAdd, size, type, contentUri))
//                    _documentItems.value = documentFiles
//                }
//            }
//        }
//    }
//
//    fun getAllWords(contentResolver: ContentResolver) {
//        val mimeWord = "'application/msword'"
//        val mimePPt = "'application/vnd.openxmlformats-officedocument.wordprocessingml.document'"
//
//        val projection = arrayOf(
//            MediaStore.Files.FileColumns._ID,
//            MediaStore.Files.FileColumns.SIZE,
//            MediaStore.Files.FileColumns.DATE_ADDED,
//            MediaStore.Files.FileColumns.MIME_TYPE,
//            MediaStore.Files.FileColumns.TITLE
//        )
//        val selection = (
//                MediaStore.Files.FileColumns.MIME_TYPE + "=" + mimeWord + " OR " + MediaStore.Files.FileColumns.MIME_TYPE +"=" +mimePPt)
//
//        val queryUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
//        viewModelScope.launch(Dispatchers.Main) {
//            contentResolver.query(
//                queryUri!!,
//                projection,
//                selection,
//                null,
//                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
//            )?.use { cursor ->
//                val documentFiles = arrayListOf<DocumentItem>()
//                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
//                val nameColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE)
//                val typeColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
//                val dateAddColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
//                val sizeColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
//                while (cursor.moveToNext()) {
//                    val id = cursor.getLong(idColumn)
//                    val name = cursor.getString(nameColumn)
//                    val size = cursor.getInt(sizeColumn)
//                    val type = cursor.getString(typeColumn)
//                    val dateAdd = cursor.getLong(dateAddColumn)
//                    val contentUri: Uri = ContentUris.withAppendedId(
//                        MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
//                        id
//                    )
//                    println("Document: $name")
//                    // Stores column values and the contentUri in a local object
//                    // that represents the media file.
//                    documentFiles.add(DocumentItem(id, name, dateAdd, size, type, contentUri))
//                    _documentItems.value = documentFiles
//                }
//            }
//        }
//    }
//
//    fun getAllExcels(contentResolver: ContentResolver) {
//        val mimeXls = "'application/vnd.ms-excel'"
//
//        val projection = arrayOf(
//            MediaStore.Files.FileColumns._ID,
//            MediaStore.Files.FileColumns.SIZE,
//            MediaStore.Files.FileColumns.DATE_ADDED,
//            MediaStore.Files.FileColumns.MIME_TYPE,
//            MediaStore.Files.FileColumns.TITLE
//        )
//        val selection = (
//                MediaStore.Files.FileColumns.MIME_TYPE + "=" + mimeXls)
//
//        val queryUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
//        viewModelScope.launch(Dispatchers.Main) {
//            contentResolver.query(
//                queryUri!!,
//                projection,
//                selection,
//                null,
//                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
//            )?.use { cursor ->
//                val documentFiles = arrayListOf<DocumentItem>()
//                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
//                val nameColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE)
//                val typeColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
//                val dateAddColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
//                val sizeColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
//                while (cursor.moveToNext()) {
//                    val id = cursor.getLong(idColumn)
//                    val name = cursor.getString(nameColumn)
//                    val size = cursor.getInt(sizeColumn)
//                    val type = cursor.getString(typeColumn)
//                    val dateAdd = cursor.getLong(dateAddColumn)
//                    val contentUri: Uri = ContentUris.withAppendedId(
//                        MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
//                        id
//                    )
//                    println("Document: $name")
//                    // Stores column values and the contentUri in a local object
//                    // that represents the media file.
//                    documentFiles.add(DocumentItem(id, name, dateAdd, size, type, contentUri))
//                    _documentItems.value = documentFiles
//                }
//            }
//        }
//    }
//
//    fun getAllPPts(contentResolver: ContentResolver) {
//        val mimePPt = "'application/vnd.openxmlformats-officedocument.presentationml.presentation'"
//
//        val projection = arrayOf(
//            MediaStore.Files.FileColumns._ID,
//            MediaStore.Files.FileColumns.SIZE,
//            MediaStore.Files.FileColumns.DATE_ADDED,
//            MediaStore.Files.FileColumns.MIME_TYPE,
//            MediaStore.Files.FileColumns.TITLE
//        )
//        val selection = (
//                MediaStore.Files.FileColumns.MIME_TYPE + "=" + mimePPt)
//
//        val queryUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
//        viewModelScope.launch(Dispatchers.Main) {
//            contentResolver.query(
//                queryUri!!,
//                projection,
//                selection,
//                null,
//                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
//            )?.use { cursor ->
//                val documentFiles = arrayListOf<DocumentItem>()
//                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
//                val nameColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE)
//                val typeColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
//                val dateAddColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
//                val sizeColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
//                while (cursor.moveToNext()) {
//                    val id = cursor.getLong(idColumn)
//                    val name = cursor.getString(nameColumn)
//                    val size = cursor.getInt(sizeColumn)
//                    val type = cursor.getString(typeColumn)
//                    val dateAdd = cursor.getLong(dateAddColumn)
//                    val contentUri: Uri = ContentUris.withAppendedId(
//                        MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
//                        id
//                    )
//                    println("Document: $name")
//                    // Stores column values and the contentUri in a local object
//                    // that represents the media file.
//                    documentFiles.add(DocumentItem(id, name, dateAdd, size, type, contentUri))
//                    _documentItems.value = documentFiles
//                }
//            }
//        }
//    }
//
//    fun getAllTXTs(contentResolver: ContentResolver) {
//        val mimePPt = "'application/vnd.openxmlformats-officedocument.presentationml.presentation'"
//        val mimeXls = "'application/vnd.ms-excel'"
//        val mimeWord = "'application/msword'"
//        val mimeWordX = "'application/vnd.openxmlformats-officedocument.wordprocessingml.document'"
//        val mimePdf = "'application/pdf'"
//
//        val projection = arrayOf(
//            MediaStore.Files.FileColumns._ID,
//            MediaStore.Files.FileColumns.SIZE,
//            MediaStore.Files.FileColumns.DATE_ADDED,
//            MediaStore.Files.FileColumns.MIME_TYPE,
//            MediaStore.Files.FileColumns.TITLE
//        )
//        val selection = (
//                MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_DOCUMENT + " AND "+
//                        MediaStore.Files.FileColumns.MIME_TYPE + "!=" + mimePPt + " AND "+
//                        MediaStore.Files.FileColumns.MIME_TYPE + "!=" + mimeWordX + " AND "+
//                        MediaStore.Files.FileColumns.MIME_TYPE + "!=" + mimePdf + " AND "+
//                        MediaStore.Files.FileColumns.MIME_TYPE + "!=" + mimeXls + " AND "+
//                        MediaStore.Files.FileColumns.MIME_TYPE + "!=" + mimeWord
//
//                )
//
//        val queryUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
//        viewModelScope.launch(Dispatchers.Main) {
//            contentResolver.query(
//                queryUri!!,
//                projection,
//                selection,
//                null,
//                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
//            )?.use { cursor ->
//                val documentFiles = arrayListOf<DocumentItem>()
//                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
//                val nameColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE)
//                val typeColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
//                val dateAddColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
//                val sizeColumn =
//                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
//                while (cursor.moveToNext()) {
//                    val id = cursor.getLong(idColumn)
//                    val name = cursor.getString(nameColumn)
//                    val size = cursor.getInt(sizeColumn)
//                    val type = cursor.getString(typeColumn)
//                    val dateAdd = cursor.getLong(dateAddColumn)
//                    val contentUri: Uri = ContentUris.withAppendedId(
//                        MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
//                        id
//                    )
//                    println("Document: $name")
//                    // Stores column values and the contentUri in a local object
//                    // that represents the media file.
//                    documentFiles.add(DocumentItem(id, name, dateAdd, size, type, contentUri))
//                    _documentItems.value = documentFiles
//                }
//            }
//        }
//    }


    fun getAllDocuments(contentResolver: ContentResolver) {

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.TITLE
        )
        val selection = (
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_DOCUMENT)

        val queryUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        viewModelScope.launch(Dispatchers.Main) {
            contentResolver.query(
                queryUri!!,
                projection,
                selection,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
            )?.use { cursor ->
                val documentFiles = arrayListOf<DocumentItem>()
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE)
                val typeColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
                val dateAddColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
                val sizeColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val size = cursor.getInt(sizeColumn)
                    val type = cursor.getString(typeColumn)
                    val dateAdd = cursor.getLong(dateAddColumn)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
                        id
                    )
                    println("Document: $type")
                    // Stores column values and the contentUri in a local object
                    // that represents the media file.

                    val mime = MimeTypeMap.getSingleton()
                    val mimeType = mime.getExtensionFromMimeType(contentUri.let { it1 ->
                        contentResolver.getType(
                            it1
                        )
                    })
                    documentFiles.add(DocumentItem(id, checkViewType(mimeType), name, dateAdd, size, type, contentUri))
                    _documentItems.value = documentFiles
                }
            }
        }

    }

    fun checkViewType(mimeType: String?):Int {
        return when (mimeType) {
            "pdf" -> ItemViewType.PDF.value
            "docx" -> ItemViewType.DOCX.value
            "doc" -> ItemViewType.DOC.value
            "xls" -> ItemViewType.XLS.value
            "ppt" -> ItemViewType.PPT.value
            else -> ItemViewType.TXT.value
        }
    }


}
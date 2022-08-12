package com.bangbangcoding.screenmirror.db.viewmodel

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangbangcoding.screenmirror.db.model.MediaItem
import com.bangbangcoding.screenmirror.utils.Common
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MediaViewModel : ViewModel() {
    private val _medias = MutableLiveData<ArrayList<MediaItem>>()
    val medias: LiveData<ArrayList<MediaItem>>
        get() = _medias

    private val _videos = MutableLiveData<ArrayList<MediaItem>>()
    val videos: LiveData<ArrayList<MediaItem>>
        get() = _videos

    private val _images = MutableLiveData<ArrayList<MediaItem>>()
    val images: LiveData<ArrayList<MediaItem>>
        get() = _images

    fun getAllVideos(contentResolver: ContentResolver) {
        val collectionVideo: Uri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

        val projectionVideo = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED
        )

        val sortOrderVideo = "${MediaStore.Video.Media.DATE_ADDED} DESC"


        viewModelScope.launch(Dispatchers.Main) {
            contentResolver.query(
                collectionVideo,
                projectionVideo,
                null,
                null,
                sortOrderVideo
            )?.use { cursor ->
                try {
                    val mediaFile = arrayListOf<MediaItem>()
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                    val nameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                    val durationColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                    val dateAddColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)

                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val name = cursor.getString(nameColumn)
                        val duration = cursor.getInt(durationColumn)
                        val size = cursor.getInt(sizeColumn)
                        val dateAdd = cursor.getLong(dateAddColumn)
                        val date = Common.getDate(dateAdd * 1000, "dd/MM/yyyy").toString()

                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        // Stores column values and the contentUri in a local object
                        // that represents the media file.
                        mediaFile.add(
                            MediaItem(
                                id,
                                true,
                                contentUri,
                                name,
                                duration,
                                size,
                                date
                            )
                        )
                        println("ABCD: $duration")
                        _videos.value = mediaFile

                    }
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }

            }
        }
    }

    fun getAllImages(contentResolver: ContentResolver) {
        val collectionImage: Uri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        val projectionImage = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_ADDED
        )
        val sortOrderImage = "${MediaStore.Images.Media.DATE_ADDED} DESC"


        viewModelScope.launch(Dispatchers.Main) {
            contentResolver.query(
                collectionImage,
                projectionImage,
                null,
                null,
                sortOrderImage
            )?.use { cursor ->
                try {
                    val mediaFile = arrayListOf<MediaItem>()
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val nameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    val dateAddColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val name = cursor.getString(nameColumn)

                        val size = cursor.getInt(sizeColumn)
                        val dateAdd = cursor.getLong(dateAddColumn)
                        val date = Common.getDate(dateAdd * 1000, "dd/MM/yyyy").toString()

                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        // Stores column values and the contentUri in a local object
                        // that represents the media file.
                        mediaFile.add(MediaItem(id, false, contentUri, name, null, size, date))
                        _images.value = mediaFile
                    }
                } catch (e: Exception) {
                    println("Error Image: ${e.message}")
                }
            }
        }
    }


    fun getAllMediaFile(contentResolver: ContentResolver) {
        val projection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DURATION,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
            )
        } else {
            arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Video.Media.DURATION,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
            )
        }

        val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)

        val queryUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.getMediaScannerUri()
        }
        viewModelScope.launch(Dispatchers.Main) {
            contentResolver.query(
                queryUri,
                projection,
                selection,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
            )?.use { cursor ->
                try {
                    val mediaFile = arrayListOf<MediaItem>()
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                    val nameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE)
                    val type = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
                    val durationColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                    println("TypeVideo: $type")
                    val dateAddColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val name = cursor.getString(nameColumn)
                        val duration = cursor.getInt(durationColumn)
                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Files.getContentUri("external"),
                            id
                        )
                        val dateAdd = cursor.getLong(dateAddColumn)
                        var date = Common.getDate(dateAdd * 1000, "dd/MM/yyyy").toString()
                        val dateNow =
                            Common.getDate(System.currentTimeMillis(), "dd/MM/yyyy").toString()
                        val dateYesterday = Common.getDate(
                            System.currentTimeMillis() - 24 * 60 * 60 * 1000,
                            "dd/MM/yyyy"
                        ).toString()

                        if (date == dateNow) {
                            date = "Today"
                        } else if (date == dateYesterday) {
                            date = "Yesterday"
                        }
                        // Stores column values and the contentUri in a local object
                        // that represents the media file.
                        mediaFile.add(
                            MediaItem(
                                id,
                                type == 3,
                                contentUri,
                                name,
                                duration,
                                null,
                                date
                            )
                        )
                        _medias.value = mediaFile
                    }
                } catch (e: Exception) {
                    println("Error Media: ${e.message}")
                }
            }
        }
    }
}
package com.bangbangcoding.screenmirror.viewmodel

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.*
import androidx.loader.content.CursorLoader
import com.bangbangcoding.screenmirror.model.MediaItem
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

    private val collectionVideo: Uri =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

    private val projectionVideo = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DURATION,
        MediaStore.Video.Media.SIZE
    )

    // Display videos in alphabetical order based on their display name.
    private val sortOrderVideo = "${MediaStore.Video.Media.DATE_ADDED} DESC"


    fun getAllVideos(contentResolver: ContentResolver) {
        viewModelScope.launch(Dispatchers.Main) {
            contentResolver.query(
                collectionVideo,
                projectionVideo,
                null,
                null,
                sortOrderVideo
            )?.use { cursor ->
                val mediaFile = arrayListOf<MediaItem>()
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val duration = cursor.getInt(durationColumn)
                    val size = cursor.getInt(sizeColumn)

                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    // Stores column values and the contentUri in a local object
                    // that represents the media file.
                    mediaFile.add(MediaItem(id, true, contentUri, name, duration, size))
                    println("ABCD: $duration")
                    _videos.value = mediaFile

                }

            }
        }

    }

    private val collectionImage: Uri =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

    private val projectionImage = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.SIZE
    )


    private val sortOrderImage = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    fun getAllImages(contentResolver: ContentResolver) {
        viewModelScope.launch(Dispatchers.Main) {
            contentResolver.query(
                collectionImage,
                projectionImage,
                null,
                null,
                sortOrderImage
            )?.use { cursor ->
                val mediaFile = arrayListOf<MediaItem>()
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)

                    val size = cursor.getInt(sizeColumn)

                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    // Stores column values and the contentUri in a local object
                    // that represents the media file.
                    mediaFile.add(MediaItem(id, false, contentUri, name, null, size))
                    _images.value = mediaFile
                }
            }
        }
    }


    fun getAllMediaFile(contentResolver: ContentResolver) {
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DURATION,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.TITLE
        )

        val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)

        val queryUri = MediaStore.Files.getContentUri("external")
        viewModelScope.launch(Dispatchers.Main) {
            contentResolver.query(
                queryUri,
                projection,
                selection,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
            )?.use { cursor ->
                val mediaFile = arrayListOf<MediaItem>()
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE)
                val type = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
                val durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                println("TypeVideo: $type")

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val duration = cursor.getInt(durationColumn)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Files.getContentUri("external"),
                        id
                    )
                    // Stores column values and the contentUri in a local object
                    // that represents the media file.
                    mediaFile.add(MediaItem(id, type==3, contentUri, name, duration,null))
                    _medias.value = mediaFile
                }
            }
        }


    }
}
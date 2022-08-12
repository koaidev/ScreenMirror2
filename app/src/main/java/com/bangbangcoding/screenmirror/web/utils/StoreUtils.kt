package com.bangbangcoding.screenmirror.web.utils

import android.annotation.SuppressLint
import android.app.RecoverableSecurityException
import android.content.*
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.webkit.URLUtil
import androidx.appcompat.app.AppCompatActivity
import com.bangbangcoding.screenmirror.web.data.local.model.AudioDownloadModel
import com.bangbangcoding.screenmirror.web.data.local.model.VideoDownloadModel
import com.bangbangcoding.screenmirror.web.storage.DownloaderPreferences
import com.bangbangcoding.screenmirror.web.ui.listener.RefreshFileListener
import com.bangbangcoding.screenmirror.web.utils.Constants.PATH_VIDEO_OLD
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


object StoreUtils {

    fun refreshFile(context: Context, target: File?, listener: RefreshFileListener) {
        val file = File(target, "")
        MediaScannerConnection.scanFile(
            context, arrayOf(file.toString()), arrayOf("video/*")
        ) { path1: String?, uri: Uri? ->
            listener.onValueFile(path1, uri)
            var uriStr: String? = null
            if (uri != null) {
                uriStr = uri.toString()
            }
            DownloaderPreferences(context).setString(
                DownloaderPreferences.KEY_LAST_FILE,
                uriStr ?: ""
            )
        }
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        mediaScanIntent.data = Uri.fromFile(target)
        context.sendBroadcast(mediaScanIntent)
    }

    fun refreshFile(context: Context, target: File) {
        MediaScannerConnection.scanFile(
            context, arrayOf(target.absolutePath), arrayOf("video/*")
        ) { path1: String, uri: Uri ->

            Log.i("ttt", "Scanned $path1:")
            Log.i("ttt", "-> uri= $uri")
        }
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        mediaScanIntent.data = Uri.fromFile(target)
        context.sendBroadcast(mediaScanIntent)
    }

    fun getListVideosCreatedExternal(context: Context?): List<VideoDownloadModel> {
        if (Utils.needsStoragePermission(context)) {
            Log.e("ttt", "getListVideosCreatedExternal: false")
            return ArrayList<VideoDownloadModel>()
        }
        val videoArrayList: MutableList<VideoDownloadModel> = ArrayList<VideoDownloadModel>()
        //looping through all rows and adding to list
        if (Constants.getExternalFile().list() != null) {
            val listData: Array<String> = Constants.getExternalFile().list()!!
            Log.e("ttt", "getListVideosExternalDir list: ${listData.size}")
            for (filename in listData) {
                if (filename.isNotEmpty() && (filename.endsWith(".mp4") || filename.endsWith(".wav")
                            || filename.endsWith(".gif") || filename.endsWith(".ts"))
                ) {
                    val path: String =
                        Constants.getExternalFile().absolutePath.toString() + File.separator + filename
                    val videoModel = getVideo(context!!, path)
                    videoModel?.let {
                        videoArrayList.add(videoModel)
                    }
                    Log.e("ttt", "video_New: size = " + videoArrayList.size)
                }
            }
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            val fileOld = File(Environment.getExternalStorageDirectory(), PATH_VIDEO_OLD)
            Log.e("ttt", "getListVideosCreatedExternal OldPath: ${fileOld.absolutePath}")
            if (fileOld.exists() && fileOld.list() != null) {
                val listData: Array<String> = fileOld.list()!!
                Log.e("ttt", "getListVideosExternalDir list: ${listData.size}")
                for (filename in listData) {
                    if (filename.isNotEmpty() && (filename.endsWith(".mp4") || filename.endsWith(".wav")
                                || filename.endsWith(".gif") || filename.endsWith(".ts"))
                    ) {
                        val path: String =
                            fileOld.absolutePath.toString() + File.separator + filename
                        val videoModel = getVideo(context!!, path)
                        videoModel?.let {
                            videoArrayList.add(videoModel)
                        }
                        Log.e("ttt", "video_OLD: All size = " + videoArrayList.size)
                    }
                }
            }
        }
        videoArrayList.sortByDescending { it.createDate }
        Log.e("ttt", "getListVideosCreated: All size = " + videoArrayList.size)
        return videoArrayList
    }

    fun getVideo(context: Context, videoPath: String): VideoDownloadModel? {
        var uri: Uri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }
        val id = getVideoPathToMediaID(videoPath, context)
        val urisToModify =
            ContentUris.withAppendedId(uri, id)

        val cursor = context.contentResolver.query(
            urisToModify,
            arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DATE_ADDED
            ),
            null, null,
            null
        )
        if (cursor != null) {
            try {
                if (cursor.count >= 1) {
                    if (cursor.moveToFirst()) {
                        val id =
                            cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
//                        var title =
//                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))
                        val display =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
//                        if (TextUtils.isEmpty(title)) {
                        val title = videoPath.substring(
                            videoPath.lastIndexOf("/") + 1,
                            videoPath.lastIndexOf(".")
                        )
//                        }
                        val dateStr =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED))
                        val date = convertDateToMillisecond(dateStr)
                        val duration =
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                        val width =
                            cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH))
                        val height =
                            cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT))
                        Log.d("ttt", "getVideo: $duration")
                        if (duration != 0L) return VideoDownloadModel(
                            id = id,
                            title = title,
                            path = videoPath,
                            display = display,
                            width = width,
                            height = height,
                            duration = duration,
                            createDate = date
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor.close()
            }
        }
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(videoPath)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!
                .toLong()
            val dateStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)!!
            val date = convertDateToMillisecond(dateStr)
            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!
                .toInt()
            val height =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!
                    .toInt()
            retriever.release()
            Log.d("ttt", "getVideo2: $duration")
            VideoDownloadModel(
                id = -1,
                title = videoPath.substring(
                    videoPath.lastIndexOf("/") + 1,
                    videoPath.lastIndexOf(".")
                ),
                path = videoPath,
                width = width,
                height = height,
                duration = duration,
                createDate = date

            )
        } catch (e: Exception) {
            val downloadDir = File(PrefUtils.getExternalFilesDirName(context))
            val fileName = URLUtil.guessFileName(videoPath, null, null)
            val from = File(downloadDir, fileName)
            from.delete()
            null
        }
    }

    fun renameFile(context: Context, oldPath: String, newName: String, listener: (Int) -> Unit) {
        val file = File(oldPath)
        val currentFileName = oldPath.substring(oldPath.lastIndexOf("/"))
        val path = oldPath.replace(currentFileName, "/$newName.mp4")
        val fileTo = File(path)
        Log.d("ttt", "rename: OK$oldPath:::$path")
        if (fileTo.exists()) {
            listener.invoke(-1)
            return
        }
        if (file.exists()) {
            if (!file.renameTo(fileTo)) {
                if (!file.delete()) {
                    if (!fileTo.delete()) {
                        Log.e("ttt", "onRename: Not delete fileTo")
                    }
                    Log.e("ttt", "onRename: Not delete file")
                }
                Log.e("ttt", "onRename: Not oK")

                testRename(context, oldPath, newName, listener)
            } else {
                Log.e("ttt", "onRename: OK")
                refreshFile(context, fileTo)
                listener.invoke(1)
            }
        }
    }

    private fun testRename(
        context: Context,
        oldPath: String,
        newName: String,
        listener: (Int) -> Unit
    ) {
        try {
            val extUri = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            val resolver = context.applicationContext.contentResolver
            val contentValues = ContentValues()
            val uri = ContentUris.withAppendedId(
                extUri,
                getVideoPathToMediaID(oldPath, context)
            )
            resolver.openFileDescriptor(uri, "w")?.use {

            }
            contentValues.clear()
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, newName)
            resolver.update(uri, contentValues, null, null)
            listener.invoke(0)
        } catch (e: Exception) {
            e.printStackTrace()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val recover = e as? RecoverableSecurityException
                val intentSender = recover?.userAction?.actionIntent?.intentSender
//                intentSender?.let {
//                    context.startIntentSender(it, "123", null, 0,0,0, null)
//                }
            }
        }

    }


    fun getVideos(context: Context): List<VideoDownloadModel> {
        val results = arrayListOf<VideoDownloadModel>()
        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                "_data",
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DATE_ADDED
            ),
//            "_data" + " like ?", arrayOf(videoPath),
            null, null,
            null
        )
        if (cursor != null) {
            try {
                if (cursor.count >= 1) {
                    while (cursor.moveToNext()) {
                        val id =
                            cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                        var title =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))
                        val path =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                        if (TextUtils.isEmpty(title)) {
                            title = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."))
                        }
                        if (path.contains(Constants.PATH_VIDEO)) {
                            val date =
                                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)) * 1000L
                            val duration =
                                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                            val width =
                                cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH))
                            val height =
                                cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT))
                            Log.d("ttt", "getVideo: $duration")
                            if (duration != 0L)
                                results.add(
                                    VideoDownloadModel(
                                        id = id,
                                        title = title,
                                        path = path,
                                        width = width,
                                        height = height,
                                        duration = duration,
                                        createDate = date
                                    )
                                )

                        }

                    }
                    return results
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor.close()
            }
        }
        return arrayListOf()
    }

    fun getListAudiosCreatedExternal(context: Context?): List<AudioDownloadModel> {
        if (Utils.needsStoragePermission(context)) {
            Log.e("ttt", "getListAudiosCreatedExternal: false")
            return ArrayList<AudioDownloadModel>()
        }
        val videoArrayList: MutableList<AudioDownloadModel> = ArrayList<AudioDownloadModel>()

        //looping through all rows and adding to list
        if (Constants.getExternalFile().list() != null) {
            val listData = Constants.getExternalFile().list()
            for (filename in listData!!) {
                if (filename.isNotEmpty() &&
                    (filename.contains(".mp3"))
                ) {
                    val path: String =
                        Constants.getExternalFile().absolutePath.toString() + File.separator + filename
                    val file = File(path)
                    val dateAdded = file.lastModified()
                    val videoModel = AudioDownloadModel()
                    videoModel.title = (filename.replace(".mp3", ""))
                    videoModel.path = (path)
                    videoModel.createDate = (dateAdded)
                    videoArrayList.add(videoModel)
                }
            }
        }
        Log.e("ttt", "getListVideosCreated123: size = " + videoArrayList.size)
        return videoArrayList
    }

    //time conversion
    private fun timeConversion(value: Long): String {
        val videoTime: String
        val dur = value.toInt()
        val hrs = dur / 3600000
        val mns = dur / 60000 % 60
        val scs = dur % 60000 / 1000
        videoTime = if (hrs > 0) {
            String.format("%02d:%02d:%02d", hrs, mns, scs)
        } else {
            String.format("%02d:%02d", mns, scs)
        }
        return videoTime
    }

    private fun convertDateToMillisecond(dateTime: String): Long {
        val p: Pattern = Pattern.compile("^[0-9]+$")
        val m: Matcher = p.matcher(dateTime)

        return if (m.matches()) {
            dateTime.toLong() * 1000
        } else {
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyyMMdd'T'HHmmss.SSS'Z'", Locale.getDefault())
            calendar.time = sdf.parse(dateTime)

            calendar.timeInMillis
        }
    }

    fun openSettingsAllFilesAccess(activity: AppCompatActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            activity.startActivity(intent)
        }
    }

    fun openNativeFileExplorer(activity: AppCompatActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val intent = Intent(StorageManager.ACTION_MANAGE_STORAGE)
            activity.startActivity(intent)
        }
    }

//    fun clearAppsCacheFiles(activity: AppCompatActivity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            val intent = Intent(StorageManager.ACTION_CLEAR_APP_CACHE)
//            activity.startActivity(intent)
//        }
//    }

    private fun externalMemoryAvailable(): Boolean {
        return Environment.getExternalStorageState() ==
                Environment.MEDIA_MOUNTED
    }

    fun getAvailableExternalMemorySize(): String {
        return if (externalMemoryAvailable()) {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize: Long = stat.blockSizeLong
            val availableBlocks: Long = stat.availableBlocksLong
            Utils.getStringSizeLengthFile(availableBlocks * blockSize)
        } else {
            Log.e("ttt", "getAvailableExternalMemorySize: fail")
            return "0"
        }
    }

    fun getUsedExternalSize() : String {
        return if (externalMemoryAvailable()) {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize: Long = stat.blockSizeLong
            val availableBlocks: Long = stat.availableBlocksLong
            val totalBlocks: Long = stat.blockCountLong
            Utils.getStringSizeLengthFile((totalBlocks - availableBlocks) * blockSize)
        } else {
            Log.e("ttt", "getAvailableExternalMemorySize: fail")
            return "0"
        }
    }

    fun getTotalExternalMemorySize(): String {
        return if (externalMemoryAvailable()) {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize: Long = stat.blockSizeLong
            val totalBlocks: Long = stat.blockCountLong
            Utils.getStringSizeLengthFile(totalBlocks * blockSize, "#")
        } else {
            Log.e("ttt", "getTotalExternalMemorySize: fail")
            return "0"
        }
    }

    fun deleteMedia(context: Context, media: List<VideoDownloadModel>) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && media.size > 1) {
//            val intentSender =
//                deleteMediaBulk(context, media)
//            _actions.postValue(
//                MainAction.ScopedPermissionRequired(
//                    intentSender,
//                    ModificationType.DELETE))
//        } else {
//            viewModelScope.launch {
//                for (item in media) {
//                    val intentSender = FileOperations.deleteMedia(
//                        getApplication<Application>(),
//                        item)
//                    if (intentSender != null) {
//                        _actions.postValue(
//                            MainAction.ScopedPermissionRequired(
//                                intentSender,
//                                ModificationType.DELETE))
//                    }
//                }
//            }
//        }
    }

    @SuppressLint("NewApi") //method only call from API 30 onwards
    fun deleteMediaBulk(context: Context, media: List<VideoDownloadModel>): IntentSender {
        val uris = media.map { Uri.fromFile(File(it.path)) }
        return MediaStore.createDeleteRequest(context.contentResolver, uris).intentSender
    }

    fun deleteFileUsingPath(context: Context, path: String): Boolean {
        val uri: Uri = ContentUris.withAppendedId(
            MediaStore.Video.Media.getContentUri("external"),
            getVideoPathToMediaID(path, context)
        )
        val resolver = context.contentResolver
        try {
            resolver.delete(
                uri,
                null,
                null
            )
            return true
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
        return false
    }

    fun getVideoPathToMediaID(songPath: String, context: Context): Long {
        var id: Long = 0
        val cr = context.contentResolver
        val uri = MediaStore.Video.Media.getContentUri("external")
        val selection = MediaStore.Video.Media.DATA
        val selectionArgs = arrayOf(songPath)
        val projection = arrayOf(MediaStore.Video.Media._ID)
        val sortOrder = MediaStore.Video.Media.TITLE + " ASC"
        val cursor: Cursor? = cr.query(uri, projection, "$selection=?", selectionArgs, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val idIndex: Int = cursor.getColumnIndex(MediaStore.Video.Media._ID)
                id = cursor.getString(idIndex).toLong()
                Log.e("ttt", "getVideoPathToMediaID: $id")
            }
        }
        return id
    }

    fun testDelete(context: Context, oldPath: String) {
        try {
            val extUri = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            val resolver = context.applicationContext.contentResolver
            val contentValues = ContentValues()
            val uri = ContentUris.withAppendedId(
//            MediaStore.Video.Media.getContentUri("external"),
                extUri,
                getVideoPathToMediaID(oldPath, context)
            )
            resolver.openFileDescriptor(uri, "w")?.use {

            }
            resolver.delete(uri, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val recover = e as? RecoverableSecurityException
                val intentSender = recover?.userAction?.actionIntent?.intentSender
//                intentSender?.let {
//                    context.startIntentSender(it, "123", null, 0,0,0, null)
//                }
            }
        }

    }
}
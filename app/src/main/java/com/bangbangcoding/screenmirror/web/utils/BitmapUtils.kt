package com.bangbangcoding.screenmirror.web.utils

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever


object BitmapUtils {

    fun retrieveVideoFrameFromVideo(videoPath: String): Bitmap? {
        var bitmap: Bitmap?
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(
                videoPath,
                HashMap()
            )
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            mediaMetadataRetriever?.release()
        }
        return bitmap
    }
}
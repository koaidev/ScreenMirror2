package com.bangbangcoding.screenmirror.adapter

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.model.MediaItem
import com.bangbangcoding.screenmirror.utils.Common
import com.bumptech.glide.Glide
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@BindingAdapter("content_uri")
fun setThumbnail(imageView: ImageView, media: MediaItem) {
    try {
        val thumbnail: Bitmap =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imageView.context.contentResolver.loadThumbnail(
                    media.uri, Size(640, 480), null
                )
            } else {
                MediaStore.Images.Thumbnails.getThumbnail(
                    imageView.context.contentResolver,
                    media.id,
                    MediaStore.Images.Thumbnails.MINI_KIND,
                    null
                )
            }
        println("Thumbnail: $thumbnail")
        Glide.with(imageView).load(thumbnail).placeholder(R.drawable.place_holder).into(imageView)
    } catch (e: Exception) {
        println(e.message)
    }
}

@BindingAdapter("duration")
fun setDuration(textView: TextView, duration: Int?) {
    if (duration != null && duration > 0) {
        var h = ""
        var m = ""
        var s = ""
        val hours = TimeUnit.HOURS.convert(duration.toLong(), TimeUnit.MILLISECONDS)
        val mins = TimeUnit.MINUTES.convert(duration.toLong(), TimeUnit.MILLISECONDS) % 60
        val ses = TimeUnit.SECONDS.convert(duration.toLong(), TimeUnit.MILLISECONDS) % 60
        h = if (hours > 9) {
            "$hours"
        } else {
            "0$hours"
        }
        m = if (mins > 9) {
            "$mins"
        } else {
            "0$mins"
        }
        s = if (ses > 9) {
            "$ses"
        } else {
            "0$ses"
        }
        textView.text = "$h:$m:$s"

        println("Duration: $duration")
        textView.visibility = View.VISIBLE
    }
}

@BindingAdapter(value = ["is_video", "duration"])
fun setVisibleVideo(imageView: ImageView, isVideo: Boolean, duration: Int?) {
    imageView.visibility = if (isVideo || ((duration != null) && (duration > 0))) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter(value = ["uri_doc", "date_create", "size"])
fun setInfoDocument(textView: TextView, uriDoc: Uri, dateCreate: Long, size: Int) {
    println("TimeCreate: $dateCreate")
    println("TimeNow: ${System.currentTimeMillis()}")

    val date = Common.getDate(dateCreate*1000, "dd/MM/yyyy hh:mm:ss")
    val now = Common.getDate(System.currentTimeMillis(),"dd/MM/yyyy hh:mm:ss")
    println("DateCreate: $date")
    println("DateNow: $now")
    val cR: ContentResolver = textView.context.contentResolver
    val mime = MimeTypeMap.getSingleton()
    val mimeType = mime.getExtensionFromMimeType(uriDoc.let { it1 ->
        cR.getType(
            it1
        )
    })
    var sizeText = ""
    sizeText = if (size > 1024) {
        if (size / 1024 / 1024 > 1) {
            sizeText.plus(String.format("%.2f", (size.toDouble() / 1024 / 1024))).plus(" GB")
        } else {
            sizeText.plus(String.format("%.2f", (size.toDouble() / 1024))).plus(" MB")
        }
    } else {
        sizeText.plus(size).plus(" KB")
    }

    textView.text = "$mimeType - $sizeText\n$date"
}

@BindingAdapter("uri")
fun setImageDoc(imageView: ImageView, uri: Uri) {
    val cR: ContentResolver = imageView.context.contentResolver
    val mime = MimeTypeMap.getSingleton()
    val mimeType = mime.getExtensionFromMimeType(uri.let { it1 ->
        cR.getType(
            it1
        )
    })

    when (mimeType) {
        "pdf" -> {
            imageView.setImageDrawable(
                imageView.resources.getDrawable(
                    R.drawable.pdf,
                    imageView.resources.newTheme()
                )
            )
        }
        "docx" -> {
            imageView.setImageDrawable(
                imageView.resources.getDrawable(
                    R.drawable.doc,
                    imageView.resources.newTheme()
                )
            )
        }
        "doc" -> {
            imageView.setImageDrawable(
                imageView.resources.getDrawable(
                    R.drawable.doc,
                    imageView.resources.newTheme()
                )
            )
        }
        "xls" -> {
            imageView.setImageDrawable(
                imageView.resources.getDrawable(
                    R.drawable.xls,
                    imageView.resources.newTheme()
                )
            )
        }
        "ppt" -> {
            imageView.setImageDrawable(
                imageView.resources.getDrawable(
                    R.drawable.ppt,
                    imageView.resources.newTheme()
                )
            )
        }
        else -> {
            imageView.setImageDrawable(
                imageView.resources.getDrawable(
                    R.drawable.txt,
                    imageView.resources.newTheme()
                )
            )
        }
    }
}
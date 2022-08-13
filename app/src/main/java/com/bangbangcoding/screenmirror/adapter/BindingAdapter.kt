package com.bangbangcoding.screenmirror.adapter

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.bangbangcoding.screenmirror.db.model.MediaItem
import com.bangbangcoding.screenmirror.db.model.group.GeneralItem
import com.bangbangcoding.screenmirror.utils.Common
import com.bumptech.glide.Glide
import com.bumptech.glide.load.data.mediastore.ThumbFetcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


@BindingAdapter("content_uri")
fun setThumbnail(imageView: ImageView, media: GeneralItem) {
    println("Uri: ${media.uri}")
    Glide.with(imageView.context).load(media.uri)
        .thumbnail(Glide.with(imageView.context).load(media.uri)).into(imageView)
}

@SuppressLint("SetTextI18n")
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
        } else if (hours in 1..9) {
            "0$hours"
        } else {
            ""
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
        textView.text = "${if (h.isNullOrEmpty()) "" else "$h:"}$m:$s"

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

@SuppressLint("SetTextI18n")
@BindingAdapter(value = ["uri_doc", "date_create", "size"])
fun setInfoDocument(textView: TextView, uriDoc: Uri, dateCreate: Long, size: Int) {
    println("TimeCreate: $dateCreate")
    println("TimeNow: ${System.currentTimeMillis()}")

    val date = Common.getDate(dateCreate * 1000, "dd/MM/yyyy hh:mm:ss")
    val now = Common.getDate(System.currentTimeMillis(), "dd/MM/yyyy hh:mm:ss")
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

@SuppressLint("UseCompatLoadingForDrawables")
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

@BindingAdapter(value = ["enable_remove", "visibility_remove"])
fun setVisible(imageView: ImageView, enableRemove: Boolean, visibilityRemove: Boolean) {
    imageView.visibility = if (enableRemove && visibilityRemove) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@OptIn(DelicateCoroutinesApi::class)
@BindingAdapter("url_website")
fun setImageIcon(imageView: ImageView, urlWebsite: String) {
    if (urlWebsite.isNotEmpty()) {
        GlobalScope.launch(Dispatchers.Main) {
            Glide.with(imageView.context)
                .load(Uri.parse("https://t0.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=$urlWebsite&size=128"))
                .placeholder(R.drawable.place_holder)
                .into(imageView)
        }
    } else {
        imageView.setImageResource(R.drawable.ic_add)
    }
}

@BindingAdapter("src")
fun setImagePreview(imageView: ImageView, image: Bitmap) {
    imageView.setImageBitmap(image)
}

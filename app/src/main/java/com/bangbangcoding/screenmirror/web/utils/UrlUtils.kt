/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bangbangcoding.screenmirror.web.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.webkit.URLUtil
import com.bangbangcoding.screenmirror.web.html.bookmark.BookmarkPageFactory
import com.bangbangcoding.screenmirror.web.html.download.DownloadPageFactory
import com.bangbangcoding.screenmirror.web.html.history.HistoryPageFactory
import com.bangbangcoding.screenmirror.web.html.homepage.HomePageFactory
import com.bangbangcoding.screenmirror.web.utils.Constants.FILE
import java.lang.Exception
import java.lang.StringBuilder
import java.net.URL
import java.util.regex.Pattern

/**
 * Utility methods for URL manipulation.
 */
object UrlUtils {

    private val ACCEPTED_URI_SCHEMA = Pattern.compile("(?i)((?:http|https|file)://|(?:inline|data|about|javascript):|(?:.*:.*@))(.*)")
    const val QUERY_PLACE_HOLDER = "%s"
    private const val URL_ENCODED_SPACE = "%20"

    val UserAgentsList = arrayOf(
        "Mozilla/5.0 (Linux; Android 10; SM-A205U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Mobile Safari/537.36",
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36",
        "Mozilla/5.0 (Linux; Android 10; SM-A102U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Mobile Safari/537.36",
        "Mozilla/5.0 (iPad; CPU OS 12_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/71.0.3578.77 Mobile/15E148 Safari/605.1"
    )

    const val USER_AGENTS_LIST = "Mozilla/5.0 (Linux; Android 10; SM-A205U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Mobile Safari/537.36"


    /**
     * Attempts to determine whether user input is a URL or search terms.  Anything with a space is
     * passed to search if [canBeSearch] is true.
     *
     * Converts to lowercase any mistakenly upper-cased scheme (i.e., "Http://" converts to
     * "http://")
     *
     * @param canBeSearch if true, will return a search url if it isn't a valid  URL. If false,
     * invalid URLs will return null.
     * @return original or modified URL.
     */
    @JvmStatic
    fun smartUrlFilter(url: String, canBeSearch: Boolean, searchUrl: String): String {
        var inUrl = url.trim()
        val hasSpace = inUrl.contains(' ')
        val matcher = ACCEPTED_URI_SCHEMA.matcher(inUrl)
        if (matcher.matches()) {
            // force scheme to lowercase
            val scheme = matcher.group(1)
            val lcScheme = scheme.toLowerCase()
            if (lcScheme != scheme) {
                inUrl = lcScheme + matcher.group(2)
            }
            if (hasSpace && Patterns.WEB_URL.matcher(inUrl).matches()) {
                inUrl = inUrl.replace(" ", URL_ENCODED_SPACE)
            }
            return inUrl
        }
        if (!hasSpace) {
            if (Patterns.WEB_URL.matcher(inUrl).matches()) {
                return URLUtil.guessUrl(inUrl)
            }
        }

        return if (canBeSearch) {
            URLUtil.composeSearchUrl(inUrl,
                searchUrl, QUERY_PLACE_HOLDER)
        } else {
            ""
        }
    }

    /**
     * Returns whether the given url is the bookmarks/history page or a normal website
     */
    @JvmStatic
    fun isSpecialUrl(url: String?): Boolean {
        return url != null
            && url.startsWith(FILE)
            && (url.endsWith(BookmarkPageFactory.FILENAME)
            || url.endsWith(DownloadPageFactory.FILENAME)
            || url.endsWith(HistoryPageFactory.FILENAME)
            || url.endsWith(HomePageFactory.FILENAME))
    }

    /**
     * Determines if the url is a url for the bookmark page.
     *
     * @param url the url to check, may be null.
     * @return true if the url is a bookmark url, false otherwise.
     */
    @JvmStatic
    fun isBookmarkUrl(url: String?): Boolean = false
//        url != null && url.startsWith(FILE) && url.endsWith(BookmarkPageFactory.FILENAME)

    /**
     * Determines if the url is a url for the bookmark page.
     *
     * @param url the url to check, may be null.
     * @return true if the url is a bookmark url, false otherwise.
     */
    @JvmStatic
    fun isDownloadsUrl(url: String?): Boolean =false
//        url != null && url.startsWith(FILE) && url.endsWith(DownloadPageFactory.FILENAME)

    /**
     * Determines if the url is a url for the history page.
     *
     * @param url the url to check, may be null.
     * @return true if the url is a history url, false otherwise.
     */
    @JvmStatic
    fun isHistoryUrl(url: String?): Boolean = false
//        url != null && url.startsWith(FILE) && url.endsWith(HistoryPageFactory.FILENAME)

    /**
     * Determines if the url is a url for the start page.
     *
     * @param url the url to check, may be null.
     * @return true if the url is a start page url, false otherwise.
     */
    @JvmStatic
    fun isStartPageUrl(url: String?): Boolean = false
//        url != null && url.startsWith(FILE) && url.endsWith(HomePageFactory.FILENAME)
    @JvmStatic
    fun getVideoThumbInTime(
        context: Context,
        videoUri: Uri,
        time: Long,
        callback: (Bitmap?) -> Unit
    ) {
//        BackgroundExecutor.execute(object : BackgroundExecutor.Task("", 0L, "") {
//            override fun execute() {
//                try {
//                    val mediaMetadataRetriever = MediaMetadataRetriever()
//                    mediaMetadataRetriever.setDataSource(context, videoUri)
//                    val bitmap = mediaMetadataRetriever.getFrameAtTime(
//                        time * 1000,
//                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC
//                    )
//                    try {
//                        val bitmapResize = Bitmap.createScaledBitmap(
//                            bitmap,
//                            240,
//                            240 * bitmap.height / bitmap.width,
//                            false
//                        )
//                        callback.invoke(bitmapResize)
//                    } catch (t: Throwable) {
//                        t.printStackTrace()
//                    }
//                    mediaMetadataRetriever.release()
//                } catch (e: Throwable) {
//                    Thread.getDefaultUncaughtExceptionHandler()
//                        .uncaughtException(Thread.currentThread(), e)
//                }
//            }
//        })
    }

    @JvmStatic
    fun getRemoteFileSize(str: String?): Long {
        return try {
            val str1 = URL(str).openConnection()
            str1.connect()
            val contentLength = str1.contentLength.toLong()
            val stringBuilder = StringBuilder()
            stringBuilder.append("file_size = ")
            stringBuilder.append(contentLength)
            Log.e("sasa", stringBuilder.toString())
            contentLength
        } catch (str2: Exception) {
            str2.printStackTrace()
            0
        }
    }

    fun checkURL(input: CharSequence): Boolean {
        if (TextUtils.isEmpty(input)) {
            return false
        }
        val URL_PATTERN = Patterns.WEB_URL
        var isURL = URL_PATTERN.matcher(input).matches()
        if (!isURL) {
            val urlString = input.toString() + ""
            if (URLUtil.isNetworkUrl(urlString)) {
                try {
                    URL(urlString)
                    isURL = true
                } catch (ignored: Exception) {
                }
            }
        }
        return isURL
    }
}

package com.bangbangcoding.screenmirror.web.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.net.toUri
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.favicon.FaviconModel
import com.bangbangcoding.screenmirror.web.favicon.toValidUri
import com.bangbangcoding.screenmirror.web.utils.ThemeUtils
import com.bangbangcoding.screenmirror.web.utils.UrlUtils
import com.bangbangcoding.screenmirror.web.utils.ext.pad
import java.lang.Exception

/**
 * [LightningViewTitle] acts as a container class
 * for the favicon and page title, the information used
 * by the tab adapters to show the tabs to the user.
 */
class LightningViewTitle(private val context: Context) {

    private var favicon: Bitmap? = null
    private var screenShot: Bitmap? = null
    private var title = context.getString(R.string.txt_menu_new_tab)

    /**
     * Set the current favicon to a new Bitmap.
     * May be null, if null, the default will be used.
     *
     * @param favicon the potentially null favicon to set.
     */
    fun setFavicon(favicon: Bitmap?) {
        this.favicon = favicon?.pad()
    }

    /**
     * Set the current favicon to a new Bitmap.
     * May be null, if null, the default will be used.
     *
     * @param favicon the potentially null favicon to set.
     */
    fun setScreenShot(favicon: Bitmap?) {
        this.screenShot = favicon?.pad()
    }

    /**
     * Gets the current title, which is not null. Can be an empty string.
     *
     * @return the non-null title.
     */
    fun getTitle(): String? = title

    /**
     * Set the current title to a new title. If the title is null, an empty title will be used.
     *
     * @param title the title to set.
     */
    fun setTitle(title: String?) {
        this.title = title ?: ""
    }

    /**
     * Gets the favicon of the page, which is not null.
     * Either the favicon, or a default icon.
     *
     * @return the favicon or a default if that is null.
     */
    fun getFavicon(darkTheme: Boolean): Bitmap = favicon ?: getDefaultIcon(context, darkTheme)


    fun getScreenShot(url: String?): Bitmap = screenShot ?: getDefaultSS(context, url)


    companion object {

        private var defaultSSIcon: Bitmap? = null

        private var defaultDarkIcon: Bitmap? = null
        private var defaultLightIcon: Bitmap? = null

        /**
         * Helper method to initialize the DEFAULT_ICON variables
         *
         * @param context   the context needed to initialize the Bitmap.
         * @param darkTheme whether the icon should be themed dark or not.
         * @return a not null icon.
         */
        private fun getDefaultIcon(context: Context, darkTheme: Boolean): Bitmap = if (darkTheme) {
            var darkIcon = defaultDarkIcon

            if (darkIcon == null) {
                darkIcon = ThemeUtils.getThemedBitmap(context, R.drawable.ic_webpage, true)
                defaultDarkIcon = darkIcon
            }

            darkIcon
        } else {
            var lightIcon = defaultLightIcon

            if (lightIcon == null) {
                lightIcon = ThemeUtils.getThemedBitmap(context, R.drawable.ic_webpage, false)
                defaultLightIcon = lightIcon
            }

            lightIcon
        }

        private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            // Raw height and width of image
            val (height: Int, width: Int) = options.run { outHeight to outWidth }
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {

                val halfHeight: Int = height / 2
                val halfWidth: Int = width / 2

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }

        private fun decodeBitmapFromFile(
            path: String,
            reqWidth: Int,
            reqHeight: Int
        ): Bitmap {
            // First decode with inJustDecodeBounds=true to check dimensions
            return BitmapFactory.Options().run {
                inJustDecodeBounds = true
                BitmapFactory.decodeFile(path, this)
                // Calculate inSampleSize
                inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
                // Decode bitmap with inSampleSize set
                inJustDecodeBounds = false
                BitmapFactory.decodeFile(path, this)
            }
        }

        /**
         * Helper method to initialize the DEFAULT_ICON variables
         *
         * @param context   the context needed to initialize the Bitmap.
         * @param darkTheme whether the icon should be themed dark or not.
         * @return a not null icon.
         */
        private fun getDefaultSS(context: Context, url: String?): Bitmap {
            return if (url == null || UrlUtils.isSpecialUrl(url) || url.contains("buzzvideo.com")) {
                ThemeUtils.getBitmapFromVectorDrawable(context, R.drawable.img_home_ss)
            } else {
                try {
                    var file =
                        FaviconModel.getScreenShotCacheFile(context, url.toUri().toValidUri()!!)
                    if (file != null && file.exists()) {
                        //BitmapFactory.decodeFile(file.absolutePath)
                        decodeBitmapFromFile(file.absolutePath, 250, 400)
                    } else {
                        ThemeUtils.getBitmapFromVectorDrawable(context, R.drawable.img_home_ss)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    ThemeUtils.getBitmapFromVectorDrawable(context, R.drawable.img_home_ss)
                }

            }
        }
    }
}

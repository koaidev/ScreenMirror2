package com.bangbangcoding.screenmirror.web.utils

import android.os.Environment
import android.util.Log
import java.io.File

object Constants {

    const val PREF_RATIO_VIDEO = "PREF_RATIO_VIDEO"
    const val FULL_SCREEN = "full_screen"
    const val DESKTOP = "desktop"
    const val AD_BLOCKER = "ad_blocker"
    const val FIRST_TIKTOK = "FIRST_TIKTOK"
    const val LINK_SAVE_TIKTOK = "LINK_SAVE_TIKTOK"
    const val FIRST_XXVIDEO = "f_xxvideo"
    const val LINK_SAVE_XXVIDEO = "save_xxvideo"
    const val FIRST_PORNHUB = "f_pornhub"
    const val LINK_SAVE_PORNHUB = "save_pornhub"

    //    public static final String KEY_FIRST_REQUEST = "KEY_FIRST_REQUEST";
//    // AdsID
//    const val ADS_INTERSTITIAL_ID = "ca-app-pub-4253116256630907/1282541638"
//    const val ADS_BANNER_ID = "ca-app-pub-4253116256630907/7847949984"
//    const val ADS_NATIVE_ID = "ca-app-pub-4253116256630907/2404051616"
    // product
    const val ADS_INTERSTITIAL_ID = "ca-app-pub-5163983242900797/4130749999"
    const val ADS_BANNER_ID = "ca-app-pub-5163983242900797/5904541256"
    const val ADS_NATIVE_ID = "ca-app-pub-5163983242900797/4005136228"


    const val TWITTER_KEY = "oZRxdaTUVN9XWxf6CUU2bXvCH"
    const val TWITTER_SECRET = "RymjqFp9pdFmKHidR9VDaY7Mfbgk3MBdvvidPxyfsZotzbo8H0"

    const val KEY_SECRET = "@u\$pb9@k)qq^b@_mx2%h\$a0ss23r@!8*23i4mh(8x\$v=q)1%7r5j"

//    const val DESKTOP_USER_AGENT = "Mozilla/5.0 (Linux; Android 4.4.4; One Build/KTU84L.H4) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/33.0.0.0 Mobile Safari/537.36"
//    const val MOBILE_USER_AGENT = "Mozilla/5.0 (Linux; Android 4.4.4; One Build/KTU84L.H4) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/33.0.0.0 Mobile Safari/537.36"

    const val DESKTOP_USER_AGENT =
        "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36"
    const val MOBILE_USER_AGENT =
        "Mozilla/5.0 (Linux; Android 4.2.2; GT-I9505 Build/JDQ39) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.59 Mobile Safari/537.36"
    const val MOBILE_USER_AGENT_2 =
        "Mozilla/5.0 (Linux; Android 5.1.1; SM-N750K Build/LMY47X; ko-kr) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Mobile Safari/537.36 Puffin/6.0.8.15804AP"

    const val EXTRA_VIDEO_DETAIL = "extra_video_detail"
    const val PREFIX_NAME = ""

    const val PATH_VIDEO = "/DownloadVideo2022Pro/"
    const val PATH_VIDEO_OLD = "/DownloadVideo2019Pro/"

    const val PATH_IMAGE = "/DownloadVideo2022Pro/images/"
    const val PATH_AUDIO = "/DownloadVideo2022Pro/audios/"

    const val HEADER_AUTH_API = "Authorization: Token fbe3afa05465d599bb5643ccd60d6ada49d3b3fb"
    const val DlApisUrl3: String = "https://iphoting-yt-dl-api.herokuapp.com/api/info?url="
    const val DlApisUrl2: String = "https://dlphpapis.herokuapp.com/api/info?url="
    const val DlApisUrl1: String = "https://dlphpapis21.herokuapp.com/api/info?url="


    const val KunApisUrl =
        "https://dev-dot-ytdl-dot-green-soft.appspot.com/api/youtube-dl/info?url="

    const val SECRET_KEY = "@u\$pb9@k)qq^b@_mx2%h\$a0ss23r@!8*23i4mh(8x\$v=q)1%7r5j"
    const val PATH_YOUTUBE_1 = "youtube.com"
    const val PATH_YOUTUBE_2 = "youtu.be"


    const val HTTPS = "https://"
    const val ABOUT = "about:"
    const val SCHEME_HOMEPAGE = "${ABOUT}home"
    const val SCHEME_BOOKMARKS = "${ABOUT}bookmarks"


    const val NO_PROXY = 0
    const val PROXY_ORBOT = 1
    const val PROXY_I2P = 2
    const val PROXY_MANUAL = 3

    const val UTF8 = "UTF-8"
    const val FOLDER = "folder://"
    const val FILE = "file://"
    const val HTTP = "http://"

    const val THEME = "theme"

    private lateinit var mBaseDir: File

    fun setConstants() {
        mBaseDir =
            File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}$PATH_VIDEO")
        if (!mBaseDir.exists()) {
            val check = mBaseDir.mkdirs()
            if (check) {
                Log.e("ttt", "setConstants: create folder OK")
            } else {
                Log.e("ttt", "setConstants: can't create folder")
            }
        } else {
            Log.e("ttt", "setConstants: folder exists")
        }
    }

    fun getExternalFile(): File {
        if (!mBaseDir.exists()) {
            val check = mBaseDir.mkdirs()
            if (check) {
                return mBaseDir
            }
        }
        return mBaseDir
    }

    const val PREF_COPY_DATA = "PREF_COPY_DATA"

    object Alarm {
        const val PREF_TIME_PUSH = "PREF_TIME_PUSH"
    }

    object LinkOpen {
        const val TOP_BUZZ_STORE = "com.ss.android.article.topbuzzvideo"
        const val TOP_BUZZ = "https://www.buzzvideo.com/"
        const val TWITTER_STORE = "com.twitter.android"
        const val TWITTER = "https://twitter.com/"
        const val FACEBOOK_STORE = "com.facebook.katana"
        const val FACEBOOK = "https://m.facebook.com"
        const val TIKTOK_STORE = "com.ss.android.ugc.trill"
        const val TIKTOK = "https://www.tiktok.com/"
        const val INSTAGRAM_STORE = "com.instagram.android"
        const val INSTAGRAM = "https://www.instagram.com/"
        const val LINKEDIN_STORE = "com.linkedin.android"
        const val LINKEDIN = "https://www.linkedin.com/"
        const val DAILYMOTION_STORE = "com.dailymotion.dailymotion"
        const val DAILYMOTION = "https://www.dailymotion.com/"
        const val VLIVE_STORE = "com.naver.vapp"
        const val VLIVE = "https://www.vlive.tv/"
        const val IMDB_STORE = "com.imdb.mobile"
        const val IMDB = "https://www.imdb.com/"
        const val VIMEO_STORE = "com.vimeo.android.videoapp"
        const val VIMEO = "https://vimeo.com/watch"
    }

    const val KEY_GO_TO_WEB = "Website"

}
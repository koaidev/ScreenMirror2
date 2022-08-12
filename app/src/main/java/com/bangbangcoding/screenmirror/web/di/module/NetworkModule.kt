package com.bangbangcoding.screenmirror.web.di.module

import android.app.Application
import com.bangbangcoding.screenmirror.BuildConfig
import com.bangbangcoding.screenmirror.web.data.remote.service.ConfigService
import com.bangbangcoding.screenmirror.web.data.remote.service.FeedbackService
import com.bangbangcoding.screenmirror.web.data.remote.service.InstagramService
import com.bangbangcoding.screenmirror.web.data.remote.service.VideoService
import com.bangbangcoding.screenmirror.web.ui.search.suggestions.RequestFactory
import com.bangbangcoding.screenmirror.web.utils.FileUtils
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {

    companion object {
        private const val DATA_URL = "https://generaldata-79d9b.firebaseapp.com/youtube-dl/"

        //        private const val DATA_URL = "https://ff78294e-bc4c-406f-8ad3-cc4b358e9d15.mock.pstmn.io/"
        private const val YTDL_URL = "http://youtube-dl-android.herokuapp.com/api/"

        private const val FEEDBACK_URL = "https://feedback4me-dot-green-soft.appspot.com/api/"

        private const val BASE_URL = "https://dev-dot-ytdl-dot-green-soft.appspot.com/api/"


        private const val BASE_URL_INSTAGRAM = "https://www.instagram.com/"
    }


    @Singleton
    @Provides
    fun providesSuggestionsRequestFactory(cacheControl: CacheControl): RequestFactory =
        object : RequestFactory {

            override fun createSuggestionsRequest(httpUrl: HttpUrl, encoding: String): Request {
                return Request.Builder().url(httpUrl)
                    .addHeader("Accept-Charset", encoding)
                    .cacheControl(cacheControl)
                    .build()
            }

        }

    private fun buildOkHttpClient(application: Application): OkHttpClient {
        val intervalDay = TimeUnit.DAYS.toSeconds(1)
        val client = OkHttpClient.Builder()
        var logging = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            //                client.interceptors().add(new LoggingInterceptors());
            client.interceptors().add(logging)
        }
        val suggestionsCache = File(application.cacheDir, "suggestion_responses")
        client.interceptors().add(
            object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val requestBuilder: Request.Builder = chain.request().newBuilder()
                    //                    requestBuilder.addHeader("Authorization", MovieApi.TOKEN);
//                    requestBuilder.addHeader("language", LANGUAGE)
                    requestBuilder.addHeader("Content-Type", "application/json")
                    requestBuilder.addHeader(
                        "Authorization",
                        "Token fbe3afa05465d599bb5643ccd60d6ada49d3b3fb"
                    )
                    requestBuilder.addHeader(
                        "X-CSRFToken",
                        "csACLagNzYBkCBldpwhHNEUKz8oCyW6xXpii7kEHtrbuwKOCDMW5nv9Xtl6xXBIt"
                    )
                    return chain.proceed(requestBuilder.build())
                }
            })
        val rewriteCacheControlInterceptor = Interceptor { chain ->
            val originalResponse = chain.proceed(chain.request())
            originalResponse.newBuilder()
                .header("cache-control", "max-age=$intervalDay, max-stale=$intervalDay")
                .build()
        }

        return client
            .addNetworkInterceptor(rewriteCacheControlInterceptor)
            .connectTimeout(30L, TimeUnit.SECONDS)
            .writeTimeout(30L, TimeUnit.SECONDS)
            .readTimeout(30L, TimeUnit.SECONDS)
            .cache(Cache(suggestionsCache, FileUtils.megabytesToBytes(1)))
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(application: Application): OkHttpClient = buildOkHttpClient(application)

    @Provides
    @Singleton
    fun provideConfigService(okHttpClient: OkHttpClient): ConfigService = Retrofit.Builder()
//        .baseUrl(DATA_URL)
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(ConfigService::class.java)

    @Provides
    @Singleton
    fun provideVideoService(okHttpClient: OkHttpClient): VideoService = Retrofit.Builder()
        .baseUrl(YTDL_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(VideoService::class.java)

    @Provides
    @Singleton
    fun provideInstagram(okHttpClient: OkHttpClient): InstagramService = Retrofit.Builder()
        .baseUrl(BASE_URL_INSTAGRAM)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(InstagramService::class.java)

    @Provides
    @Singleton
    fun provideFeedbackService(okHttpClient: OkHttpClient): FeedbackService = Retrofit.Builder()
        .baseUrl(FEEDBACK_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(FeedbackService::class.java)
}

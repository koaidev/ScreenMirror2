package com.bangbangcoding.screenmirror.web.html.homepage

import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.html.HtmlPageFactory
import com.bangbangcoding.screenmirror.web.html.jsoup.*
import android.app.Application
import com.bangbangcoding.screenmirror.web.ui.search.SearchEngineProvider
import com.bangbangcoding.screenmirror.web.utils.Constants.FILE
import com.bangbangcoding.screenmirror.web.utils.Constants.UTF8
import io.reactivex.Single
import java.io.File
import java.io.FileWriter
import javax.inject.Inject

/**
 * A factory for the home page.
 */
class HomePageFactory @Inject constructor(
    private val application: Application,
    private val searchEngineProvider: SearchEngineProvider,
    private val homePageReader: HomePageReader

) : HtmlPageFactory{

    private val title = application.getString(R.string.home)

    override fun buildPage(): Single<String> = Single
        .just(searchEngineProvider.provideSearchEngine())
        .map { (iconUrl, queryUrl, _) ->
            parse(homePageReader.provideHtml()) andBuild {
                title { title }
                charset { UTF8 }
//                body {
//                    id("image_url") { attr("src", iconUrl) }
//                    tag("script") {
//                        html(
//                            html()
//                                .replace("\${BASE_URL}", queryUrl)
//                                .replace("&", "\\u0026")
//                        )
//                    }
//                }
            }
        }
        .map { content -> Pair(createHomePage(), content) }
        .doOnSuccess { (page, content) ->
            FileWriter(page, false).use {
                it.write(content)
            }
        }
        .map { (page, _) -> "$FILE$page" }

    fun createHomePage() = File(application.filesDir, FILENAME)

    companion object {
        const val FILENAME = "homepage.html"
    }
}

package com.bangbangcoding.screenmirror.web.ui.search.suggestions

import com.bangbangcoding.screenmirror.R
import android.app.Application
import com.bangbangcoding.screenmirror.web.data.local.SearchSuggestion
import com.bangbangcoding.screenmirror.web.logger.Logger
import com.bangbangcoding.screenmirror.web.utils.Constants.UTF8
import com.bangbangcoding.screenmirror.web.utils.ext.map
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

/**
 * The search suggestions provider for the DuckDuckGo search engine.
 */
class DuckSuggestionsModel(
    httpClient: OkHttpClient,
    requestFactory: RequestFactory,
    application: Application,
    logger: Logger
) : BaseSuggestionsModel(httpClient, requestFactory, UTF8, logger) {

    private val searchSubtitle = application.getString(R.string.suggestion)

    // https://duckduckgo.com/ac/?q={query}
    override fun createQueryUrl(query: String, language: String): HttpUrl = HttpUrl.Builder()
        .scheme("https")
        .host("duckduckgo.com")
        .encodedPath("/ac/")
        .addEncodedQueryParameter("q", query)
        .build()

    @Throws(Exception::class)
    override fun parseResults(responseBody: ResponseBody): List<SearchSuggestion> {
        return JSONArray(responseBody.string())
            .map { it as JSONObject }
            .map { it.getString("phrase") }
            .map { SearchSuggestion("$searchSubtitle \"$it\"", it) }
    }

}

package com.bangbangcoding.screenmirror.web.ui.search

import android.app.Application
import com.bangbangcoding.screenmirror.web.logger.Logger
import com.bangbangcoding.screenmirror.web.ui.preference.UserPreferences
import com.bangbangcoding.screenmirror.web.ui.search.engine.AskSearch
import com.bangbangcoding.screenmirror.web.ui.search.engine.BaiduSearch
import com.bangbangcoding.screenmirror.web.ui.search.engine.BaseSearchEngine
import com.bangbangcoding.screenmirror.web.ui.search.engine.BingSearch
import com.bangbangcoding.screenmirror.web.ui.search.engine.CustomSearch
import com.bangbangcoding.screenmirror.web.ui.search.engine.DuckLiteSearch
import com.bangbangcoding.screenmirror.web.ui.search.engine.DuckSearch
import com.bangbangcoding.screenmirror.web.ui.search.engine.GoogleSearch
import com.bangbangcoding.screenmirror.web.ui.search.engine.NaverSearch
import com.bangbangcoding.screenmirror.web.ui.search.engine.StartPageMobileSearch
import com.bangbangcoding.screenmirror.web.ui.search.engine.StartPageSearch
import com.bangbangcoding.screenmirror.web.ui.search.engine.YahooSearch
import com.bangbangcoding.screenmirror.web.ui.search.engine.YandexSearch
import com.bangbangcoding.screenmirror.web.ui.search.suggestions.BaiduSuggestionsModel
import com.bangbangcoding.screenmirror.web.ui.search.suggestions.DuckSuggestionsModel
import com.bangbangcoding.screenmirror.web.ui.search.suggestions.GoogleSuggestionsModel
import com.bangbangcoding.screenmirror.web.ui.search.suggestions.NaverSuggestionsModel
import com.bangbangcoding.screenmirror.web.ui.search.suggestions.NoOpSuggestionsRepository
import com.bangbangcoding.screenmirror.web.ui.search.suggestions.RequestFactory
import com.bangbangcoding.screenmirror.web.ui.search.suggestions.SuggestionsRepository
import okhttp3.OkHttpClient
import javax.inject.Inject

/**
 * The model that provides the search engine based
 * on the user's preference.
 */
class SearchEngineProvider @Inject constructor(
    private val userPreferences: UserPreferences,
    private val httpClient: OkHttpClient,
    private val requestFactory: RequestFactory,
    private val application: Application,
    private val logger: Logger
) {

    /**
     * Provide the [SuggestionsRepository] that maps to the user's current preference.
     */
    fun provideSearchSuggestions(): SuggestionsRepository =
        when (userPreferences.searchSuggestionChoice) {
            0 -> NoOpSuggestionsRepository()
            1 -> GoogleSuggestionsModel(httpClient, requestFactory, application, logger)
            2 -> DuckSuggestionsModel(httpClient, requestFactory, application, logger)
            3 -> BaiduSuggestionsModel(httpClient, requestFactory, application, logger)
            4 -> NaverSuggestionsModel(httpClient, requestFactory, application, logger)
            else -> GoogleSuggestionsModel(httpClient, requestFactory, application, logger)
        }

    /**
     * Provide the [BaseSearchEngine] that maps to the user's current preference.
     */
    fun provideSearchEngine(): BaseSearchEngine =
        when (userPreferences.searchChoice) {
//            0 -> CustomSearch(userPreferences.searchUrl)
            0 -> GoogleSearch()
            3 -> AskSearch()
            1 -> BingSearch()
            4 -> YahooSearch()
//            5 -> StartPageSearch()
//            6 -> StartPageMobileSearch()
            2 -> DuckSearch()
//            8 -> DuckLiteSearch()
            5 -> BaiduSearch()
            6 -> YandexSearch()
            7 -> NaverSearch()
            else -> GoogleSearch()
        }

    /**
     * Return the serializable index of of the provided [BaseSearchEngine].
     */
    fun mapSearchEngineToPreferenceIndex(searchEngine: BaseSearchEngine): Int =
        when (searchEngine) {
//            is CustomSearch -> 0
            is GoogleSearch -> 0
            is AskSearch -> 3
            is BingSearch -> 1
            is YahooSearch -> 4
//            is StartPageSearch -> 5
//            is StartPageMobileSearch -> 6
            is DuckSearch -> 2
//            is DuckLiteSearch -> 8
            is BaiduSearch -> 5
            is YandexSearch -> 6
            is NaverSearch -> 7
            else -> throw UnsupportedOperationException("Unknown search engine provided: " + searchEngine.javaClass)
        }

    /**
     * Provide a list of all supported search engines.
     */
    fun provideAllSearchEngines(): List<BaseSearchEngine> = listOf(
        CustomSearch(userPreferences.searchUrl),
        GoogleSearch(),
        AskSearch(),
        BingSearch(),
        YahooSearch(),
        StartPageSearch(),
        StartPageMobileSearch(),
        DuckSearch(),
        DuckLiteSearch(),
        BaiduSearch(),
        YandexSearch(),
        NaverSearch()
    )

    fun provideSearchEngines(): List<BaseSearchEngine> = listOf(
        GoogleSearch(),
        BingSearch(),
        DuckSearch(),
        AskSearch(),
        YahooSearch(),
        BaiduSearch(),
        YandexSearch(),
        NaverSearch()
    )

}

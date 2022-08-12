package com.bangbangcoding.screenmirror.web.ui.search.engine

import com.bangbangcoding.screenmirror.R

/**
 * A custom search engine.
 */
class CustomSearch(queryUrl: String) : BaseSearchEngine(
    "file:///android_asset/lightning.png",
    queryUrl,
    R.string.search_engine_custom
)

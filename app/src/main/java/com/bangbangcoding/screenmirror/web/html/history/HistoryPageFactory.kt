package com.bangbangcoding.screenmirror.web.html.history

import android.app.Application
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.data.history.HistoryRepository
import com.bangbangcoding.screenmirror.web.html.HtmlPageFactory
import com.bangbangcoding.screenmirror.web.html.ListPageReader
import com.bangbangcoding.screenmirror.web.html.jsoup.*
import com.bangbangcoding.screenmirror.web.utils.Constants.FILE
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File
import java.io.FileWriter
import javax.inject.Inject

class HistoryPageFactory @Inject constructor(
    private val listPageReader: ListPageReader,
    private val application: Application,
    private val historyRepository: HistoryRepository
) : HtmlPageFactory {

    private val title = application.getString(R.string.txt_menu_history)

    override fun buildPage(): Single<String> = historyRepository
        .lastHundredVisitedHistoryEntries()
        .map { list ->
            parse(listPageReader.provideHtml()) andBuild {
                title { title }
                body {
                    val repeatedElement = id("repeated").removeElement()
                    id("content") {
                        list.forEach {
                            appendChild(repeatedElement.clone {
                                tag("a") { attr("href", it.url) }
                                id("title") { text(it.title) }
                                id("url") { text(it.url) }
                            })
                        }
                    }
                }
            }
        }
        .map { content -> Pair(createHistoryPage(), content) }
        .doOnSuccess { (page, content) ->
            FileWriter(page, false).use { it.write(content) }
        }
        .map { (page, _) -> "$FILE$page" }


    fun deleteHistoryPage(): Completable = Completable.fromAction {
        with(createHistoryPage()) {
            if (exists()) {
                delete()
            }
        }
    }

    private fun createHistoryPage() = File(application.filesDir, FILENAME)

    companion object {
        const val FILENAME = "history.html"
    }

}

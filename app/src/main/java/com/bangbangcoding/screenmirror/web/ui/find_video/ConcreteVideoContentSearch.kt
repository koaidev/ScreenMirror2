package com.bangbangcoding.screenmirror.web.ui.find_video

class ConcreteVideoContentSearch(
    private val startInspectingURL: () -> Unit,
    private val finishedInspectingURL: (finishedAll: Boolean) -> Unit,
    private val videoFound: (size: String?,
                       type: String?,
                       link: String?,
                       name: String?,
                       page: String?,
                       chunked: Boolean,
                       website: String?) -> Unit
) : VideoContentSearch() {

    override fun onStartInspectingURL() {
        startInspectingURL.invoke()
    }

    override fun onFinishedInspectingURL(finishedAll: Boolean) {
        finishedInspectingURL.invoke(finishedAll)
    }

    override fun onVideoFound(
        size: String?,
        type: String?,
        link: String?,
        name: String?,
        page: String?,
        chunked: Boolean,
        website: String?
    ) {
        videoFound.invoke(size, type, link, name, page, chunked, website)
    }
}
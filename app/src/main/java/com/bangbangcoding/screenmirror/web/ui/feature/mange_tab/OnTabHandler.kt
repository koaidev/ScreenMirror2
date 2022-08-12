package com.bangbangcoding.screenmirror.web.ui.feature.mange_tab

interface OnTabHandler {
    /**
     * Called when a tab has been added.
     */
    fun tabAdded()

    /**
     * Called when a tab has been removed.
     *
     * @param position the position of the tab that has been removed.
     */
    fun tabRemoved(position: Int)

    /**
     * Called when a tab's metadata has been changed.
     *
     * @param position the position of the tab that has been changed.
     */
    fun tabChanged(position: Int)

    /**
     * Called when the tabs are completely initialized for the first time.
     */
    fun tabsInitialized()
}
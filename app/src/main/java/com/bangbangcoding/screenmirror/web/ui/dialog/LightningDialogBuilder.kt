package com.bangbangcoding.screenmirror.web.ui.dialog

import android.app.Activity
import android.content.ClipboardManager
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.controller.UIController
import com.bangbangcoding.screenmirror.web.data.bookmark.BookmarkRepository
import com.bangbangcoding.screenmirror.web.data.downloads.DownloadsRepository
import com.bangbangcoding.screenmirror.web.data.history.HistoryRepository
import com.bangbangcoding.screenmirror.web.data.local.Bookmark
import com.bangbangcoding.screenmirror.web.data.local.asFolder
import com.bangbangcoding.screenmirror.databinding.PopupWindowActionHistoryBinding
import com.bangbangcoding.screenmirror.web.di.module.DatabaseScheduler
import com.bangbangcoding.screenmirror.web.di.module.MainScheduler
import com.bangbangcoding.screenmirror.web.html.bookmark.BookmarkPageFactory
import com.bangbangcoding.screenmirror.web.utils.Constants.HTTP
import com.bangbangcoding.screenmirror.web.utils.IntentUtils
import com.bangbangcoding.screenmirror.web.utils.ScreenUtil
import com.bangbangcoding.screenmirror.web.utils.UrlUtils
import com.bangbangcoding.screenmirror.web.utils.Utils
import com.bangbangcoding.screenmirror.web.utils.ext.copyToClipboard
import io.reactivex.Scheduler
import javax.inject.Inject

/**
 * A builder of various dialogs.
 */
class LightningDialogBuilder @Inject constructor(
    private val bookmarkManager: BookmarkRepository,
    private val downloadsModel: DownloadsRepository,
    private val historyModel: HistoryRepository,
    private val clipboardManager: ClipboardManager,
    @DatabaseScheduler private val databaseScheduler: Scheduler,
    @MainScheduler private val mainScheduler: Scheduler
) {

    enum class NewTab {
        FOREGROUND,
        BACKGROUND,
        INCOGNITO
    }

    /**
     * Show the appropriated dialog for the long pressed link. It means that we try to understand
     * if the link is relative to a bookmark or is just a folder.
     *
     * @param activity used to show the dialog
     * @param url      the long pressed url
     */
    fun showLongPressedDialogForBookmarkUrl(
            activity: Activity,
            uiController: UIController,
            url: String
    ) {
        if (UrlUtils.isBookmarkUrl(url)) {
            // TODO hacky, make a better bookmark mechanism in the future
            val uri = url.toUri()
            val filename = requireNotNull(uri.lastPathSegment) { "Last segment should always exist for bookmark file" }
            val folderTitle = filename.substring(0, filename.length - BookmarkPageFactory.FILENAME.length - 1)
            showBookmarkFolderLongPressedDialog(activity, uiController, folderTitle.asFolder())
        } else {
            bookmarkManager.findBookmarkForUrl(url)
                    .subscribeOn(databaseScheduler)
                    .observeOn(mainScheduler)
                    .subscribe { historyItem ->
                        // TODO: 6/14/17 figure out solution to case where slashes get appended to root urls causing the item to not exist
                        showLongPressedDialogForBookmarkUrl(activity, uiController, historyItem) {}
                    }
        }
    }

    fun showLongPressedDialogForBookmarkUrl(
        activity: Activity,
        uiController: UIController,
        entry: Bookmark.Entry,
        isReload : (Boolean) -> Unit
    ) = BrowserDialog.show(activity, R.string.txt_menu_bookmarks,
            DialogItem(title = R.string.dialog_open_new_tab) {
                uiController.handleNewTab(NewTab.FOREGROUND, entry.url)
                isReload.invoke(true)
            },
            DialogItem(title = R.string.dialog_open_background_tab) {
                uiController.handleNewTab(NewTab.BACKGROUND, entry.url)
                isReload.invoke(true)
            },
            DialogItem(title = R.string.share) {
                IntentUtils(activity).shareUrl(entry.url, entry.title)
            },
            DialogItem(title = R.string.dialog_copy_link) {
                clipboardManager.copyToClipboard(entry.url)
            },
            DialogItem(title = R.string.dialog_remove_bookmark) {
                bookmarkManager.deleteBookmark(entry)
                        .subscribeOn(databaseScheduler)
                        .observeOn(mainScheduler)
                        .subscribe { success ->
                            if (success) {
                                uiController.handleBookmarkDeleted(entry)
                            }
                        }
                isReload.invoke(true)
            },
            DialogItem(title = R.string.dialog_edit_bookmark) {
                showEditBookmarkDialog(activity, uiController, entry)
            })

    /**
     * Show the appropriated dialog for the long pressed link.
     *
     * @param activity used to show the dialog
     * @param url      the long pressed url
     */
    // TODO allow individual downloads to be deleted.
    fun showLongPressedDialogForDownloadUrl(
            activity: Activity,
            uiController: UIController,
            url: String
    ) = BrowserDialog.show(activity, R.string.action_downloads,
            DialogItem(title = R.string.dialog_delete_all_downloads) {
                downloadsModel.deleteAllDownloads()
                        .subscribeOn(databaseScheduler)
                        .observeOn(mainScheduler)
                        .subscribe(uiController::handleDownloadDeleted)
            })

    private fun showEditBookmarkDialog(
            activity: Activity,
            uiController: UIController,
            entry: Bookmark.Entry
    ) {
        val editBookmarkDialog = AlertDialog.Builder(activity)
        editBookmarkDialog.setTitle(R.string.txt_menu_add_bookmark)
        val dialogLayout = View.inflate(activity, R.layout.dialog_edit_bookmark, null)
        val getTitle = dialogLayout.findViewById<EditText>(R.id.bookmark_title)
        getTitle.setText(entry.title)
        val getUrl = dialogLayout.findViewById<EditText>(R.id.bookmark_url)
        getUrl.setText(entry.url)
        val getFolder = dialogLayout.findViewById<AutoCompleteTextView>(R.id.bookmark_folder)
        getFolder.setHint(R.string.folder)
        getFolder.setText(entry.folder.title)

        bookmarkManager.getFolderNames()
                .subscribeOn(databaseScheduler)
                .observeOn(mainScheduler)
                .subscribe { folders ->
                    val suggestionsAdapter = ArrayAdapter(activity,
                            android.R.layout.simple_dropdown_item_1line, folders)
                    getFolder.threshold = 1
                    getFolder.setAdapter(suggestionsAdapter)
                    editBookmarkDialog.setView(dialogLayout)
                    editBookmarkDialog.setPositiveButton(activity.getString(R.string.txt_ok)) { _, _ ->
                        val editedItem = Bookmark.Entry(
                                title = getTitle.text.toString(),
                                url = getUrl.text.toString(),
                                folder = getFolder.text.toString().asFolder(),
                                position = entry.position
                        )
                        bookmarkManager.editBookmark(entry, editedItem)
                                .subscribeOn(databaseScheduler)
                                .observeOn(mainScheduler)
                                .subscribe(uiController::handleBookmarksChange)
                    }
                    val dialog = editBookmarkDialog.show()
                    BrowserDialog.setDialogSize(activity, dialog)
                }
    }

    fun showBookmarkFolderLongPressedDialog(
            activity: Activity,
            uiController: UIController,
            folder: Bookmark.Folder
    ) = BrowserDialog.show(activity, R.string.folder,
            DialogItem(title = R.string.txt_title_rename) {
                showRenameFolderDialog(activity, uiController, folder)
            },
            DialogItem(title = R.string.delete) {
                bookmarkManager.deleteFolder(folder.title)
                        .subscribeOn(databaseScheduler)
                        .observeOn(mainScheduler)
                        .subscribe {
                            uiController.handleBookmarkDeleted(folder)
                        }
            })

    private fun showRenameFolderDialog(
            activity: Activity,
            uiController: UIController,
            folder: Bookmark.Folder
    ) = BrowserDialog.showEditText(activity,
            R.string.txt_title_rename,
            R.string.txt_title,
            folder.title,
            R.string.txt_ok) { text ->
        if (!TextUtils.isEmpty(text)) {
            val oldTitle = folder.title
            bookmarkManager.renameFolder(oldTitle, text)
                    .subscribeOn(databaseScheduler)
                    .observeOn(mainScheduler)
                    .subscribe(uiController::handleBookmarksChange)
        }
    }

    fun showLongPressedHistoryLinkDialog(
            activity: Activity,
            uiController: UIController,
            url: String
    ) = BrowserDialog.show(activity, R.string.txt_menu_history,
            DialogItem(title = R.string.dialog_open_new_tab) {
                uiController.handleNewTab(NewTab.FOREGROUND, url)
            },
            DialogItem(title = R.string.dialog_open_background_tab) {
                uiController.handleNewTab(NewTab.BACKGROUND, url)
            },
            DialogItem(title = R.string.share) {
                IntentUtils(activity).shareUrl(url, null)
            },
            DialogItem(title = R.string.txt_menu_copy_link) {
                clipboardManager.copyToClipboard(url)
            },
            DialogItem(title = R.string.dialog_remove_from_history) {
                historyModel.deleteHistoryEntry(url)
                        .subscribeOn(databaseScheduler)
                        .observeOn(mainScheduler)
                        .subscribe(uiController::handleHistoryChange)
            })

    fun showActionHistoryLinkDialog(
        activity: Activity,
        uiController: UIController,
        url: String,
        v: View,
        finish: () -> Unit
    ) {
        val popupWindow = PopupWindow()
        val binding = PopupWindowActionHistoryBinding.inflate(LayoutInflater.from(activity), null, false)
//        val layout: View =
//            LayoutInflater.from(activity).inflate(R.layout.popup_window_action_history, null)
        binding.tvOpenNewTab.setOnClickListener {
            uiController.handleNewTab(NewTab.FOREGROUND, url)
            popupWindow.dismiss()
            finish.invoke()
        }
        binding.tvShare.setOnClickListener {
            IntentUtils(activity).shareUrl(url, null)
            popupWindow.dismiss()
            finish.invoke()
        }
        binding.tvCopyLink.setOnClickListener {
            clipboardManager.copyToClipboard(url)
            popupWindow.dismiss()
            finish.invoke()
        }
        binding.tvDelete.setOnClickListener {
            historyModel.deleteHistoryEntry(url)
                .subscribeOn(databaseScheduler)
                .observeOn(mainScheduler)
                .subscribe(uiController::handleHistoryChange)
            popupWindow.dismiss()
            finish.invoke()
        }
        popupWindow.contentView = binding.root
        popupWindow.width = Utils.dpToPixels(activity, 190)
        popupWindow.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        popupWindow.isFocusable = true
        val currentLocation: IntArray = checkShowAtLocation(activity, v)
        if (currentLocation[0] == 0 && currentLocation[1] == 0) {
            popupWindow.showAsDropDown(v)
        } else {
            popupWindow.showAtLocation(v, Gravity.TOP, currentLocation[0], currentLocation[1])
        }
    }

    private fun checkShowAtLocation(activity: Activity, anchor: View): IntArray {
        val screenLocation = IntArray(2)
        anchor.getLocationOnScreen(screenLocation)
        val height: Int = ScreenUtil.getHeightScreen(activity)
        if (height > Utils.dpToPixels(activity, 250) + screenLocation[1]) {
            return intArrayOf(0, 0)
        }
        screenLocation[1] -= Utils.dpToPixels(activity, 192)
        return screenLocation
    }

    // TODO There should be a way in which we do not need an activity reference to dowload a file
    fun showLongPressImageDialog(
            activity: Activity,
            uiController: UIController,
            url: String,
            userAgent: String
    ) = BrowserDialog.show(activity, url.replace(HTTP, ""),
            DialogItem(title = R.string.dialog_open_new_tab) {
                uiController.handleNewTab(NewTab.FOREGROUND, url)
            },
            DialogItem(title = R.string.dialog_open_background_tab) {
                uiController.handleNewTab(NewTab.BACKGROUND, url)
            },
            DialogItem(title = R.string.share) {
                IntentUtils(activity).shareUrl(url, null)
            },
            DialogItem(title = R.string.txt_menu_copy_link) {
                clipboardManager.copyToClipboard(url)
            },
            DialogItem(title = R.string.dialog_download_image) {
                Toast.makeText(activity,R.string.cannot_download_image, Toast.LENGTH_SHORT).show()
//                downloadHandler.onDownloadStart(activity, userPreferences, url, userAgent, "attachment", null, "")
            })

    fun showLongPressLinkDialog(
            activity: Activity,
            uiController: UIController,
            url: String
    ) = BrowserDialog.show(activity, url,
            DialogItem(title = R.string.dialog_open_new_tab) {
                uiController.handleNewTab(NewTab.FOREGROUND, url)
            },
            DialogItem(title = R.string.dialog_open_background_tab) {
                uiController.handleNewTab(NewTab.BACKGROUND, url)
            },
            DialogItem(title = R.string.share) {
                IntentUtils(activity).shareUrl(url, null)
            },
            DialogItem(title = R.string.dialog_copy_link) {
                clipboardManager.copyToClipboard(url)
            })

}

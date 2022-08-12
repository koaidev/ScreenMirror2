package com.bangbangcoding.screenmirror.web.ui.feature.bookmarks

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.controller.UIController
import com.bangbangcoding.screenmirror.web.data.local.Bookmark
import com.bangbangcoding.screenmirror.databinding.FragmentBookmarksBinding
import com.bangbangcoding.screenmirror.web.di.module.DatabaseScheduler
import com.bangbangcoding.screenmirror.web.di.module.MainScheduler
import com.bangbangcoding.screenmirror.web.di.module.NetworkScheduler
import com.bangbangcoding.screenmirror.web.favicon.FaviconModel
import com.bangbangcoding.screenmirror.web.ui.base.BaseFragment
import com.bangbangcoding.screenmirror.web.ui.dialog.LightningDialogBuilder
import com.bangbangcoding.screenmirror.web.ui.home.TabsManager
import com.bangbangcoding.screenmirror.web.ui.preference.UserPreferences
import com.bangbangcoding.screenmirror.web.utils.ThemeUtils
import io.reactivex.Scheduler
import javax.inject.Inject

class BookmarksFragment : BaseFragment(), BookmarksView {

    @Inject
    lateinit var faviconModel: FaviconModel

    @Inject
    @DatabaseScheduler
    internal lateinit var databaseScheduler: Scheduler

    @Inject
    @NetworkScheduler
    internal lateinit var networkScheduler: Scheduler

    @Inject
    @MainScheduler
    internal lateinit var mainScheduler: Scheduler

    // Preloaded images
    private var webPageBitmap: Bitmap? = null
    private var folderBitmap: Bitmap? = null

    private lateinit var viewModel: BookmarksViewModel

    // Dialog builder
    @Inject
    internal lateinit var bookmarksDialogBuilder: LightningDialogBuilder

    @Inject
    internal lateinit var userPreferences: UserPreferences


    private lateinit var uiController: UIController
    private var iconColor: Int = 0
    private var scrollIndex: Int = 0
    private var isIncognito: Boolean = false
    private var darkTheme: Boolean = false


    // Adapter
    private var bookmarkAdapter: BookmarksAdapter? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val uiModel = BookmarkUiModel()

    private var _binding: FragmentBookmarksBinding? = null

    private val binding get() = _binding!!

    companion object {
        private const val TAG = "BookmarksFragment"

        private const val INCOGNITO_MODE = "$TAG.INCOGNITO_MODE"

        fun newInstance(isIncognito: Boolean) = BookmarksFragment().apply {
            arguments = Bundle().apply {
                putBoolean(BookmarksFragment.INCOGNITO_MODE, isIncognito)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        uiController = requireContext() as UIController
        isIncognito = arguments?.getBoolean(INCOGNITO_MODE, false) == true

        darkTheme = userPreferences.useTheme != 0 || isIncognito
        webPageBitmap =
            ThemeUtils.getThemedBitmap(requireContext(), R.drawable.ic_webpage, darkTheme)
        folderBitmap = ThemeUtils.getThemedBitmap(requireContext(), R.drawable.ic_folder, darkTheme)
        iconColor = ThemeUtils.getIconThemeColor(requireContext(), darkTheme)
        viewModel =
            ViewModelProvider(this, viewModelFactory)[BookmarksViewModel::class.java]

        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.e("ttt", "Bookmarks onResume: ")
        if (bookmarkAdapter != null) {
            viewModel.loadBookmarks(null)
        }
    }

    override fun observerViewModel() {
        viewModel.isShowBookmarks.observe(viewLifecycleOwner, {
            setBookmarkDataSet(it, false)
        })

        viewModel.isUpdateBookmarks.observe(viewLifecycleOwner, {
            val activity = activity ?: return@observe
            if (it) {
                binding.ivBookmark.setImageResource(R.drawable.ic_bookmark)
                binding.ivBookmark.setColorFilter(
                    ThemeUtils.getAccentColor(activity),
                    PorterDuff.Mode.SRC_IN
                )
            } else {
                binding.ivBookmark.setImageResource(R.drawable.ic_bookmark)
                binding.ivBookmark.setColorFilter(iconColor, PorterDuff.Mode.DARKEN)
            }
        })
    }

    private fun getTabsManager(): TabsManager = uiController.getTabModel()

    private fun setBookmarkDataSet(items: List<Bookmark>, animate: Boolean) {
        bookmarkAdapter?.updateItems(items.map { BookmarkViewModel(it) })
//        val resource = if (uiModel.isCurrentFolderRoot()) {
//            R.drawable.ic_search_22
//        } else {
//            R.drawable.ic_action_back
//        }
//
//        if (animate) {
//            binding.ivBookmark.let {
//                val transition = AnimationUtils.createRotationTransitionAnimation(it, resource)
//                it.startAnimation(transition)
//            }
//        } else {
//            binding.ivBookmark.setImageResource(resource)
//        }
    }

    override fun setupUI() {
        bookmarkAdapter = BookmarksAdapter(
            faviconModel,
            folderBitmap!!,
            webPageBitmap!!,
            networkScheduler,
            mainScheduler,
            this::handleItemLongPress,
            this::handleItemClick
        )

        binding.recycler.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = bookmarkAdapter
        }

        viewModel.loadBookmarks(null)
    }


    private fun handleItemLongPress(bookmark: Bookmark): Boolean {
        (context as Activity?)?.let {
            when (bookmark) {
                is Bookmark.Folder -> bookmarksDialogBuilder.showBookmarkFolderLongPressedDialog(
                    it,
                    uiController,
                    bookmark
                )
                is Bookmark.Entry -> bookmarksDialogBuilder.showLongPressedDialogForBookmarkUrl(
                    it,
                    uiController,
                    bookmark
                ) { isReload ->
                    if (isReload) {
                        reloadData()
                    }
                }
            }
        }
        return true
    }


    private fun handleItemClick(bookmark: Bookmark) = when (bookmark) {
        is Bookmark.Folder -> {
            scrollIndex =
                (binding.recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            viewModel.loadBookmarks(bookmark.title)
        }
        is Bookmark.Entry -> uiController.bookmarkItemClicked(bookmark)
    }

    override fun navigateBack() {
        if (uiModel.isCurrentFolderRoot()) {
            uiController.onBackButtonPressed()
        } else {
            viewModel.loadBookmarks(null)
//            bookmark_list_view?.layoutManager?.scrollToPosition(scrollIndex)
        }
    }

    override fun handleUpdatedUrl(url: String) {
        viewModel.updateBookmarkIndicator(url)
        val folder = uiModel.currentFolder
        viewModel.loadBookmarks(folder)
    }

    override fun handleBookmarkDeleted(bookmark: Bookmark) {
//        TODO("Not yet implemented")
//        viewModel.(null)
        if (bookmarkAdapter != null) {
            viewModel.loadBookmarks(null)
        }
    }

    override fun reloadData() {
        if (bookmarkAdapter != null) {
            viewModel.loadBookmarks(null)
        }
    }
}
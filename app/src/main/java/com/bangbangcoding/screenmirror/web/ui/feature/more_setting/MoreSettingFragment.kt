package com.bangbangcoding.screenmirror.web.ui.feature.more_setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.lifecycle.ViewModelProvider
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.data.history.HistoryRepository
import com.bangbangcoding.screenmirror.databinding.FragmentMoreSettingBinding
import com.bangbangcoding.screenmirror.web.di.module.DatabaseScheduler
import com.bangbangcoding.screenmirror.web.di.module.MainScheduler
import com.bangbangcoding.screenmirror.web.ui.base.BaseFragment
import com.bangbangcoding.screenmirror.web.ui.dialog.BrowserDialog
import com.bangbangcoding.screenmirror.web.ui.dialog.DialogItem
import com.bangbangcoding.screenmirror.web.ui.preference.UserPreferences
import com.bangbangcoding.screenmirror.web.ui.search.SearchEngineProvider
import com.bangbangcoding.screenmirror.web.ui.search.engine.BaseSearchEngine
import com.bangbangcoding.screenmirror.web.ui.search.engine.CustomSearch
import com.bangbangcoding.screenmirror.web.utils.WebUtils
import com.bangbangcoding.screenmirror.web.utils.ext.snackbar
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class MoreSettingFragment : BaseFragment() {

    companion object {
        fun newInstance() = MoreSettingFragment()
    }

    private var _binding: FragmentMoreSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MoreSettingViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    @Inject
    lateinit var searchEngineProvider: SearchEngineProvider

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var historyRepository: HistoryRepository

    @Inject
    @DatabaseScheduler
    lateinit var databaseScheduler: Scheduler

    @Inject
    @MainScheduler
    lateinit var mainScheduler: Scheduler
    private var disposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this, viewModelFactory)[MoreSettingViewModel::class.java]

        _binding = FragmentMoreSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupUI() {
        binding.tvSearch.text = getString(searchEngineProvider.provideSearchEngine().titleRes)
        binding.toolbarLayout.tvTitle.text = getString(R.string.text_more_setting)
        binding.switchSavePassword.isChecked = userPreferences.savePasswordsEnabled
        binding.switchSync.isChecked = userPreferences.syncToGallery
        binding.switchBlockAds.isChecked = userPreferences.adBlockEnabled
        binding.tvStatusBlockAds.text = if (userPreferences.adBlockEnabled)
            getString(R.string.txt_turn_on) else getString(R.string.txt_turn_off)

        binding.switchBlockAds.setOnCheckedChangeListener { _, isChecked ->
            userPreferences.adBlockEnabled = isChecked
            binding.tvStatusBlockAds.text =
                if (isChecked) getString(R.string.txt_turn_on) else getString(R.string.txt_turn_off)
        }

        binding.tvBlockAds.setOnClickListener {
            binding.switchBlockAds.isChecked = !binding.switchBlockAds.isChecked

        }

        binding.switchSync.setOnCheckedChangeListener { _, isChecked ->
            userPreferences.syncToGallery = isChecked
        }

        binding.tvSyncGallery.setOnClickListener {
            binding.switchSync.isChecked = !binding.switchSync.isChecked
        }

        binding.switchSavePassword.setOnCheckedChangeListener { _, isChecked ->
            userPreferences.savePasswordsEnabled = isChecked
        }

        binding.tvSavePassword.setOnClickListener {
            binding.switchSavePassword.isChecked = !binding.switchSavePassword.isChecked
        }

        binding.toolbarLayout.ivBackToolbar.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.tvSearchEngine.setOnClickListener {
            showSearchProviderDialog()
        }

        binding.tvClearCache.setOnClickListener {
            clearCache()
        }

        binding.tvClearHistory.setOnClickListener {
            clearHistoryDialog()
        }

        binding.tvClearCookies.setOnClickListener {
            clearCookiesDialog()
        }
    }

    private fun clearCookiesDialog() {
        BrowserDialog.showPositiveNegativeDialog(
            activity = requireActivity(),
            title = R.string.title_clear_cookies,
            message = R.string.dialog_cookies,
            positiveButton = DialogItem(title = R.string.yes) {
                disposable = Completable.fromAction {
                    WebUtils.clearCookies()
                }
                    .subscribeOn(databaseScheduler)
                    .observeOn(mainScheduler)
                    .subscribe {
                        requireActivity().snackbar(R.string.message_cookies_cleared)
                    }
            },
            negativeButton = DialogItem(title = R.string.no) {},
            onCancel = {}
        )
    }

    private fun clearHistory(): Completable = Completable.fromAction {
        WebUtils.clearHistory(requireContext(), historyRepository, databaseScheduler)
    }

    private fun clearHistoryDialog() {
        BrowserDialog.showPositiveNegativeDialog(
            activity = requireActivity(),
            title = R.string.title_clear_history,
            message = R.string.dialog_history,
            positiveButton = DialogItem(title = R.string.yes) {
                disposable = clearHistory()
                    .subscribeOn(databaseScheduler)
                    .observeOn(mainScheduler)
                    .subscribe {
                        requireActivity().snackbar(R.string.message_clear_history)
                    }
            },
            negativeButton = DialogItem(title = R.string.no) {},
            onCancel = {}
        )
    }

    private fun clearCache() {
        WebView(requireActivity()).apply {
            clearCache(true)
            destroy()
        }
        requireActivity().snackbar(R.string.message_cache_cleared)
    }

    private fun convertSearchEngineToString(searchEngines: List<BaseSearchEngine>): Array<CharSequence> =
        searchEngines.map { getString(it.titleRes) }.toTypedArray()

    private fun showSearchProviderDialog() {
        BrowserDialog.showCustomDialog(requireActivity()) {
            setTitle(resources.getString(R.string.txt_search_engine))

            val searchEngineList = searchEngineProvider.provideSearchEngines()

            val chars = convertSearchEngineToString(searchEngineList)

            val n = userPreferences.searchChoice

            setSingleChoiceItems(chars, n) { _, which ->
                val searchEngine = searchEngineList[which]
                binding.tvSearch.text = getString(searchEngine.titleRes)

                // Store the search engine preference
                val preferencesIndex =
                    searchEngineProvider.mapSearchEngineToPreferenceIndex(searchEngine)
                userPreferences.searchChoice = preferencesIndex

                if (searchEngine is CustomSearch) {
                    // Show the URL picker
                    showCustomSearchDialog(searchEngine)
                } else {
                    // Set the new search engine summary
                    //no-op
                }
            }
            setPositiveButton(R.string.dialog_ok, null)
        }
    }

    private fun showCustomSearchDialog(customSearch: CustomSearch) {
        activity?.let {
            BrowserDialog.showEditText(
                it,
                R.string.search_engine_custom,
                R.string.search_engine_custom,
                userPreferences.searchUrl,
                R.string.txt_ok
            ) { searchUrl ->
                userPreferences.searchUrl = searchUrl
            }
        }
    }

    private fun getSearchEngineSummary(baseSearchEngine: BaseSearchEngine): String {
        return if (baseSearchEngine is CustomSearch) {
            baseSearchEngine.queryUrl
        } else {
            getString(baseSearchEngine.titleRes)
        }
    }
}

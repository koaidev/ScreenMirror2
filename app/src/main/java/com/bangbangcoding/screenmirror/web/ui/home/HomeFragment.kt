package com.bangbangcoding.screenmirror.web.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.http.SslError
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.webkit.*
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.FragmentHomeBinding
import com.bangbangcoding.screenmirror.web.controller.UIController
import com.bangbangcoding.screenmirror.web.ui.SearchBoxModel
import com.bangbangcoding.screenmirror.web.ui.base.BaseFragment
import com.bangbangcoding.screenmirror.web.ui.dialog.BrowserDialog
import com.bangbangcoding.screenmirror.web.ui.dialog.DialogItem
import com.bangbangcoding.screenmirror.web.ui.feature.feedback.FeedbackFragment
import com.bangbangcoding.screenmirror.web.ui.preference.UserPreferences
import com.bangbangcoding.screenmirror.web.utils.*
import java.util.*
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory


class HomeFragment : BaseFragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var uiController: UIController

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var topPageAdapter: TopPageAdapter

    @Inject
    lateinit var userPreferences: UserPreferences

    private var stackUrl: Stack<String> = Stack()

//    private lateinit var suggestionAdapter: SuggestionAdapter

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @Inject
    lateinit var searchBoxModel: SearchBoxModel
    private var currentPage: String = ""
    private var idFB: String = ""
    private lateinit var defaultSSLSF: SSLSocketFactory

    private var isRefreshButton: Boolean = false
    private lateinit var ivRefresh: ImageView
    private lateinit var actionBar: ActionBar
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var clTop : ConstraintLayout
    private lateinit var clSearch : ConstraintLayout
    private lateinit var progressDialogGeneratingLink: ProgressDialog

    private var isLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defaultSSLSF = HttpsURLConnection.getDefaultSSLSocketFactory()
        uiController = requireActivity() as UIController
        setHasOptionsMenu(true)
    }

    private fun showHeaderActionBar() {
        if (binding.rvTopPages.visibility == View.VISIBLE) {
            clTop.visibility = View.VISIBLE
            actionBar.show()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI(view: View) {
        if (view !is ConstraintLayout) {
            view.setOnTouchListener { v: View?, _: MotionEvent? ->
                v?.let {
                    AppUtil.hideSoftKeyboard(v)
                    autoCompleteTextView.clearFocus()
                    showHeaderActionBar()
                }
                false
            }
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                if (innerView is EditText) {
                    continue
                }
                setupUI(innerView)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        AlarmUtil.saveAlarm(requireContext(), true)
        homeViewModel =
            ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(binding.root)
        homeViewModel.start()
        currentPage = binding.webview.url ?: ""
    }

    private val requestOpenSetting = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Do something after user returned from app settings screen, like showing a Toast.
            result.data?.let {
                val link = it.getStringExtra("Website")
                link?.let { l ->
                    isLoading = true
                    homeViewModel.loadPage(l)
                }
            }
        }
    }

    fun updateUrl(url: String, title: String?, isLoading: Boolean) {
        autoCompleteTextView.setText(searchBoxModel.getDisplayContent(url, title, isLoading))
        binding.webview.onResume()
        homeViewModel.loadPage(url)
        binding.webview.visibility = View.VISIBLE
        binding.rvTopPages.visibility = View.GONE
    }

    fun getCurrentPage(): String {
        return currentPage
    }

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun setupUI() {
        setDataActionBar()
        binding.etSearch.setOnTouchListener { view, motionEvent ->
            view.requestFocus()
            AppUtil.showSoftKeyboard(view)
            clTop.animate()
                .translationY(Utils.dpToPixels(requireContext(), -48).toFloat())
                .alpha(0.0f)
                .setDuration(2000L)
                .setListener(@SuppressLint("ClickableViewAccessibility")
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        clTop.visibility = View.GONE
                        actionBar.hide()
                    }
                })
            true
        }

        binding.webview.visibility = View.GONE
        binding.webview.webChromeClient = browserWebChromeClient
        binding.webview.webViewClient = browserWebViewClient
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.settings.domStorageEnabled = true
        binding.webview.settings.allowUniversalAccessFromFileURLs = true
        binding.webview.settings.javaScriptCanOpenWindowsAutomatically = true


        binding.rvTopPages.apply {
            this.layoutManager = GridLayoutManager(requireContext(), 4)
            this.adapter = topPageAdapter
        }

        binding.etSearch.setOnClickListener {
            isRefreshButton = true
            homeViewModel.changeFocus(true)
        }
//        autoCompleteTextView.setAdapter(suggestionAdapter)
        binding.etSearch.addTextChangedListener(onInputChangeListener)

        binding.etSearch.setOnKeyListener(onKeyPressEnterListener)
        binding.tvGetFeedback.setOnClickListener {
            startActivity(
                IntentActivity.getIntent(requireContext(), FeedbackFragment::class.java)
            )
        }
    }

    private fun launchToFragment(fragment: Class<out Fragment>, link: String) {
        requestOpenSetting.launch(
            IntentActivity.getIntent(
                requireContext(),
                fragment,
                Bundle().apply {
                    putString("link", link)
                }
            )
        )
    }

    override fun observerViewModel() {
        homeViewModel.isShowPage.observe(viewLifecycleOwner) {
            Log.e("ttt", "observer isShowPage: $it")
            if (!it) {
                binding.progressBar.visibility = View.GONE

                binding.webview.clearView()

            }

            binding.webview.visibility = if (it) View.VISIBLE else View.GONE
            binding.rvTopPages.visibility = if (it) View.GONE else View.VISIBLE
            binding.tvGetFeedback.visibility = if (it) View.GONE else View.VISIBLE
        }
//        homeViewModel.isExpandedAppbar.observe(viewLifecycleOwner, {
//            Log.e("ttt", "observer isExpandedAppbar: $it")
//            binding.toolbar.visibility = if (it) View.GONE else View.VISIBLE
//        })
        homeViewModel.isFocus.observe(viewLifecycleOwner) {
            Log.e("ttt", "observer isFocus: $it")
            changeFocusSearch(it)
        }
        homeViewModel.textInput.observe(viewLifecycleOwner) {
            Log.e("ttt", "observer textInput: $it")
            if (it.isNotEmpty() && currentPage != it) {
                currentPage = it
                binding.etSearch.setText(it)
                autoCompleteTextView.setText(it)

                if (it.contains("about:blank")) {
                    currentPage = ""
                    autoCompleteTextView.setText("")
                }
            }
        }

        homeViewModel.progress.observe(viewLifecycleOwner) {
            Log.e("ttt", "observer progress: $it")
            binding.progressBar.progress = it
        }
        homeViewModel.isShowProgress.observe(viewLifecycleOwner) {
            Log.e("ttt", "observer isShowProgress: $it")
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
//        homeViewModel.isLoadingVideoInfo.observe(viewLifecycleOwner) {
//            Log.e("ttt", "observer isLoadingVideoInfo: $it")
//            binding.loadingCircle.visibility = if (it) View.VISIBLE else View.GONE
//        }
        homeViewModel.pageUrl.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
//                Log.e("ttt", "observer pageUrl: $it")
                binding.webview.onResume()
                binding.webview.loadUrl(it)
            }
        }

        homeViewModel.listPages.observe(viewLifecycleOwner) {
            Log.e("ttt", "observer listPages: $it")
//            topPageAdapter.setData(it)
        }


        homeViewModel.finishLoadPage.observe(viewLifecycleOwner) {
            if (it) {
//                uiController.handleCanBack(isEnable = true)
            }
        }

        handleChangeFocusEvent()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        homeViewModel.stop()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        checkHasClipboard()
    }

    private fun checkHasClipboard() {
        ClipboardUtils.checkHasCopy(requireContext())?.let {
            PrefUtils.getString(requireContext(), Constants.PREF_COPY_DATA)?.let { data ->
                if (data == it) {
                    return
                } else {
                    PrefUtils.putString(requireContext(), Constants.PREF_COPY_DATA, it)
                }
            } ?: PrefUtils.putString(requireContext(), Constants.PREF_COPY_DATA, it)

            BrowserDialog.showPositiveNegativeDialog(
                requireActivity(),
                R.string.title_open_link_copy,
                R.string.path_link_copy,
                arrayOf(it),
                DialogItem(title = R.string.txt_ok, onClick = {
                    autoCompleteTextView.setText(it)
                    isLoading = true
                    homeViewModel.loadPage(it)
                }),
                DialogItem(title = R.string.dialog_cancel, onClick = {}),
                onCancel = {}
            )
        }
    }

    private val onInputChangeListenerEtSearch = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            val input = s.toString()
            homeViewModel.showSuggestions()
            homeViewModel.publishSubject.onNext(input)
            if (!isLoading) {
                homeViewModel.getHistoryEntry(input)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            //no-op
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //no-op
        }
    }

    private val onInputChangeListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            val input = s.toString()
//            homeViewModel.setTextInput(input)
            homeViewModel.showSuggestions()
            homeViewModel.publishSubject.onNext(input)
//            autoCompleteTextView.requestFocus()
            if (!isLoading) {
                homeViewModel.getHistoryEntry(input)
            }
            if (input.isNotEmpty()) {
                ivRefresh.setImageResource(R.drawable.ic_close_24dp)
//                homeViewModel.getHistoryEntry(input)
            } else {
                ivRefresh.setImageResource(R.drawable.ic_refresh_gray_24dp)
//                homeViewModel.getHistoryEntry("")
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            //no-op
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //no-op
        }
    }

    private fun showHeaderActionBar1(status : Int = 0) {
        if (status == 0) {
            clTop.visibility = View.VISIBLE
            clSearch.visibility = View.GONE
            actionBar.show()
        } else {
            clTop.visibility = View.GONE
            clSearch.visibility = View.VISIBLE
            actionBar.show()
        }
    }

    private val onKeyPressEnterListener = View.OnKeyListener { v, keyCode, _ ->
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            changeFocusSearch(false)
            isLoading = true
            binding.webview.onResume()
            if(v.id == R.id.et_search) {
                showHeaderActionBar1(1)
                binding.clWebSearch.visibility = View.GONE
            }
            homeViewModel.loadPage((v as EditText).text.toString())
            return@OnKeyListener true
        }
        return@OnKeyListener false
    }

    private val browserWebChromeClient = object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView?, title: String?) {
            Log.e("ttt--", "onReceivedTitle: ${view?.url} ++ $title")
            super.onReceivedTitle(view, title)
            uiController.updateHistory(title, view?.url!!)
        }

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            Log.e("ttt--", "onProgressChanged: ${view.url} ++ $newProgress")
            homeViewModel.setProgress(newProgress)
            super.onProgressChanged(view, newProgress)
        }
    }

    private fun onInspectingUrl() {
        Log.e("ttt", "onInspectingUrl:")
        Utils.disableSSLCertificateChecking()
    }

    private val browserWebViewClient = object : WebViewClient() {
        @SuppressLint("WebViewClientOnReceivedSslError")
        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            super.onReceivedSslError(view, handler, error)
            handler?.proceed()
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            Log.e("ttt----->", "onPageStarted: ")
//            homeViewModel.setTextInput(url)
            homeViewModel.startPage(view.url ?: "")
//            uiController.updateHistory(view.title, url)
            super.onPageStarted(view, url, favicon)
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            Log.e("ttt----->", "shouldOverrideUrlLoading: ")
//            homeViewModel.setPageUrl(url)
            return super.shouldOverrideUrlLoading(view, url)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            Log.e("ttt----->", "shouldOverrideUrlLoading: ${view?.url} : request ${request}")
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onLoadResource(view: WebView, url: String) {
            homeViewModel.loadResource(view.url ?: "")
            val page = view.url ?: ""
            val title = view.title ?: ""
            Log.e("ttt----->", "onLoadResource: $page+++ $title+++ $url")
        }

        override fun onPageFinished(view: WebView, url: String) {
            Log.e("ttt----->", "onPageFinished: ")
            homeViewModel.finishPage(view.url ?: "")
            changeFocusSearch(false)
            isRefreshButton = true
            isLoading = false
        }
    }

    private fun handleChangeFocusEvent() {
        homeViewModel.changeFocusEvent.observe(viewLifecycleOwner, { isFocus ->
            isFocus?.let {
                isRefreshButton = !it
                changeFocusSearch(it)
            }
        })
    }

    private fun changeFocusSearch(isFocus : Boolean) {
        if (isFocus) {
            AppUtil.showSoftKeyboard(autoCompleteTextView)
            autoCompleteTextView.requestFocus()
            ivRefresh.setImageResource(R.drawable.ic_close_24dp)
        } else {
            AppUtil.hideSoftKeyboard(autoCompleteTextView)
            autoCompleteTextView.clearFocus()
            ivRefresh.setImageResource(R.drawable.ic_refresh_gray_24dp)
        }
    }

    fun onBackPressed() : Boolean {
        when {
            binding.webview.canGoBack() -> {
                val url = binding.webview.url
                Log.e("ttt", "onBackPressed: canGoBack $url")
                stackUrl.push(url)
                binding.webview.goBack()
                return binding.webview.canGoBack()
            }
            homeViewModel.isShowPage.value == true -> {
                val url = binding.webview.url
                Log.e("ttt", "onBackPressed: last-url : $url")
                stackUrl.push(url)
                homeViewModel.setIsShowPage(false)
                autoCompleteTextView.setText("")
                binding.etSearch.setText("")
                binding.webview.clearHistory()
                binding.webview.clearCache(true)
                return binding.webview.canGoBack()
            }
            else -> return false
        }
    }

    fun onNext(): Boolean {
        return when {
            !stackUrl.empty() -> {
                binding.webview.loadUrl(stackUrl.pop())
                !stackUrl.empty()
            }
            else -> {
                false
            }
        }
    }

    private fun setDataActionBar() {

        actionBar = requireNotNull((requireActivity() as AppCompatActivity).supportActionBar)
//        // set display options of the ActionBar
//        actionBar.setDisplayShowTitleEnabled(false)
//        actionBar.setDisplayShowHomeEnabled(false)
//        actionBar.setDisplayShowCustomEnabled(true)
//        actionBar.setCustomView(R.layout.layout_custom_toolbar)
//        actionBar.setBackgroundDrawable(ColorDrawable(Color.WHITE))


        val customView = actionBar.customView
        customView.layoutParams = customView.layoutParams.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.MATCH_PARENT
        }

        ivRefresh = customView.findViewById<ImageView>(R.id.ivRefresh)
        ivRefresh.setImageResource(R.drawable.ic_refresh_gray_24dp)
        ivRefresh.setOnClickListener {
            if (isRefreshButton) {
                binding.webview.loadUrl(currentPage)
            } else {
                autoCompleteTextView.setText("")
            }
        }
        clTop = customView.findViewById(R.id.clTop)
        clSearch = customView.findViewById(R.id.clSearch)

        // create the search EditText in the ToolBar
        autoCompleteTextView = customView.findViewById<AutoCompleteTextView>(R.id.et_search).apply {
            setOnKeyListener(onKeyPressEnterListener)
            addTextChangedListener(onInputChangeListener)
            setOnClickListener {
                homeViewModel.changeFocus(true)
            }

            onItemClickListener =
                AdapterView.OnItemClickListener { p0, p1, p2, p3 ->
//                    val search = suggestionAdapter.getItem(p2)
//                    if (search is Suggestion) {
//                        isLoading = true
//                        autoCompleteTextView.setText(search.url)
//                        autoCompleteTextView.clearFocus()
//                        AppUtil.hideSoftKeyboard(autoCompleteTextView)
//                        homeViewModel.loadPage(search.url)
//                    }
                }
        }
    }

    fun clearFocusSearch() {
        autoCompleteTextView.clearFocus()
    }

    fun refreshPage() {
//        if (binding.webview.canGoBack()) {
//        binding.webview.clearView()
        binding.webview.loadUrl("about:blank")
        autoCompleteTextView.setText("")
        autoCompleteTextView.clearFocus()
        binding.webview.stopLoading()
        binding.webview.onPause()
//        }

        val it = false
        binding.webview.visibility = if (it) View.VISIBLE else View.GONE
        binding.rvTopPages.visibility = if (it) View.GONE else View.VISIBLE
        binding.tvGetFeedback.visibility = if (it) View.GONE else View.VISIBLE
    }

}
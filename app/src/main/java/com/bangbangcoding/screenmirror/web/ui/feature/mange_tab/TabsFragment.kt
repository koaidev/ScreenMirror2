package com.bangbangcoding.screenmirror.web.ui.feature.mange_tab

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.controller.UIController
import com.bangbangcoding.screenmirror.databinding.FragmentTabsBinding
import com.bangbangcoding.screenmirror.web.ui.base.BaseDialogFragment
import com.bangbangcoding.screenmirror.web.ui.feature.TabViewState
import com.bangbangcoding.screenmirror.web.ui.home.TabsManager
import com.bangbangcoding.screenmirror.web.ui.home.TabsView
import com.bangbangcoding.screenmirror.web.ui.preference.UserPreferences
import com.bangbangcoding.screenmirror.web.ui.progress.TabsAdapter
import com.bangbangcoding.screenmirror.web.ui.widget.LightningView
import com.bangbangcoding.screenmirror.web.utils.Constants
import javax.inject.Inject

class TabsFragment : BaseDialogFragment(), TabsView {
    private var isIncognito: Boolean = false

    @Inject
    internal lateinit var userPreferences: UserPreferences
    private lateinit var handler: Handler
    private lateinit var uiController: UIController

    companion object {
        private const val IS_INCOGNITO = "IS_INCOGNITO"

        fun newInstance(isIncognito: Boolean) = TabsFragment().apply {
            this.arguments = Bundle().apply {
                this.putBoolean(IS_INCOGNITO, isIncognito)
            }
        }
    }

    private lateinit var viewModel: TabsViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var _binding: FragmentTabsBinding? = null
    private val binding get() = _binding!!

    private lateinit var tabsAdapter: TabsAdapter
    private var mInterstitialAd: InterstitialAd? = null
    private var isShowAd = false
    private var mTypeAds = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler(Looper.getMainLooper())
        setStyle(
            DialogFragment.STYLE_NORMAL, R.style.CustomDialogTheme
        )
        dialog?.window?.statusBarColor = Color.WHITE
        uiController = activity as UIController
        isIncognito = arguments?.getBoolean(IS_INCOGNITO, false) ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this, viewModelFactory)[TabsViewModel::class.java]

        _binding = FragmentTabsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupUI() {
        loadInterstitialAd(requireContext())
        val list = toViewModels(getTabsManager().allTabs.filter { it.isIncognito == isIncognito })

        if (isIncognito && list.isEmpty()) {
            binding.tvPrivateDesc.visibility = View.VISIBLE
        } else {
            binding.tvPrivateDesc.visibility = View.GONE
        }

        tabsAdapter = TabsAdapter(
            list,
            {
                onClickTabItem(it)
            }, {
                onRemove(it)
            })
        binding.recycler.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = tabsAdapter
        }

        binding.ivBack.setOnClickListener {
            dismiss()
            uiController.showActionBar()
        }
        binding.ivbAddNew.setOnClickListener {
            showInterstitial()
        }
    }

    private fun onClickTabItem(pos: Int) {
        uiController.tabClicked(pos)
        handler.postDelayed({
            dismiss()
        }, 180)
    }

    private fun onRemove(pos: Int) {
        val size = tabsAdapter.getTabsList().size
        if (tabsAdapter.getTabsList()[pos].isSelect) {
            if(size >= 2) {
                Log.e("ttt", "onRemove: $size")
                if (pos >= size - 2 && pos >= 1) {
                    tabsAdapter.getTabsList()[pos - 1].isSelect = true
                } else {
                    tabsAdapter.getTabsList()[pos + 1].isSelect = true
                }
            }
        }
        tabsAdapter.remove(pos)
        uiController.tabCloseClicked(pos)

        if (size == 1) {
            handler.postDelayed({
                dismiss()
            }, 180)
        }
    }

    private fun getTabsManager(): TabsManager = uiController.getTabModel()

    private fun toViewModels(tabs: List<LightningView>) = tabs.map(::TabViewState)

    fun addTab() {
        tabsAdapter.let {
            it.showTabs(toViewModels(getTabsManager().allTabs))
            binding.recycler.postDelayed(
                { binding.recycler.smoothScrollToPosition(it.itemCount - 1) }, 500
            )
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        handler.removeCallbacksAndMessages(null)
        super.onDismiss(dialog)
    }

    override fun tabAdded() {
//        TODO("Not yet implemented")
    }

    override fun tabRemoved(position: Int) {
//        TODO("Not yet implemented")
    }

    override fun tabChanged(position: Int) {
//        TODO("Not yet implemented")
    }

    override fun tabsInitialized() {
//        TODO("Not yet implemented")
    }

    private fun loadInterstitialAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, Constants.ADS_INTERSTITIAL_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    mInterstitialAd = interstitialAd
                    Log.i("TAG", "onAdLoaded")
                    interstitialAd.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null
                                Log.d("TAG", "The ad was dismissed.")
                                if (isShowAd) {
                                    openScreen()
                                }
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                // Called when fullscreen content failed to show.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null
                                Log.d("TAG", "The ad failed to show.")
                            }

                            override fun onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                Log.d("TAG", "The ad was shown.")
                                isShowAd = true
                            }
                        }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    Log.i("TAG", loadAdError.message)
                    mInterstitialAd = null
                    if (isShowAd) {
                        openScreen()
                    }
                }
            })
    }

    private fun showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (mInterstitialAd != null) {
            mInterstitialAd!!.show(requireActivity())
        } else {
            openScreen()
        }
    }

    private fun openScreen() {
        uiController.newTabButtonClicked()
//            uiController.showActionBar()
        handler.postDelayed({
            dismiss()
        }, 180)
    }
}

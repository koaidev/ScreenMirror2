package com.bangbangcoding.screenmirror.web.ui.feature.history

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewDatabase
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.DeleteHistoryDialogBinding
import com.bangbangcoding.screenmirror.databinding.DialogAddNewShortcutBinding
import com.bangbangcoding.screenmirror.web.controller.UIController
import com.bangbangcoding.screenmirror.web.data.local.HistoryEntry
import com.bangbangcoding.screenmirror.databinding.FragmentHistoryBinding
import com.bangbangcoding.screenmirror.db.model.entity.Shortcut
import com.bangbangcoding.screenmirror.web.ui.base.BaseDialogFragment
import com.bangbangcoding.screenmirror.web.ui.dialog.BrowserDialog
import com.bangbangcoding.screenmirror.web.ui.dialog.DialogItem
import com.bangbangcoding.screenmirror.web.ui.dialog.LightningDialogBuilder
import com.bangbangcoding.screenmirror.web.utils.AdsUtils
import com.bangbangcoding.screenmirror.web.utils.Utils
import com.bangbangcoding.screenmirror.web.utils.ext.snackbar
import com.google.android.gms.ads.AdRequest
import com.shashank.sony.fancytoastlib.FancyToast
import javax.inject.Inject

class HistoryFragment : BaseDialogFragment() {

    companion object {
        fun newInstance() = HistoryFragment()
    }

    private lateinit var viewModel: HistoryViewModel

    private lateinit var historyAdapter: HistoryAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    // Dialog builder
    @Inject
    internal lateinit var bookmarksDialogBuilder: LightningDialogBuilder

    private lateinit var uiController: UIController

    private var _binding: FragmentHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            DialogFragment.STYLE_NORMAL, R.style.CustomDialogTheme)
        dialog?.window?.statusBarColor = Color.WHITE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        uiController = requireActivity() as UIController
        viewModel =
            ViewModelProvider(this, viewModelFactory)[HistoryViewModel::class.java]

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupUI() {
        binding.toolbarLayout.tvTitle.text = getString(R.string.txt_menu_history)
        binding.toolbarLayout.ivBackToolbar.setOnClickListener {
            dismiss()
        }
        binding.toolbarLayout.ivAction.visibility = View.VISIBLE
        binding.toolbarLayout.ivAction.setOnClickListener {
            showDialogClearHistory()
        }

        historyAdapter = HistoryAdapter(
            this::handleItemLongPress,
            this::handleItemClick
        )

        binding.recycler.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = historyAdapter
        }

        viewModel.start()
        loadAds()

        AdsUtils.loadBanner(requireActivity(), binding.adContainerView)
    }


    private fun loadAds() {
        val adRequest1: AdRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest1)
    }

    private fun handleItemLongPress(history: HistoryEntry, v: View): Boolean {
        requireActivity().let {
            bookmarksDialogBuilder.showActionHistoryLinkDialog(
                it,
                uiController,
                history.url,
                v
            ) {
                dismiss()
            }
            return true
        }
    }

    private fun showDialogClearHistory() {
        val dialog = Dialog(requireContext(), R.style.MyDialog)
        val dialogBinding = DeleteHistoryDialogBinding.inflate(layoutInflater)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(dialogBinding.root)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        dialogBinding.btnNo.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnYes.setOnClickListener {
            viewModel.deleteAllHistory()
            dialog.dismiss()
        }
    }

    private fun handleItemClick(history: HistoryEntry) {
        uiController.updateUrl(history.url, true)
        uiController.historyItemClicked(history.url)
        dismiss()
//        requireActivity().supportFragmentManager.popBackStack()
//        uiController.showActionBar()
    }

    override fun observerViewModel() {
        viewModel.listHistory.observe(viewLifecycleOwner){
            if (it.isEmpty()) {
                binding.recycler.visibility = View.GONE
                binding.tvNoHistory.visibility = View.VISIBLE
            } else {
                binding.recycler.visibility = View.VISIBLE
                binding.tvNoHistory.visibility = View.GONE
                historyAdapter.updateItems(it)
            }
        }

        viewModel.isComplete.observe(viewLifecycleOwner) {
            if (it) {
                historyAdapter.updateItems(arrayListOf())
                binding.recycler.visibility = View.GONE
                binding.tvNoHistory.visibility = View.VISIBLE
                requireActivity().snackbar(R.string.message_clear_history)
                val webViewDatabase = WebViewDatabase.getInstance(requireContext())
                webViewDatabase.clearFormData()
                webViewDatabase.clearHttpAuthUsernamePassword()
                Utils.trimCache(requireContext())
            }
        }
    }

    fun clearHistoryEntry() {
        viewModel.start()
    }
}

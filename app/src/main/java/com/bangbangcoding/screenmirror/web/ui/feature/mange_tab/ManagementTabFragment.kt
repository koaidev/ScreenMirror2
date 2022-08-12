package com.bangbangcoding.screenmirror.web.ui.feature.mange_tab

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.controller.UIController
import com.bangbangcoding.screenmirror.databinding.FragmentManagementTabBinding
import com.bangbangcoding.screenmirror.web.ui.base.BaseFragment
import com.bangbangcoding.screenmirror.web.ui.feature.TabViewState
import com.bangbangcoding.screenmirror.web.ui.home.TabsManager
import com.bangbangcoding.screenmirror.web.ui.widget.LightningView
import com.bangbangcoding.screenmirror.web.utils.PrefUtils
import kotlinx.android.synthetic.main.fragment_guide.*
import javax.inject.Inject

class ManagementTabFragment : BaseFragment(), OnTabHandler {

    companion object {
        fun newInstance() = ManagementTabFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var uiController: UIController

    private lateinit var viewModel: ManagementTabViewModel

    private var _binding: FragmentManagementTabBinding? = null
    private val binding get() = _binding!!


    private lateinit var tabsPagerAdapter: TabsPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiController = activity as UIController
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this, viewModelFactory)[ManagementTabViewModel::class.java]

        _binding = FragmentManagementTabBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun setupUI() {
        super.setupUI()
        binding.viewPager.isUserInputEnabled = false
        tabsPagerAdapter = TabsPagerAdapter(this)
        binding.viewPager.adapter = tabsPagerAdapter

        binding.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            uiController.showActionBar()
        }
        binding.ivbAddNew.setOnClickListener {
            uiController.newTabButtonClicked()
            requireActivity().supportFragmentManager.popBackStack()
            uiController.showActionBar()
        }

        binding.tabLayout.addOnTabSelectedListener(onTabClick)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            val quantityTab = PrefUtils.getInt(requireContext(), PrefUtils.PREF_TAB_QUANTITY)
            val quantityTabPriv = PrefUtils.getInt(requireContext(), PrefUtils.PREF_TAB_QUANTITY_PRIV)
            val tabName = when (position) {
                0 -> {
                    "TAB $quantityTab"
                }
                else -> {
                    "PRIVATE TAB $quantityTabPriv"
                }
            }

            tab.text = tabName
        }.attach()

        toViewModels(getTabsManager().allTabs)
    }

    private fun toViewModels(tabs: List<LightningView>) = tabs.map(::TabViewState)

    private fun getTabsManager(): TabsManager = uiController.getTabModel()

    private val onTabClick = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab?.let {
                if(it.text!!.contains("PRIVATE TAB")) {
                    requireActivity().window.statusBarColor = Color.parseColor("#15172c")
                    requireActivity().window.decorView.systemUiVisibility = 0
                    binding.viewPager.setBackgroundColor(Color.parseColor("#565656"))
                    binding.tabLayout.setBackgroundColor(Color.parseColor("#15172c"))
                } else {
                    requireActivity().window.statusBarColor = Color.WHITE
                    requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    binding.viewPager.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                    binding.tabLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
//            TODO("Not yet implemented")
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
//            TODO("Not yet implemented")
        }

    }

    override fun tabAdded() {
        tabsPagerAdapter.f0.addTab()
        tabsPagerAdapter.f1.addTab()
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

}
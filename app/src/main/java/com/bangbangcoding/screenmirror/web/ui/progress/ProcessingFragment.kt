package com.bangbangcoding.screenmirror.web.ui.progress

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.ui.base.BaseFragment

class ProcessingFragment : BaseFragment() {

    companion object {
        fun newInstance() = ProcessingFragment()
    }

    private lateinit var viewModel: ProcessingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_processing, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProcessingViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
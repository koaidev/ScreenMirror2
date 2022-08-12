package com.bangbangcoding.screenmirror.web.ui.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.ui.base.BaseFragment

class VideosFragment : BaseFragment() {

    companion object {
        fun newInstance() = VideosFragment()
    }

    private lateinit var viewModel: VideosViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_videos, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(VideosViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
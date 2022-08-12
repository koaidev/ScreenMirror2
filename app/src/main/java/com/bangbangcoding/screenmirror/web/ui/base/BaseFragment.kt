package com.bangbangcoding.screenmirror.web.ui.base

import android.os.Bundle
import android.view.View
import dagger.android.support.DaggerFragment

/**
 * Created by ThaoBKN on 25/11/2021
 */

abstract class BaseFragment : DaggerFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observerViewModel()
    }

    open fun observerViewModel() {}

    open fun setupUI() {}

}
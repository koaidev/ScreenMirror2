package com.bangbangcoding.screenmirror.web.ui.base

import android.os.Bundle
import android.view.View
import dagger.android.support.DaggerDialogFragment

/**
 * Created by ThaoBKN on 25/11/2021
 */

abstract class BaseDialogFragment : DaggerDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observerViewModel()
    }

    open fun observerViewModel() {}

    open fun setupUI() {}

}
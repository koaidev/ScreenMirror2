package com.bangbangcoding.screenmirror.web.ui.studio.callback;

import android.view.View;

public interface OnRecyclerViewItemClickListener<Model> {
    void onItemClick(View view, Model model, int i);
}


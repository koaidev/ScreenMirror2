package com.bangbangcoding.screenmirror.web.ui.studio.callback;

import android.view.View;

public interface SingleSelectionItemClickListener<T> {
    void onItemClick(View view, T item);
}

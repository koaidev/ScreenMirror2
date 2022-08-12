package com.bangbangcoding.screenmirror.web.ui.studio.callback;

import android.view.View;

public interface SingleSelectionItemLongClickListener<T> {
    boolean onItemLongClick(View view, T item);
}

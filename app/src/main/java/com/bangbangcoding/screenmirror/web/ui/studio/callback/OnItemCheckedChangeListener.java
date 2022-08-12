package com.bangbangcoding.screenmirror.web.ui.studio.callback;

import android.widget.CompoundButton;

public interface OnItemCheckedChangeListener<T> {
    void onItemCheckedChanged(CompoundButton buttonView, boolean isChecked, T item);
}

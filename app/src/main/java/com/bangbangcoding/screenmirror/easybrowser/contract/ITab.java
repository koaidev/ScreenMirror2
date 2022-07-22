package com.bangbangcoding.screenmirror.easybrowser.contract;

import android.graphics.Bitmap;

import com.bangbangcoding.screenmirror.easybrowser.entity.bo.TabInfo;


public interface ITab {
    TabInfo provideTabInfo();

    boolean onBackPressed();

    void goForward();

    void gotoHomePage();

    void loadUrl(String url);

    Bitmap getTabPreview();
}

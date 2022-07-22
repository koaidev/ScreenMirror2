package com.bangbangcoding.screenmirror.easybrowser.contract;

import android.graphics.Bitmap;

import com.bangbangcoding.screenmirror.easybrowser.entity.bo.ClickInfo;
import com.bangbangcoding.screenmirror.easybrowser.entity.bo.TabInfo;


public interface IWebView {

    void setOnWebInteractListener(OnWebInteractListener listener);

    OnWebInteractListener getOnWebInteractListener();

    void loadUrl(String url);

    void goBack();

    boolean canGoBack();

    void goForward();

    boolean canGoForward();

    void releaseSession();

    void onResume();

    void onPause();

    void onDestroy();

    Bitmap capturePreview();


    interface OnWebInteractListener {
        void onPageTitleChange(TabInfo tabInfo);

        void onLongClick(ClickInfo clickInfo);
    }
}

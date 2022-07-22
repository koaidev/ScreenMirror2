package com.bangbangcoding.screenmirror.easybrowser.utils;

import android.content.Context;
import android.net.Uri;

import com.bangbangcoding.screenmirror.easybrowser.common.BrowserConst;
import com.bangbangcoding.screenmirror.easybrowser.contract.IBrowser;
import com.bangbangcoding.screenmirror.easybrowser.entity.bo.TabInfo;

public class TabHelper {

    public static void createTab(Context context, int titleResId, String uriStr, boolean backStage) {
        if (context == null) {
            return;
        }

        String title = context.getResources().getString(titleResId);
        createTab(context, title, uriStr, backStage);
    }

    public static void createTab(Context context, String title, String uriStr, boolean backStage) {
        IBrowser browser = null;
        IBrowser.ITabController tabController = null;
        if (context instanceof IBrowser) {
            browser = (IBrowser) context;
            tabController = (IBrowser.ITabController) browser.provideBrowserComponent(BrowserConst.TAB_COMPONENT);
        }
        if (tabController == null) {
            return;
        }
        if (StringUtils.isEmpty(uriStr)) {
            return;
        }
        Uri uri = null;
        try {
            uri = Uri.parse(uriStr);
        } catch (Exception e) {
            uri = null;
        }
        if (uri == null) {
            return;
        }
        TabInfo tabInfo = TabInfo.create(
                System.currentTimeMillis() + "",
                title,
                uri);
        tabController.onTabCreate(tabInfo, backStage);
    }
}

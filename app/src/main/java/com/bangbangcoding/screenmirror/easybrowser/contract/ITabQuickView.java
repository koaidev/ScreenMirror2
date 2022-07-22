package com.bangbangcoding.screenmirror.easybrowser.contract;

import com.bangbangcoding.screenmirror.easybrowser.entity.bo.TabInfo;

import java.util.List;


public interface ITabQuickView {

    interface Subject {
        void attach(Observer observer);

        void detach();

        List<TabInfo> provideInfoList();

        void updateTabInfo(TabInfo info);
    }

    interface Observer {
        void updateQuickView();
    }
}

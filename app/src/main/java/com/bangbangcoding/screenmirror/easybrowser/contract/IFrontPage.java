package com.bangbangcoding.screenmirror.easybrowser.contract;

import com.bangbangcoding.screenmirror.easybrowser.entity.dao.WebSite;

import java.util.List;


public interface IFrontPage {

    interface View {
        void showWebSite(List<WebSite> webSiteList);
    }

    interface Presenter {
        void getWebSite();
    }
}

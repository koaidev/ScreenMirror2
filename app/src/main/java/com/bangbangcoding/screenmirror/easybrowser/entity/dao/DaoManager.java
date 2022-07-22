package com.bangbangcoding.screenmirror.easybrowser.entity.dao;

public class DaoManager {
    public static void createDefaultSiteList(AppDatabase db) {
        try {
            WebSite baidu = new WebSite(null, "FaceBook", "https://www.facebook.com");
            WebSite bing = new WebSite(null, "Youtube", "https://youtube.com");
            WebSite qq = new WebSite(null, "Tiktok", "https://www.tiktok.com");
            WebSite w163net = new WebSite(null, "Vimeo", "https://www.vimeo.com");

            db.webSiteDao().insertAllWebSite(baidu, bing, qq, w163net);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

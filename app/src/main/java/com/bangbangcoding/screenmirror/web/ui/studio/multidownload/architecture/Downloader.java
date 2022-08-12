package com.bangbangcoding.screenmirror.web.ui.studio.multidownload.architecture;

public interface Downloader {

    interface OnDownloaderDestroyedListener {
        void onDestroyed(String key, Downloader downloader);
    }

    boolean isRunning();

    void start();

    void pause();

    void cancel();

    void onDestroy();

}

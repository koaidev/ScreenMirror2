package com.bangbangcoding.screenmirror.web.ui.studio.multidownload.architecture;


import com.bangbangcoding.screenmirror.web.ui.studio.multidownload.DownloadException;

public interface ConnectTask extends Runnable {

    interface OnConnectListener {
        void onConnecting();

        void onConnected(long time, long length, boolean isAcceptRanges);

        void onConnectPaused();

        void onConnectCanceled();

        void onConnectFailed(DownloadException de);
    }

    void pause();

    void cancel();

    boolean isConnecting();

    boolean isConnected();

    boolean isPaused();

    boolean isCanceled();

    boolean isFailed();

    @Override
    void run();
}

package com.bangbangcoding.screenmirror.web.ui.studio.multidownload.architecture;


import com.bangbangcoding.screenmirror.web.ui.studio.multidownload.DownloadException;

/**
 * Created by Aspsine on 2015/7/22.
 */
public interface DownloadTask extends Runnable {

    interface OnDownloadListener {
        void onDownloadConnecting();

        void onDownloadProgress(long finished, long length);

        void onDownloadCompleted();

        void onDownloadPaused();

        void onDownloadCanceled();

        void onDownloadFailed(DownloadException de);
    }

    void cancel();

    void pause();

    boolean isDownloading();

    boolean isComplete();

    boolean isPaused();

    boolean isCanceled();

    boolean isFailed();

    @Override
    void run();
}

package com.bangbangcoding.screenmirror.web.ui.studio;

import android.app.Activity;
import android.app.DownloadManager;

import androidx.annotation.NonNull;

import com.bangbangcoding.screenmirror.web.data.downloads.DownloadsRepository;
import com.bangbangcoding.screenmirror.web.di.DiExtensionsKt;
import com.bangbangcoding.screenmirror.web.di.module.DatabaseScheduler;
import com.bangbangcoding.screenmirror.web.di.module.MainScheduler;
import com.bangbangcoding.screenmirror.web.di.module.NetworkScheduler;
import com.bangbangcoding.screenmirror.web.logger.Logger;
import com.bangbangcoding.screenmirror.web.ui.studio.model.DownloadItem;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Scheduler;

@Singleton
public class DownloadHandler {

    @Inject
    DownloadsRepository downloadsRepository;
    @Inject
    DownloadManager downloadManager;
    @Inject
    @DatabaseScheduler
    Scheduler databaseScheduler;
    @Inject
    @NetworkScheduler
    Scheduler networkScheduler;
    @Inject
    @MainScheduler
    Scheduler mainScheduler;
    @Inject
    Logger logger;

    @Inject
    public DownloadHandler() {
        DiExtensionsKt.getInjector().inject(this);
    }


    public void onDownloadStart(@NonNull Activity context, @NonNull String url, @NonNull String name) {
    }

}

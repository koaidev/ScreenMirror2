package com.bangbangcoding.screenmirror.web.ui.studio.multidownload.core;


import com.bangbangcoding.screenmirror.web.ui.studio.multidownload.DownloadInfo;
import com.bangbangcoding.screenmirror.web.ui.studio.multidownload.architecture.DownloadTask;
import com.bangbangcoding.screenmirror.web.ui.studio.multidownload.db.DataBaseManager;
import com.bangbangcoding.screenmirror.web.ui.studio.multidownload.db.ThreadInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * Created by Aspsine on 2015/7/22.
 */
public class SingleDownloadTask extends DownloadTaskImpl {

    private DataBaseManager mDBManager;

    public SingleDownloadTask(DownloadInfo mDownloadInfo, ThreadInfo mThreadInfo, DataBaseManager dbManager, OnDownloadListener mOnDownloadListener) {
        super(mDownloadInfo, mThreadInfo, mOnDownloadListener);
        this.mDBManager = dbManager;
    }

    @Override
    protected void insertIntoDB(ThreadInfo info) {
        // don't support
        if (!mDBManager.exists(info.getTag(), info.getId())) {
            mDBManager.insert(info);
        }
    }

    @Override
    protected int getResponseCode() {
        return HttpURLConnection.HTTP_OK;
    }

    @Override
    protected void updateDB(ThreadInfo info) {
        // needn't Override this
//        mDBManager.update(info.getTag(), info.getId(), info.getFinished());
    }

    @Override
    protected Map<String, String> getHttpHeaders(ThreadInfo info) {
        // simply return null
        return null;
    }

    @Override
    protected RandomAccessFile getFile(File dir, String name, long offset) throws IOException {
        File file = new File(dir, name);
        RandomAccessFile raf = new RandomAccessFile(file, "rwd");
        raf.seek(0);
        return raf;
    }

    @Override
    protected String getTag() {
        return this.getClass().getSimpleName();
    }
}


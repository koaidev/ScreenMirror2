package com.bangbangcoding.screenmirror.web.ui.studio.multidownload;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.bangbangcoding.screenmirror.web.ui.studio.multidownload.architecture.DownloadResponse;
import com.bangbangcoding.screenmirror.web.ui.studio.multidownload.architecture.DownloadStatusDelivery;
import com.bangbangcoding.screenmirror.web.ui.studio.multidownload.architecture.Downloader;
import com.bangbangcoding.screenmirror.web.ui.studio.multidownload.core.DownloadResponseImpl;
import com.bangbangcoding.screenmirror.web.ui.studio.multidownload.core.DownloadStatusDeliveryImpl;
import com.bangbangcoding.screenmirror.web.ui.studio.multidownload.core.DownloaderImpl;
import com.bangbangcoding.screenmirror.web.ui.studio.multidownload.db.DataBaseManager;
import com.bangbangcoding.screenmirror.web.ui.studio.multidownload.db.ThreadInfo;
import com.bangbangcoding.screenmirror.web.ui.studio.multidownload.util.L;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadManager implements Downloader.OnDownloaderDestroyedListener {

    public static final String TAG = DownloadManager.class.getSimpleName();

    /**
     * singleton of DownloadManager
     */
    private static DownloadManager sDownloadManager;

    private DataBaseManager mDBManager;

    private Map<String, Downloader> mDownloaderMap;

    private DownloadConfiguration mConfig;

    private ExecutorService mExecutorService;

    private DownloadStatusDelivery mDelivery;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private OnCountDownloadingListener mListener;

    public static DownloadManager getInstance() {
        if (sDownloadManager == null) {
            synchronized (DownloadManager.class) {
                if (sDownloadManager == null) {
                    sDownloadManager = new DownloadManager();
                }
            }
        }
        return sDownloadManager;
    }

    public void setCountListener(OnCountDownloadingListener listener) {
        mListener = listener;
    }

    /**
     * private construction
     */
    private DownloadManager() {
        mDownloaderMap = new LinkedHashMap<String, Downloader>();
    }

    public void init(Context context) {
        init(context, new DownloadConfiguration());
    }

    public void init(Context context, @NonNull DownloadConfiguration config) {
        if (config.getThreadNum() > config.getMaxThreadNum()) {
            throw new IllegalArgumentException("thread num must < max thread num");
        }
        mConfig = config;
        mDBManager = DataBaseManager.getInstance(context);
        mExecutorService = Executors.newFixedThreadPool(mConfig.getMaxThreadNum());
        mDelivery = new DownloadStatusDeliveryImpl(mHandler);
    }

    @Override
    public void onDestroyed(final String key, Downloader downloader) {
        mHandler.post(() -> {
            if (mDownloaderMap.containsKey(key)) {
                mDownloaderMap.remove(key);
            }
            if (mListener != null) {
                mListener.onCountDownloading(mDownloaderMap.size());
            }
        });

    }

    public void download(DownloadRequest request, String tag, CallBack callBack) {
        final String key = createKey(tag);
        if (check(key)) {
            DownloadResponse response = new DownloadResponseImpl(mDelivery, callBack);
            Downloader downloader = new DownloaderImpl(request, response, mExecutorService, mDBManager, key, mConfig, this);
            mDownloaderMap.put(key, downloader);
            downloader.start();
            if (mListener != null) {
                mListener.onCountDownloading(mDownloaderMap.size());
            }
        }
    }

    public void


    pause(String tag) {
        String key = createKey(tag);
        if (mDownloaderMap.containsKey(key)) {
            Downloader downloader = mDownloaderMap.get(key);
            if (downloader != null) {
                if (downloader.isRunning()) {
                    downloader.pause();
                }
            }
            mDownloaderMap.remove(key);
        }
    }

    public void cancel(String tag) {
        String key = createKey(tag);
        if (mDownloaderMap.containsKey(key)) {
            Downloader downloader = mDownloaderMap.get(key);
            if (downloader != null) {
                downloader.cancel();
            }
            mDownloaderMap.remove(key);
        }
    }

    public void pauseAll() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (Downloader downloader : mDownloaderMap.values()) {
                    if (downloader != null) {
                        if (downloader.isRunning()) {
                            downloader.pause();
                        }
                    }
                }
            }
        });
    }


    public void cancelAll() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (Downloader downloader : mDownloaderMap.values()) {
                    if (downloader != null) {
                        if (downloader.isRunning()) {
                            downloader.cancel();
                        }
                    }
                }
            }
        });
    }

    public void delete(String tag) {
        String key = createKey(tag);
        mDBManager.delete(key);
    }

    public boolean isRunning(String tag) {
        String key = createKey(tag);
        if (mDownloaderMap.containsKey(key)) {
            Downloader downloader = mDownloaderMap.get(key);
            if (downloader != null) {
                return downloader.isRunning();
            }
        }
        return false;
    }

    public DownloadInfo getDownloadInfo(String tag) {
        String key = createKey(tag);
        List<ThreadInfo> threadInfos = mDBManager.getThreadInfos(key);
        DownloadInfo downloadInfo = null;
        if (!threadInfos.isEmpty()) {
            int finished = 0;
            int progress = 0;
            int total = 0;
            for (ThreadInfo info : threadInfos) {
                finished += info.getFinished();
                total += (info.getEnd() - info.getStart());
            }
            progress = (int) ((long) finished * 100 / total);
            downloadInfo = new DownloadInfo();
            downloadInfo.setFinished(finished);
            downloadInfo.setLength(total);
            downloadInfo.setProgress(progress);
        }
        return downloadInfo;
    }

    public List<DownloadInfo> getAllDownloads() {
        List<ThreadInfo> threadInfos = mDBManager.getAllThreadInfos();
        if (threadInfos.isEmpty()) {
            return new ArrayList<>();
        }
        Set<ThreadInfo> uniqueThreadInfos = new HashSet<>(threadInfos);
        List<DownloadInfo> downloadInfos = new ArrayList<>();
        for (ThreadInfo uniqueThreadInfo : uniqueThreadInfos) {
            List<ThreadInfo> threadInfosWithTag = getThreadInfosWithTag(threadInfos, uniqueThreadInfo.getTag());
            if (!threadInfosWithTag.isEmpty()) {
                int finished = 0;
                int total = 0;
                for (ThreadInfo info : threadInfosWithTag) {
                    finished = (int) (((long) finished) + info.getFinished());
                    total = (int) (((long) total) + (info.getEnd() - info.getStart()));
                }
                int progress = (int) ((((long) finished) * 100) / ((long) total));
                DownloadInfo downloadInfo = new DownloadInfo();
                downloadInfo.setFinished((long) finished);
                downloadInfo.setLength((long) total);
                downloadInfo.setProgress(progress);
                downloadInfo.setUri(uniqueThreadInfo.getUri());
                downloadInfo.setName(uniqueThreadInfo.getName());
                downloadInfos.add(downloadInfo);
            }
        }
        return downloadInfos;
    }

    private List<ThreadInfo> getThreadInfosWithTag(List<ThreadInfo> allThreadInfos, String tag) {
        List<ThreadInfo> threadInfosWithTag = new ArrayList<>();
        for (ThreadInfo threadInfo : allThreadInfos) {
            if (threadInfo.getTag().equals(tag)) {
                threadInfosWithTag.add(threadInfo);
            }
        }
        return threadInfosWithTag;
    }

    private boolean check(String key) {
        if (mDownloaderMap.containsKey(key)) {
            Downloader downloader = mDownloaderMap.get(key);
            if (downloader != null) {
                if (downloader.isRunning()) {
                    L.w("Task has been started!");
                    return false;
                } else {
                    throw new IllegalStateException("Downloader instance with same tag has not been destroyed!");
                }
            }
        }
        return true;
    }

    private static String createKey(String tag) {
        if (tag == null) {
            throw new NullPointerException("Tag can't be null!");
        }
        return String.valueOf(tag.hashCode());
    }

    public int getCurrentDownloadCount() {
        return this.mDownloaderMap.size();

    }
}

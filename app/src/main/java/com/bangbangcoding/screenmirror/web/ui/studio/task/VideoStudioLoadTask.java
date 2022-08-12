package com.bangbangcoding.screenmirror.web.ui.studio.task;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.bangbangcoding.screenmirror.web.ui.studio.model.VideoDetail;
import com.bangbangcoding.screenmirror.web.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoStudioLoadTask extends AsyncTask<Void, Void, List<VideoDetail>> {
    private final Context context;
    private final LoadCallback<List<VideoDetail>> callback;
    private int type;


    public VideoStudioLoadTask(@NonNull Context context, LoadCallback<List<VideoDetail>> callback, int type) {
        this.context = context;
        this.callback = callback;
        this.type = type;
    }

    @Override
    protected List<VideoDetail> doInBackground(Void... voids) {
        List<VideoDetail> VideoDetails = new ArrayList<>();

        File[] audios;
        if (type == 1) {
            audios = FileUtils.getVideoFolder(context).listFiles(file -> file.length() > 512 && (file.getPath().toLowerCase().endsWith(".mp4")));
        } else {
            audios = FileUtils.getVideoFolder(context).listFiles(file -> (file.getPath().toLowerCase().endsWith(".jpg") || file.getPath().toLowerCase().endsWith(".png")));
        }

        if (audios != null) {
            for (File file : audios) {
                if (isCancelled()) {
                    return null;
                }
//                VideoDetails.add(VideoHelper.getVideo(context, file.getPath()));
            }
        }
        return VideoDetails;
    }

    @Override
    protected void onPostExecute(List<VideoDetail> VideoDetails) {
        if (callback != null) {
            callback.onLoadCompleted(VideoDetails);
        }
    }
}

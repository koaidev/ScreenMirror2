package com.bangbangcoding.screenmirror.web.utils.downloader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Looper;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bangbangcoding.screenmirror.R;
import com.bangbangcoding.screenmirror.web.ui.model.apis_models.VideoInfo;
import com.bangbangcoding.screenmirror.web.ui.paste.DownloadVideosMain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TwitterVideoDownloader implements VideoDownloader {

    private final Context context;
    private final String videoURL;
    private DownloadVideosMain.OnDetectListener listener;
    public ProgressDialog pd;
    boolean isFromWeb = true;

    public TwitterVideoDownloader(Context context, String videoURL) {
        this.context = context;
        this.videoURL = videoURL;
    }

    public void setFromWeb(boolean isFromWeb) {
        this.isFromWeb = isFromWeb;
    }

    private static int ordinalIndexOf(String str, String substr, int n) {
        int pos = -1;
        do {
            pos = str.indexOf(substr, pos + 1);
        } while (n-- > 0 && pos != -1);
        return pos;
    }


    @Override
    public String getVideoId(String link) {
        if (link.contains("?")) {
            link = link.substring(link.indexOf("status"));
            link = link.substring(link.indexOf("/") + 1, link.indexOf("?"));
        } else {
            link = link.substring(link.indexOf("status"));
            link = link.substring(link.indexOf("/") + 1);
        }
        return link;
    }

    @Override
    public void downloadVideo() {
        if (!isFromWeb) {
            pd = new ProgressDialog(context);
            pd.setMessage(context.getResources().getString(R.string.txt_genarating_download_link));
            pd.setCancelable(false);
            pd.show();
        }
        if (
                videoURL.contains("twitter.com/home") ||
                        !videoURL.matches(".*twitter.com/.+")
        ) {
            if (!isFromWeb) {
                pd.dismiss();
            }
            return;
        }
        AndroidNetworking.post("https://twittervideodownloaderpro.com/twittervideodownloadv2/index.php")
                .addBodyParameter("id", getVideoId(videoURL))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.e("Hello", response.toString());
                        String url = response.toString();
                        try {
                            if (!isFromWeb) {
                                pd.dismiss();
                            }
                            JSONArray array = response.getJSONArray("videos");
                            if (array.length() > 0) {
                                ArrayList<VideoInfo> videos = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    String title = obj.getString("text");
                                    long duration = obj.getLong("duration");
                                    String thumbnail = obj.getString("thumb");
                                    String downloadUrl = obj.getString("url");
                                    String format = obj.getString("bitrate");
                                    long size = obj.getLong("size");

                                    VideoInfo videoInfo = new VideoInfo();
                                    videoInfo.setTitle("TW_" + title);
                                    videoInfo.setDuration(String.valueOf(duration / 1000L));
                                    videoInfo.setThumbnail(thumbnail);
                                    videoInfo.setUrl(downloadUrl);
                                    videoInfo.setFileSize(size);
                                    videoInfo.setFormat(format);
                                    videoInfo.setExt(".mp4");
                                    videos.add(videoInfo);
                                }
                                listener.onDataResult(true, false, videos);
                            }
                        } catch (JSONException e) {
                            if (!isFromWeb) {
                                pd.dismiss();
                            }
                            listener.onDataResult(false, false, null);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (!isFromWeb) {
                            pd.dismiss();
                        }
                        if (Looper.myLooper() == null)
                            Looper.prepare();
                        if (listener != null) {
                            listener.onDataResult(false, false, null);
                        }
                        Looper.loop();
                    }
                });
    }

    public void setListener(DownloadVideosMain.OnDetectListener listener) {
        this.listener = listener;
    }
}

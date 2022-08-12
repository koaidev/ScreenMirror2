package com.bangbangcoding.screenmirror.web.utils.downloader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.bangbangcoding.screenmirror.R;
import com.bangbangcoding.screenmirror.web.ui.model.apis_models.VideoInfo;
import com.bangbangcoding.screenmirror.web.ui.paste.DownloadVideosMain;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class BuzzVideoDownloader {

    private Context context;
    private String finalURL;
    private String videoURL;
    private String videoTitle;
    private DownloadVideosMain.OnDetectListener listener;
    public boolean isFromWeb = true;
    private ProgressDialog pd;

    public BuzzVideoDownloader(Context context, String videoURL) {
        this.context = context;
        this.videoURL = videoURL;
    }

    public void setListener(DownloadVideosMain.OnDetectListener listener) {
        this.listener = listener;
    }

    private static int ordinalIndexOf(String str, String substr, int n) {
        int pos = -1;
        do {
            pos = str.indexOf(substr, pos + 1);
        } while (n-- > 0 && pos != -1);
        return pos;
    }

    public void DownloadVideo() {
        Log.e("ttt", "download buzzvideo");
        if (!isFromWeb) {
            pd = new ProgressDialog(context);
            pd.setMessage(context.getResources().getString(R.string.txt_genarating_download_link));
            pd.setCancelable(false);
            pd.show();
        }
        new Data().execute(getVideoId(videoURL));
    }

    private String getVideoId(String link) {
        return link;
    }

    private class Data extends AsyncTask<String, String, List<VideoInfo>> {

        @Override
        protected List<VideoInfo> doInBackground(String... parm) {
            BufferedReader reader = null;
            try {
                URL url = new URL(parm[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = reader.readLine();
//                while ((line = reader.readLine()) != null) {

                Document document = Jsoup.connect(parm[0]).get();
                Log.e("ttt", "doInBackground: " + line.contains("article"));

                try {
                    Elements es = document.select("script");
                    for (Element e : es) {
                        if (e.data().contains("INITIAL_STATE")) {
                            String result = e.data().replace("window.__INITIAL_STATE__=", "");
                            result = result.replace("undefined", "\"\"");
                            Gson gson = new Gson();
                            BuzzModel gsonObj = gson.fromJson(result, BuzzModel.class);

                            BuzzModel.Video video;
                            if (gsonObj.getArticle() != null) {
                                video = gsonObj.getArticle().getVideo();
//                            } else if (gsonObj.getStory() != null) {
//
                            } else {
                                video = gsonObj.getStory().getVideo();
                            }

                            VideoInfo videoInfo = new VideoInfo();
                            videoInfo.setExt(".mp4");
                            videoInfo.setThumbnail(video.getVideoThumbnailExt());
                            videoInfo.setDuration(video.getVideoDuration());
                            videoInfo.setUrl(video.getVideoList().getVideo3().getMainUrl());
                            videoInfo.setTitle("BuzzVideo_" + video.getVideoTitle());
                            videoInfo.setFormat(video.getVideoList().getVideo3().getDefinition());
                            getSize(videoInfo.getUrl(), videoInfo);

                            VideoInfo video1 = new VideoInfo();
                            video1.setExt(".mp4");
                            video1.setThumbnail(video.getVideoThumbnailExt());
                            video1.setDuration(video.getVideoDuration());
                            video1.setUrl(video.getVideoList().getVideo2().getMainUrl());
                            video1.setTitle("BuzzVideo_" + video.getVideoTitle());
                            video1.setFormat(video.getVideoList().getVideo2().getDefinition());
                            getSize(video1.getUrl(), video1);

                            VideoInfo video2 = new VideoInfo();
                            video2.setExt(".mp4");
                            video2.setThumbnail(video.getVideoThumbnailExt());
                            video2.setDuration(video.getVideoDuration());
                            video2.setUrl(video.getVideoList().getVideo1().getMainUrl());
                            video2.setTitle("BuzzVideo_" + video.getVideoTitle());
                            video2.setFormat(video.getVideoList().getVideo1().getDefinition());
                            getSize(video2.getUrl(), video2);

                            List<VideoInfo> videoInfoList = new ArrayList<>();
                            videoInfoList.add(videoInfo);
                            videoInfoList.add(video1);
                            videoInfoList.add(video2);
                            if (!isFromWeb) {
                                pd.dismiss();
                            }
                            return videoInfoList;
//                            https://p16-va.topbuzzcdn.com/origin/pgc-image-va/SyH3vIoEWjNNlJ.jp
                        }
                    }
                } catch (Exception e) {
                    if (!isFromWeb) {
                        pd.dismiss();
                    }
                    return null;
                }
            } catch (IOException e) {
                if (!isFromWeb) {
                    pd.dismiss();
                }
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<VideoInfo> videoInfos) {
            super.onPostExecute(videoInfos);
            if (!isFromWeb) {
                pd.dismiss();
            }
            if (listener != null) {
                listener.onDataResult(videoInfos != null, true, videoInfos);
            }
        }
    }

    private void getSize(String link, VideoInfo info) {
        URL url = null;
        try {
            url = new URL(link);
            URLConnection c = url.openConnection();
            c.connect();
            int lengthOfFile = c.getContentLength();
            Log.e("ttt", "getContentLength " + lengthOfFile);
            info.setFileSize(lengthOfFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

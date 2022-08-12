package com.bangbangcoding.screenmirror.web.utils.downloader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bangbangcoding.screenmirror.R;
import com.bangbangcoding.screenmirror.web.ui.model.apis_models.VideoInfo;
import com.bangbangcoding.screenmirror.web.ui.paste.DownloadVideosMain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TopBuzzDownloader {

    private Context context;
    private String finalURL;
    private String videoURL;
    private String videoTitle;
    private DownloadVideosMain.OnDetectListener listener;
    public boolean isFromWeb = true;
    private ProgressDialog pd;

    public TopBuzzDownloader(Context context, String videoURL) {
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

    public void downloadVideo() {
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

    private class Data extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... parm) {
            BufferedReader reader = null;
            try {
                URL url = new URL(parm[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    if (line.contains("INITIAL_STATE")) {
                        if (line.contains("title")) {
                            if (line.contains("videoTitle")) {
                                line = line.substring(line.indexOf("videoTitle"));
                            }
                            Log.e("ttt", "doInBackground: " + line);
                            if (ordinalIndexOf(line, "\"", 1) + 1 > -1
                                    && ordinalIndexOf(line, "\\", 2) > -1) {
                                videoTitle = line.substring(ordinalIndexOf(line, "\"", 1) + 1, ordinalIndexOf(line, "\\", 2));
                            }
                            if (line.contains("480p")) {
                                line = line.substring(line.indexOf("480p"));
                                line = line.substring(line.indexOf("main_url"));
                                line = line.substring(ordinalIndexOf(line, "\"", 1) + 1, ordinalIndexOf(line, "\"", 2));
                                line = line.replace("u002F", "");
                                if (line.contains("http")) {
                                    line = line.replace("http", "https");
                                }
                                buffer.append(line);
                            } else {
                                buffer.append("No URL");
                            }
                        } else {
                            Log.e("ttt", "doInBackground: not title");
                        }
                    } else {
                        Log.e("ttt", "doInBackground: not INITIAL_STATE");
                    }
                }
                if (!isFromWeb) {
                    pd.dismiss();
                }
                return buffer.toString();
            } catch (IOException e) {
                if (!isFromWeb) {
                    pd.dismiss();
                }
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);
            if (o == null || o.isEmpty() || o.contains("No URL")) {
                Log.e("ttt", "TopDownloader onPostExecute: " + o);
                if (!isFromWeb) {
                    pd.dismiss();
                }
                listener.onDataResult(false, false, null);
            } else {
                finalURL = o;
                Log.e("ttt", "TopDownloader onPostExecute: " + o);
                VideoInfo videoInfo = new VideoInfo();
                videoInfo.setExt(".mp4");
                videoInfo.setUrl(finalURL);
                videoInfo.setTitle("TopBuzz_" + videoTitle.trim());
                List<VideoInfo> videoInfoList = new ArrayList<>();
                videoInfoList.add(videoInfo);
                if (!isFromWeb) {
                    pd.dismiss();
                }
                if (listener != null) {
                    listener.onDataResult(true, true, videoInfoList);
                }

            }
        }
    }
}

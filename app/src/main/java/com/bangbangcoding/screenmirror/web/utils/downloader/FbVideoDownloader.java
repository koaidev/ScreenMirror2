package com.bangbangcoding.screenmirror.web.utils.downloader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Keep;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bangbangcoding.screenmirror.R;
import com.bangbangcoding.screenmirror.web.ui.model.apis_models.VideoInfo;
import com.bangbangcoding.screenmirror.web.ui.model.apis_models.VideoModel;
import com.bangbangcoding.screenmirror.web.ui.paste.DownloadFileMain;
import com.bangbangcoding.screenmirror.web.ui.paste.DownloadVideosMain;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Keep
public class FbVideoDownloader implements VideoDownloader {

    public String downlink;
    private Context context;
    String matag;
    boolean isFromWeb = true;
    int myselmethode = 0;
    private String VideoURL;
    private DownloadVideosMain.OnDetectListener listener;
    private ProgressDialog pd;

    public FbVideoDownloader(Context context, String videoURL, int method) {
        this.context = context;
        VideoURL = videoURL;
        myselmethode = method;
    }

    public void setListener(DownloadVideosMain.OnDetectListener listener) {
        this.listener = listener;
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
        try {
            downlink = VideoURL;
            switch (myselmethode) {
                case 0: {
//                    downlink = downlink.contains("www.facebook") ? downlink.replace("www.facebook", "m.facebook") : downlink;
//                    downlink = downlink.contains("story.php") ? downlink : downlink + "?refsrc=https%3A%2F%2Fm.facebook.com%2F&refid=28&_rdr";
//                    new Fbwatch_myown().execute();
                    runWithApi(downlink);
                    break;
                }
                case 1: {
                    new FbWatchGetId().execute();
                    break;
                }
                case 2: {
                    new Fbwatch_fbdown().execute();
                    break;
                }
            }
        } catch (Exception e) {
            if (!isFromWeb) {
                pd.dismiss();
            }
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    listener.onDataResult(false, false, null);
                }
            });
        }
    }

    private void runWithApi(String downlink) {
        Log.e("ttt", "Case 1: runWithApi: " + downlink);

        new Thread() {

            @Override
            public void run() {
                super.run();
                try {
                    Looper.prepare();
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .writeTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .build();
                    Request request = new Request.Builder()
                            .url(downlink)
                            //.header("Downloader", "xxx")
                            .method("GET", null)
                            .build();

                    Response response = null;

                    response = client.newCall(request).execute();

                    Document document = Jsoup.parse(response.body().string());

                    String unused = document.select("meta[property=\"og:video\"]").last().attr("content");
                    Log.e("ttt", "unused: " + unused);
                    String thumbnail = document.select("meta[property=\"og:image\"]").last().attr("content");

                    Elements elements = document.select("meta[property=\"og:description\"]");
                    String title;
                    if (!elements.isEmpty()) {
                        title = elements.last().attr("content");
                    } else {
                        title = document.select("meta[property=\"og:title\"]").last().attr("content");
                    }

                    if (!unused.equals("")) {
                        Log.e("ttt", "1");
                        VideoInfo info2 = new VideoInfo();
                        info2.setUrl(unused);
                        info2.setThumbnail(thumbnail);
                        info2.setExt(".mp4");
                        info2.setFormat("480p");
                        info2.setTitle(title);
                        Log.e("ttt", "title: " + info2.getTitle());
                        getSize(unused, info2);
                        List<VideoInfo> videoInfoList = new ArrayList<>();
                        videoInfoList.add(info2);
                        if (!isFromWeb) {
                            pd.dismiss();
                        }
                        if (listener != null) {
                            listener.onDataResult(true, true, videoInfoList);
                        }
                    } else {
                        Log.e("ttt", "2");
//                        if (!isFromWeb) {
//                            pd.dismiss();
//                        }
                        new FbWatchGetId().execute();
//                        listener.onDataResult(false, true, null);
                    }
                } catch (Exception e) {
                    Log.e("ttt", "3 " + e.toString());
//                    if (!isFromWeb) {
//                        pd.dismiss();
//                    }
                    new FbWatchGetId().execute();
//                    listener.onDataResult(false, true, null);
                    e.printStackTrace();
                }
            }
        }.start();
    }


    private class FbWatchGetId extends AsyncTask<Void, Boolean, List<VideoInfo>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("ttt", "Case 2: runWithApi: " + downlink);
        }

        @Override
        protected List<VideoInfo> doInBackground(Void... voids) {
            try {
                ArrayList<VideoInfo> results = new ArrayList<>();
                Document doc = Jsoup.connect("https://downvideo.net/download.php")
                        .data("URL", downlink)
                        .data("token", "2c17c6393771ee3048ae34d6b380c5ecz")
                        .timeout(100 * 1000)
                        .post();

                String thumbnail = doc.select("img").get(2).attr("src");
                String title = doc.select("strong").get(3).text();
                String myhd9 = doc.select("a").get(4).attr("href");
                String myhd = doc.select("a").get(5).attr("href");

                Log.e("TextLink myrrr 0.5 ", myhd);
                Log.e("TextLink myrrr 0 ", myhd9);

                if (myhd.isEmpty() && myhd9.isEmpty()) {
                    return null;
                }
                if (title == null || title.isEmpty()) {
                    title = "FB_" + (System.currentTimeMillis() + 1000L);
                }

                if (!myhd.isEmpty()) {
                    VideoInfo info1 = new VideoInfo();
                    info1.setUrl(myhd);
                    info1.setThumbnail(thumbnail);
                    info1.setExt(".mp4");
                    info1.setFormat("HD");
                    info1.setTitle(title);
                    results.add(info1);
                    getSize(myhd, info1);
                }

                if (!myhd9.isEmpty()) {
                    VideoInfo info2 = new VideoInfo();
                    info2.setUrl(myhd9);
                    info2.setThumbnail(thumbnail);
                    info2.setExt(".mp4");
                    info2.setFormat("SD");
                    info2.setTitle(title);
                    results.add(info2);
                    getSize(myhd9, info2);
                }
                return results;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<VideoInfo> videoInfos) {
            if (!isFromWeb) {
                pd.dismiss();
            }
            if (videoInfos != null && !videoInfos.isEmpty()) {
                listener.onDataResult(true, false, videoInfos);
            } else {
                new Handler(Looper.getMainLooper()).post(() -> {
                    new Fbwatch_fbdown().execute();
//                    if (listener != null) {
//                        listener.onDataResult(false, true, null);
//                    }
                });
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

    private void runWithApiHeroku(String finalUrl1) {
        AndroidNetworking.get("https://dlphpapis21.herokuapp.com/api/info?url=" + finalUrl1 + "&flatten=True")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("fjhjfhjsdfsdhf " + response);
                        String matag;
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            matag = jsonObject.getJSONArray("videos").getJSONObject(0).getString("url");
                            System.out.println("wojfdjhfdjh " + matag);
                            VideoInfo info2 = new VideoInfo();
                            info2.setUrl(matag);
//                            info2.setThumbnail(thumbnail);
                            info2.setExt(".mp4");
                            info2.setFormat("SD");
                            info2.setTitle("FB_" + System.currentTimeMillis());
//                            getSize(unused, info2);
                            List<VideoInfo> videoInfoList = new ArrayList<>();
                            videoInfoList.add(info2);
                            listener.onDataResult(true, true, videoInfoList);
//                            DownloadFileMain.startDownloading(Mcontext, matag, "Facebook_" + System.currentTimeMillis(), ".mp4");

                        } catch (Exception e) {
                            Log.e("ttt", "onResponse: FB " + e.getMessage());
                            if (!isFromWeb) {
                                pd.dismiss();
                            }
                            listener.onDataResult(false, true, null);
                        }
                    }

                    @Override/**/
                    public void onError(ANError error) {
                        if (!isFromWeb) {
                            pd.dismiss();
                        }
                        listener.onDataResult(false, true, null);
                    }
                });
    }

    public class Fbwatch_myown extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {

                String uag = "";
                Random rand = new Random();
                int rand_int1 = rand.nextInt(2);

                if (rand_int1 == 0) {
                    uag = "Mozilla/5.0 (Linux; Android 5.0.2; SAMSUNG SM-G925F Build/LRX22G) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36";
                } else {
                    uag = "Mozilla/5.0 (Linux; U; Android 4.1.1; en-gb; Build/KLP) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Safari/534.30";
                }

                this.RoposoDoc = Jsoup.connect(downlink)
                        .header("Accept", "*/*")
                        .userAgent(uag)
                        .get();

            } catch (Exception e) {
                if (!isFromWeb) {
                    pd.dismiss();
                }
                listener.onDataResult(false, false, null);
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            if (!isFromWeb) {
                pd.dismiss();
            }
            boolean isSecon = false;

            try {
                System.out.println("myresponseis111 exp166 " + document);

                String data = "";

                Elements elements = document.select("script");
                for (Element element : elements) {
                    if (element.attr("type").equals("application/ld+json")) {


                        JSONObject obj = new JSONObject(element.html());

                        System.out.println("myresponseis111 list_of_qualities" + obj.toString());


                        String replaceString = obj.getString("contentUrl");
                        System.out.println("myresponseis111 list_of_qualities" + replaceString);

                        listener.onDataResult(true, true, null);
                        DownloadFileMain.startDownloading(context, replaceString, "Facebook_" + System.currentTimeMillis(), ".mp4");
                        downlink = "";
                    }
                }


            } catch (Exception unused) {
                System.out.println("myresponseis111 exp " + unused.getMessage());
                downlink = "";

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onDataResult(false, false, null);

                    }
                });
            }
        }


    }

    public class Fbwatch_tiktok extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                this.RoposoDoc = Jsoup.connect("https://vm.tiktok.com/ZSQTnNWu/")
                        .header("Accept", "*/*")
                        .userAgent("PostmanRuntime/7.26.8")
                        .get();

            } catch (Exception e) {
                if (!isFromWeb) {
                    pd.dismiss();
                }
                e.printStackTrace();
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            if (!isFromWeb) {
                pd.dismiss();
            }
            boolean isSecon = false;

            try {
                System.out.println("myresponseis111 exp166 " + document);

                String data = "";

                Elements elements = document.select("script");
                for (Element element : elements) {
                    if (element.attr("type").equals("application/ld+json")) {
                        JSONObject obj = new JSONObject(element.html());
                        System.out.println("myresponseis111 list_of_qualities" + obj.toString());
                        String replaceString = obj.getString("contentUrl");
                        System.out.println("myresponseis111 list_of_qualities" + replaceString);
                        if (listener != null) {
                            listener.onDataResult(true, false, null);
                        } else {
                            DownloadFileMain.startDownloading(context, replaceString, "Facebook_" + System.currentTimeMillis(), ".mp4");
                        }
                        downlink = "";
                    }
                }


            } catch (Exception unused) {
                System.out.println("myresponseis111 exp " + unused.getMessage());
                downlink = "";
                if (listener != null) {
                    listener.onDataResult(false, false, null);
                }
            }
        }


    }

    private class Fbwatch_fbdown extends AsyncTask<Void, Boolean, Boolean> {
        String title;
        ArrayList<VideoModel> removeDuplicatesvalues = new ArrayList<>();
        CharSequence[] charSequenceArr = {context.getString(R.string.txt_hdlink) + "", context.getString(R.string.txt_sdlink) + ""};

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("ttt", "Case 3: runWithApi: " + downlink);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect("https://fbdown.net/download.php")
                        .data("URLz", downlink)
                        .timeout(100 * 1000)
                        .post();

                Element link = doc.body().getElementById("hdlink");
                String atag = link.select("a").first().attr("href");

                Element slink = doc.body().getElementById("sdlink");
                String satag = slink.select("a").first().attr("href");
                // atag = URLDecoder.decode(atag, "UTF-8");
                Log.e("TextLink", atag);
                VideoModel videoModel = new VideoModel();
                videoModel.setUrl(atag);
                removeDuplicatesvalues.add(videoModel);

                VideoModel videoModel1 = new VideoModel();
                videoModel1.setUrl(satag);
                removeDuplicatesvalues.add(videoModel1);
                matag = atag;
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            if (!isFromWeb) {
                pd.dismiss();
            }
            if (aVoid) {
                try {
                    if (listener != null) {
                        listener.onDataResult(false, true, null);
                    } else {
                        List<VideoInfo> videoInfoList = new ArrayList<>();
                        for (int i = 0; i < charSequenceArr.length; i++) {
                            VideoInfo info2 = new VideoInfo();
                            info2.setUrl(removeDuplicatesvalues.get(i).getUrl());
                            info2.setExt(".mp4");
                            info2.setFormat("N" + i);
                            info2.setTitle("FB_" + (System.currentTimeMillis() + i *1000L));
                            videoInfoList.add(info2);
                        }
                        listener.onDataResult(true, true, videoInfoList);
                    }
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onDataResult(false, false, null);
                        }
                    });
                }

            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onDataResult(false, true, null);
                        }
                    }
                });
            }
        }
    }
}

package com.bangbangcoding.screenmirror.web.ui.paste;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.WINDOW_SERVICE;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Keep;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.bangbangcoding.screenmirror.R;
import com.bangbangcoding.screenmirror.web.ui.model.ModelInstagramResponse;
import com.bangbangcoding.screenmirror.web.ui.model.apis_models.DLDataParser;
import com.bangbangcoding.screenmirror.web.ui.model.apis_models.Format;
import com.bangbangcoding.screenmirror.web.ui.model.apis_models.Video;
import com.bangbangcoding.screenmirror.web.ui.model.apis_models.VideoInfo;
import com.bangbangcoding.screenmirror.web.ui.model.storymodels.ModelEdNode;
import com.bangbangcoding.screenmirror.web.ui.model.storymodels.ModelGetEdgetoNode;
import com.bangbangcoding.screenmirror.web.utils.Constants;
import com.bangbangcoding.screenmirror.web.utils.DownloadUtils;
import com.bangbangcoding.screenmirror.web.utils.Utils;
import com.bangbangcoding.screenmirror.web.utils.downloader.BuzzVideoDownloader;
import com.bangbangcoding.screenmirror.web.utils.downloader.DailyMotionDownloader;
import com.bangbangcoding.screenmirror.web.utils.downloader.FbVideoDownloader;
import com.bangbangcoding.screenmirror.web.utils.downloader.TopBuzzDownloader;
import com.bangbangcoding.screenmirror.web.utils.downloader.TwitterVideoDownloader;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DownloadVideosMain {
    private static final int DOWNLOAD_NOTIFICATION_ID = 231;
    public static Context mContext;
    public static ProgressDialog pd;
    public static Dialog dialog;
    public static SharedPreferences prefs;
    public static Boolean fromService;
    public static String VideoUrl;
    static String title;
    static LinearLayout mainLayout;
    static WindowManager windowManager2;
    static WindowManager.LayoutParams params;
    static View mChatHeadView;
    static ImageView img_dialog;
    static String myURLIS = "";
    static Dialog dialog_quality_allvids;
    private static String newurl;
    public static OnDetectListener mListener;
    public static OnLoginInstagListener mLoginInstaListener;
    public static boolean IssupportUrl = false;


    public interface OnDetectListener {
        void onDataResult(boolean hasData, boolean downloadNow, List<VideoInfo> videos);
    }

    public interface OnLoginInstagListener{
        void OnCheckLogin(boolean login);
    }

    public static StringBuilder concatString(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        return sb;
    }

    public static String concatString(String str, String str2, String str3) {
        return str + str2 + str3;
    }

    public static String md5(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(str.getBytes());
            byte[] digest = instance.digest();
            StringBuilder stringBuffer = new StringBuilder();
            for (byte b : digest) {
                String hexString = Integer.toHexString(b & 255);
                while (hexString.length() < 2) {
                    hexString = "0" + hexString;
                }
                stringBuffer.append(hexString);
            }
            return stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("ttt", e.getLocalizedMessage());
            return "";
        }
    }

    public static void checInsta(String url, OnLoginInstagListener listener){
        mLoginInstaListener = listener;
        checkCookie(url);
    }

    private static void checkCookie(String url) {
        String urlSer;
        try {
            URI uri = new URI(url);
            urlSer = new URI(
                    uri.getScheme(),
                    uri.getAuthority(),
                    uri.getPath(),
                    null,  // Ignore the query part of the input url
                    uri.getFragment()
            ).toString();
        } catch (Exception ex) {
            mLoginInstaListener.OnCheckLogin(false);
            return;
        }
        String cookie = CookieManager.getInstance().getCookie(url);
        Log.e("ttt", "Cookie:" + cookie);
        if (cookie == null) {
            cookie = "";
            mLoginInstaListener.OnCheckLogin(false);
            return;
        }else {
            mLoginInstaListener.OnCheckLogin(true);
        }
    }

    public static void startDownload(Fragment fragment, final Context context, String url, Boolean service, OnDetectListener listener) {
        try {
            mContext = context;
            fromService = service;
            mListener = listener;
            Log.i("ttt clip", "work 2");
            myURLIS = url;
            if (!fromService) {
                pd = new ProgressDialog(context);
                pd.setMessage(mContext.getResources().getString(R.string.txt_genarating_download_link));
                pd.setCancelable(false);
                pd.show();
            }
            if (url.contains("tiktok")) {
                if (!((Activity) mContext).isFinishing()) {
                    if (mContext instanceof AppCompatActivity) {
                        String finalUrl1 = url;
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Looper.prepare();
                                    AndroidNetworking.get("https://youtube4kdownloader.com/ajax/getLinks.php?video=" + finalUrl1 + "&rand=7a6d56d6659b6")
                                            .setPriority(Priority.HIGH)
                                            .build()
                                            .getAsJSONObject(new JSONObjectRequestListener() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    pd.dismiss();
                                                    String matag;
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(response.toString());
                                                        JSONObject jdata = jsonObject.getJSONObject("data");
                                                        matag = jdata.getJSONArray("av").getJSONObject(0).getString("url");
                                                        matag = matag.substring(matag.indexOf("video_id=") + 9, matag.indexOf("&line="));
                                                        Log.e("ttt", "onResponse: tiktok " + matag);
                                                        String myttt = "https://api2-16-h2.musical.ly/aweme/v1/play/?video_id=" + matag + "&vr_type=0&is_play_url=1&source=PackSourceEnum_PUBLISH&media_type=4&ratio=default&improve_bitrate=1";
//                                                        String a112 = "https://v16-web.tiktok.com/video/tos/alisg/tos-alisg-pve-0037c001/d9c32b92f3c248749c23d67704dd58bb/?a=1988&br=2540&bt=1270&cd=0%7C0%7C1%7C0&ch=0&cr=0&cs=0&cv=1&dr=0&ds=3&er=&expire=1643278883&ft=sd03~3LGnz7ThYESGlXq&l=202201270421130102451001170894B7BA&lr=tiktok_m&mime_type=video_mp4&net=0&pl=0&policy=3&qs=0&rc=M2c5NGU6ZmtlOjMzODczNEApPDg0NjlkOmQ6NztmNjxkNGdvaWwxcjRvbjFgLS1kMS1zcy1fLjE0X14tYS1hMy4zLjE6Yw%3D%3D&signature=8b5034a45bbcd2b57c067b6b3aaed2a1&tk=0&vl=&vr=";


                                                        String thumbnail = jdata.getString("thumbnail");
                                                        String title = jdata.getString("title");
                                                        String duration = jdata.getString("duration");

                                                        ArrayList<VideoInfo> videos = new ArrayList<VideoInfo>();
                                                        boolean isGet = true;

                                                        for (int i = jdata.getJSONArray("av").length() - 1; i >= 0; i--) {
                                                            JSONObject obj2 = jdata.getJSONArray("av").getJSONObject(i);

                                                            if (obj2.getString("url").contains("api-h2.tiktokv")) {
                                                                isGet = !isGet;
                                                                if (!isGet) {
                                                                    JSONObject obj = jdata.getJSONArray("av").getJSONObject(i + 1);

//                                                            }
//                                                            if (obj.getString("url").contains("v19.tiktokcdn.com") ||
//                                                                    obj.getString("url").contains("v16m.tiktokcdn")||
//                                                                    obj.getString("url").contains("v9-vn.tiktokcdn.com")||
//                                                                    obj.getString("url").contains("v16.tiktokcdn")
//                                                            ){
                                                                    VideoInfo videoInfo = new VideoInfo();
                                                                    videoInfo.setTitle(title);
                                                                    videoInfo.setThumbnail(thumbnail);
                                                                    videoInfo.setDuration(duration);
                                                                    videoInfo.setUrl(obj.getString("url"));
                                                                    videoInfo.setFileSize(obj.getInt("size"));
                                                                    String ext = DownloadUtils.addCharacterExt(obj.getString("ext"));
                                                                    videoInfo.setExt(ext);
                                                                    videoInfo.setFormat(obj.getString("quality"));
                                                                    videos.add(videoInfo);
                                                                }
                                                            }
                                                        }
                                                        mListener.onDataResult(true, true, videos);
                                                    } catch (Exception e) {
//
                                                    }
                                                }

                                                @Override/**/
                                                public void onError(ANError error) {
                                                    if (!fromService) {
                                                        pd.dismiss();
                                                    }
                                                    System.out.println("ttt myresponseis111 exp " + error.getMessage());
                                                    mListener.onDataResult(false, false, null);
                                                }
                                            });

                                } catch (Exception unused) {
                                    if (!fromService) {
                                        pd.dismiss();
                                    }
                                    System.out.println("myresponseis111 exp " + unused.getMessage());
                                }
                            }
                        }.start();
                    }
                }
                Log.e("ttt", "tiktok url=" + url);
            } else if (url.contains("popcornflix")) {
                url = url.substring(url.lastIndexOf("/") + 1);
                Log.e("ttt", "fjhjfhjsdfsdhf " + url);
                AndroidNetworking.get("https://api.unreel.me/v2/sites/popcornflix/videos/" + url + "/play-url?__site=popcornflix&__source=web&embed=false&protocol=https&tv=false")
                        .addHeaders("User-Agent", "Mozilla/5.0 (Linux; Android 10;TXY567) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/8399.0.9993.96 Mobile Safari/599.36")
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e("ttt", "fjhjfhjsdfsdhf " + response);
                                if (!fromService) {
                                    pd.dismiss();
                                }
                                String matag;
                                try {
                                    JSONObject jsonObject = new JSONObject(response.toString());
                                    matag = jsonObject.getString("url");
                                    Log.e("ttt", "wojfdjhfdjh " + matag);

                                    VideoInfo videoInfo = new VideoInfo();
                                    videoInfo.setTitle("Popcornflex_" + System.currentTimeMillis());
                                    videoInfo.setUrl(matag);
                                    videoInfo.setExt(".mp4");
                                    ArrayList<VideoInfo> videos = new ArrayList<VideoInfo>();
                                    videos.add(videoInfo);
                                    mListener.onDataResult(true, true, videos);

//                                    DownloadFileMain.startDownloading(context, matag, "Popcornflex_" + System.currentTimeMillis(), ".mp4");
                                } catch (Exception e) {
                                    matag = "";
                                    if (!fromService) {
                                        pd.dismiss();
                                    }
                                    e.printStackTrace();
                                }
                            }

                            @Override/**/
                            public void onError(ANError error) {
                                if (!fromService) {
                                    pd.dismiss();
                                }
                            }
                        });
            } else if (url.contains("veoh")) {
                url = url.substring(url.lastIndexOf("/") + 1);
                Log.e("ttt", "fjhjfhjsdfsdhf " + url);
                AndroidNetworking.get("http://www.veoh.com/watch/getVideo/" + url)
                        .addHeaders("User-Agent", "Mozilla/5.0 (Linux; Android 10;TXY567) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/8399.0.9993.96 Mobile Safari/599.36")
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e("ttt", "fjhjfhjsdfsdhf " + response);
                                if (!fromService) {
                                    pd.dismiss();
                                }
                                String matag;
                                try {
                                    JSONObject jsonObject = new JSONObject(response.toString());
                                    matag = jsonObject.getJSONObject("video").getJSONObject("src").getString("HQ");
                                    Log.e("ttt", "wojfdjhfdjh " + matag);
                                    DownloadFileMain.startDownloading(context, matag, "Veoh_" + System.currentTimeMillis(), ".mp4");
                                } catch (Exception e) {
                                    matag = "";
                                    if (!fromService) {
                                        pd.dismiss();
                                    }
                                    e.printStackTrace();
                                }
                            }

                            @Override/**/
                            public void onError(ANError error) {
                                if (!fromService) {
                                    pd.dismiss();
                                }
                            }
                        });
            } else if (url.contains("moj")) {
                try {
                    url = url.substring(url.lastIndexOf("/") + 1);
                    if (url.contains("?referrer=share")) {
                        url = url.substring(0, url.indexOf("?"));
                        Log.e("ttt", "fjhjfhjsdfsdhf 000=" + url + " size " + url.indexOf("?"));
                        Log.e("ttt", "fjhjfhjsdfsdhf 000=" + "https://moj-apis.sharechat.com/videoFeed?postId=" + url + "&firstFetch=true");
                    }
                    JSONObject jsonObject = new JSONObject("{\"appVersion\":83,\"bn\":\"broker1\",\"client\":\"android\",\"deviceId\":\"ebb088d29e7287b1\",\"message\":{\"adData\":{\"adsShown\":0,\"firstFeed\":false},\"deviceInfoKey\":\"OSyQoHJLJ4NsXPLyQePkAICh3Q0ih0bveFwm1KEV+vReMuldqo+mSyMjdhb4EeryKxk1ctAbYaDH\\\\nTI+PMRPZVYH5pBccAm7OT2uz69vmD/wPqGuSgWV2aVNMdM75DMb8NZn1JU2b1bo/oKs80baklsvx\\\\n1X7jrFPL6M5EDTdPDhs=\\\\n\",\"deviceInfoPayload\":\"M6g+6j6irhFT/H6MsQ/n/tEhCl7Z5QgtVfNKU8M90zTJHxqljm2263UkjRR9bRXAjmQFXXOTXJ25\\\\nOHRjV7L5Lw+tUCONoYfyUEzADihWfAiUgXJEcKePfZONbdXXuwGgOPeD0k4iSvI7JdzroRCScKXd\\\\n41CkmXFayPaRL9aqgAgs6kSoIncCWBU2gEXiX1lgPVvdmUzCZ+yi2hFA+uFOmv1MJ6dcFKKcpBM6\\\\nHSPIrGV+YtTyfd8nElx0kyUbE4xmjOuMrctkjnJkd2tMdxB8qOFKeYrcLzy4LZJNXyUmzs29XSE+\\\\nhsrMZib8fFPJhJZIyGCWqfWiURut4Bg5HxYhYhg3ejPxFjNyXxS3Ja+/pA+A0olt5Uia7ync/Gui\\\\n58tlDQ4SKPthCzGa1tCVN+2y/PW30+LM79t0ltJ/YrNZivQx4eEnszlM9nwmIuj5z5LPniQghA6x\\\\nrfQ8IqVUZfiitXj/Fr7UjKg1cs/Ajj8g4u/KooRvVkg9tMwWePtJFqrkk1+DU4cylnSEG3XHgfer\\\\nslrzj5NNZessMEi+4Nz0O2D+b8Y+RjqN6HqpwZPDHhZwjz0Iuj2nhZLgu1bgNJev5BwxAr8akDWv\\\\nvKsibrJS9auQOYVzbYZFdKMiBnh+WHq0qO2aW1akYWCha3ZsSOtsnyPnFC+1PnMbBv+FiuJmPMXg\\\\nSODFoRIXfxgA/qaiKBipS+kIyfaPxn6O1i6MOwejVuQiWdAPTO132Spx0cFtdyj2hX6wAMe21cSy\\\\n8rs3KQxiz+cq7Rfwzsx4wiaMryFunfwUwnauGwTFOW98D5j6oO8=\\\\n\",\"lang\":\"Hindi\",\"playEvents\":[{\"authorId\":\"18326559001\",\"networkBitrate\":1900000,\"initialBufferPercentage\":100,\"isRepost\":false,\"sg\":false,\"meta\":\"NotifPostId\",\"md\":\"Stream\",\"percentage\":24.68405,\"p\":\"91484006\",\"radio\":\"wifi\",\"r\":\"deeplink_VideoPlayer\",\"repeatCount\":0,\"timeSpent\":9633,\"duration\":15,\"videoStartTime\":3916,\"t\":1602255552820,\"clientType\":\"Android\",\"i\":79,\"appV\":83,\"sessionId\":\"72137847101_8863b3f5-ad2d-4d59-aa7c-cf1fb9ef32ea\"},{\"authorId\":\"73625124001\",\"networkBitrate\":1900000,\"initialBufferPercentage\":100,\"isRepost\":false,\"sg\":false,\"meta\":\"list2\",\"md\":\"Stream\",\"percentage\":17.766666,\"p\":\"21594412\",\"radio\":\"wifi\",\"r\":\"First Launch_VideoPlayer\",\"repeatCount\":0,\"tagId\":\"0\",\"tagName\":\"\",\"timeSpent\":31870,\"duration\":17,\"videoStartTime\":23509,\"t\":1602218215942,\"clientType\":\"Android\",\"i\":79,\"appV\":83,\"sessionId\":\"72137847101_db67c0c9-a267-4cec-a3c3-4c0fa4ea16e1\"}],\"r\":\"VideoFeed\"},\"passCode\":\"9e32d6145bfe53d14a0c\",\"resTopic\":\"response/user_72137847101_9e32d6145bfe53d14a0c\",\"userId\":\"72137847101\"}");
                    AndroidNetworking.post("https://moj-apis.sharechat.com/videoFeed?postId=" + url + "&firstFetch=true")
                            .addHeaders("X-SHARECHAT-USERID", "72137847101")
                            .addHeaders("X-SHARECHAT-SECRET", "9e32d6145bfe53d14a0c")
                            .addHeaders("APP-VERSION", "83")
                            .addHeaders("PACKAGE-NAME", "in.mohalla.video")
                            .addHeaders("DEVICE-ID", "ebb088d29e7287b1")
                            .addHeaders("CLIENT-TYPE", "Android:")
                            .addHeaders("Content-Type", "application/json; charset=UTF-8")
                            .addHeaders("Host", "moj-apis.sharechat.com")
                            .addHeaders("Connection", "Keep-Alive:")
                            .addHeaders("User-Agent", "okhttp/3.12.12app-version:83")
                            .addHeaders("cache-control", "no-cache")
                            .addHeaders("client-type", "Android")
                            .addHeaders("connection", "Keep-Alive")
                            .addHeaders("content-type", "application/json;charset=UTF-8")
                            .addHeaders("device-id", "ebb088d29e7287b1")
                            .addHeaders("host", "moj-apis.sharechat.com")
                            .addHeaders("package-name", "in.mohalla.video")
                            .addHeaders("postman-token", "37d59a7c-f247-3b70-ab3c-1dedf4079852")
                            .addHeaders("user-agent", "okhttp/3.12.12")
                            .addHeaders("x-sharechat-secret", "9e32d6145bfe53d14a0c")
                            .addHeaders("x-sharechat-userid", "72137847101")
                            .addJSONObjectBody(jsonObject)
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.e("ttt", "fjhjfhjsdfsdhf " + response);
                                    if (!fromService) {
                                        pd.dismiss();
                                    }
                                    String matag;
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.toString());
                                        matag = jsonObject.getJSONObject("payload")
                                                .getJSONArray("d")
                                                .getJSONObject(0)
                                                .getJSONArray("bandwidthParsedVideos")
                                                .getJSONObject(3)
                                                .getString("url");
                                        Log.e("ttt", "wojfdjhfdjh " + matag);
                                        DownloadFileMain.startDownloading(context, matag, "Moj_" + System.currentTimeMillis(), ".mp4");
                                    } catch (Exception e) {
                                        matag = "";
                                        if (!fromService) {
                                            pd.dismiss();
                                        }
                                        e.printStackTrace();
                                    }
                                }

                                @Override/**/
                                public void onError(ANError error) {
                                    if (!fromService) {
                                        pd.dismiss();
                                    }
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!fromService) {
                        pd.dismiss();
                    }
                }
            } else if (url.contains("fairtok")) {
                try {
                    url = url.substring(url.lastIndexOf("/") + 1);
                    JSONObject jsonObject = new JSONObject("{\"device_fingerprint_id\":\"838529202513017602\",\"identity_id\":\"838529202537882206\",\"hardware_id\":\"ebb088d29e7287b1\",\"is_hardware_id_real\":true,\"brand\":\"samsung\",\"model\":\"SM-J200G\",\"screen_dpi\":240,\"screen_height\":960,\"screen_width\":540,\"wifi\":true,\"ui_mode\":\"UI_MODE_TYPE_NORMAL\",\"os\":\"Android\",\"os_version\":22,\"cpu_type\":\"armv7l\",\"build\":\"LMY47X.J200GDCU2ARL1\",\"locale\":\"en_GB\",\"connection_type\":\"wifi\",\"os_version_android\":\"5.1.1\",\"country\":\"GB\",\"language\":\"en\",\"local_ip\":\"192.168.43.18\",\"app_version\":\"1.19\",\"facebook_app_link_checked\":false,\"is_referrable\":0,\"debug\":false,\"update\":1,\"latest_install_time\":1601158937336,\"latest_update_time\":1601158937336,\"first_install_time\":1601158937336,\"previous_update_time\":1601158937336,\"environment\":\"FULL_APP\",\"android_app_link_url\":\"https:\\/\\/fairtok.app.link\\/" + url + "\",\"external_intent_uri\":\"https:\\/\\/fairtok.app.link\\/Y7ov2al149\",\"cd\":{\"mv\":\"-1\",\"pn\":\"com.fairtok\"},\"metadata\":{},\"advertising_ids\":{\"aaid\":\"094dfa1f-77cf-4f84-b373-2c15bf74f9d1\"},\"lat_val\":0,\"google_advertising_id\":\"094dfa1f-77cf-4f84-b373-2c15bf74f9d1\",\"instrumentation\":{\"v1\\/open-qwt\":\"0\"},\"sdk\":\"android5.0.1\",\"branch_key\":\"key_live_hjLYp0Wi3i6R1qQ1Lr51TlpcBvkxEp53\",\"retryNumber\":0}");
                    AndroidNetworking.post("https://api2.branch.io/v1/open")
                            .addHeaders("cache-control", "no-cache")
                            .addJSONObjectBody(jsonObject)
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.e("ttt", "fjhjfhjsdfsdhf " + response);
                                    if (!fromService) {
                                        pd.dismiss();
                                    }
                                    String matag;
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.toString());
                                        matag = "https://bucket-fairtok.s3.ap-south-1.amazonaws.com/" + jsonObject.getString("post_video");
                                        Log.e("ttt", "wojfdjhfdjh " + matag);
                                        DownloadFileMain.startDownloading(context, matag, "Fairtok_" + System.currentTimeMillis(), ".mp4");
                                    } catch (Exception e) {
                                        matag = "";
                                        if (!fromService) {
                                            pd.dismiss();
                                        }
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    if (!fromService) {
                                        pd.dismiss();
                                    }
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!fromService) {
                        pd.dismiss();
                    }
                }
            }

            else if (url.contains("vlipsy")) {
                try {
                    url = url.substring(url.lastIndexOf("/") + 1);
                    if (url.length() > 8) {
                        String[] aa = url.split("-");
                        url = aa[aa.length - 1];
                    }
                    AndroidNetworking.get("https://apiv2.vlipsy.com/v1/vlips/" + url + "?key=vl_R8daJGhs67i7Ej7y")
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    //  Log.e("ttt","fjhjfhjsdfsdhf " + response);
                                    if (!fromService) {
                                        pd.dismiss();
                                    }
                                    String matag;
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.toString());
                                        matag = jsonObject.getJSONObject("data").getJSONObject("media").getJSONObject("mp4").getString("url");
                                        Log.e("ttt", "wojfdjhfdjh " + matag);
                                        DownloadFileMain.startDownloading(context, matag, "Vlipsy_" + System.currentTimeMillis(), ".mp4");
                                    } catch (Exception e) {
                                        matag = "";
                                        if (!fromService) {
                                            pd.dismiss();
                                        }
                                        e.printStackTrace();
                                    }
                                }

                                @Override/**/
                                public void onError(ANError error) {
                                    if (!fromService) {
                                        pd.dismiss();
                                    }
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!fromService) {
                        pd.dismiss();
                    }
                }
            } else if (url.contains("likee")) {
//            https://likee.video/@Aish4/video/6989882553836555260?postId=6989882553836555260
                String finalUrl3 = url;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        HttpURLConnection con = null;
                        try {
                            con = (HttpURLConnection) (new URL(finalUrl3).openConnection());
                            con.setInstanceFollowRedirects(false);
                            con.connect();
                            int responseCode = con.getResponseCode();
                            Log.e("ttt", "" + responseCode);
                            String location = con.getHeaderField("Location");
                            Log.e("ttt", location);
                            Uri uri;
                            if (location != null) {
                                uri = Uri.parse(location);
                            } else {
                                uri = Uri.parse(finalUrl3);
                            }
                            String v = uri.getQueryParameter("postId");
                            OkHttpClient client = new OkHttpClient().newBuilder()
                                    .build();
                            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("postIds", v)
                                    .build();
                            Request request = new Request.Builder()
                                    .url("https://likee.video/official_website/VideoApi/getVideoInfo")
                                    .method("POST", body)
                                    .addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36")
                                    .build();
                            Response response = client.newCall(request).execute();
                            if (response.code() == 200) {
                                if (!fromService) {
                                    pd.dismiss();
                                }
                                String urls = new JSONObject(response.body().string()).getJSONObject("data").getJSONArray("videoList").getJSONObject(0).getString("videoUrl");
                                if (urls.contains("_4")) {
                                    urls = urls.replace("_4", "");
                                }
                                if (!urls.equals("")) {
                                    String nametitle = "Likee_" +
                                            System.currentTimeMillis();
                                    DownloadFileMain.startDownloading(mContext, urls, nametitle, ".mp4");
                                }
                            } else {
                                if (!fromService) {
                                    pd.dismiss();
                                    //

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (!fromService) {
                                pd.dismiss();
                                //

                            }
                        }
                    }
                }).start();
            } else if (url.contains("gfycat")) {
                CalldlApisDataData(url, true);
            } else if (url.contains("funimate")) {
                CalldlApisDataData(url, true);
//            } else if (url.contains("kooapp")) {
//                KooDownloader kooDownloader = new KooDownloader(Mcontext, url);
//                kooDownloader.DownloadVideo();
            } else if (url.contains("9gag.com")) {
                new Call9gagData().execute(url);
            } else if (url.contains("wwe")) {
                CalldlApisDataData(url, true);
            } else if (url.contains("1tv.ru")) {
                CalldlApisDataData(url, true);
            } else if (url.contains("naver")) {
                CalldlApisDataData(url, true);
            } else if (url.contains("gloria.tv")) {
                CalldlApisDataData(url, true);
            } else if (url.contains("vidcommons.org")) {
                CalldlApisDataData(url, true);
            } else if (url.contains("media.ccc.de")) {
                CalldlApisDataData(url, true);
            } else if (url.contains("vlive")) {
                CalldlApisDataData(url, true);
            } else if (url.contains("sharechat.com")) {
                Log.i("ttt clip", "work 66666");
                try {
                    // new CallGetShareChatData().execute(url);
                    url = Utils.extractUrls(url).get(0);
                    Log.e("ttt", "subssssssss11 " + url);
                    int index = url.lastIndexOf('/') + 1;
                    url = url.substring(index);
                    Log.e("ttt", "subssssssss " + url);
                    JSONObject jsonObject = new JSONObject("{\"bn\":\"broker3\",\"userId\":644045091,\"passCode\":\"52859d76753457f8dcae\",\"client\":\"web\",\"message\":{\"key\":\"" + url + "\",\"ph\":\"" + url + "\"}}");
                    AndroidNetworking.post("https://apis.sharechat.com/requestType45")
                            .addJSONObjectBody(jsonObject)
                            .addHeaders("Content-Type", "application/json")
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    if (!fromService) {
                                        pd.dismiss();
                                    }
                                    String matag;
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.toString());
                                        matag = jsonObject.getJSONObject("payload").getJSONObject("d").getString("v");
                                        Log.e("ttt", "wojfdjhfdjh " + matag);
                                        DownloadFileMain.startDownloading(context, matag, "Sharechat_" + System.currentTimeMillis(), ".mp4");
                                    } catch (Exception e) {
                                        matag = "";
                                        e.printStackTrace();
                                        if (!fromService) {
                                            pd.dismiss();
                                        }
                                    }
                                }

                                @Override
                                public void onError(ANError error) {
                                    if (!fromService) {
                                        pd.dismiss();
                                    }
                                }
                            });
                } catch (Exception e) {
                    if (!fromService) {
                        pd.dismiss();
                    }
                }
            } else if (url.contains("roposo.com")) {
                Log.i("ttt clip", "work 66666");
                new callGetRoposoData().execute(url);
                Log.i("ttt clip", "work 1111111");
//            } else if (url.contains("snackvideo.com") || url.contains("sck.io")) {
//
//                //    new callGetSnackAppData().execute(url);
//
//                if (url.contains("snackvideo.com")) {
//                    new callGetSnackAppData().execute(url);
//                } else if (url.contains("sck.io")) {
//                    getSnackVideoData(url, Mcontext);
//                }
            } else if (url.contains("facebook.com") || url.contains("fb.watch")) {
//                FbVideoDownloader fbVideoDownloader = new FbVideoDownloader(mContext, url, 0);
                FbVideoDownloader fbVideoDownloader = new FbVideoDownloader(mContext, url, 0);
//                FbVideoDownloader fbVideoDownloader = new FbVideoDownloader(mContext, url, 2);
                fbVideoDownloader.setFromWeb(false);
                fbVideoDownloader.setListener(listener);
                fbVideoDownloader.downloadVideo();
                if (!fromService) {
                    pd.dismiss();
                }
            } else if (url.contains("blogspot.com")) {
                CalldlApisDataData(url, true);
            } else if (url.contains("instagram.com")) {
//                new GetInstagramVideo().execute(url);
                startInstaDownload(url);
            } else if (url.contains("bilibili.com")) {
                new callGetbilibiliAppData().execute(url);
            } else if (url.contains("mitron.tv")) {
                new CallMitronData().execute(url);
            } else if (url.contains("josh")) {
                new CallJoshData().execute(url);
            } else if (url.contains("triller")) {
                //  new CallTrillerData().execute(url);
                getTrillerData(url);
            } else if (url.contains("rizzle")) {
                new CallRizzleData().execute(url);
//            } else if (url.contains("solidfiles")) {
//
//                SolidfilesDownloader solidfilesDownloader = new SolidfilesDownloader(Mcontext, url);
//                solidfilesDownloader.DownloadVideo();
//
//            } else if (url.contains("audioboom")) {
//
//                AudioboomDownloader audioboomDownloader = new AudioboomDownloader(Mcontext, url);
//                audioboomDownloader.DownloadVideo();
            } else if (url.contains("ifunny")) {
                new CallIfunnyData().execute(url);
            } else if (url.contains("xvideos-vn.com")) {
                new CallXVideoData().execute(url);
            } else if (url.contains("trell.co")) {
                new CalltrellData().execute(url);
            } else if (url.contains("boloindya.com")) {
                new CallBoloindyaData().execute(url);
            } else if (url.contains("chingari")) {
                CallchingariData(url);
            } else if (url.contains("dubsmash")) {
                new CalldubsmashData().execute(url);
            } else if (url.contains("bittube")) {
                String myurlis1 = url;
                if (myurlis1.contains(".tv")) {
                    String str = "/";
                    myurlis1 = myurlis1.split(str)[myurlis1.split(str).length - 1];
                    myurlis1 = "https://bittube.video/videos/watch/" +
                            myurlis1;
                }
//                new CallDriveData().execute(myurlis1);
            } else if (url.contains("drive.google.com") ||
                    url.contains("mp4upload") ||
                    url.contains("ok.ru") ||
                    url.contains("mediafire") ||
                    url.contains("gphoto") ||
                    url.contains("uptostream") ||
                    url.contains("fembed") ||
                    url.contains("cocoscope") ||
                    url.contains("sendvid") ||
                    url.contains("vivo") ||
                    url.contains("fourShared")) {
//                new CallDriveData().execute(url);
            } else if (url.contains("hind")) {
            } else if (url.contains("hind")) {
                new CallhindData().execute(url);
            } else if (url.contains("topbuzz.com")) {
                TopBuzzDownloader downloader = new TopBuzzDownloader(mContext, url);
                downloader.setListener(listener);
                downloader.isFromWeb = false;
                downloader.downloadVideo();
                pd.dismiss();
            } else if (url.contains("buzzvideo.com")) {
                BuzzVideoDownloader downloader = new BuzzVideoDownloader(mContext, url);
                downloader.setListener(listener);
                downloader.isFromWeb = false;
                downloader.DownloadVideo();
                pd.dismiss();
            } else if (url.contains("vimeo.com")) {
                CalldlApisDataData(url, true);
            } else if (url.contains("twitter.com")) {
                TwitterVideoDownloader downloader = new TwitterVideoDownloader(mContext, url);
                downloader.setListener(listener);
                downloader.setFromWeb(false);
                downloader.downloadVideo();
                pd.dismiss();
            }
            //new
            //working
            else if (url.contains("buzzfeed.com")) {
                if (!fromService) {
                    pd.dismiss();

                }
            }
            //TODO Add quality list
            else if (url.contains("flickr") && url.contains("flic.kr")) {
                CalldlApisDataData(url, true);
            } else if (url.contains("streamable")) {
                CalldlApisDataData(url, true);
            } else if (url.contains("vk.com")) {
                //    CalldlApisDataData(url, false);
                CallVKData(url, true);
            } else if (url.contains("redd.it") || url.contains("reddit")) {
                //  CalldlApisDataData(url, true);
                new CallRedditData().execute("https://redditsave.com/info?url=" + url);
                //  CallREditData(url, true);
            } else if (url.contains("soundcloud")) {
//                if (Constants.showsoundcloud) {
//                    url = url.replace("//m.", "//");
//                    CalldlApisDataData(url, true);
//                } else {
//                    if (!fromService) {
//                        pd.dismiss();
//                       
//                    }
//                }
            } else if (url.contains("bandcamp")) {
                CalldlApisDataData(url, true);
                //   CallsoundData(url, false);
            } else if (url.contains("mxtakatak")) {
                String finalUrl3 = url;
                if (finalUrl3.contains("share.mxtakatak.com")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            HttpURLConnection con = null;
                            try {
                                con = (HttpURLConnection) (new URL(finalUrl3).openConnection());
                                con.setInstanceFollowRedirects(false);
                                con.connect();
                                int responseCode = con.getResponseCode();
                                Log.e("ttt", "" + responseCode);
                                String location = con.getHeaderField("Location");
                                Log.e("ttt", location);
                                if (location != null && !location.equals("") && location.contains("https://www.mxtakatak.com/")) {
                                    String urls = location.split("/")[5];
                                    urls = urls.substring(0, urls.indexOf("?"));
                                    String newuuu = "https://mxshorts.akamaized.net/video/" + urls + "/download/1/h264_high_720.mp4";
                                    String nametitle = "Mxtaktak_" +
                                            System.currentTimeMillis();
                                    DownloadFileMain.startDownloading(mContext, newuuu, nametitle, ".mp4");
                                }
                                if (!fromService) {
                                    pd.dismiss();
                                }
                            } catch (Exception e) {
                                if (!fromService) {
                                    pd.dismiss();

                                }
                            }
                        }
                    }).start();
                } else {
                    try {
                        String urls = finalUrl3.split("/")[5];
                        urls = urls.substring(0, urls.indexOf("?"));
                        String newuuu = "https://mxshorts.akamaized.net/video/" + urls + "/download/1/h264_high_720.mp4";
                        String nametitle = "Mxtaktak_" +
                                System.currentTimeMillis();
                        if (!fromService) {
                            pd.dismiss();
                        }
                        DownloadFileMain.startDownloading(mContext, newuuu, nametitle, ".mp4");
                    } catch (Exception e) {
                        if (!fromService) {
                            pd.dismiss();

                        }
                    }
                }

            } else if (url.contains("cocoscope")) {
                // CallsoundData(url, false);
                CalldlApisDataData(url, true);
            } else if (url.contains("test.com")) {
                new CallgaanaData().execute(url);
                // CallsoundData(url, false);
                //  CalldlApisDataData(url, true);
            } else if (url.contains("20min.ch")) {
//                Twenty_min_ch_Downloader twenty_min_ch_downloader = new Twenty_min_ch_Downloader(Mcontext, url);
//                twenty_min_ch_downloader.DownloadVideo();
            } else if (url.contains("gaana")) {
                String finalUrl = url;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpURLConnection con = null;
                        try {
                            con = (HttpURLConnection) (new URL("https://tinyurl.com/f67p797b").openConnection());
                            con.setInstanceFollowRedirects(false);
                            con.connect();
                            int responseCode = con.getResponseCode();
                            Log.e("ttt", "" + responseCode);
                            String location = con.getHeaderField("Location");
                            Log.e("ttt", location);
                            AndroidNetworking.post(location)
                                    .addBodyParameter("url", finalUrl)
                                    .addBodyParameter("weburl", "https://video.infusiblecoder.com/")
                                    .setPriority(Priority.MEDIUM)
                                    .build()
                                    .getAsJSONObject(new JSONObjectRequestListener() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            if (!fromService) {
                                                pd.dismiss();
                                            }
                                            String matag;
                                            try {
                                                JSONObject jsonObject = new JSONObject(response.toString());
                                                matag = jsonObject.getJSONArray("songlinks").getJSONObject(0).getString("songurl");
                                                Log.e("ttt", "wojfdjhfdjh " + matag);
                                                DownloadFileMain.startDownloading(context, matag, "Gaana_" + System.currentTimeMillis(), ".mp3");
                                            } catch (Exception e) {
                                                matag = "";
                                                e.printStackTrace();
                                                if (!fromService) {
                                                    pd.dismiss();
                                                }
                                            }
                                        }

                                        @Override/**/
                                        public void onError(ANError error) {
                                            Log.e("ttt", "wojfdjhfdjh error = " + error.getMessage());
                                            if (!fromService) {
                                                pd.dismiss();
                                            }
                                        }
                                    });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else if (url.contains("izlesene")) {
                //CallsoundData(url, false);
                CalldlApisDataData(url, false);
            } else if (url.contains("linkedin")) {
                //   CalldlApisDataData(url, false);
                new CallLinkedinData().execute(url);
            } else if (url.contains("kwai") || url.contains("kw.ai")) {
                //http://s.kw.ai/p/BhhKA7HG
                //  CalldlApisDataData(url, false);
//                KwaiDownloader kwaiDownloader = new KwaiDownloader(Mcontext, url);
//                kwaiDownloader.DownloadVideo();
            } else if (url.contains("bitchute")) {
                CalldlApisDataData(url, false);
            } else if (url.contains("douyin")) {
                try {
                    String[] idis = url.split("/");
                    AndroidNetworking.get("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + idis[idis.length - 1])
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    if (!fromService) {
                                        pd.dismiss();
                                    }
                                    String matag;
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.toString());
                                        JSONArray itemlist = jsonObject.getJSONArray("item_list");
                                        matag = itemlist.getJSONObject(0).getJSONObject("video").getJSONObject("play_addr").getJSONArray("url_list").getString(0);
                                        Log.e("ttt", "wojfdjhfdjh " + matag);
                                        DownloadFileMain.startDownloading(context, matag, "Douyin_" + System.currentTimeMillis(), ".mp4");
                                    } catch (Exception e) {
                                        matag = "";
                                        e.printStackTrace();
                                        if (!fromService) {
                                            pd.dismiss();
                                        }
                                    }
                                }

                                @Override/**/
                                public void onError(ANError error) {
                                    if (!fromService) {
                                        pd.dismiss();
                                    }
                                }
                            });
                } catch (Exception e) {
                }
            } else if (url.contains("dailymotion") || url.contains("dai.ly")) {
                DailyMotionDownloader mashableDownloader = new DailyMotionDownloader(mContext, url);
                mashableDownloader.setListener(listener);
                mashableDownloader.isFromWeb = false;
                mashableDownloader.DownloadVideo();
                pd.dismiss();
//                mashableDownloader.DownloadVideo();

                Log.i("DailyMotionDownloader", "work 0");

                String mnnn = DailyMotionDownloader.getVideoId(url);
                Log.i("DailyMotionDownloader", "work 2 " + mnnn);

//                String mnnn = DownloadUtils.getDailyVideoId(url);
//                Log.i("DailyMotionDownloader", "work 2 " + mnnn);
//                Log.i("DailyMotionDownloader", "URL: " + url);
//                AndroidNetworking.get("https://api.1qvid.com/api/v1/videoInfo?videoId=" + mnnn + "&host=dailymotion")
//                //AndroidNetworking.get("https://freedownloadvideo.net/dailymotion-video-downloader#url=" + url)
//                        //    .addHeaders("User-Agent", "Mozilla/5.0 (Linux; Android 10;TXY567) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/8399.0.9993.96 Mobile Safari/599.36")
//                        .setPriority(Priority.MEDIUM)
//                        .build()
//                        .getAsJSONArray(new JSONArrayRequestListener() {
//                            @Override
//                            public void onResponse(JSONArray response) {
//                                Log.i("DailyMotionDownloader", "work 2");
//                                try {
//                                    JSONArray jsonArray12 = new JSONArray(response.toString());
//                                    JSONArray jsonArraye = jsonArray12.getJSONObject(0).getJSONArray("formats");
//                                    ArrayList<String> urls = new ArrayList<>();
//                                    ArrayList<String> qualities = new ArrayList<>();
//                                    ArrayList<VideoInfo> videos = new ArrayList<>();
//                                    String title = jsonArray12.getJSONObject(0).getString("title");
//                                    String thumbnail = jsonArray12.getJSONObject(0).getString("thumbnail");
//                                    String duration = jsonArray12.getJSONObject(0).getString("duration");
//                                    for (int i = 0; i < jsonArraye.length(); i++) {
////                                        String urlis = jsonArraye.getJSONObject(i).getString("url");
//                                        String urlis = jsonArraye.getJSONObject(i).getString("url");
//                                        urls.add(urlis);
//                                        Log.i("DailyMotionDownloader", "URLIS: " + urlis);
//                                        String resolutionis = jsonArraye.getJSONObject(i).getString("formatNote");
//                                        qualities.add(resolutionis);
//                                        String formatName = jsonArraye.getJSONObject(i).getString("formatName");
//                                        String ext = DownloadUtils.addCharacterExt(formatName);
//                                        VideoInfo videoInfo = new VideoInfo();
//                                        videoInfo.setUrl(urlis);
//                                        videoInfo.setFormat(resolutionis);
//                                        videoInfo.setTitle(title);
//                                        videoInfo.setThumbnail(thumbnail);
//                                        videoInfo.setDuration(duration);
//                                        videoInfo.setExt(ext);
//                                        videos.add(videoInfo);
//                                    }
//                                    String[] arr = new String[qualities.size()];
//                                    arr = qualities.toArray(arr);
////                                    InfoListDownloadBottomSheet downloadBottomSheet =
////                                            InfoListDownloadBottomSheet.Companion.newInstance(
////                                                    title,
////                                                    duration,
////                                                    thumbnail,
////                                                    videos);
////                                    downloadBottomSheet.setMListener(video ->
////                                            DownloadFileMain.startDownloading(context, video.getUrl(), video.getTitle(), video.getExt()));
////                                    downloadBottomSheet.show(fragment.getParentFragmentManager(), "aaa");
//                                    listener.onDataResult(true, true, videos);
//                                    if (!fromService) {
//                                        pd.dismiss();
//                                    }
//                                } catch (Exception e) {
//                                    if (!fromService) {
//                                        pd.dismiss();
//                                    }
//                                    listener.onDataResult(false, false, null);
//                                    Log.i("DailyMotionDownloader", "work 3" + e.getLocalizedMessage());
//                                }
//                            }
//
//                            @Override
//                            public void onError(ANError anError) {
//                                if (!fromService) {
//                                    pd.dismiss();
//                                }
//                                listener.onDataResult(false, false, null);
//                                Log.i("DailyMotionDownloader", "work 4" + anError.getLocalizedMessage());
//                            }
//                        });
            } else if (url.contains("espn.com")) {
                CalldlApisDataData(url, true);
            } else if (url.contains("mashable.com")) {
                //   CalldlApisDataData(url, true);
//                MashableDownloader mashableDownloader = new MashableDownloader(Mcontext, url);
//                mashableDownloader.DownloadVideo();
            } else if (url.contains("coub")) {
                CalldlApisDataData(url, true);
                //Todo revert later
//            } else if (url.contains("kickstarter")) {
//                KickstarterDownloader kickstarterDownloader = new KickstarterDownloader(Mcontext, url);
//                kickstarterDownloader.DownloadVideo();
//            } else if (url.contains("aparat")) {
//                AparatDownloader aparatDownloader = new AparatDownloader(Mcontext, url);
//                aparatDownloader.DownloadVideo();
//
//
//            } else if (url.contains("allocine.fr")) {
//                AllocineDownloader allocineDownloader = new AllocineDownloader(Mcontext, url);
//                allocineDownloader.DownloadVideo();
//Todo revert later
            } else if (url.contains("ted.com")) {
                CalldlApisDataData(url, true);
            } else if (url.contains("twitch")) {
                CalldlApisDataData(url, true);
            } else if (url.contains("imdb.com")) {
//                CalldlApisDataData(url, false);
                new CallIMDBData().execute(url);
            } else if (url.contains("camdemy")) {
                CalldlApisDataData(url, false);
            } else if (url.contains("pinterest") || url.contains("pin.it")) {
                CalldlApisDataData(url, false);
            } else if (url.contains("imgur.com")) {
                url = url.replace("//m.", "//");
                CalldlApisDataData(url, false);
            } else if (url.contains("tumblr.com")) {
                new CalltumblerData().execute(url);
            }
            //TODO youtube from here
//            else if (url.contains("youtube.com") || url.contains("youtu.be")) {
//                if (Constants.showyoutube) {
//                    Log.i("ttt clip", "work 3");
//                    // getYoutubeDownloadUrl(url);
//                    CalldlApisDataData(url, true);
//                } else {
//                    if (!fromService) {
//                        pd.dismiss();
//                       
//                    }
//                }
//            }
            //TODO Till Here
            else {
                CalldlApisDataData(url, true);
                if (!fromService) {
                    pd.dismiss();

                }
            }
            prefs = mContext.getSharedPreferences("AppConfig", MODE_PRIVATE);
        } catch (Exception e) {
            //no-op
        }
    }

    private static void startInstaDownload(String url) {
        String urlSer;
        try {
            URI uri = new URI(url);
            urlSer = new URI(
                    uri.getScheme(),
                    uri.getAuthority(),
                    uri.getPath(),
                    null,  // Ignore the query part of the input url
                    uri.getFragment()
            ).toString();
        } catch (Exception ex) {
            mListener.onDataResult(false, false, null);
            return;
        }
        String cookie = CookieManager.getInstance().getCookie(url);
        if (cookie == null) {
            cookie = "";
        }
        Log.e("ttt", "startInstaDownload: " + url + ";;" + cookie);

        String urlWithoutLetterSqp = urlSer;

        urlWithoutLetterSqp = urlWithoutLetterSqp + "?__a=1";
        Log.e("ttt", "workkkkkkkkk 878888 " + urlWithoutLetterSqp);

        IssupportUrl = false;
        if (urlWithoutLetterSqp.contains("/reel/")) {
            urlWithoutLetterSqp = urlWithoutLetterSqp.replace("/reel/", "/p/");
            IssupportUrl = true;
        }

        if (urlWithoutLetterSqp.contains("/tv/")) {
            urlWithoutLetterSqp = urlWithoutLetterSqp.replace("/tv/", "/p/");
            IssupportUrl = true;
        }
        Log.e("ttt", "workkkkkkkkk 777777 " + urlWithoutLetterSqp);

        downloadInstagramImageOrVideoDataOld(
                urlWithoutLetterSqp,
                cookie
        );
    }

    private static void downloadInstagramImageOrVideoDataOld(String urlWithoutLetterSqp, String cookie) {
        final String[] myVideoUrlIs = new String[1];
        new Thread() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    String agent = System.getProperty("http.agent");
                    Log.e("ttt", "$agent: " + agent);
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    Request request = new Request.Builder()
                            .url(urlWithoutLetterSqp)
                            .method("GET", null)
                            .addHeader("Cookie", cookie)
                            .addHeader("User-Agent", agent)
                            .build();
                    Response response = null;
                    response = client.newCall(request).execute();

                    if (response.code() == 200) {
                        String dataJson = response.body().string();
                        try {
                            ModelInstagramResponse modelInstagramResponse = new Gson().fromJson(
                                    dataJson,
                                    ModelInstagramResponse.class
                            );

                            if (modelInstagramResponse.getModelGraphShortCode().getShortcode_media().getEdge_sidecar_to_children() != null) {
                                ModelGetEdgetoNode modelGetEdgetoNode =
                                        modelInstagramResponse.getModelGraphShortCode().getShortcode_media().getEdge_sidecar_to_children();

                                List<ModelEdNode> modelEdNodeArrayList =
                                        modelGetEdgetoNode.getModelEdNodes();
                                for (int i = 0; i < modelEdNodeArrayList.size(); i++) {
                                    Log.e("ttt", "instagram: " + modelEdNodeArrayList.size());
                                    if (modelEdNodeArrayList.get(i).getModelNode().isIs_video()) {
                                        myVideoUrlIs[0] = modelEdNodeArrayList.get(i).getModelNode().getVideo_url();
                                        VideoInfo videoInfo = new VideoInfo();
                                        videoInfo.setTitle("Instagram_" + System.currentTimeMillis());
                                        videoInfo.setUrl(myVideoUrlIs[0]);
                                        videoInfo.setExt(".mp4");
                                        ArrayList<VideoInfo> videos = new ArrayList<VideoInfo>();
                                        videos.add(videoInfo);
                                        if (!fromService) {
                                            pd.dismiss();
                                        }
                                        mListener.onDataResult(true, true, videos);
                                        myVideoUrlIs[0] = "";
                                        break;
                                    } else {
                                        if (!fromService) {
                                            pd.dismiss();
                                        }
                                    }
                                }
                            } else {
                                boolean isVideo =
                                        modelInstagramResponse.getModelGraphShortCode().getShortcode_media().isIs_video();
                                if (isVideo) {
                                    myVideoUrlIs[0] =
                                            modelInstagramResponse.getModelGraphShortCode().getShortcode_media().getVideo_url();

                                    VideoInfo videoInfo = new VideoInfo();
                                    videoInfo.setTitle("Instagram_" + System.currentTimeMillis());
                                    videoInfo.setUrl(myVideoUrlIs[0]);
                                    videoInfo.setExt(".mp4");
                                    ArrayList<VideoInfo> videos = new ArrayList<VideoInfo>();
                                    videos.add(videoInfo);
                                    if (!fromService) {
                                        pd.dismiss();
                                    }
                                    mListener.onDataResult(true, true, videos);
                                    myVideoUrlIs[0] = "";
                                } else {
                                    if (!fromService) {
                                        pd.dismiss();
                                    }
                                    mListener.onDataResult(false, false, null);
                                    Log.e("ttt", "error 1" );
                                }
                            }
                        } catch (Exception e) {
                            if (!fromService) {
                                pd.dismiss();
                            }
                            JSONObject objVideo = new JSONObject(dataJson)
                                    .getJSONArray("items").getJSONObject(0);

                            JSONArray jsonArrayVideo = objVideo
                                    .getJSONArray("video_versions");

                            double duration = objVideo.getDouble("video_duration");
                            String d = String.valueOf(Math.round(duration));

                            String thumbnail = objVideo.getJSONObject("image_versions2")
                                    .getJSONArray("candidates").getJSONObject(0)
                                    .getString("url");
                            ArrayList<VideoInfo> videos = new ArrayList<VideoInfo>();

                            for (int i = 0; i < jsonArrayVideo.length(); i++) {
                                Log.e("ttt", "catch 1 instagram: " + jsonArrayVideo.length());
                                JSONObject jsonObject = jsonArrayVideo.getJSONObject(i);
                                VideoInfo videoInfo = new VideoInfo();
                                videoInfo.setTitle("Instagram_" + System.currentTimeMillis());
                                videoInfo.setUrl(jsonObject.getString("url"));
                                videoInfo.setFormat(jsonObject.getString("width"));
                                videoInfo.setExt(".mp4");
                                videoInfo.setThumbnail(thumbnail);
                                videoInfo.setDuration(d);

                                videos.add(videoInfo);
                            }

                            mListener.onDataResult(true, true, videos);
                        }
                    } else {
                        if (!fromService) {
                            pd.dismiss();
                        }
                        mListener.onDataResult(false, false, null);
                        Log.e("ttt", "error 2" );
                    }
                } catch (Exception unused) {
                    if (!fromService) {
                        pd.dismiss();
                    }
                    mListener.onDataResult(false, false, null);
                    System.out.println("abc exp " + unused.getMessage());
                    Log.e("ttt", "error 3" );
                }
            }
        }.start();
    }

    private static void getTrillerData(String url1) {
        new Thread(() -> {
            HttpURLConnection con = null;
            try {
                con = (HttpURLConnection) (new URL(url1).openConnection());
                con.setInstanceFollowRedirects(false);
                con.connect();
                int responseCode = con.getResponseCode();
                Log.e("ttt", " " + responseCode);
                String location = con.getHeaderField("Location");
                Log.e("ttt", location);
                newurl = "https://social.triller.co/v1.5/api/videos/" + location.substring(location.indexOf("/video/") + 7);
                Log.e("ttt", "mydnewurlis=" + newurl);
                AndroidNetworking.get(newurl)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e("ttt", "mydnewurlis res =" + response);
                                if (!fromService) {
                                    pd.dismiss();
                                }
                                String matag;
                                try {
                                    matag = response.getJSONArray("videos").getJSONObject(0).getString("video_url");
                                    DownloadFileMain.startDownloading(mContext, matag, "Triller_" + System.currentTimeMillis(), ".mp4");
                                } catch (Exception e) {
                                    matag = "";
                                    e.printStackTrace();
                                    if (!fromService) {
                                        pd.dismiss();
                                    }
                                }
                            }

                            @Override/**/
                            public void onError(ANError error) {
                                Log.e("ttt", "wojfdjhfdjh error = " + error.getMessage());
                                if (!fromService) {
                                    pd.dismiss();
                                }
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void CallchingariData(String url) {
        try {
            String[] urlstr = url.split("=");
            AndroidNetworking.get("https://api.chingari.io/post/post_details/" + urlstr[1])
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (!fromService) {
                                pd.dismiss();
                            }
                            String matag = "";
                            try {
                                Log.e("ttt", "fjhjfhjsdfsdhf " + response);
                                JSONObject jsonObject = new JSONObject(response.toString());
                                JSONObject transcode = jsonObject.getJSONObject("data").getJSONObject("mediaLocation").getJSONObject("transcoded");
                                if (transcode.has("p1024")) {
                                    matag = transcode.getString("p1024");
                                } else if (transcode.has("p720")) {
                                    matag = transcode.getString("p720");
                                } else if (transcode.has("p480")) {
                                    matag = transcode.getString("p480");
                                }
                                matag = "https://media.chingari.io" + matag;
                                Log.e("ttt", "wojfdjhfdjh " + matag);
                                DownloadFileMain.startDownloading(mContext, matag, "Chingari_" + System.currentTimeMillis(), ".mp4");
                            } catch (Exception e) {
                                matag = "";
                                if (!fromService) {
                                    pd.dismiss();
                                }
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            if (!fromService) {
                                pd.dismiss();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showdownload_progress(int timeelipsed, long filesize) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                String filesize2 = Utils.getStringSizeLengthFile(filesize);
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);
                String contentTitle = "Downloaded";
                Intent notifyIntent = new Intent();
                PendingIntent notifyPendingIntent = PendingIntent.getActivity(mContext, DOWNLOAD_NOTIFICATION_ID, notifyIntent, (PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));
                NotificationCompat.Builder notificationBuilder = createNotificationBuilder("downloader_channel");
                notificationBuilder.setContentIntent(notifyPendingIntent);
                notificationBuilder.setTicker("Start downloading from the server");
                notificationBuilder.setOngoing(true);
                notificationBuilder.setAutoCancel(false);
                notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download);
                notificationBuilder.setContentTitle(contentTitle);
                notificationManagerCompat.notify(DOWNLOAD_NOTIFICATION_ID, notificationBuilder.build());
                long total = 0;
                int count, tmpPercentage = 0;
                Log.e("ttt", "mobile-ffmpegusa prog :" + "time elipsed: " + Utils.formatDuration(timeelipsed) + " downloaded : " + filesize2 + "%");
                notificationBuilder.setContentText("time elipsed: " + Utils.formatDuration(timeelipsed) + " downloaded : " + filesize2);
                notificationBuilder.setProgress(100, timeelipsed, false);
                notificationManagerCompat.notify(DOWNLOAD_NOTIFICATION_ID, notificationBuilder.build());
                notificationBuilder.setContentTitle(contentTitle);
                notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
                notificationBuilder.setOngoing(false);
                notificationBuilder.setAutoCancel(true);
                //notificationBuilder.setContentText("0");
                // notificationBuilder.setProgress(0, 0, false);
                notificationManagerCompat.notify(DOWNLOAD_NOTIFICATION_ID, notificationBuilder.build());
            }
        };
        thread.start();
    }

    private static NotificationCompat.Builder createNotificationBuilder(String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = mContext.getString(R.string.app_name);
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        return new NotificationCompat.Builder(mContext, channelId);
    }

    //TODO youtube comment till here
    public static void download(String url12) {
        String readLine;
        URL url = null;
        try {
            url = new URL(url12);
            Log.d("ThumbnailURL11111_1 ", url12);
//        URLConnection openConnection = url.openConnection();
//        openConnection.setRequestProperty("ModelUserInstagram-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            //       BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openConnection.getInputStream()));
            URL url1 = new URL(url12);
            URLConnection connection = url1.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            while ((readLine = bufferedReader.readLine()) != null) {
                //  readLine = bufferedReader.readLine();
                Log.d("ThumbnailURL11111_2  ", readLine);
                readLine = readLine.substring(readLine.indexOf("VideoObject"));
                String substring = readLine.substring(readLine.indexOf("thumbnailUrl") + 16);
                substring = substring.substring(0, substring.indexOf("\""));
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("ThumbnailURL: ");
                stringBuilder.append(substring);
                Log.d("ThumbnailURL", substring);
                readLine = readLine.substring(readLine.indexOf("contentUrl") + 13);
                readLine = readLine.substring(0, readLine.indexOf("?"));
                stringBuilder = new StringBuilder();
                stringBuilder.append("ContentURL: ");
                stringBuilder.append(readLine);
                Log.d("ContentURL1111 thumb  ", substring);
                Log.d("ContentURL1111", stringBuilder.toString());
                if (readLine == null) {
                    break;
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
//            Log.d("ContentURL1111 errrr", e.getMessage());
            e.printStackTrace();
        }
        // new downloadFile().Downloading(Mcontext, URL, Title, ".mp4");
        //   new DownloadFileFromURL().execute(new String[]{readLine});
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private static void addButtonToMainLayouttest_allvideo(final String videoTitle, String ytfile, String video_title) {
        // Display some buttons and let the user choose the format
        String btnText = videoTitle;
        Button btn = new Button(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 8, 8, 8);
        btn.setLayoutParams(params);
        // btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn.setBackground(mContext.getResources().getDrawable(R.drawable.btn_bg_download_screen));
        btn.setTextColor(Color.WHITE);
        btn.setText(btnText);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (windowManager2 != null) {
                    try {
                        windowManager2.removeView(mChatHeadView);
                    } catch (Exception e) {
                        Log.i("ttt", "error is " + e.getMessage());
                    }
                }
//                downloadFromUrl(ytfile.getUrl(), videoTitle, filename);
//
//
//                String downloadUrl = ytFiles.get(itag).getUrl();
                if (btnText.equals("audio/mp4")) {
                    DownloadFileMain.startDownloading(mContext, ytfile, video_title + "_" + videoTitle, ".mp3");
                } else {
                    DownloadFileMain.startDownloading(mContext, ytfile, video_title + "_" + videoTitle, ".mp4");
                }
                dialog_quality_allvids.dismiss();
            }
        });
        mainLayout.addView(btn);
    }

    @Keep
    public static void CallVKData(String url, boolean hasQualityOption) {
        if (hasQualityOption) {
            AndroidNetworking.get("https://api.vk.com/method/video.search?q=" + url + "&from=wall-51189706_396016&oauth=1&search_own=0&adult=0&search_own=0&count=1&extended=1&files=1&access_token=d9f1c406aeec6341131a62556d9eb76c7fe6d53defca0d9ce54535299664abf46e0a37af79004c30eb9b3&v=5.124")
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("ttt", "reccccc VVKK " + response);
                            try {
                                JSONObject reponsobj = response.getJSONObject("response");
                                JSONObject itemsarr = reponsobj.getJSONArray("items").getJSONObject(0);
                                JSONObject filesobj = itemsarr.getJSONObject("files");
                                ArrayList<String> mp4List = new ArrayList<>();
                                ArrayList<String> qualitylist = new ArrayList<>();
                                if (!filesobj.getString("mp4_240").isEmpty()) {
                                    String mp4_240 = filesobj.getString("mp4_240");
                                    mp4List.add(mp4_240);
                                    qualitylist.add("240p");
                                }
                                if (!filesobj.getString("mp4_360").isEmpty()) {
                                    String mp4_360 = filesobj.getString("mp4_360");
                                    mp4List.add(mp4_360);
                                    qualitylist.add("360p");
                                }
                                if (!filesobj.getString("mp4_480").isEmpty()) {
                                    String mp4_480 = filesobj.getString("mp4_480");
                                    mp4List.add(mp4_480);
                                    qualitylist.add("480p");
                                }
                                if (!filesobj.getString("mp4_720").isEmpty()) {
                                    String mp4_720 = filesobj.getString("mp4_720");
                                    mp4List.add(mp4_720);
                                    qualitylist.add("720p");
                                }
                                if (!filesobj.getString("mp4_1080").isEmpty()) {
                                    String mp4_1080 = filesobj.getString("mp4_1080");
                                    mp4List.add(mp4_1080);
                                    qualitylist.add("1080p");
                                }
                                if (hasQualityOption) {
                                    if (!((Activity) mContext).isFinishing()) {
                                        dialog_quality_allvids = new Dialog(mContext);
                                        if (!fromService) {
                                            pd.dismiss();
                                        }
                                        windowManager2 = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
                                        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        mChatHeadView = layoutInflater.inflate(R.layout.dialog_quality_ytd, null);
                                        dialog_quality_allvids.setContentView(mChatHeadView);
                                        mainLayout = mChatHeadView.findViewById(R.id.linlayout_dialog);
                                        img_dialog = mChatHeadView.findViewById(R.id.img_dialog);
                                        mainLayout = dialog_quality_allvids.findViewById(R.id.linlayout_dialog);
                                        img_dialog = dialog_quality_allvids.findViewById(R.id.img_dialog);
                                        int size = 0;
                                        try {
                                            DisplayMetrics displayMetrics = new DisplayMetrics();
                                            ((Activity) mContext).getWindowManager()
                                                    .getDefaultDisplay()
                                                    .getMetrics(displayMetrics);
                                            int height = displayMetrics.heightPixels;
                                            int width = displayMetrics.widthPixels;
                                            size = width / 2;
                                        } catch (Exception e) {
                                            size = WindowManager.LayoutParams.WRAP_CONTENT;
                                        }
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            params = new WindowManager.LayoutParams(
                                                    size,
                                                    WindowManager.LayoutParams.WRAP_CONTENT,
                                                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                                                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                                                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                                                    PixelFormat.TRANSLUCENT);
                                            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                                            params.x = 0;
                                            params.y = 100;
                                        } else {
                                            params = new WindowManager.LayoutParams(
                                                    size,
                                                    WindowManager.LayoutParams.WRAP_CONTENT,
                                                    WindowManager.LayoutParams.TYPE_PHONE,
                                                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                                                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                                                    PixelFormat.TRANSLUCENT);
                                            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                                            params.x = 0;
                                            params.y = 100;
                                        }
                                        // mainLayout.setLayoutParams(params);
                                        for (int i = 0; i < mp4List.size(); i++) {
                                            addButtonToMainLayouttest_allvideo(qualitylist.get(i), mp4List.get(i), "VK_" + qualitylist.get(i) + "_" + System.currentTimeMillis());
                                        }
                                        img_dialog.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog_quality_allvids.dismiss();
                                            }
                                        });
                                        dialog_quality_allvids.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                                        dialog_quality_allvids.getWindow().setAttributes(params);
                                        //  dialogquality.getWindow().setColorMode(Mcontext.getResources().getColor(R.color.colorAccent));
                                        dialog_quality_allvids.show();
                                        dialog_quality_allvids.show();
                                    }
                                } else {
                                    DownloadFileMain.startDownloading(mContext, mp4List.get(0), "VK_240p" + System.currentTimeMillis(), ".mp4");
                                    if (!fromService) {
                                        pd.dismiss();
                                    }
                                }
                            } catch (Exception str2) {
                                str2.printStackTrace();
                                if (!fromService) {
                                    pd.dismiss();
                                }
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.e("ttt", "reccccc VVKK error " + error);
                        }
                    });
        } else {
            AndroidNetworking.get(Constants.DlApisUrl2 + url + "&flatten=True")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("ttt", "reccccc VVKK " + response);
                            // Log.e("myresponse ",response.toString());
                            ArrayList<String> mp4List = new ArrayList<>();
                            ArrayList<String> qualitylist = new ArrayList<>();
                            try {
                                String url1 = response.getJSONArray("videos").getJSONObject(0).getJSONArray("formats").getJSONObject(0).getString("url");
                                String name1 = response.getJSONArray("videos").getJSONObject(0).getJSONArray("formats").getJSONObject(0).getString("format");
                                mp4List.add(url1);
                                qualitylist.add(name1);
                                String url2 = response.getJSONArray("videos").getJSONObject(0).getJSONArray("formats").getJSONObject(1).getString("url");
                                String name2 = response.getJSONArray("videos").getJSONObject(0).getJSONArray("formats").getJSONObject(1).getString("format");
                                mp4List.add(url2);
                                qualitylist.add(name2);
                                String url3 = response.getJSONArray("videos").getJSONObject(0).getJSONArray("formats").getJSONObject(2).getString("url");
                                String name3 = response.getJSONArray("videos").getJSONObject(0).getJSONArray("formats").getJSONObject(2).getString("format");
                                mp4List.add(url3);
                                qualitylist.add(name3);
                                dialog_quality_allvids = new Dialog(mContext);
                                if (!fromService) {
                                    pd.dismiss();
                                }
                                windowManager2 = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
                                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                mChatHeadView = layoutInflater.inflate(R.layout.dialog_quality_ytd, null);
                                dialog_quality_allvids.setContentView(mChatHeadView);
                                mainLayout = mChatHeadView.findViewById(R.id.linlayout_dialog);
                                img_dialog = mChatHeadView.findViewById(R.id.img_dialog);
                                mainLayout = dialog_quality_allvids.findViewById(R.id.linlayout_dialog);
                                img_dialog = dialog_quality_allvids.findViewById(R.id.img_dialog);
                                int size = 0;
                                try {
                                    DisplayMetrics displayMetrics = new DisplayMetrics();
                                    ((Activity) mContext).getWindowManager()
                                            .getDefaultDisplay()
                                            .getMetrics(displayMetrics);
                                    int height = displayMetrics.heightPixels;
                                    int width = displayMetrics.widthPixels;
                                    size = width / 2;
                                } catch (Exception e) {
                                    size = WindowManager.LayoutParams.WRAP_CONTENT;
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    params = new WindowManager.LayoutParams(
                                            size,
                                            WindowManager.LayoutParams.WRAP_CONTENT,
                                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                                                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                                            PixelFormat.TRANSLUCENT);
                                    params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                                    params.x = 0;
                                    params.y = 100;
                                } else {
                                    params = new WindowManager.LayoutParams(
                                            size,
                                            WindowManager.LayoutParams.WRAP_CONTENT,
                                            WindowManager.LayoutParams.TYPE_PHONE,
                                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                                                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                                            PixelFormat.TRANSLUCENT);
                                    params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                                    params.x = 0;
                                    params.y = 100;
                                }
                                // mainLayout.setLayoutParams(params);
                                for (int i = 0; i < mp4List.size(); i++) {
                                    addButtonToMainLayouttest_allvideo(qualitylist.get(i), mp4List.get(i), "VK_" + qualitylist.get(i) + "_" + System.currentTimeMillis());
                                }
                                img_dialog.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog_quality_allvids.dismiss();
                                    }
                                });
                                dialog_quality_allvids.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                                dialog_quality_allvids.getWindow().setAttributes(params);
                                //  dialogquality.getWindow().setColorMode(Mcontext.getResources().getColor(R.color.colorAccent));
                                dialog_quality_allvids.show();
                                dialog_quality_allvids.show();
                            } catch (Exception e) {
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.e("ttt", "reccccc VVKK error " + error.getErrorBody());
                            if (!fromService) {
                                pd.dismiss();
                            }
                        }
                    });
        }
    }

    @Keep
    public static void CalldlApisDataData(String url, boolean hasQualityOption) {
        AndroidNetworking.get(Constants.DlApisUrl3 + url + "&flatten=True")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("ttt", "reccccc VVKK " + response);
                        parseCalldlApisDataData(response);
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.e("ttt", "reccccc VVKK error " + error.getErrorBody());
                        if (!fromService) {
                            pd.dismiss();
                        }
                    }
                });
    }

    @Keep
    public static void parseCalldlApisDataData(JSONObject response) {
        try {
            try {
                if (!response.getString("error").equals("")) {
                    if (!fromService) {
                        pd.dismiss();
                    }
                    mListener.onDataResult(false, false, null);
                }
            } catch (Exception e) {
                Gson gson = new Gson();
                DLDataParser gsonObj = gson.fromJson(response.toString(), DLDataParser.class);
                Log.e(TAG, "parseCallDLApisDataData: url = " + gsonObj.getURL());
                if (gsonObj.getVideos().size() > 1) {
                    Log.e("ttt", "reccccc VVKK 0 ");
                    String title = gsonObj.getVideos().get(0).getTitle();
                    String duration = gsonObj.getVideos().get(0).getDuration();
                    String thumbnail = gsonObj.getVideos().get(0).getThumbnail();

                    if (gsonObj.getVideos().get(0).getProtocol() != null) {
                        List<Video> listData = gsonObj.getVideos();
                        List<Video> videoListSub = new ArrayList<>();
                        List<Video> videoListSubVideo = new ArrayList<>();
                        for (int i = 0; i < listData.size(); i++) {
                            if (listData.get(i).getProtocol().contains("http") && !listData.get(i).getProtocol().contains("http_dash_segments") && !listData.get(i).getURL().contains(".m3u8")) {
                                Log.e("ttt", " vifro= " + listData.get(i).getURL());
                                if (listData.get(i).getEXT().equals("m4a") ||
                                        listData.get(i).getEXT().equals("mp3") ||
                                        listData.get(i).getEXT().equals("wav")) {
                                    videoListSub.add(listData.get(i));
                                } else if (listData.get(i).getEXT().equals("mp4") || listData.get(i).getEXT().equals("mpeg")) {
                                    videoListSubVideo.add(listData.get(i));
                                }
                            }
                        }
                        Collections.reverse(videoListSubVideo);
                        ArrayList<VideoInfo> videos = new ArrayList<>();
                        for (Video format : videoListSub) {
                            VideoInfo videoInfo = new VideoInfo();
                            videoInfo.setTitle(title);
                            videoInfo.setDuration(duration);
                            videoInfo.setThumbnail(thumbnail);

                            videoInfo.setUrl(format.getUrl());
                            String ext = format.getExt();
                            if (!ext.contains(".")) {
                                ext = "." + ext;
                            }
                            videoInfo.setExt(ext);
                            videoInfo.setFormat(format.getFormat());
                            videoInfo.setFormatID(format.getFormatID());
                            videoInfo.setFileSize(format.getFileSize());
                            videoInfo.setHeight(format.getHeight());
                            videoInfo.setWidth(format.getWidth());
                            videos.add(videoInfo);
                        }
                        mListener.onDataResult(true, true, videos);
                    }
                    if (!fromService) {
                        pd.dismiss();
                    }
                } else {
                    Log.e("ttt", "reccccc VVKK 6 ");
                    String title = gsonObj.getVideos().get(0).getTitle();
                    String duration = gsonObj.getVideos().get(0).getDuration();
                    String thumbnail = gsonObj.getVideos().get(0).getThumbnail();

                    List<Format> formats = gsonObj.getVideos().get(0).getFormats();
                    ArrayList<VideoInfo> videos = new ArrayList<>();

                    if (formats != null || !formats.isEmpty()) {
                        for (Format format : formats) {
                            if ((format.getExt().equals("mp4") ||
                                    format.getExt().equals("mp3") ||
                                    format.getExt().equals("wav")) &&
                                    format.getProtocol().contains("http") &&
                                    !format.getProtocol().contains("http_dash_segments") &&
                                    !format.getURL().contains(".m3u8")
                            ) {
                                VideoInfo videoInfo = new VideoInfo();
                                videoInfo.setTitle(title);
                                videoInfo.setDuration(duration);
                                videoInfo.setThumbnail(thumbnail);
                                videoInfo.setUrl(format.getUrl());
                                String ext = format.getExt();
                                if (!ext.contains(".")) {
                                    ext = "." + ext;
                                }
                                videoInfo.setExt(ext);
                                videoInfo.setFormat(format.getFormat());
                                videoInfo.setFormatID(format.getFormatID());
                                videoInfo.setFileSize(format.getFilesize());
                                videoInfo.setHeight(format.getHeight());
                                videoInfo.setWidth(format.getWidth());
                                videos.add(videoInfo);
                            }
                        }
                    } else {
                        VideoInfo videoInfo = new VideoInfo();
                        videoInfo.setTitle(title);
                        videoInfo.setDuration(duration);
                        videoInfo.setThumbnail(thumbnail);
                        videoInfo.setUrl(gsonObj.getVideos().get(0).getUrl());

                        String ext = gsonObj.getVideos().get(0).getExt();
                        if (!ext.contains(".")) {
                            ext = "." + ext;
                        }
                        videoInfo.setExt(ext);
                        videoInfo.setFormat(gsonObj.getVideos().get(0).getFormat());
                        videoInfo.setFormatID(gsonObj.getVideos().get(0).getFormatID());
                        videoInfo.setFileSize(gsonObj.getVideos().get(0).getFileSize());
                        videoInfo.setHeight(gsonObj.getVideos().get(0).getHeight());
                        videoInfo.setWidth(gsonObj.getVideos().get(0).getWidth());
                        videos.add(videoInfo);
                    }
                    mListener.onDataResult(true, true, videos);
                    if (!fromService) {
                        pd.dismiss();
                    }
                }
            }
        } catch (Exception str2) {
            mListener.onDataResult(false, false, null);
            Log.e("ttt", "reccccc VVKK Error= " + str2);
            if (!fromService) {
                pd.dismiss();
            }
        }
    }

    private static class GetInstagramVideo extends AsyncTask<String, Void, Document> {
        Document doc;

        @Override
        protected Document doInBackground(String... urls) {
            Log.e("ttt", "instagram  " + urls[0]);
            try {
                doc = Jsoup.connect(urls[0]).get();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: Error" + e.getMessage());
            }
            return doc;
        }

        protected void onPostExecute(Document result) {
            if (!fromService) {
                pd.dismiss();
            }
            try {
                String url = result.select("meta[property=\"og:video\"]").last().attr("content");
                title = result.title();
                Log.e("ttt", "instagram  " + title);
                DownloadFileMain.startDownloading(mContext, url, title + ".mp4", ".mp4");
            } catch (Exception e) {
                Log.e("ttt", "instagram  " + e.getMessage());
                e.printStackTrace();

            }
        }
    }

    private static class callGetShareChatData extends AsyncTask<String, Void, Document> {
        Document ShareChatDoc;

        callGetShareChatData() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Document doInBackground(String... strArr) {
            //https://sharechat.com/post/rqM9Z47
            try {
                String myurlqq = strArr[0].replace("/post/", "/video/") + "?referrer=url";
                Log.d("ContentValues", "" + myurlqq);
                this.ShareChatDoc = Jsoup.connect(myurlqq).get();
            } catch (Exception strArr2) {
                strArr2.printStackTrace();
                Log.d("ContentValues", "" + strArr2);
            }
            return this.ShareChatDoc;
        }

        protected void onPostExecute(Document document) {
            try {
                VideoUrl = document.select("video").last().attr("src");
                Log.e("onPostExecute: ", VideoUrl);
                if (!VideoUrl.equals("")) {
                    if (!fromService) {
                        pd.dismiss();
                    }
                    try {
                        String myurldocument = VideoUrl;
                        String nametitle = "sharechat_" +
                                System.currentTimeMillis() +
                                ".mp4";
                        DownloadFileMain.startDownloading(mContext, myurldocument, nametitle, ".mp4");
                        //  Utils.startDownload(document, str, shareChatActivity, stringBuilder);
                        VideoUrl = "";
                        //   binding.etText.setText(charSequence);
                    } catch (Exception document2) {
                        if (!fromService) {
                            pd.dismiss();
                        }
                        document2.printStackTrace();

                    }
                }
            } catch (Exception document22) {
                if (!fromService) {
                    pd.dismiss();
                }
                document22.printStackTrace();

            }
        }
    }

    private static class callGetRoposoData extends AsyncTask<String, Void, Document> {
        Document ShareChatDoc;

        callGetRoposoData() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Document doInBackground(String... strArr) {
            try {
                this.ShareChatDoc = Jsoup.connect(strArr[0]).get();
            } catch (Exception strArr2) {
                strArr2.printStackTrace();
                Log.d("ContentValues roposo_", "doInBackground: Error");
            }
            return this.ShareChatDoc;
        }

        protected void onPostExecute(Document document) {
            String charSequence = "";
            try {
                if (!fromService) {
                    pd.dismiss();
                }
                VideoUrl = document.select("meta[property=\"og:video\"]").last().attr("content");
                Log.e("onPostExecute:roposo_ ", VideoUrl);
                if (!VideoUrl.equals(charSequence)) {
                    try {
                        String myurldocument = VideoUrl;
                        String nametitle = "roposo_" +
                                System.currentTimeMillis() +
                                ".mp4";
                        DownloadFileMain.startDownloading(mContext, myurldocument, nametitle, ".mp4");
                        //  Utils.startDownload(document, str, shareChatActivity, stringBuilder);
                        VideoUrl = charSequence;
                        //   binding.etText.setText(charSequence);
                    } catch (Exception document2) {
                        if (!fromService) {
                            pd.dismiss();
                        }
                        document2.printStackTrace();

                    }
                }
            } catch (Exception document22) {
                document22.printStackTrace();

                if (!fromService) {
                    pd.dismiss();
                }
            }
        }
    }

    public static class CallMitronData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            String str;
            try {
                String str2 = strArr[0];
                if (str2.contains("api.mitron.tv")) {
                    String[] split = str2.split("=");
                    str = "https://web.mitron.tv/video/" + split[split.length - 1];
                } else {
                    str = strArr[0];
                }
                this.RoposoDoc = Jsoup.connect(str).get();
            } catch (IOException e) {
                e.printStackTrace();
                if (!fromService) {
                    pd.dismiss();
                }
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            //   Log.e("ttt","myresponseis111 " + document.html());
            try {
                if (!fromService) {
                    pd.dismiss();
                }
                String html = document.select("script[id=\"__NEXT_DATA__\"]").last().html();
                if (!html.equals("")) {
                    this.VideoUrl = String.valueOf(new JSONObject(html).getJSONObject("props").getJSONObject("pageProps").getJSONObject("video").get("videoUrl"));
                    if (this.VideoUrl != null && !this.VideoUrl.equals("")) {
                        try {
                            String myurldocument = VideoUrl;
                            String nametitle = "mitron_" +
                                    System.currentTimeMillis();
                            DownloadFileMain.startDownloading(mContext, myurldocument, nametitle, ".mp4");
                            //  Utils.startDownload(document, str, shareChatActivity, stringBuilder);
                            VideoUrl = "";
                            //   binding.etText.setText(charSequence);
                        } catch (Exception document2) {
                            Log.e("ttt", "myresponseis111 exp1 " + document2.getMessage());
                            if (!fromService) {
                                pd.dismiss();
                            }
                            document2.printStackTrace();

                        }
                        return;
                    }
                    return;
                }

            } catch (Exception unused) {
                Log.e("ttt", "myresponseis111 exp " + unused.getMessage());
                if (!fromService) {
                    pd.dismiss();
                }

            }
        }
    }

    public static class CallJoshData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                this.RoposoDoc = Jsoup.connect(strArr[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            try {
                if (!fromService) {
                    pd.dismiss();
                }
                String html = document.select("script[id=\"__NEXT_DATA__\"]").last().html();
                if (!html.equals("")) {
                    this.VideoUrl = String.valueOf(new JSONObject(html)
                            .getJSONObject("props")
                            .getJSONObject("pageProps")
                            .getJSONObject("detail")
                            .getJSONObject("data")
                            .get("download_url"));
                    if (this.VideoUrl != null && !this.VideoUrl.equals("")) {
                        try {
                            String myurldocument = VideoUrl.replace("_wmj_480.mp4", "_480.mp4");
                            String nametitle = "joshvideo_" +
                                    System.currentTimeMillis();
                            DownloadFileMain.startDownloading(mContext, myurldocument, nametitle, ".mp4");
                            //  Utils.startDownload(document, str, shareChatActivity, stringBuilder);
                            VideoUrl = "";
                            //   binding.etText.setText(charSequence);
                        } catch (Exception document2) {
                            Log.e("ttt", "myresponseis111 exp1 " + document2.getMessage());
                            if (!fromService) {
                                pd.dismiss();
                            }
                            document2.printStackTrace();

                        }
                        return;
                    }
                    return;
                }

            } catch (Exception unused) {
                Log.e("ttt", "myresponseis111 exp " + unused.getMessage());
                if (!fromService) {
                    pd.dismiss();
                }

            }
        }
    }

    public static class CallRizzleData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                this.RoposoDoc = Jsoup.connect(strArr[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            try {
                if (!fromService) {
                    pd.dismiss();
                }
                String html = document.select("script[id=\"__NEXT_DATA__\"]").last().html();
                if (!html.equals("")) {
                    this.VideoUrl = String.valueOf(new JSONObject(html).getJSONObject("props").getJSONObject("pageProps").getJSONObject("post").getJSONObject("video").get("originalUrl"));
                    if (this.VideoUrl != null && !this.VideoUrl.equals("")) {
                        try {
                            String myurldocument = VideoUrl;
                            String nametitle = "rizzlevideo_" +
                                    System.currentTimeMillis();
                            DownloadFileMain.startDownloading(mContext, myurldocument, nametitle, ".mp4");
                            //  Utils.startDownload(document, str, shareChatActivity, stringBuilder);
                            VideoUrl = "";
                            //   binding.etText.setText(charSequence);
                        } catch (Exception document2) {
                            Log.e("ttt", "myresponseis111 exp1 " + document2.getMessage());
                            if (!fromService) {
                                pd.dismiss();
                            }
                            document2.printStackTrace();

                        }
                        return;
                    }
                    return;
                }

            } catch (Exception unused) {
                Log.e("ttt", "myresponseis111 exp " + unused.getMessage());
                if (!fromService) {
                    pd.dismiss();
                }

            }
        }
    }

    public static class CallIfunnyData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                this.RoposoDoc = Jsoup.connect(strArr[0])
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36")
                        .get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            try {
                if (!fromService) {
                    pd.dismiss();
                }
                this.VideoUrl = document.select("meta[property=\"og:video:url\"]").last().attr("content");
                if (this.VideoUrl != null && !this.VideoUrl.equals("")) {
                    try {
                        String myurldocument = VideoUrl;
                        String nametitle = "ifunny_" +
                                System.currentTimeMillis();
                        DownloadFileMain.startDownloading(mContext, myurldocument, nametitle, ".mp4");
                        VideoUrl = "";
                    } catch (Exception document2) {
                        Log.e("ttt", "myresponseis111 exp1 " + document2.getMessage());
                        if (!fromService) {
                            pd.dismiss();
                        }
                        document2.printStackTrace();

                    }
                    return;
                }

            } catch (Exception unused) {
                Log.e("ttt", "myresponseis111 exp " + unused.getMessage());
                if (!fromService) {
                    pd.dismiss();
                }

            }
        }
    }

    public static class CallXVideoData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                this.RoposoDoc = Jsoup.connect(strArr[0])
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36")
                        .get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            try {
                if (!fromService) {
                    pd.dismiss();
                }
                this.VideoUrl = document.select("meta[property=\"og:video\"]").last().attr("content");
                if (this.VideoUrl != null && !this.VideoUrl.equals("")) {
                    try {
                        String myurldocument = VideoUrl;
                        String nametitle = "AAA_" +
                                System.currentTimeMillis();
                        DownloadFileMain.startDownloading(mContext, myurldocument, nametitle, ".mp4");
                        VideoUrl = "";
                    } catch (Exception document2) {
                        Log.e("ttt", "myresponseis111 exp1 " + document2.getMessage());
                        if (!fromService) {
                            pd.dismiss();
                        }
                        document2.printStackTrace();
                    }
                    return;
                }

            } catch (Exception unused) {
                Log.e("ttt", "myresponseis111 exp " + unused.getMessage());
                if (!fromService) {
                    pd.dismiss();
                }

            }
        }
    }

    public static class CallLikeeDataOld extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                String str = strArr[0];
                if (str.contains("com")) {
                    str = str.replace("com", "video");
                }
                this.RoposoDoc = Jsoup.connect("https://likeedownloader.com/results").data("id", str).userAgent("Mozilla").post();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            try {
                if (!fromService) {
                    pd.dismiss();
                }
                this.VideoUrl = document.select("a.without_watermark").last().attr("href");
                if (this.VideoUrl != null && !this.VideoUrl.equals("")) {
                    try {
                        String myurldocument = VideoUrl;
                        String nametitle = "Likeevideo_" +
                                System.currentTimeMillis();
                        DownloadFileMain.startDownloading(mContext, myurldocument, nametitle, ".mp4");
                        VideoUrl = "";
                    } catch (Exception document2) {
                        Log.e("ttt", "myresponseis111 exp1 " + document2.getMessage());
                        if (!fromService) {
                            pd.dismiss();
                        }
                        document2.printStackTrace();

                    }
                    return;
                }

            } catch (Exception unused) {
                Log.e("ttt", "myresponseis111 exp " + unused.getMessage());
                if (!fromService) {
                    pd.dismiss();
                }

            }
        }
    }

    public static class CalltrellData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                this.RoposoDoc = Jsoup.connect(strArr[0]).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            try {
                if (!fromService) {
                    pd.dismiss();
                }
                String html = document.select("script[id=\"__NEXT_DATA__\"]").last().html();
                if (!html.equals("")) {
                    this.VideoUrl = String.valueOf(new JSONObject(new JSONObject(html)
                            .getJSONObject("props")
                            .getJSONObject("pageProps")
                            .getJSONObject("result")
                            .getJSONObject("result").getJSONObject("trail")
                            .getJSONArray("posts").get(0).toString())
                            .get("video"));
                    Log.e("ttt", "myresponseis111 exp991 " + VideoUrl);
                    if (this.VideoUrl != null && !this.VideoUrl.equals("")) {
                        try {
                            String myurldocument = VideoUrl;
                            String nametitle = "trellvideo_" +
                                    System.currentTimeMillis();
                            DownloadFileMain.startDownloading(mContext, myurldocument, nametitle, ".mp4");
                            //  Utils.startDownload(document, str, shareChatActivity, stringBuilder);
                            VideoUrl = "";
                            //   binding.etText.setText(charSequence);
                        } catch (Exception document2) {
                            Log.e("ttt", "myresponseis111 exp1 " + document2.getMessage());
                            if (!fromService) {
                                pd.dismiss();
                            }
                            document2.printStackTrace();

                        }
                        return;
                    }
                    return;
                }

            } catch (Exception unused) {
                Log.e("ttt", "myresponseis111 exp " + unused.getMessage());
                if (!fromService) {
                    pd.dismiss();
                }

            }
        }
    }

    public static class CallBoloindyaData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                this.RoposoDoc = Jsoup.connect(strArr[0]).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            try {
                if (!fromService) {
                    pd.dismiss();
                }
                Iterator it = document.getElementsByTag("script").iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Element element = (Element) it.next();
                    if (element.data().contains("videoFileCDN")) {
                        for (String str : element.data().split(StringUtils.LF)) {
                            if (str.contains("var videoFileCDN=\"https")) {
                                this.VideoUrl = str.split("=")[1]
                                        .replace("\"", "")
                                        .replace("\"", "")
                                        .replace(";", "");
                            }
                        }
                    }
                }
                if (this.VideoUrl.startsWith("//")) {
                    this.VideoUrl = "https:" + this.VideoUrl;
                }
                if (this.VideoUrl != null && !this.VideoUrl.equals("")) {
                    try {
                        String myurldocument = VideoUrl;
                        String nametitle = "Boloindyavideo_" +
                                System.currentTimeMillis();
                        DownloadFileMain.startDownloading(mContext, myurldocument, nametitle, ".mp4");
                        //  Utils.startDownload(document, str, shareChatActivity, stringBuilder);
                        VideoUrl = "";
                        //   binding.etText.setText(charSequence);
                    } catch (Exception document2) {
                        Log.e("ttt", "myresponseis111 exp1 " + document2.getMessage());
                        if (!fromService) {
                            pd.dismiss();
                        }
                        document2.printStackTrace();

                    }
                    return;
                }

            } catch (Exception unused) {
                Log.e("ttt", "myresponseis111 exp " + unused.getMessage());
                if (!fromService) {
                    pd.dismiss();
                }

            }
        }
    }

    public static class CallhindData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                this.RoposoDoc = Jsoup.connect(strArr[0]).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            try {
                if (!fromService) {
                    pd.dismiss();
                }
                Iterator it = document.getElementsByTag("script").iterator();
                while (it.hasNext()) {
                    Element element = (Element) it.next();
                    if (element.data().contains("window.__STATE__")) {
                        String replace = element.html().replace("window.__STATE__", "").replace(";", "")
                                .replace("=", "");
                        this.VideoUrl = String.valueOf(new JSONObject(new JSONObject(new JSONArray("[" + replace + "]")
                                .get(0).toString()).getJSONObject("feed").getJSONArray("feed")
                                .get(0).toString()).get("download_media"));
                        if (this.VideoUrl != null && !this.VideoUrl.equals("")) {
                            try {
                                String myurldocument = VideoUrl;
                                String nametitle = "hindvideo_" +
                                        System.currentTimeMillis();
                                DownloadFileMain.startDownloading(mContext, myurldocument, nametitle, ".mp4");
                                //  Utils.startDownload(document, str, shareChatActivity, stringBuilder);
                                VideoUrl = "";
                                //   binding.etText.setText(charSequence);
                            } catch (Exception document2) {
                                Log.e("ttt", "myresponseis111 exp1 " + document2.getMessage());
                                if (!fromService) {
                                    pd.dismiss();
                                }
                                document2.printStackTrace();

                            }
                            return;
                        }
                    }
                }

            } catch (Exception unused) {
                Log.e("ttt", "myresponseis111 exp " + unused.getMessage());
                if (!fromService) {
                    pd.dismiss();
                }

            }
        }
    }

    public static class CalldubsmashData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                this.RoposoDoc = Jsoup.connect(strArr[0]).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            try {
                if (!fromService) {
                    pd.dismiss();
                }
                this.VideoUrl = document.select("video").last().attr("src");
                if (this.VideoUrl != null && !this.VideoUrl.equals("")) {
                    try {
                        String myurldocument = VideoUrl;
                        String nametitle = "dubsmashvideo_" +
                                System.currentTimeMillis();
                        DownloadFileMain.startDownloading(mContext, myurldocument, nametitle, ".mp4");
                        //  Utils.startDownload(document, str, shareChatActivity, stringBuilder);
                        VideoUrl = "";
                        //   binding.etText.setText(charSequence);
                    } catch (Exception document2) {
                        Log.e("ttt", "myresponseis111 exp1 " + document2.getMessage());
                        if (!fromService) {
                            pd.dismiss();
                        }
                        document2.printStackTrace();

                    }
                    return;
                }

            } catch (Exception unused) {
                Log.e("ttt", "myresponseis111 exp " + unused.getMessage());
                if (!fromService) {
                    pd.dismiss();
                }

            }
        }
    }

    public static class CalltumblerData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                this.RoposoDoc = Jsoup.connect(strArr[0]).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            try {
                if (!fromService) {
                    pd.dismiss();
                }
                this.VideoUrl = document.select("source").last().attr("src");
                Log.e("ttt", "myresponseis111 exp1 " + VideoUrl);
                if (this.VideoUrl != null && !this.VideoUrl.equals("")) {
                    try {
                        String myurldocument = VideoUrl;
                        String nametitle = "tumbler_" +
                                System.currentTimeMillis();
                        DownloadFileMain.startDownloading(mContext, myurldocument, nametitle, ".mp4");
                        //  Utils.startDownload(document, str, shareChatActivity, stringBuilder);
                        VideoUrl = "";
                        //   binding.etText.setText(charSequence);
                    } catch (Exception document2) {
                        Log.e("ttt", "myresponseis111 exp1 " + document2.getMessage());
                        if (!fromService) {
                            pd.dismiss();
                        }
                        document2.printStackTrace();

                    }
                    return;
                }

            } catch (Exception unused) {
                Log.e("ttt", "myresponseis111 exp " + unused.getMessage());
                if (!fromService) {
                    pd.dismiss();
                }

            }
        }
    }

    public static class CallRedditData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                this.RoposoDoc = Jsoup.connect(strArr[0]).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            try {
                if (!fromService) {
                    pd.dismiss();
                }
                Log.e("ttt", "myresponseis111 exp166 " + document.select("a"));
                this.VideoUrl = document.select("a").get(10).attr("href");
                Log.e("ttt", "myresponseis111 exp1 " + VideoUrl);
                if (this.VideoUrl != null && !this.VideoUrl.equals("")) {
                    try {
                        String myurldocument = VideoUrl;
                        String nametitle = "Reddit_" +
                                System.currentTimeMillis();
                        DownloadFileMain.startDownloading(mContext, myurldocument, nametitle, ".mp4");
                        //  Utils.startDownload(document, str, shareChatActivity, stringBuilder);
                        VideoUrl = "";
                        //   binding.etText.setText(charSequence);
                    } catch (Exception document2) {
                        Log.e("ttt", "myresponseis111 exp1 " + document2.getMessage());
                        if (!fromService) {
                            pd.dismiss();
                        }
                        document2.printStackTrace();

                    }
                    return;
                }

            } catch (Exception unused) {
                Log.e("ttt", "myresponseis111 exp12 " + unused.getMessage());
                if (!fromService) {
                    pd.dismiss();
                }
            }
        }
    }

    public static class CallLinkedinData extends AsyncTask<String, Void, List<VideoInfo>> {
        Document roposoDoc;
        String VideoUrl = "";

        public List<VideoInfo> doInBackground(String... strArr) {
            try {
                this.roposoDoc = Jsoup.connect(strArr[0]).get();
                this.VideoUrl = roposoDoc.select("video").first().attr("data-sources");
                String title1 = getContentHTML(roposoDoc, "meta[property=\"og:title\"]", "content");
                String thumbnail = getContentHTML(roposoDoc, "meta[property=\"og:image\"]", "content");
                JSONArray jsonArray = new JSONArray(VideoUrl);
                Log.e("ttt", "myresponseis111 exp1 " + jsonArray.getJSONObject(0).getString("src"));
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(jsonArray.getJSONObject(0).getString("src"));
                arrayList.add(jsonArray.getJSONObject(1).getString("src"));
                CharSequence[] charSequenceArr = new CharSequence[arrayList.size()];
                charSequenceArr[0] = "SD";
                charSequenceArr[1] = "HD";

                ArrayList<VideoInfo> videos = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String title = "Linkedin_" + System.currentTimeMillis();
//                    long duration = obj.getLong("duration");
//                    String thumbnail = obj.getString("thumb");
                    String downloadUrl = obj.getString("src");
//                    String format = obj.getString("data-bitrate");
                    String format = getFormat(downloadUrl);
//                    long size = obj.getLong("size");

                    VideoInfo videoInfo = new VideoInfo();
//                    videoInfo.setTitle(title);
                    videoInfo.setTitle(title1);
//                    videoInfo.setDuration(String.valueOf(duration / 1000L));
                    videoInfo.setThumbnail(thumbnail);
                    videoInfo.setUrl(downloadUrl);
//                    videoInfo.setFileSize(size);
                    videoInfo.setFormat(format);
                    videoInfo.setExt(".mp4");
                    getSize(downloadUrl, videoInfo);
                    videos.add(videoInfo);
                }
                if (!fromService) {
                    pd.dismiss();
                }
                return videos;
            } catch (Exception e) {
                e.printStackTrace();
                if (!fromService) {
                    pd.dismiss();
                }
            }
//            return this.roposoDoc;
            return null;
        }

        public void onPostExecute(List<VideoInfo> list) {
            if (!fromService) {
                pd.dismiss();
            }
            mListener.onDataResult(list != null, true, list);

        }
    }

    private static void getSize(String link, VideoInfo info) {
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

    private static String getFormat(String link) {
        Pattern pattern = Pattern.compile("\\d+p");
        Matcher matcher = pattern.matcher(link);
        List<String> formats = new ArrayList<>();
        while (matcher.find()) {
            String data = link.substring(matcher.start(), matcher.end());
            formats.add(data);
            Log.e("ttt", "getFormat: " + data);
        }
        if (formats.isEmpty()) {
            return "";
        }
        return formats.get(0);
    }

    private static String getContentHTML(Document document, String query, String attrKey) {
        Elements es = document.select(query);
        if (es != null && !es.isEmpty()) {
            return es.first().attr(attrKey);
        }
        return null;
    }

    public static class CallgaanaData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                //   this.RoposoDoc = Jsoup.connect(strArr[0]).get();
                //   download_hlsganna("", ".mp4");
            } catch (Exception e) {
                Log.e("ttt", "jskdfhksdhfkshdfkhsdj " + e.getMessage());
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            if (!fromService) {
                pd.dismiss();
            }
        }
    }

    public static class CallmxtaktakData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                this.RoposoDoc = Jsoup.connect(strArr[0]).get();
                //   download_hlsganna("", ".mp4");
            } catch (Exception e) {
                Log.e("ttt", "jskdfhksdhfkshdfkhsdj " + e.getMessage());
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            String charSequence = "";
            try {
                String data = "";
                Iterator<Element> documentitrator = document.select("script").iterator();
                do {
                    if (!documentitrator.hasNext()) {
                        break;
                    }
                    data = documentitrator.next().data();
                    Log.e("onP4342424te:datais ", data);
                } while (!data.contains("window._state"));
                String stringbuil = data.substring(data.indexOf("{"), data.indexOf("};")) + "}";
                Log.e("onPostbjnkjh:oso_11 ", stringbuil);
                if (!document.equals("")) {
                    try {
                        JSONObject jSONObject = new JSONObject(stringbuil);
                        VideoUrl = jSONObject.getJSONObject("sharePhoto").getString("mp4Url");
                        Log.e("onPostExecute:roposo_ ", VideoUrl);
//                        getSnackVideoData(jSONObject.getString("shortUrl"), Mcontext);
                        VideoUrl = charSequence;
                    } catch (Exception document2) {
                        document2.printStackTrace();
                        Log.e("ttt", "respossss112212121qerrr " + document2.getMessage());
                        if (!fromService) {
                            pd.dismiss();
                        }
                    }
                }
            } catch (Exception e) {
                if (!fromService) {
                    pd.dismiss();
                }
                e.printStackTrace();
                Log.e("ttt", "respossss112212121qerrr " + e.getMessage());

            }
        }
    }

    public static class callGetSnackAppDataV2 extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                this.RoposoDoc = Jsoup.connect(strArr[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            try {
                if (!fromService) {
                    pd.dismiss();
                }
                String html = document.select("script[id=\"__NEXT_DATA__\"]").last().html();
                if (!html.equals("")) {
                    this.VideoUrl = String.valueOf(new JSONObject(html)
                            .getJSONObject("props")
                            .getJSONObject("pageProps")
                            .getJSONObject("detail")
                            .getJSONObject("data")
                            .get("download_url"));
                    if (this.VideoUrl != null && !this.VideoUrl.equals("")) {
                        try {
                            String myurldocument = VideoUrl.replace("_wmj_480.mp4", "_480.mp4");
                            String nametitle = "joshvideo_" +
                                    System.currentTimeMillis();
                            DownloadFileMain.startDownloading(mContext, myurldocument, nametitle, ".mp4");
                            //  Utils.startDownload(document, str, shareChatActivity, stringBuilder);
                            VideoUrl = "";
                            //   binding.etText.setText(charSequence);
                        } catch (Exception document2) {
                            Log.e("ttt", "myresponseis111 exp1 " + document2.getMessage());
                            if (!fromService) {
                                pd.dismiss();
                            }
                            document2.printStackTrace();

                        }
                        return;
                    }
                    return;
                }

            } catch (Exception unused) {
                Log.e("ttt", "myresponseis111 exp " + unused.getMessage());
                if (!fromService) {
                    pd.dismiss();
                }

            }
        }
    }

    private static class callGetSnackAppData extends AsyncTask<String, Void, Document> {
        Document ShareChatDoc;
        private Iterator<Element> abk;

        callGetSnackAppData() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Document doInBackground(String... strArr) {
            try {
                this.ShareChatDoc = Jsoup.connect(strArr[0]).get();
            } catch (Exception strArr2) {
                strArr2.printStackTrace();
                Log.d("ContentValues roposo_", "doInBackground: Error");
            }
            return this.ShareChatDoc;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        protected void onPostExecute(Document document) {
            String charSequence = "";
            try {
                String data = "";
                Iterator<Element> documentitrator = document.select("script").iterator();
                do {
                    if (!documentitrator.hasNext()) {
                        break;
                    }
                    data = documentitrator.next().data();
                    //  Log.e("onP4342424te:datais ", data);
                } while (!data.contains("window.__INITIAL_STATE__"));
                String stringbuil = data.substring(data.indexOf("{"), data.indexOf("}}}};")) + "}}}}";
                if (!document.equals("")) {
                    try {
                        JSONObject jSONObject = new JSONObject(stringbuil);
                        VideoUrl = jSONObject.getJSONObject("initData").getString("mp4Url");
                        Log.e("onPostExecute:snackvv_ ", VideoUrl);
                        // getSnackVideoData(jSONObject.getString("shortUrl"), Mcontext);
                        String nametitle = "snackvideo_" +
                                System.currentTimeMillis();
                        DownloadFileMain.startDownloading(mContext, VideoUrl, nametitle, ".mp4");
                        VideoUrl = charSequence;
                        if (!fromService) {
                            pd.dismiss();
                        }
                    } catch (Exception document2) {
                        document2.printStackTrace();
                        Log.e("ttt", "respossss112212121qerrr " + document2.getMessage());

                        if (!fromService) {
                            pd.dismiss();
                        }
                    }
                }

            } catch (Exception document22) {
                if (!fromService) {
                    pd.dismiss();
                }
                document22.printStackTrace();
                Log.e("ttt", "respossss112212121qerrr " + document22.getMessage());

            }
        }
    }

    private static class callGetbilibiliAppData extends AsyncTask<String, Void, Document> {
        Document ShareChatDoc;
        private Iterator<Element> abk;

        callGetbilibiliAppData() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Document doInBackground(String... strArr) {
            try {
                this.ShareChatDoc = Jsoup.connect(strArr[0]).get();
            } catch (Exception strArr2) {
                strArr2.printStackTrace();
                Log.d("ContentValues roposo_", "doInBackground: Error");
            }
            return this.ShareChatDoc;
        }

        protected void onPostExecute(Document document) {
            String charSequence = "";
            try {
                ArrayList<String> mp4List = new ArrayList<>();
                ArrayList<String> qualitylist = new ArrayList<>();
                String data = "";
                Iterator<Element> documentitrator = document.select("script").iterator();
                do {
                    if (!documentitrator.hasNext()) {
                        break;
                    }
                    data = documentitrator.next().data();
                    Log.e("onP4342424te:datais ", data);
                } while (!data.contains("window.__playinfo__="));
                String stringbuil = data.substring(data.indexOf("{"), data.lastIndexOf("}"));
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(stringbuil);
                stringBuilder.append("}");
                Log.e("onPostbjnkjhoso_11 ", stringBuilder.toString());
                if (!document.equals("")) {
                    try {
                        JSONObject jSONObject = new JSONObject(stringBuilder.toString());
                        JSONObject datajSONObject = jSONObject.getJSONObject("data");
                        JSONObject dashjSONObject1 = datajSONObject.getJSONObject("dash");
                        JSONArray videojSONObject1 = dashjSONObject1.getJSONArray("video");
                        Log.e("ttt", "respossss112212121URL)) " + videojSONObject1.getJSONObject(0).getString("base_url"));
                        for (int i = 0; i < videojSONObject1.length(); i++) {
                            JSONObject jsonObject12 = videojSONObject1.getJSONObject(i);
                            mp4List.add(jsonObject12.getString("base_url"));
                            qualitylist.add(jsonObject12.getString("width"));
                            Log.e("ttt", "respossss112212121URL " + jsonObject12.getString("base_url"));
                        }
                        try {
                            JSONArray audiojSONObject1 = dashjSONObject1.getJSONArray("audio");
                            for (int i = 0; i < audiojSONObject1.length(); i++) {
                                JSONObject jsonObject12 = audiojSONObject1.getJSONObject(i);
                                mp4List.add(jsonObject12.getString("base_url"));
                                qualitylist.add(jsonObject12.getString("mime_type"));
                                Log.e("ttt", "respossss112212121URL " + jsonObject12.getString("base_url"));
                            }
                        } catch (Exception e) {
                            if (!fromService) {
                                pd.dismiss();
                            }
                        }
                        if (videojSONObject1.length() > 0) {
                            if (!((Activity) mContext).isFinishing()) {
                                dialog_quality_allvids = new Dialog(mContext);
                                if (!fromService) {
                                    pd.dismiss();
                                }
                                windowManager2 = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
                                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                mChatHeadView = layoutInflater.inflate(R.layout.dialog_quality_ytd, null);
                                dialog_quality_allvids.setContentView(mChatHeadView);
                                mainLayout = mChatHeadView.findViewById(R.id.linlayout_dialog);
                                img_dialog = mChatHeadView.findViewById(R.id.img_dialog);
                                mainLayout = dialog_quality_allvids.findViewById(R.id.linlayout_dialog);
                                img_dialog = dialog_quality_allvids.findViewById(R.id.img_dialog);
                                int size = 0;
                                try {
                                    DisplayMetrics displayMetrics = new DisplayMetrics();
                                    ((Activity) mContext).getWindowManager()
                                            .getDefaultDisplay()
                                            .getMetrics(displayMetrics);
                                    int height = displayMetrics.heightPixels;
                                    int width = displayMetrics.widthPixels;
                                    size = width / 2;
                                } catch (Exception e) {
                                    size = WindowManager.LayoutParams.WRAP_CONTENT;
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    params = new WindowManager.LayoutParams(
                                            size,
                                            WindowManager.LayoutParams.WRAP_CONTENT,
                                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                                                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                                            PixelFormat.TRANSLUCENT);
                                    params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                                    params.x = 0;
                                    params.y = 100;
                                } else {
                                    params = new WindowManager.LayoutParams(
                                            size,
                                            WindowManager.LayoutParams.WRAP_CONTENT,
                                            WindowManager.LayoutParams.TYPE_PHONE,
                                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                                                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                                            PixelFormat.TRANSLUCENT);
                                    params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                                    params.x = 0;
                                    params.y = 100;
                                }
                                for (int i = 0; i < mp4List.size(); i++) {
                                    addButtonToMainLayouttest_allvideo(qualitylist.get(i), mp4List.get(i), "Bilibili_" + qualitylist.get(i) + "_" + System.currentTimeMillis());
                                }
                                img_dialog.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog_quality_allvids.dismiss();
                                    }
                                });
                                dialog_quality_allvids.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                                dialog_quality_allvids.getWindow().setAttributes(params);
                                //  dialogquality.getWindow().setColorMode(Mcontext.getResources().getColor(R.color.colorAccent));
                                dialog_quality_allvids.show();
                                dialog_quality_allvids.show();
                            }
                        } else {
                            DownloadFileMain.startDownloading(mContext, mp4List.get(0), "Bilibili_" + System.currentTimeMillis(), ".mp4");
                            if (!fromService) {
                                pd.dismiss();
                            }
                        }
                    } catch (Exception document2) {
                        document2.printStackTrace();
                        Log.e("ttt", "respossss112212121qerrr " + document2.getMessage());
                        if (!fromService) {
                            pd.dismiss();
                        }

                    }
                }
            } catch (Exception document22) {
                if (!fromService) {
                    pd.dismiss();
                }
                document22.printStackTrace();
                Log.e("ttt", "respossss112212121qerrr " + document22.getMessage());

            }
        }
    }

    public static class Call9gagData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://9gag.com/gag/aXowVXz")
                    .method("GET", null)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                Log.e("ttt", "mybodyhh1111>>> " + response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            Log.e("ttt", "myresponseis111 exp12222 " + document);
            try {
                if (!fromService) {
                    pd.dismiss();
                }
                Iterator it = document.getElementsByTag("script").iterator();
                while (it.hasNext()) {
                    Element element = (Element) it.next();
                    if (element.data().contains("window.__STATE__")) {
                        String replace = element.html().replace("window.__STATE__", "").replace(";", "")
                                .replace("=", "");
                        this.VideoUrl = String.valueOf(new JSONObject(new JSONObject(new JSONArray("[" + replace + "]")
                                .get(0).toString()).getJSONObject("feed").getJSONArray("feed")
                                .get(0).toString()).get("download_media"));
                        if (this.VideoUrl != null && !this.VideoUrl.equals("")) {
                            try {
                                String myurldocument = VideoUrl;
                                String nametitle = "hindvideo_" +
                                        System.currentTimeMillis();
                                DownloadFileMain.startDownloading(mContext, myurldocument, nametitle, ".mp4");
                                VideoUrl = "";
                            } catch (Exception e) {
                                Log.e("ttt", "myresponseis111 exp1 " + e.getMessage());
                                if (!fromService) {
                                    pd.dismiss();
                                }
                                e.printStackTrace();

                            }
                            return;
                        }
                    }
                }

            } catch (Exception unused) {
                Log.e("ttt", "myresponseis111 exp " + unused.getMessage());
                if (!fromService) {
                    pd.dismiss();
                }

            }
        }
    }

    public static class CallIMDBData extends AsyncTask<String, Void, Document> {
        Document RoposoDoc;
        String VideoUrl = "";

        public Document doInBackground(String... strArr) {
            try {
                String str2 = strArr[0];

                this.RoposoDoc = Jsoup.connect(str2).get();
            } catch (IOException e) {
                e.printStackTrace();
                if (!fromService) {
                    pd.dismiss();
                }
            }
            return this.RoposoDoc;
        }

        public void onPostExecute(Document document) {
            try {
                if (!fromService) {
                    pd.dismiss();
                }
                String html = document.select("script[id=\"__NEXT_DATA__\"]").last().html();
                if (!html.equals("")) {
                    JSONObject videoObj = new JSONObject(html)
                            .getJSONObject("props")
                            .getJSONObject("pageProps")
                            .getJSONObject("videoPlaybackData")
                            .getJSONObject("video");

                    JSONArray listOfUrls = videoObj
                            .getJSONArray("playbackURLs");
                    String thumbnail = videoObj.getJSONObject("thumbnail").getString("url");
                    String title = videoObj.getJSONObject("name").getString("value");
                    int duration = videoObj.getJSONObject("runtime").getInt("value");

                    ArrayList<String> arrayList = new ArrayList<>();
                    ArrayList<String> qualityArrayList = new ArrayList<>();
                    ArrayList<VideoInfo> videos = new ArrayList<>();

                    for (int i = 1; i < listOfUrls.length(); i++) {
                        System.out.println("myressss urls " + listOfUrls.getJSONObject(i).getString("url"));

                        arrayList.add(listOfUrls.getJSONObject(i).getString("url"));
                        qualityArrayList.add(listOfUrls.getJSONObject(i).getJSONObject("displayName").getString("value"));

                        VideoInfo videoInfo = new VideoInfo();
                        videoInfo.setTitle(title);
                        videoInfo.setDuration(String.valueOf(duration));
                        videoInfo.setThumbnail(thumbnail);
                        videoInfo.setUrl(listOfUrls.getJSONObject(i).getString("url"));
                        videoInfo.setExt(".mp4");
                        videoInfo.setFormat(listOfUrls.getJSONObject(i).getJSONObject("displayName").getString("value"));
                        videos.add(videoInfo);
                    }

                    mListener.onDataResult(true, true, videos);
                } else {
                    mListener.onDataResult(false, false, null);
                }
            } catch (Throwable f) {
                f.printStackTrace();
                if (!fromService) {
                    pd.dismiss();
                }
                mListener.onDataResult(false, false, null);
            }
        }


    }
}

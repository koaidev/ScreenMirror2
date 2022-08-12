package com.bangbangcoding.screenmirror.web.ui.paste;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import static com.bangbangcoding.screenmirror.web.utils.Constants.PREFIX_NAME;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import com.bangbangcoding.screenmirror.R;
import com.bangbangcoding.screenmirror.web.ui.CustomToast;
import com.bangbangcoding.screenmirror.web.ui.preference.UserPreferences;
import com.bangbangcoding.screenmirror.web.utils.Constants;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;

public class DownloadFileMain {
    public static DownloadManager downloadManager;
    public static long downloadID;
    private static String mBaseFolderPath;
    public static HashMap<Long, String> hashMapDownload = new HashMap<>();

    public static void startDownloading(final Context context, String url, String title, String ext) {
        try {
            String cutTitle = "";
            cutTitle = PREFIX_NAME + title;
            
            String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
            cutTitle = cutTitle.replaceAll(characterFilter, "");
            cutTitle = cutTitle.replaceAll("['+.^:,#\"\\\\/]", "-");
            cutTitle = cutTitle.replace(" ", "-").replace("!", "").replace(":", "");
            if (cutTitle.length() > 40) {
                cutTitle = cutTitle.substring(0, 40) + ext;
            } else {
                cutTitle +=  ext;
            }

            downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Log.e("ttt ", "URL-1: "+ url);

            url = url.replace("\"", "");

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

            Log.e("ttt ", "URL: "+ url);
            Log.e("ttt", "URI: "+ Uri.parse(url));
            request.setTitle(title);
            Log.e("ttt ", "Title: "+ title);
            request.setDescription(context.getString(R.string.txt_downloading));
            request.setDescription(context.getString(R.string.app_name));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            File fileVideo = Constants.INSTANCE.getExternalFile();
            if (!fileVideo.exists()) {
                fileVideo.mkdir();
            }
            File file_i = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + Constants.PATH_IMAGE);
            if (!file_i.exists()) {
                file_i.mkdir();
            }

            File file_a = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + Constants.PATH_AUDIO);
            if (!file_a.exists()) {
                file_a.mkdir();
            }
            switch (ext) {
                case ".png":
                    Log.e("ttt", "DownloadFile " + Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + Constants.PATH_IMAGE);
                    request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, Constants.PATH_IMAGE + cutTitle);
                    System.out.println("ttt folder" + Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + Constants.PATH_IMAGE);
                    break;
                case ".gif":
                    System.out.println("derringer " + Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + Constants.PATH_IMAGE);
                    request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, Constants.PATH_IMAGE + cutTitle);
                    System.out.println("ttt img " + Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + Constants.PATH_IMAGE);
                    break;
                case ".mp4":
                case ".webm":
                    Log.e("ttt", "DownloadFile " + DIRECTORY_DOWNLOADS + Constants.PATH_VIDEO);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        request.setDestinationInExternalPublicDir(context, DIRECTORY_DOWNLOADS, Constants.PATH_VIDEO + cutTitle);
//                    } else {
                        request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, Constants.PATH_VIDEO + cutTitle);
//                    }
                    System.out.println("ttt img " + Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + Constants.PATH_VIDEO);
                    break;
                case ".mp3":
                case ".m4a":
                case ".wav":
                    Log.e("ttt", "DownloadFile " + DIRECTORY_DOWNLOADS + Constants.PATH_AUDIO);
                    request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, Constants.PATH_AUDIO + cutTitle);
                    System.out.println("ttt img " + Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + Constants.PATH_AUDIO);
                    break;
            }

            request.allowScanningByMediaScanner();
            downloadID = downloadManager.enqueue(request);
//            hashMapDownload[downloadID] = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + Constants.PATH_VIDEO + cutTitle;
            Log.e("downloadFileName", cutTitle);
            Log.e("ttt", "download url = " + url);
            expandNotificationBar(context);
            CustomToast.makeText(context,context.getResources().getString(R.string.txt_down_start), CustomToast.SHORT, CustomToast.SUCCESS).show();
        } catch (Exception e) {
            CustomToast.makeText(context,context.getResources().getString(R.string.txt_error_occ), CustomToast.SHORT, CustomToast.WARNING).show();
        }
    }

    public static void startDownloadingWithWifi(final Context context, String url, String title, String ext, UserPreferences userPreferences) {
        if (userPreferences.getDownloadOnlyWifi() && !checkWifi(context)) {
            CustomToast.makeText(context,context.getResources().getString(R.string.txt_please_open_wifi), CustomToast.SHORT, CustomToast.WARNING).show();
            return;
        }
        startDownloading(context, url, title, ext);
    }

    static void expandNotificationBar(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.EXPAND_STATUS_BAR) != PackageManager.PERMISSION_GRANTED)
            return;

        try {
            Object service = context.getSystemService("statusbar");
            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
            Method expand = statusbarManager.getMethod("expandNotificationsPanel"); //<-
            expand.invoke(service);
        } catch (Exception e) {
            Log.e("StatusBar", e.toString());
            Toast.makeText(context.getApplicationContext(), "Expansion Not Working", Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean checkWifi(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            // connected to the internet
            switch (activeNetwork.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return true;
                default:
                    return false;
            }
        } else {
           return false;
        }
    }
}

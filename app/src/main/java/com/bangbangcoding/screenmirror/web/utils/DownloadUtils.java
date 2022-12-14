package com.bangbangcoding.screenmirror.web.utils;

import static com.bangbangcoding.screenmirror.web.utils.Constants.PREFIX_NAME;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import java.util.List;

public class DownloadUtils {

    public static long downloadData(Context context, DownloadManager downloadManager, String link, String filename, long id) {
        long downloadReference;


        Uri uri = Uri.parse(link);
        // Create request for android download manager
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle("Data Download");

        //Setting description of request
        request.setDescription("Android Data download using DownloadManager.");

        String subPath = PREFIX_NAME + id + "/" + filename;
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, subPath);

//        request.setde(context, Environment.DIRECTORY_DOWNLOADS, filename);

        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }

    public static void downloadListData(Context context, DownloadManager downloadManager, List<String> links, String path) {
        int i = 0;
        for (String l : links) {
            Uri uri = Uri.parse(l);
            DownloadManager.Request request = new DownloadManager.Request(uri);

            i++;

            //Setting title of request
            request.setTitle("Data Download");

            //Setting description of request
            request.setDescription("Android Data download using DownloadManager.");

            request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS,
                    path + i + ".png");

//        request.setde(context, Environment.DIRECTORY_DOWNLOADS, filename);

            //Enqueue download and save into referenceId
            /*downloadReference = */
//            downloadManager.enqueue(request);
            downloadManager.enqueue(request);

//            return downloadReference;
        }

        // Create request for android download manager
    }

    private static String downloadStatus(Cursor cursor) {
        //column for download  status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        //column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);

//        int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
//        String filename = cursor.getString(filenameIndex);

//        get the download filename
        int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
        String filename = cursor.getString(filenameIndex);

        String statusText = "";
        String reasonText = "";

        switch (status) {
            case DownloadManager.STATUS_FAILED:
                statusText = "STATUS_FAILED";
                switch (reason) {
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonText = "ERROR_CANNOT_RESUME";
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonText = "ERROR_DEVICE_NOT_FOUND";
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonText = "ERROR_FILE_ALREADY_EXISTS";
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonText = "ERROR_FILE_ERROR";
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonText = "ERROR_HTTP_DATA_ERROR";
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonText = "ERROR_INSUFFICIENT_SPACE";
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonText = "ERROR_TOO_MANY_REDIRECTS";
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reasonText = "ERROR_UNKNOWN";
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                statusText = "STATUS_PAUSED";
                switch (reason) {
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonText = "PAUSED_QUEUED_FOR_WIFI";
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonText = "PAUSED_UNKNOWN";
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonText = "PAUSED_WAITING_FOR_NETWORK";
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonText = "PAUSED_WAITING_TO_RETRY";
                        break;
                }
                break;
            case DownloadManager.STATUS_PENDING:
                statusText = "STATUS_PENDING";
                break;
            case DownloadManager.STATUS_RUNNING:
                statusText = "STATUS_RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = "STATUS_SUCCESSFUL";
                reasonText = "Filename:\n" + filename;
                break;
        }
        return filename;
    }

    public static String checkStatus(DownloadManager downloadManager, long downloadId) {
        String status = "";
        DownloadManager.Query ImageDownloadQuery = new DownloadManager.Query();
        //set the query filter to our previously Enqueued download
        ImageDownloadQuery.setFilterById(downloadId);

        //Query the download manager about downloads that have been requested.
        Cursor cursor = downloadManager.query(ImageDownloadQuery);
        if (cursor.moveToFirst()) {
            status = downloadStatus(cursor);
        }
        return status;
    }

    public static void downloadList() {

    }

    public static String getDailyVideoId(String link) {
        String videoId;
        if (link.contains("?")) {
            videoId = link.substring(link.indexOf("video/") + 1, link.indexOf("?"));
        } else {
            videoId = link.substring(link.indexOf("video/") + 1);
        }
        videoId = videoId.substring(videoId.lastIndexOf("/") + 1);
        return videoId;
    }

    public static String addCharacterExt(String ext) {
        String type1 = ext;

        if (!ext.contains(".")) {
            type1 = "." + ext;
        }
        return type1;
    }
}

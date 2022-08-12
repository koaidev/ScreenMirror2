package com.bangbangcoding.screenmirror.web.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.bangbangcoding.screenmirror.R;

import java.io.File;

public class ShareUtils {
    public static final String TEXT_PLAIN = "text/plain";

    public static void openInOtherApp(String filePath, Context context) {
        Uri uri = Uri.fromFile(new File(filePath));
        Intent share = new Intent("android.intent.action.VIEW");
        share.setDataAndType(uri, Utils.getMimeType(filePath));
        context.startActivity(Intent.createChooser(share, context.getString(R.string.share)));
    }

    public static void shareVideo(String path, Context context) {
        Intent share = new Intent("android.intent.action.SEND");
        share.setType(Utils.getMimeType(path));
        share.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(path)));
        context.startActivity(Intent.createChooser(share, context.getString(R.string.share_video)));
    }

    public static void shareTextUrl(String url, Context context) {
        Intent share = new Intent("android.intent.action.SEND");
        share.setType("text/plain");
        share.putExtra("android.intent.extra.TEXT", url);
        context.startActivity(Intent.createChooser(share, context.getString(R.string.share_link)));
    }
}

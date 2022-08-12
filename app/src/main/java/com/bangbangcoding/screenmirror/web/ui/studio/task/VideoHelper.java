package com.bangbangcoding.screenmirror.web.ui.studio.task;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.bangbangcoding.screenmirror.web.ui.studio.model.VideoDetail;


public class VideoHelper {
    public static VideoDetail getVideo(@NonNull Context context, @NonNull Uri videoUri) {
        Cursor cursor = context.getContentResolver().query(
                videoUri,
                new String[]{
                        MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.TITLE,
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media.WIDTH,
                        MediaStore.Video.Media.HEIGHT,
                        MediaStore.Video.Media.DURATION
                },
                null,
                null,
                null
        );

        if (cursor != null) {
            try {
                if (cursor.getCount() >= 1) {
                    if (cursor.moveToFirst()) {
                        int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        if (TextUtils.isEmpty(title)) {
                            title = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
                        }
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.WIDTH));
                        int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT));
                        return new VideoDetail(id, title, path, width, height, duration);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }

        return null;
    }

    public static String getVideoPath(@NonNull Context context, @NonNull Uri videoUri) {
        Cursor cursor = context.getContentResolver().query(videoUri, new String[]{MediaStore.Video.Media.DATA}, null, null, null);

        if (cursor != null) {
            try {
                if (cursor.getCount() >= 1) {
                    if (cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static VideoDetail getVideo(@NonNull Context context, @NonNull String videoPath) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.TITLE,
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media.WIDTH,
                        MediaStore.Video.Media.HEIGHT,
                        MediaStore.Video.Media.DURATION
                },
                MediaStore.Video.Media.DATA + " like ?",
                new String[]{videoPath},
                null
        );

        if (cursor != null) {
            try {
                if (cursor.getCount() >= 1) {
                    if (cursor.moveToFirst()) {
                        int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        if (TextUtils.isEmpty(title)) {
                            title = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
                        }
                        long duration;
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                            duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        } else {
                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            retriever.setDataSource(videoPath);
                            duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                        }
                        int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.WIDTH));
                        int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT));
                        return new VideoDetail(id, title, path, width, height, duration);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }

        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(videoPath);
            long duration = 0;
            if (!TextUtils.isEmpty(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))) {
                duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            }
            int width = 0;
            if (!TextUtils.isEmpty(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))) {
                width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            }
            int height = 0;
            if (!TextUtils.isEmpty(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))) {
                height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            }
            retriever.release();
            return new VideoDetail(-1, videoPath.substring(videoPath.lastIndexOf("/") + 1, videoPath.lastIndexOf(".")), videoPath, width, height, duration);
        } catch (Exception e) {
            Log.d("TAG", "error: " + e.getMessage());
            return new VideoDetail(-1, videoPath.substring(videoPath.lastIndexOf("/") + 1, videoPath.lastIndexOf(".")), videoPath, 0, 0, 0);
        }
    }
}

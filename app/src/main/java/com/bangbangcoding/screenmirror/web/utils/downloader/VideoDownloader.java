package com.bangbangcoding.screenmirror.web.utils.downloader;

public interface VideoDownloader {

    String getVideoId(String link);

    void downloadVideo();
}

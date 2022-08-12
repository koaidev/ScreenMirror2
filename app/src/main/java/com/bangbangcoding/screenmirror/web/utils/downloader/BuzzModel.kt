package com.bangbangcoding.screenmirror.web.utils.downloader

import com.google.gson.annotations.SerializedName

class BuzzModel {
    @SerializedName("article")
    var article: Article? = null


    @SerializedName("story")
    var story: Story? = null


    inner class Article {
        @SerializedName("video")
        var video: Video? = null
    }

    inner class Story {
        @SerializedName("video")
        var video: Video? = null
    }

    inner class Video {
        @SerializedName("videoTitle")
        var videoTitle: String? = null

        @SerializedName("videoHeight")
        var videoHeight: String? = null

        @SerializedName("videoThumbnail")
        var videoThumbnail: VideoThumbnail? = null

        @SerializedName("videoDuration")
        var videoDuration: String? = null

        @SerializedName("videoList")
        var videoList: VideoList? = null

        fun getVideoThumbnailExt() : String {
            return "https://p16-va.topbuzzcdn.com/origin/" + (videoThumbnail?.webUri ?: "")
        }

    }

    inner class VideoList {
        @SerializedName("video_3")
        var video3: Info? = null

        @SerializedName("video_2")
        var video2: Info? = null

        @SerializedName("video_1")
        var video1: Info? = null
    }

    inner class VideoThumbnail {
        @SerializedName("web_uri")
        var webUri: String? = null
    }

    inner class Info {
        @SerializedName("definition")
        var definition: String? = null

        @SerializedName("main_url")
        var mainUrl: String? = null
    }
}
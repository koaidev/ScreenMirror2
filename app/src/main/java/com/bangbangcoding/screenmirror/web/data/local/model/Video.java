package com.bangbangcoding.screenmirror.web.data.local.model;

import java.io.Serializable;

public class Video implements Serializable {
    public String title;
    public String videoUrl;

    public boolean isVideoAvailable() {
        return this.videoUrl != null;
    }
}

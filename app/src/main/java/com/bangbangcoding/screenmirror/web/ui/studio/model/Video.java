package com.bangbangcoding.screenmirror.web.ui.studio.model;

import java.io.Serializable;

public class Video implements Serializable {
    public String title;
    public String videoUrl;

    public boolean isVideoAvailable() {
        return this.videoUrl != null;
    }
}

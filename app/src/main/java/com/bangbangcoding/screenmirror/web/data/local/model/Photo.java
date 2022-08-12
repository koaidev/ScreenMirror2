package com.bangbangcoding.screenmirror.web.data.local.model;

import java.io.Serializable;

public class Photo implements Serializable {
    public long _id;
    public String description;
    public int height;
    public String ownerId;
    public String photoUrl;
    public String title;
    public int width;

    public boolean isGif() {
        return this.photoUrl.contains("gif");
    }
}

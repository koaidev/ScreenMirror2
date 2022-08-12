package com.bangbangcoding.screenmirror.web.ui.model.storymodels;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class ModelVersionOfVideo implements Serializable {

    @SerializedName("type")
    private int type;
    @SerializedName("width")
    private int width;
    @SerializedName("height")
    private int height;
    @SerializedName("url")
    private String url;
    @SerializedName("id")
    private String id;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
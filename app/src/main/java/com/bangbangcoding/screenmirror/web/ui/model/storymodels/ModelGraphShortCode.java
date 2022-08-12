package com.bangbangcoding.screenmirror.web.ui.model.storymodels;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep

public class ModelGraphShortCode implements Serializable {

    @SerializedName("shortcode_media")
    private ModelInstagramshortMediacode shortcode_media;

    public ModelInstagramshortMediacode getShortcode_media() {
        return shortcode_media;
    }

    public void setShortcode_media(ModelInstagramshortMediacode shortcode_media) {
        this.shortcode_media = shortcode_media;
    }
}

package com.bangbangcoding.screenmirror.web.ui.model.bulkdownloader;


import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep

public class EdgeLikedBy implements Serializable {
    @SerializedName("count")
    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    int count;
}

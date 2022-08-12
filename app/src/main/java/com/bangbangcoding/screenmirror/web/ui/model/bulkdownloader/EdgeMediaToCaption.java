package com.bangbangcoding.screenmirror.web.ui.model.bulkdownloader;

import androidx.annotation.Keep;

import java.util.List;

@Keep

public class EdgeMediaToCaption {
    public List<EdgeInfo> getEdges() {
        return this.edges;
    }

    public void setEdges(List<EdgeInfo> edges) {
        this.edges = edges;
    }

    List<EdgeInfo> edges;
}

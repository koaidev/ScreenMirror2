package com.bangbangcoding.screenmirror.web.ui.model.bulkdownloader;

import androidx.annotation.Keep;

@Keep

public class EdgeInfo {
    public NodeInfo getNode() {
        return this.node;
    }

    public void setNode(NodeInfo node) {
        this.node = node;
    }

    NodeInfo node;
}

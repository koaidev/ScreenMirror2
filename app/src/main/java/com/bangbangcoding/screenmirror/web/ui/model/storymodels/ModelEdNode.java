package com.bangbangcoding.screenmirror.web.ui.model.storymodels;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class ModelEdNode implements Serializable {

    @SerializedName("node")
    private ModelNode modelNode;

    public ModelNode getModelNode() {
        return modelNode;
    }

    public void setModelNode(ModelNode modelNode) {
        this.modelNode = modelNode;
    }
}

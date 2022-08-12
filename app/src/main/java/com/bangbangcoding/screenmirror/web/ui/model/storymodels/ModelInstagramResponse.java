package com.bangbangcoding.screenmirror.web.ui.model.storymodels;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class ModelInstagramResponse implements Serializable {

    @SerializedName("graphql")
    private ModelGraphShortCode modelGraphshortcode;

    public ModelGraphShortCode getModelGraphshortcode() {
        return modelGraphshortcode;
    }

    public void setModelGraphshortcode(ModelGraphShortCode modelGraphshortcode) {
        this.modelGraphshortcode = modelGraphshortcode;
    }
}

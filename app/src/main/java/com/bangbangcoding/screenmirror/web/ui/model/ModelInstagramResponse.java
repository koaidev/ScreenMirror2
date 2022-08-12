package com.bangbangcoding.screenmirror.web.ui.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;
import com.bangbangcoding.screenmirror.web.ui.model.storymodels.ModelGraphShortCode;

import java.io.Serializable;

@Keep
public class ModelInstagramResponse implements Serializable {

    @SerializedName("graphql")
    private ModelGraphShortCode modelGraphshortcode;

    public ModelGraphShortCode getModelGraphShortCode() {
        return modelGraphshortcode;
    }

    public void setModelGraphshortcode(ModelGraphShortCode modelGraphshortcode) {
        this.modelGraphshortcode = modelGraphshortcode;
    }
}

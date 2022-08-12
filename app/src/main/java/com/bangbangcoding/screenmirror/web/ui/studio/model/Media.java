package com.bangbangcoding.screenmirror.web.ui.studio.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public class Media implements Serializable {
    public Date dateAdded;
    public String orginalMediaPath;
    public Photo photo;
    public int size;
    public Video video;

    public static final transient Comparator<Media> DESCENDING_COMPARATOR = new Comparator<Media>() {
        @Override
        public int compare(Media d, Media d1) {
            return d1.size - d.size;
        }
    };


    public Media() {
        photo = new Photo();
        video = new Video();
        dateAdded = new Date();
    }

    public Media(String mediaUri) {
        orginalMediaPath = mediaUri;
        photo = new Photo();
        video = new Video();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Media media = (Media) o;
        if (orginalMediaPath != null) {
            return orginalMediaPath.equals(media.orginalMediaPath);
        } else if (media.orginalMediaPath == null) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return orginalMediaPath != null ? orginalMediaPath.hashCode() : 0;
    }

    public String getTitle() {
        String title = "";
        if (photo.title != null && photo.title.length() > 0) {
            return photo.title;
        }
        if (video.title != null && video.title.length() > 0) {
            return video.title;
        }
        if (photo.description != null && photo.description.length() > 0) {
            return photo.description;
        }
        if (orginalMediaPath != null) {
            return orginalMediaPath;
        }
        return title;
    }
}

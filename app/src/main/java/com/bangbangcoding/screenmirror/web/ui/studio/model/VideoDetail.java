package com.bangbangcoding.screenmirror.web.ui.studio.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class VideoDetail implements Parcelable, Comparable<VideoDetail> {
    private int id;
    private String title;
    private String path;
    private int width;
    private int height;
    private long duration;

    public VideoDetail(int id, String title, String path, int width, int height, long duration) {
        this.id = id;
        this.title = title;
        this.path = path;
        this.width = width;
        this.height = height;
        this.duration = duration;
    }


    protected VideoDetail(Parcel in) {
        id = in.readInt();
        title = in.readString();
        path = in.readString();
        width = in.readInt();
        height = in.readInt();
        duration = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(path);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeLong(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VideoDetail> CREATOR = new Creator<VideoDetail>() {
        @Override
        public VideoDetail createFromParcel(Parcel in) {
            return new VideoDetail(in);
        }

        @Override
        public VideoDetail[] newArray(int size) {
            return new VideoDetail[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getUri() {
        return "content://media/external/video/media/" + getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VideoDetail that = (VideoDetail) o;

        if (id != that.id) return false;
        if (width != that.width) return false;
        if (height != that.height) return false;
        if (duration != that.duration) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        return path != null ? path.equals(that.path) : that.path == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        return result;
    }

    @Override
    public int compareTo(@NonNull VideoDetail videoDetail) {
        if (id == Integer.MIN_VALUE) {
            return 1;
        }
        return title.compareToIgnoreCase(videoDetail.title);
    }

    @Override
    public String toString() {
        return "VideoDetail{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", path='" + path + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", duration=" + duration +
                '}';
    }
}

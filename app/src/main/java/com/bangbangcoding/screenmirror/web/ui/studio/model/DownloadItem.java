package com.bangbangcoding.screenmirror.web.ui.studio.model;

import android.util.Log;

import com.bangbangcoding.screenmirror.web.ui.studio.multidownload.DownloadInfo;
import com.bangbangcoding.screenmirror.web.utils.Utils;

import java.io.Serializable;

public class DownloadItem implements Serializable {
    private String dir;
    private String downloadPerSize;
    private String name;
    private int progress;
    private long size;
    private int status;
    private String url;
    private String cutTitle = "";

    public DownloadItem(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public DownloadItem(DownloadInfo downloadInfo) {
        setUrl(downloadInfo.getUri());
        setProgress(downloadInfo.getProgress());
        setDownloadPerSize(Utils.getDownloadPerSize(downloadInfo.getFinished(), downloadInfo.getLength()));
        this.status = 4;
        this.size = downloadInfo.getLength();
        this.name = downloadInfo.getName();
        Log.e("ttt", "Name: " + downloadInfo.getName());
        if (downloadInfo.getDir() != null) {
            this.dir = downloadInfo.getDir().getAbsolutePath();
        }
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDownloadPerSize() {
        return this.downloadPerSize;
    }

    public void setDownloadPerSize(String downloadPerSize) {
        this.downloadPerSize = downloadPerSize;
    }

    public int getProgress() {
        return this.progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDir() {
        return this.dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DownloadItem other = (DownloadItem) obj;
        if (url.equals(other.url))
            return false;
        if (progress != other.progress)
            return false;
        if (status != other.status)
            return false;
        if (size != other.size)
            return false;
        if (name == null) {
            return other.name == null;
        } else return name.equals(other.name);
    }

    public int hashCode() {
        int result;
        int hashCode;
        int i = 0;
        if (this.url != null) {
            result = this.url.hashCode();
        } else {
            result = 0;
        }
        int i2 = ((result * 31) + this.progress) * 31;
        if (this.downloadPerSize != null) {
            hashCode = this.downloadPerSize.hashCode();
        } else {
            hashCode = 0;
        }
        i2 = (((((i2 + hashCode) * 31) + this.status) * 31) + ((int) (this.size ^ (this.size >>> 32)))) * 31;
        if (this.name != null) {
            hashCode = this.name.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode = (i2 + hashCode) * 31;
        if (this.dir != null) {
            i = this.dir.hashCode();
        }
        return hashCode + i;

    }
}

package com.bangbangcoding.screenmirror.web.ui.studio.multidownload.db;

/**
 * Created by aspsine on 15-4-19.
 */
public class ThreadInfo {
    private int id;
    private String tag;
    private String uri;
    private long start;
    private long end;
    private long finished;
    private String name;

    public ThreadInfo() {
    }

    public ThreadInfo(int id, String tag, String uri, long finished) {
        this.id = id;
        this.tag = tag;
        this.uri = uri;
        this.finished = finished;
    }

    public ThreadInfo(int id, String tag, String uri, long start, long end, long finished) {
        this.id = id;
        this.tag = tag;
        this.uri = uri;
        this.start = start;
        this.end = end;
        this.finished = finished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getFinished() {
        return finished;
    }

    public void setFinished(long finished) {
        this.finished = finished;
    }

    public String getName(){ return name; }

    public void setName(String name){ this.name = name;}
}

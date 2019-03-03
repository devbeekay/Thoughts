package com.beekay.thoughts.model;

import java.io.Serializable;

public class Thought implements Serializable {

    private Long id;
    private String thoughtText;
    private String timestamp;
    private String imgSource;
    private byte[] img;
    private boolean starred;

    public Thought() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getThoughtText() {
        return thoughtText;
    }

    public void setThoughtText(String thoughtText) {
        this.thoughtText = thoughtText;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getImgSource() {
        return imgSource;
    }

    public void setImgSource(String imgSource) {
        this.imgSource = imgSource;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }
}

package com.example.ecommerceapplication.models;

import java.util.Date;

public class StoryModel {

    private String imageurl;
    private Date timestart;
    private Date timeend;
    private String storyid;
    private String userid;

    public StoryModel() {
    }

    public StoryModel(String imageurl, Date timestart, Date timeend, String storyid, String userid) {
        this.imageurl = imageurl;
        this.timestart = timestart;
        this.timeend = timeend;
        this.storyid = storyid;
        this.userid = userid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public long getTimestartMillis() {
        return timestart != null ? timestart.getTime() : 0;
    }

    public long getTimeendMillis() {
        return timeend != null ? timeend.getTime() : 0;
    }

    public Date getTimestart() {
        return timestart;
    }

    public void setTimestart(Date timestart) {
        this.timestart = timestart;
    }

    public Date getTimeend() {
        return timeend;
    }

    public void setTimeend(Date timeend) {
        this.timeend = timeend;
    }

    public String getStoryid() {
        return storyid;
    }

    public void setStoryid(String storyid) {
        this.storyid = storyid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}

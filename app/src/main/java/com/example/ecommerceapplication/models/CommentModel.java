package com.example.ecommerceapplication.models;

public class CommentModel {

    private String comment;
    private String publisherid;

    public CommentModel() {
    }

    public CommentModel(String comment, String publisherid) {
        this.comment = comment;
        this.publisherid = publisherid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisherid() {
        return publisherid;
    }

    public void setPublisherid(String publisherid) {
        this.publisherid = publisherid;
    }
}

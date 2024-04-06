package com.example.ecommerceapplication.models;

public class NotificationModel {

    private String userId;
    private String text;
    private String postId;
    private boolean isPost;
    private boolean isComment;

    public NotificationModel() {
    }

    public NotificationModel(String userId, String text, String postId, boolean isPost, boolean isComment) {
        this.userId = userId;
        this.text = text;
        this.postId = postId;
        this.isPost = isPost;
        this.isComment = isComment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }

    public boolean isComment() {
        return isComment;
    }

    public void setComment(boolean comment) {
        isComment = comment;
    }
}


package com.armjld.gymzo;

public class RatingData {
    String comment;
    String uname;
    String uid;
    String timestamp;
    String rating;
    String ppurl;

    public RatingData() {}

    public RatingData(String comment, String uname, String uid, String timestamp, String rating, String ppurl) {
        this.comment = comment;
        this.uname = uname;
        this.uid = uid;
        this.timestamp = timestamp;
        this.rating = rating;
        this.ppurl = ppurl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPpurl() {
        return ppurl;
    }

    public void setPpurl(String ppurl) {
        this.ppurl = ppurl;
    }
}

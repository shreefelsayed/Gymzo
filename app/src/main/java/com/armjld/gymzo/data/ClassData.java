package com.armjld.gymzo.data;

public class ClassData {

    String classid;
    String datee;
    String gymname;
    String gid;
    String gymurl;
    String israted;
    String rateid;
    String uid;


    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getDatee() {
        return datee;
    }

    public void setDatee(String datee) {
        this.datee = datee;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getGymurl() {
        return gymurl;
    }

    public void setGymurl(String gymurl) {
        this.gymurl = gymurl;
    }

    public String getIsrated() {
        return israted;
    }

    public void setIsrated(String israted) {
        this.israted = israted;
    }

    public String getRateid() {
        return rateid;
    }

    public void setRateid(String rateid) {
        this.rateid = rateid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGymname() {
        return gymname;
    }

    public void setGymname(String gymname) {
        this.gymname = gymname;
    }

    public ClassData() {}

    public ClassData(String classid, String datee, String gid, String gymurl, String israted, String rateid, String uid, String gymname) {
        this.classid = classid;
        this.datee = datee;
        this.gid = gid;
        this.gymurl = gymurl;
        this.israted = israted;
        this.rateid = rateid;
        this.uid = uid;
        this.gymname = gymname;
    }
}

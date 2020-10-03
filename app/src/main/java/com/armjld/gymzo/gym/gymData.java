package com.armjld.gymzo.gym;

public class gymData {

    private String gid;
    private String name;
    private String _long;
    private String lat;
    private String address;
    private String gov;
    private String city;
    private String rate;
    private String photo;
    private String type;
    private String classes;

    public double getDistance() {
        return distance;
    }

    private double distance = 0;

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String get_long() {
        return _long;
    }

    public void set_long(String _long) {
        this._long = _long;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGov() {
        return gov;
    }

    public void setGov(String gov) {
        this.gov = gov;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public gymData(){ }

    public gymData(String gid, String name, String _long, String lat, String address, String gov, String city, String rate, String photo, String type, String classes) {
        this.gid = gid;
        this.name = name;
        this._long = _long;
        this.lat = lat;
        this.address = address;
        this.gov = gov;
        this.city = city;
        this.rate = rate;
        this.photo = photo;
    }


}

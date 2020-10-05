package com.armjld.gymzo.data;

public class userData {
    
    private String id ;
    private String firstname ;
    private String lastname;
    private String pass ;
    private String email ;
    private String uPhone ;
    private String userDate ;
    private String supType;
    private String userURL ;
    private String supDate;
    private String remainClasses ;
    private String age;
    private String weight;
    private String hight;
    private String level;

    public userData(){ }

    public userData(String id, String firstname, String lastname, String pass, String email, String uPhone, String userDate, String supType, String userURL, String supDate, String remainClasses, String age, String weight, String hight, String level) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.pass = pass;
        this.email = email;
        this.uPhone = uPhone;
        this.userDate = userDate;
        this.supType = supType;
        this.userURL = userURL;
        this.supDate = supDate;
        this.remainClasses = remainClasses;
        this.age = age;
        this.weight = weight;
        this.hight = hight;
        this.level = level;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHight() {
        return hight;
    }

    public void setHight(String hight) {
        this.hight = hight;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getuPhone() {
        return uPhone;
    }

    public void setuPhone(String uPhone) {
        this.uPhone = uPhone;
    }

    public String getUserDate() {
        return userDate;
    }

    public void setUserDate(String userDate) {
        this.userDate = userDate;
    }

    public String getSupType() {
        return supType;
    }

    public void setSupType(String supType) {
        this.supType = supType;
    }

    public String getUserURL() {
        return userURL;
    }

    public void setUserURL(String userURL) {
        this.userURL = userURL;
    }

    public String getSupDate() {
        return supDate;
    }

    public void setSupDate(String supDate) {
        this.supDate = supDate;
    }

    public String getRemainClasses() {
        return remainClasses;
    }

    public void setRemainClasses(String remainClasses) {
        this.remainClasses = remainClasses;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}

package com.armjld.gymzo.login;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class UserInFormation {

    private static String id = "";

    private static String firstname = "";
    private static String lastname = "";
    private static String pass = "";
    private static String email = "";
    private static String uPhone = "";
    private static String userDate = "";
    private static String supType = "0";
    private static String userURL = "";
    private static String supDate ="";
    private static String remainClasses = "0";
    private static String birthdate = "";
    private static String gymdistance = "10";
    private static String recivenoti = "true";



    public static String getGymdistance() {
        return gymdistance;
    }

    public static void setGymdistance(String gymdistance) {
        UserInFormation.gymdistance = gymdistance;
    }


    public static String getRecivenoti() {
        return recivenoti;
    }

    public static void setRecivenoti(String recivenoti) {
        UserInFormation.recivenoti = recivenoti;
    }

    public static String getBirthdate() {
        return birthdate;
    }

    public static void setBirthdate(String birthdate) {
        UserInFormation.birthdate = birthdate;
    }

    public static String getGender() {
        return gender;
    }

    public static void setGender(String gender) {
        UserInFormation.gender = gender;
    }

    private static String gender = "";


    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        UserInFormation.id = id;
    }


    public static String getPass() {
        return pass;
    }

    public static void setPass(String pass) {
        UserInFormation.pass = pass;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        UserInFormation.email = email;
    }

    public static String getuPhone() {
        return uPhone;
    }

    public static void setuPhone(String uPhone) {
        UserInFormation.uPhone = uPhone;
    }

    public static String getUserDate() {
        return userDate;
    }

    public static void setUserDate(String userDate) {
        UserInFormation.userDate = userDate;
    }

    public static String getSupType() {
        return supType;
    }

    public static void setSupType(String supType) {
        UserInFormation.supType = supType;
    }

    public static String getUserURL() {
        return userURL;
    }

    public static void setUserURL(String userURL) {
        UserInFormation.userURL = userURL;
    }

    public static String getSupDate() {
        return supDate;
    }

    public static void setSupDate(String supDate) {
        UserInFormation.supDate = supDate;
    }

    public static String getRemainClasses() {
        return remainClasses;
    }

    public static void setRemainClasses(String remainClasses) {
        UserInFormation.remainClasses = remainClasses;
    }

    public static String getFirstname() {
        return firstname;
    }

    public static void setFirstname(String firstname) {
        UserInFormation.firstname = firstname;
    }

    public static String getLastname() {
        return lastname;
    }

    public static void setLastname(String lastname) {
        UserInFormation.lastname = lastname;
    }

    public static void clearUser() {
        setSupType("0");
        setSupDate("");
        setEmail("");
        setId("");
        setPass("");
        setuPhone("");
        setUserDate("");
        setFirstname("");
        setLastname("");
        setUserURL("");
        setRemainClasses("0");
        setBirthdate("");
        setGender("");
        setGymdistance("20");
    }
}

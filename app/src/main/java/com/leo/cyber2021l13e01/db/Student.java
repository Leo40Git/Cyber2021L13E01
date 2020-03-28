package com.leo.cyber2021l13e01.db;

public class Student {
    private String name;
    private String address;
    private String phoneHome;
    private String phoneMobile;
    private String momName;
    private String momPhone;
    private String dadName;
    private String dadPhone;

    public Student(String name, String address, String phoneHome, String phoneMobile, String momName, String momPhone, String dadName, String dadPhone) {
        this.name = name;
        this.address = address;
        this.phoneHome = phoneHome;
        this.phoneMobile = phoneMobile;
        this.momName = momName;
        this.momPhone = momPhone;
        this.dadName = dadName;
        this.dadPhone = dadPhone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneHome() {
        return phoneHome;
    }

    public void setPhoneHome(String phoneHome) {
        this.phoneHome = phoneHome;
    }

    public String getPhoneMobile() {
        return phoneMobile;
    }

    public void setPhoneMobile(String phoneMobile) {
        this.phoneMobile = phoneMobile;
    }

    public String getMomName() {
        return momName;
    }

    public void setMomName(String momName) {
        this.momName = momName;
    }

    public String getMomPhone() {
        return momPhone;
    }

    public void setMomPhone(String momPhone) {
        this.momPhone = momPhone;
    }

    public String getDadName() {
        return dadName;
    }

    public void setDadName(String dadName) {
        this.dadName = dadName;
    }

    public String getDadPhone() {
        return dadPhone;
    }

    public void setDadPhone(String dadPhone) {
        this.dadPhone = dadPhone;
    }
}

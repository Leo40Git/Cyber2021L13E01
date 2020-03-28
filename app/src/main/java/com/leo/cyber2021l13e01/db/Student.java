package com.leo.cyber2021l13e01.db;

/**
 * Represents a student in the database.<br>
 * Created by Leo40Git on 28/03/2020.
 * @author Leo40Git
 */
public class Student {
    private String name;
    private String address;
    private String phoneHome;
    private String phoneMobile;
    private String momName;
    private String momPhone;
    private String dadName;
    private String dadPhone;

    public Student() {}

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

    public String getAddress() {
        return address;
    }

    public String getPhoneHome() {
        return phoneHome;
    }

    public String getPhoneMobile() {
        return phoneMobile;
    }

    public String getMomName() {
        return momName;
    }

    public String getMomPhone() {
        return momPhone;
    }

    public String getDadName() {
        return dadName;
    }

    public String getDadPhone() {
        return dadPhone;
    }
}

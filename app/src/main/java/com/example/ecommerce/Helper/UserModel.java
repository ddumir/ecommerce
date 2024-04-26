package com.example.ecommerce.Helper;

import com.google.firebase.Timestamp;

public class UserModel {
    private String phone;
    private String UserName;
    private String Email;
    private Timestamp createdTimeStamp;


    public UserModel() {
    }

    public UserModel(String phone, String userName, String email, Timestamp createdTimeStamp) {
        this.phone = phone;
        UserName = userName;
        Email = email;
        this.createdTimeStamp = createdTimeStamp;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public Timestamp getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    public void setCreatedTimeStamp(Timestamp createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }
}
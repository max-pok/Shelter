package com.e.shelter.utilities;

public class reviewBox {
    private String userName;
    private String email;
    private String review;
    private String address;

    public reviewBox(String name, String email, String review, String address) {
        this.userName = name;
        this.address = address;
        this.email = email;
        this.review = review;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

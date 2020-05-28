package com.e.shelter.utilities;

public class Review {

    private String shelterName;
    private String userName;
    private String userEmail;
    private String review;
    private String star;
    private String time;

    public Review() {
    }

    public Review(String shelterName, String userName, String userEmail, String review, String star, String time){
        this.review=review;
        this.shelterName=shelterName;
        this.userName=userName;
        this.userEmail=userEmail;
        this.star=star;
        this.time=time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getShelterName() {
        return shelterName;
    }

    public void setShelterName(String shelterName) {
        this.shelterName = shelterName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }



}

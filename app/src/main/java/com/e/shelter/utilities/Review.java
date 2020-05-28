package com.e.shelter.utilities;

public class Review {

    private String shelterName;
    private String userName;
    private String userEmail;
    private String review;
    private String stars;
    private String time;

    public Review() {
    }

    public Review(String shelterName, String userName, String userEmail, String review, String stars, String time){
        this.review=review;
        this.shelterName=shelterName;
        this.userName=userName;
        this.userEmail=userEmail;
        this.stars = stars;
        this.time=time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
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

package com.e.shelter.utilities;

import org.bson.types.ObjectId;

public class InfoWindowData {
    public static String name;
    public static String address;
    public static String status;
    public static String capacity;
    public static String rating;
    public static ObjectId id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }
    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
    public ObjectId getId() {
        return id;
    }

    public void setid(ObjectId id) {
        this.id = id;
    }

}
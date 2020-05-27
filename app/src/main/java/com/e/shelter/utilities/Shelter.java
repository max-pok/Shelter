package com.e.shelter.utilities;

public class Shelter {
    private String name;
    private String address;
    private String lat;
    private String lon;
    private String status;
    private String capacity;
    private String rating;
    private String rateCount;

    public Shelter(String name, String address, String lat, String lon, String status, String capacity, String rating, String rateCount) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.status = status;
        this.capacity = capacity;
        this.rating = rating;
        this.rateCount = rateCount;
    }

    public Shelter() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getStatus() {
        return status;
    }

    public String getCapacity() {
        return capacity;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRateCount() {
        return rateCount;
    }

    public void setRateCount(String rateCount) {
        this.rateCount = rateCount;
    }

    @Override
    public String toString() {
        return "Shelter{"
                + "name='" + name + '\''
                + ", address='" + address + '\''
                + ", lat='" + lat + '\''
                + ", lon='" + lon + '\''
                + ", status='" + status + '\''
                + ", capacity='" + capacity + '\''
                + ", rating='" + rating + '\''
                + ", rateCount='" + rateCount
                + '\''
                + '}';
    }
}

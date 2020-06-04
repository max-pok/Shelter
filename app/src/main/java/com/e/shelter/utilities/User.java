package com.e.shelter.utilities;

import java.io.Serializable;


public class User implements Serializable {
    private String name;
    private String phoneNumber;
    private String permission;
    private String email;
    private Boolean blocked;



    public User() {
        //do not delete.
    }

    public User(String name, String phoneNumber, String permission,String email,Boolean blocked) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.permission = permission;
        this.email=email;
        this.blocked=blocked;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}

package com.e.shelter.utilities;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String phoneNumber;
    private String permission;

    public User() {
        //do not delete.
    }

    public User(String name, String phoneNumber, String permission) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.permission = permission;
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

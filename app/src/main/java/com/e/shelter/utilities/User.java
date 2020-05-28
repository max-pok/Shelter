package com.e.shelter.utilities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class User implements Serializable {
    private String name;
    private String phoneNumber;
    private String permission;
    public static List<String> Emails = new ArrayList<String>();


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

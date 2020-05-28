package com.e.shelter.utilities;

import java.io.Serializable;

public class Contact implements Serializable {
    private String name;
    private String nameInEnglish;
    private String phoneNumber;

    public Contact() {
    }



    public Contact(String name, String nameInEnglish, String phoneNumber) {
        this.name = name;
        this.nameInEnglish = nameInEnglish;
        this.phoneNumber = phoneNumber;
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

    public void setPhoneNumber(String phoneNumbers) {
        this.phoneNumber = phoneNumbers;
    }

    public String getNameInEnglish() {
        return nameInEnglish;
    }

    public void setNameInEnglish(String nameInEnglish) {
        this.nameInEnglish = nameInEnglish;
    }
}

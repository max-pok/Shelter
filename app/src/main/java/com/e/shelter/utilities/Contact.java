package com.e.shelter.utilities;

public class Contact {

    private String name;
    private String phoneNumber;
    private String nameInEnglish;

    public Contact(String name, String nameInEnglish, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.nameInEnglish = nameInEnglish;
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

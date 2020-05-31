package com.e.shelter.utilities;

public class Emails {
    private String email;
    private Boolean blocked;
    public Emails(){}
    public Emails(String email,Boolean blocked){
        this.email=email;
        this.blocked=blocked;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setBlocked(Boolean blocked){
         this.blocked=blocked;
    }
    public Boolean getBlocked(){
        return this.blocked;
    }
}

package com.e.shelter.utilities;

import java.util.ArrayList;

public class FavoriteShelter {
    private String email;
    private ArrayList<FavoriteCard> favoriteShelters;

    public FavoriteShelter() {
    }

    public FavoriteShelter(String email, ArrayList<FavoriteCard> favoriteShelters) {
        this.email = email;
        this.favoriteShelters = favoriteShelters;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<FavoriteCard> getFavoriteShelters() {
        return favoriteShelters;
    }

    public void setFavoriteShelters(ArrayList<FavoriteCard> favoriteShelters) {
        this.favoriteShelters = favoriteShelters;
    }
}

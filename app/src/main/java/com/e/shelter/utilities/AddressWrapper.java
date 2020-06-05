package com.e.shelter.utilities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

public class AddressWrapper implements Parcelable {
    @SerializedName("addressInHebrew")
    @Expose
    private String addressInHebrew;
    @SerializedName("addressInEnglish")
    @Expose
    private String addressInEnglish;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lon")
    @Expose
    private String lon;

    public AddressWrapper(Parcel in) {
        this.addressInHebrew = in.readString();
        this.addressInEnglish = in.readString();
        this.lat = in.readString();
        this.lon = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(addressInHebrew);
        dest.writeString(addressInEnglish);
        dest.writeString(lat);
        dest.writeString(lon);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public AddressWrapper(String addressInHebrew, String addressInEnglish, String lat, String lon) {
        this.addressInHebrew = addressInHebrew;
        this.addressInEnglish = addressInEnglish;
        this.lat = lat;
        this.lon = lon;
    }

    public String getAddressInHebrew() {
        return addressInHebrew;
    }

    public void setAddressInHebrew(String addressInHebrew) {
        this.addressInHebrew = addressInHebrew;
    }

    public String getAddressInEnglish() {
        return addressInEnglish;
    }

    public void setAddressInEnglish(String addressInEnglish) {
        this.addressInEnglish = addressInEnglish;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public static final Creator<AddressWrapper> CREATOR = new Creator<AddressWrapper>() {
        @Override
        public AddressWrapper createFromParcel(Parcel in) {
            return new AddressWrapper(in);
        }

        @Override
        public AddressWrapper[] newArray(int size) {
            return new AddressWrapper[size];
        }
    };
}

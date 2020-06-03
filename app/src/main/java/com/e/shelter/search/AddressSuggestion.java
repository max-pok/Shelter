package com.e.shelter.search;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

public class AddressSuggestion implements SearchSuggestion {
    private String addressName;
    private boolean isHistory = false;

    public AddressSuggestion(String suggestion) {
        this.addressName = suggestion.toLowerCase();
    }

    public AddressSuggestion(Parcel source) {
        this.addressName = source.readString();
        this.isHistory = source.readInt() != 0;
    }

    public void setIsHistory(boolean isHistory) {
        this.isHistory = isHistory;
    }

    public boolean getIsHistory() {
        return this.isHistory;
    }

    @Override
    public String getBody() {
        return addressName;
    }

    public static final Creator<AddressSuggestion> CREATOR = new Creator<AddressSuggestion>() {
        @Override
        public AddressSuggestion createFromParcel(Parcel in) {
            return new AddressSuggestion(in);
        }

        @Override
        public AddressSuggestion[] newArray(int size) {
            return new AddressSuggestion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(addressName);
        dest.writeInt(isHistory ? 1 : 0);
    }
}

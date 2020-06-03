package com.e.shelter.search;

import android.content.Context;
import android.widget.Filter;

import com.e.shelter.map.MapViewActivity;
import com.e.shelter.utilities.Address;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchHelper extends MapViewActivity {
    private static final String ADDRESSES_FILE_NAME = "addresses2.json";

    private static List<Address> addressList = new ArrayList<>();

    private static List<AddressSuggestion> addressSuggestions = new ArrayList<>();

    public interface OnFindAddressListener {
        void onResults(List<Address> results);
    }

    public interface OnFindSuggestionsListener {
        void onResults(List<AddressSuggestion> results);
    }

    public static List<AddressSuggestion> getHistory(Context context, int count) {
        List<AddressSuggestion> suggestionList = new ArrayList<>();
        AddressSuggestion colorSuggestion;
        for (int i = 0; i < addressSuggestions.size(); i++) {
            colorSuggestion = addressSuggestions.get(i);
            colorSuggestion.setIsHistory(true);
            suggestionList.add(colorSuggestion);
            if (suggestionList.size() == count) {
                break;
            }
        }
        return suggestionList;
    }

    public static void resetSuggestionsHistory() {
        for (AddressSuggestion colorSuggestion : addressSuggestions) {
            colorSuggestion.setIsHistory(false);
        }
    }

    public static void findSuggestions(Context context, String query, final int limit, final long simulatedDelay,
                                       final OnFindSuggestionsListener listener) {
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                SearchHelper.resetSuggestionsHistory();
                List<AddressSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (AddressSuggestion suggestion : addressSuggestions) {
                        if (suggestion.getBody().toUpperCase().startsWith(constraint.toString().toUpperCase())) {
                            suggestionList.add(suggestion);
                            if (limit != -1 && suggestionList.size() == limit) {
                                break;
                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
                Collections.sort(suggestionList, new Comparator<AddressSuggestion>() {
                    @Override
                    public int compare(AddressSuggestion lhs, AddressSuggestion rhs) {
                        return lhs.getIsHistory() ? -1 : 0;
                    }
                });
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (listener != null) {
                    listener.onResults((List<AddressSuggestion>) results.values);
                }
            }
        }.filter(query);

    }


    public static void findAddresses(Context context, String query, final int limit, final OnFindAddressListener listener) {
        initAddressesList(context);
        new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Address> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {
                    for (Address address : addressList) {
                        if (address.getAddressInEnglish().toUpperCase().startsWith(constraint.toString().toUpperCase())) {
                            suggestionList.add(address);
                        }
                        if (suggestionList.size() == limit) break;
                    }
                }
                FilterResults results = new FilterResults();
                results.values = suggestionList;
                results.count = suggestionList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (listener != null) {
                    listener.onResults((List<Address>) results.values);
                }
            }
        }.filter(query);

    }

    public static void initAddressesList(Context context) {
        if (addressList.isEmpty()) {
            String jsonString = loadJson(context);
            addressList = deserializeColors(jsonString);
        }
    }

    private static String loadJson(Context context) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(ADDRESSES_FILE_NAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return jsonString;
    }

    private static List<Address> deserializeColors(String jsonString) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<List<Address>>() {
        }.getType();
        return gson.fromJson(jsonString, collectionType);
    }
}

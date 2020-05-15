package com.e.shelter.adapers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.e.shelter.MapViewActivity;
import com.e.shelter.R;
import com.google.android.material.button.MaterialButton;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;

import org.bson.Document;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;
import com.e.shelter.utilities.reviewBox;

public class ReviewAdapter extends ArrayAdapter<reviewBox> {
    private static final String TAG = "CustomListAdapter";

    private Context mContext;
    private int mResource;
    //private int lastPosition = -1;
    private String mUserEmail;
    ReviewAdapter adapter;
    ArrayList<reviewBox> cards;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView name;
        TextView address;
        TextView email;
        TextView review;
    }

    public ReviewAdapter(Context context, int resource, ArrayList<reviewBox> objects, String userEmail) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mUserEmail = userEmail;
        adapter = this;
        cards = objects;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final View result;
        final ReviewAdapter.ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ReviewAdapter.ViewHolder();
            holder.name = convertView.findViewById(R.id.userName);
            holder.address = convertView.findViewById(R.id.shelterAddress);
            holder.email = convertView.findViewById(R.id.userEmail);
            holder.review = convertView.findViewById(R.id.userReview);
            result = convertView;
            convertView.setTag(holder);
        } else {
            holder = (ReviewAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        return convertView;

    }
}

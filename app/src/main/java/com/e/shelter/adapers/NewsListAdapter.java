package com.e.shelter.adapers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.e.shelter.R;
import com.e.shelter.utilities.News;
import com.google.android.material.button.MaterialButton;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NewsListAdapter extends ArrayAdapter<News> {
    private static final String TAG = "FavoriteListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    NewsListAdapter adapter;
    ArrayList<News> cards;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView title;
        TextView date;
        TextView description;
        ImageView newsImageView;
        MaterialButton shareButton;
        MaterialButton showMoreButton;
    }

    /**
     * Default constructor for the PersonListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public NewsListAdapter(Context context, int resource, ArrayList<News> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        adapter = this;
        cards = objects;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final View result;
        final NewsListAdapter.ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.news_title);
            holder.date = convertView.findViewById(R.id.news_date);
            holder.description = convertView.findViewById(R.id.news_description);
            holder.shareButton = convertView.findViewById(R.id.news_share_button);
            holder.showMoreButton = convertView.findViewById(R.id.news_show_more_button);
            result = convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(mContext,
//                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
//        result.startAnimation(animation);
        lastPosition = position;

        holder.title.setText(getItem(position).getTitle());
        holder.date.setText(getItem(position).getDate());
        holder.description.setText(getItem(position).getDescription());
        try {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(getItem(position).getUrlToImage()).getContent());
            holder.newsImageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}

package com.e.shelter;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;

import com.e.shelter.adapers.NewsListAdapter;
import com.e.shelter.utilities.News;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class NewsActivity extends MainActivity {

    private ListView newsListView;
    private ArrayList<News> newsArrayList = new ArrayList<>();
    private NewsListAdapter adapter;
    String url = "https://newsapi.org/v2/top-headlines?country=il&apiKey=aba21d0a39774bd2bd4fda9a3885db8e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Contacts</font>"));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        newsListView = findViewById(R.id.news_list_view);
        new JSONParser().execute();
    }

    private void getLatestNews(JSONObject value) {
        
        //adapter = new NewsListAdapter(NewsActivity.this, R.layout.content_news, newsArrayList);
        //newsListView.setAdapter(adapter);
    }

    protected class JSONParser extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... params) {
            String str="https://newsapi.org/v2/top-headlines?country=il&apiKey=aba21d0a39774bd2bd4fda9a3885db8e";
            URLConnection urlConn = null;
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(str);
                urlConn = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }

                return new JSONObject(stringBuffer.toString());
            }
            catch(Exception ex) {
                Log.e("App", "yourDataTask", ex);
                return null;
            }
            finally
            {
                if(bufferedReader != null)
                {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            if(response != null) {
                getLatestNews(response);
            }
        }
    }
}



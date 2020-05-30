package com.e.shelter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;

import com.e.shelter.adapers.NewsListAdapter;
import com.e.shelter.utilities.News;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
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
    private String url = "https://newsapi.org/v2/top-headlines?country=il&apiKey=aba21d0a39774bd2bd4fda9a3885db8e";
    private FloatingActionButton refreshButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Top Headlines</font>"));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        newsListView = findViewById(R.id.news_list_view);
        new JSONParser().execute();
    }

    private void getLatestNews(JSONObject value) {
        try {
            JSONArray articles = value.getJSONArray("articles");
            for (int i = 0 ; i < articles.length() ; i++) {
                JSONObject object = (JSONObject) articles.get(i);
                JSONObject source = object.getJSONObject("source");
                News news = new News(object.getString("title"), object.getString("description"), object.getString("publishedAt"),
                        object.getString("urlToImage"), object.getString("url"), source.getString("name"));
                newsArrayList.add(news);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter = new NewsListAdapter(NewsActivity.this, R.layout.content_news, newsArrayList);
        newsListView.setAdapter(adapter);
    }

    protected class JSONParser extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... params) {
            String str = "https://newsapi.org/v2/top-headlines?country=il&apiKey=aba21d0a39774bd2bd4fda9a3885db8e";
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
            catch (Exception e) {
                Log.e("App", "yourDataTask", e);
                return null;
            }
            finally {
                if (bufferedReader != null) {
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
            if (response != null) {
                getLatestNews(response);
            }
        }
    }
}



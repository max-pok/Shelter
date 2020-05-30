package com.e.shelter.utilities;

public class News {
    private String title;
    private String description;
    private String date;
    private String urlToImage;
    private String url;
    private String id;

    public News(String title, String description, String date, String urlToImage, String url, String id) {
        setTitle(title);
        setDescription(description);
        setDate(date);
        setUrlToImage(urlToImage);
        setUrl(url);
        setId(id);
    }

    public String getTitle() {
        return title;
    }

    private void setTitle(String title) {
        if (!title.equals("null")) this.title = title;
        else this.title = " ";

    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        if (!description.equals("null")) this.description = description;
        else this.description = " ";
    }

    public String getDate() {
        return date;
    }

    private void setDate(String date) {
        if (!date.equals("null")) {
            this.date = date.replace("T", " ").replace("Z", "");
        }
        else this.date = "";
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    private void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getUrl() {
        return url;
    }

    private void setUrl(String url) {
        if (!url.equals("null")) this.url = url;
        else this.url = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

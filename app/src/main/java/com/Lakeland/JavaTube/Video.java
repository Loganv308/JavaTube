package com.Lakeland.JavaTube;

import android.util.Log;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.URL;
import java.util.Calendar;

public class Video
{
    public final String TAG = "videos";
    public final String DELIM = "|";

    private int videoID;
    private String videoTitle;
    private double videoLength;
    private String videoAuthor;
    private String videoLink;
    private Calendar timeStamp;

    public Video() // Creates the Video() object. Sets the video ID to the last one available (I think), and grabs the current time as a TimeStamp for the DB entry.
    {
        videoID=-1;
        timeStamp = Calendar.getInstance();
    }

    public Video(int id, // Creates the Video() object but with all the database constructors
                 String title,
                 float length,
                 String author,
                 String link,
                 Calendar timestamp)
    {
        this.videoID = id;
        this.videoTitle = title;
        this.videoLength = length;
        this.videoAuthor = author;
        this.videoLink = link;
        this.timeStamp = timestamp;
    }

    public int getVideoID()
    {
        return videoID;
    }

    public void setVideoID(int i)
    {
        videoID = i;
    }

    public String getVideoTitle()
    {
        return videoTitle;
    }

    public void setVideoTitle(String s) { videoTitle = s; }

    public double getVideoLength(){ return videoLength; };

    public void setVideoLength(float i){ videoLength = i; }

    public String getVideoAuthor(){ return videoAuthor; }

    public void setVideoAuthor(String s){ videoAuthor = s; }

    public String getVideoLink(){ return videoLink; }

    public void setVideoLink(String s){ videoLink = s; }

    public Calendar getTimeStamp(){ return timeStamp; }

    public void setTimeStamp(Calendar v){ timeStamp = v; }

    public String toString()
    {
        String data = videoID + DELIM
                + videoTitle + DELIM
                + videoLength + DELIM
                + videoAuthor + DELIM
                + videoLink + DELIM
                + timeStamp + DELIM;
        Log.d(TAG, "toString: " + data);
        return data;
    }


}

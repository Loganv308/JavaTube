package com.Lakeland.JavaTube;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.net.IDN;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class VideoDataSource
{
    private SQLiteDatabase database;
    private VideoDBHelper videodbHelper;
    public static final String TAG = "VideoDataSource";

    public VideoDataSource(Context context) // Connects the DBhelper to the source
    {
        videodbHelper = new VideoDBHelper(context);
    }

    public void open() throws SQLException // Opens database connections
    {
        database = videodbHelper.getWritableDatabase();
    }

    public int deleteAll() // Deletes entire database
    {
        try
        {
            return database.delete("video", null, null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public void refreshData()
    {
        // Setting up a new Video Array for the Video
        ArrayList<Video> videos = new ArrayList<Video>();

        Calendar timeStamp = Calendar.getInstance();

        timeStamp.set(2022, 11, 21);
        videos.add(new Video(1, "Video Example", (float)5.16, "YouTube", "https://www.youtube.com/", timeStamp));

        timeStamp.set(2022, 11, 22);
        videos.add(new Video(2, ".NET 7 Update: Raw String Literals in 10 Minutes or Less", (float) 10.16, "IAmTimCorey", "https://www.youtube.com/watch?v=Jb8g3zRey6U", timeStamp));

        timeStamp.set(2022, 11, 23);
        videos.add(new Video(3, "How To Hack The Internet Using A Pringles Can | Mr. Robot", (float)6.52, "Mr.Robot", "https://www.youtube.com/watch?v=5qTSoCzp-LY", timeStamp));

        timeStamp.set(2022, 11, 23);
        videos.add(new Video(4, "Origins Of A Hacker | Mr. Robot", (float)6.01, "Mr.Robot", "https://www.youtube.com/watch?v=F38hR2D7rmc", timeStamp));

        Log.d(TAG, "refreshData: ");

        deleteAll();

        for (Video t: videos) insertVideo(t);
    }



    public void close() {
        videodbHelper.close();
    }

    public boolean insertVideo(Video v) // Inserts the video object into the database
    {
        boolean didSucceed = false;

        try
        {
            ContentValues initialValues = new ContentValues();

            initialValues.put("_ID", v.getVideoID());
            initialValues.put("videoTitle", v.getVideoTitle());
            initialValues.put("videoLength", v.getVideoLength());
            initialValues.put("videoAuthor", v.getVideoAuthor());
            initialValues.put("videoLink", v.getVideoLink());
            initialValues.put("timeStamp", String.valueOf(v.getTimeStamp().getTimeInMillis()));

            didSucceed = database.insert("video", null, initialValues) > 0;
        }
        catch(Exception e)
        {
            // Does nothing but will return false if there is an exception
        }

        return didSucceed;
    }

    public boolean updateVideo(Video v) // Updates video information
    {
        boolean didSucceed = false;
        try
        {
            Long rowID = (long) v.getVideoID();
            ContentValues updateValues = new ContentValues();

            updateValues.put("videoTitle", v.getVideoTitle());
            updateValues.put("videoLength", v.getVideoLength());
            updateValues.put("videoAuthor", v.getVideoAuthor());
            updateValues.put("videoLink", v.getVideoLink());
            updateValues.put("timeStamp", String.valueOf(v.getTimeStamp().getTimeInMillis()));

            didSucceed = database.update("video", updateValues, "_id=" + rowID, null) > 0;
        }
        catch (Exception e)
        {
            //Do nothing but will return false if there is an exception
        }

        return didSucceed;
    }

    public int getLastVideoID() // Get the last ID entered into the Database
    {
        int lastId;
        try
        {
            String query = "Select MAX(_id) from video";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            lastId = cursor.getInt(0);
            cursor.close();
        }
        catch (Exception e)
        {
            lastId = -1;
        }
        return lastId;
    }

    public ArrayList<String> getVideoName() // Get all video Names
    {
        ArrayList<String> videoNames = new ArrayList<>();

        try
        {
            Video video = new Video();
            String query = "select videoTitle from video";
            Cursor cursor = database.rawQuery(query, null);

           cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                videoNames.add(cursor.getString(0));
                cursor.moveToNext();
            }
            cursor.close();
        }
        catch (Exception e)
        {
            videoNames = new ArrayList<String>();
        }
        return videoNames;
    }

    public Video getSpecificVideo(int vidID) // Grabs from database with specified ID
    {
        Video video = new Video();
        String query = "SELECT * FROM video WHERE _id =" + vidID;
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            video.setVideoID(cursor.getInt(0));
            video.setVideoTitle(cursor.getString(1));
            video.setVideoAuthor(cursor.getString(2));
            video.setVideoLength(cursor.getFloat(3));
            video.setVideoLink(cursor.getString(4));

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(cursor.getString(5)));
            video.setTimeStamp(calendar);

            cursor.close();
        }
        return video;
    }

    public boolean deleteVideo(int videoId) // Deletes from Database query
    {
        boolean didDelete = false;

        try
        {
            didDelete = database.delete("video", "_id=" + videoId, null) > 0;
        }
        catch (Exception e)
        {
            //Do nothing -return value already set to false
        }
        return didDelete;
    }

    public ArrayList<Video> getVideos(String sortField, String sortOrder) {
        ArrayList<Video> videos = new ArrayList<Video>();
        try {
            String query = "SELECT  * FROM video ORDER BY " + sortField + " " + sortOrder;
            Cursor cursor = database.rawQuery(query, null);

            Video newVideo;
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                newVideo = new Video();
                Log.d(TAG, "getVideos: " + cursor.getString(1));
                newVideo.setVideoID(cursor.getInt(0));
                newVideo.setVideoTitle(cursor.getString(1));
                newVideo.setVideoLength(cursor.getFloat(2));
                newVideo.setVideoAuthor(cursor.getString(3));
                newVideo.setVideoLink(cursor.getString(4));

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.valueOf(cursor.getString(5)));
                newVideo.setTimeStamp(calendar);
                videos.add(newVideo);
                cursor.moveToNext();
            }

            cursor.close();
        }
        catch (Exception e)
        {
            Log.d(TAG, "getVideos: Error: " + e.getMessage());
            videos = new ArrayList<Video>();
        }
        return videos;
    }

    public Cursor getData()
    {
        database = videodbHelper.getWritableDatabase();
        Cursor cursor  = database.rawQuery("Select * from video", null);
        return cursor;
    }

    public ArrayList<Integer> getVideoIDs()
    {
        ArrayList<Integer> IDNames = new ArrayList<>();
        try
        {
            String query = "select _id from video";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();

            while(!cursor.isAfterLast())
            {
                IDNames.add(cursor.getInt(0));
                cursor.moveToNext();
            }
        }
        catch(Exception e)
        {
            IDNames = new ArrayList<Integer>();
        }

        return IDNames;
    }
}

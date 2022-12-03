package com.Lakeland.JavaTube;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Lakeland.youtubedl_android.YoutubeDL;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class activity_edit_data extends AppCompatActivity implements View.OnClickListener{
    public static final String TAG = activity_edit_data.class.getSimpleName();
    private Video currentVideo = new Video();
    private ArrayList<Video> videos = new ArrayList<>();
    private ArrayList<String> videoID = new ArrayList<>();
    private ArrayList<String> videoTitle = new ArrayList<>();
    private ArrayList<String> videoLength = new ArrayList<>();
    private ArrayList<String> videoAuthor = new ArrayList<>();
    private ArrayList<String> videoLink = new ArrayList<>();
    private ArrayList<String> videoTimeStamp = new ArrayList<>();
    private RecyclerView recyclerView;

    EditText rowID, title, length, author, link, timeStamp, deleteValue;

    VideoDBHelper dbHelper;
    VideoAdapter adapter;

    private String processId = "MyDlProcess";

    private Button btnApplyChanges;
    private Button btnDeleteValue;

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            int videoId = videos.get(position).getVideoID();
            Log.d(TAG, "onClick: " + position);
            Intent intent = new Intent(activity_edit_data.this, activity_edit_data.class);
            intent.putExtra("videoId", videoId);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_data);

        dbHelper = new VideoDBHelper(this);

        rowID = findViewById(R.id.delete_id_input2);
        title = findViewById(R.id.video_title_edit);
        length = findViewById(R.id.video_length_edit);
        author = findViewById(R.id.video_author_update);
        link = findViewById(R.id.delete_id_input6);
        deleteValue = findViewById(R.id.delete_id_input);

        btnDeleteValue = findViewById(R.id.delete_value_button);
        btnApplyChanges = findViewById(R.id.apply_changes);

        displayData();
        initViews();
        initListeners();

        try
        {
            VideoDataSource db = new VideoDataSource(activity_edit_data.this);
            db.open();
            if(db.getVideos("_id","ASC").size() == 0){ db.refreshData(); }
            db.close();
        }
        catch (Exception e)
        {
            Toast.makeText(activity_edit_data.this, "Unable to create database entries", Toast.LENGTH_LONG).show();
        }

        try
        {
            VideoDataSource db = new VideoDataSource(activity_edit_data.this);

            db.open();

            ArrayList<Video> result = db.getVideos("_id", "ASC");

            db.close();

            videos = result;

            RecyclerView videoList = findViewById(R.id.recyclerView);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

            videoList.setLayoutManager(layoutManager);

            adapter = new VideoAdapter(videos, activity_edit_data.this);
            adapter.setOnItemClickListener(onItemClickListener);

            videoList.setAdapter(adapter);

        }
        catch(Exception e)
        {
            Toast.makeText(activity_edit_data.this, "Unable to show RecyclerView", Toast.LENGTH_LONG).show();
        }

        btnDeleteValue.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                VideoDataSource ds = new VideoDataSource(activity_edit_data.this);

                try
                {
                    ds.open();

                    int deleteText = Integer.valueOf(deleteValue.getText().toString());

                    ds.deleteVideo(deleteText);

                    ds.close();
                    Toast.makeText(activity_edit_data.this, "Value has been deleted.", Toast.LENGTH_LONG).show();
                }
                catch(Exception e)
                {
                    Toast.makeText(activity_edit_data.this, "Please type in a value to delete.", Toast.LENGTH_LONG).show();
                }
            }
        });


        btnApplyChanges.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                VideoDataSource ds = new VideoDataSource(activity_edit_data.this);
                ArrayList<Integer> videoIDs = ds.getVideoIDs();
                try
                {
                    ds.open();

                    String titleText = title.getText().toString();
                    String authorText = length.getText().toString();
                    String linkText = link.getText().toString();
                    float number = Float.valueOf(length.getText().toString());
                    int columnID = Integer.valueOf(rowID.getText().toString());

                    currentVideo.setVideoID(columnID);
                    currentVideo.setVideoTitle(titleText);
                    currentVideo.setVideoAuthor(authorText);
                    currentVideo.setVideoLink(linkText);
                    currentVideo.setVideoLength(number);

                    if(ds.getVideoIDs().contains(columnID))
                    {
                        ds.updateVideo(currentVideo);
                        Toast.makeText(activity_edit_data.this, "Data has been updated!", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        currentVideo.setVideoID(ds.getLastVideoID() + 1);
                        ds.insertVideo(currentVideo);
                        Toast.makeText(activity_edit_data.this, "Data has been inserted!", Toast.LENGTH_LONG).show();
                    }

                    ds.close();
                }
                catch(Exception e)
                {
                    Toast.makeText(activity_edit_data.this,"Please fill in all the text", Toast.LENGTH_LONG).show();
                    // Toast.makeText(activity_edit_data.this,"Error: " + e.toString(), Toast.LENGTH_LONG).show(); This is for troubleshooting
                }
            }
        });
    }

    private void initViews() {
        btnApplyChanges = findViewById(R.id.apply_changes);

    }
    private void initListeners() {
        btnApplyChanges.setOnClickListener(this);

    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.apply_changes:

                Toast.makeText(activity_edit_data.this, "Data Updated", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void displayData()
    {
        VideoDataSource db = new VideoDataSource(activity_edit_data.this);

        Cursor cursor = db.getData();

        cursor.moveToFirst();

        if(cursor.getCount()==0)
        {
            Toast.makeText(activity_edit_data.this, "No Entry Exists", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {

            while(cursor.moveToNext())
            {
                videoID.add(cursor.getString(0));
                videoTitle.add(cursor.getString(1));
                videoAuthor.add(cursor.getString(2));
                videoLink.add(cursor.getString(3));
                videoLink.add(cursor.getString(4));
                videoTimeStamp.add(cursor.getString(5));
            } // videoTitle, videoLength, videoAuthor, videoLink, videoTimeStamp
        }
    }
}
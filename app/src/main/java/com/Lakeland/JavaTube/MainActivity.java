package com.Lakeland.JavaTube;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.devbrackets.android.exomedia.BuildConfig;
import com.Lakeland.youtubedl_android.YoutubeDL;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnStreamingExample;
    private Button btnDownloadingExample;
    private Button btnViewData;

    private Video currentVideo;

    VideoDataSource db = new VideoDataSource(MainActivity.this);

    private ProgressBar progressBar;

    private boolean updating = false;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initListeners();
        initMapButton();

        ArrayList<String> videos = new ArrayList<>();

        try
        {
            FileIO fileIO = new FileIO();
            db.open();
            int newID = db.getLastVideoID();
            videos = db.getVideoName();

            currentVideo.setVideoID(newID);

            Log.d(TAG, "Set new video ID: New Id" + newID);

            ArrayList<String> video2 = new ArrayList<String>();

            video2.add(currentVideo.toString());

            fileIO.writeFile(MainActivity.this, video2);

            Log.d(currentVideo.TAG, currentVideo.getVideoTitle() + " written");

            Toast.makeText(MainActivity.this, currentVideo.getVideoTitle() + " written", Toast.LENGTH_LONG).show();

            db.updateVideo(currentVideo);

            db.close();
        }
        catch(Exception e)
            {
            }

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    private void initListeners() {
        btnStreamingExample.setOnClickListener(this);
        btnDownloadingExample.setOnClickListener(this);
        btnViewData.setOnClickListener(this);

    }

    private void initViews() {
        btnStreamingExample = findViewById(R.id.btn_streaming_example);
        btnDownloadingExample = findViewById(R.id.btn_downloading_example);
        btnViewData = findViewById(R.id.btn_edit_data);
        progressBar = findViewById(R.id.progress_bar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_streaming_example: {
                Intent i = new Intent(MainActivity.this, StreamingExampleActivity.class);
                startActivity(i);
                break;
            }
            case R.id.btn_downloading_example: {
                Intent i = new Intent(MainActivity.this, DownloadingExampleActivity.class);
                startActivity(i);
                break;
            }
            case R.id.btn_edit_data:{
                Intent i = new Intent(MainActivity.this, activity_edit_data.class);
                startActivity(i);
                break;
            }

        }
    }

    private void initMapButton()
    {
        Button button1 = findViewById(R.id.mapsButton);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoMapActivity.class);
                intent.setFlags((Intent.FLAG_ACTIVITY_CLEAR_TOP));
                startActivity(intent);
            }
        });
    }

    private void updateJavaTube() {
        if (updating) {
            Toast.makeText(MainActivity.this, "update is already in progress", Toast.LENGTH_LONG).show();
            return;
        }

        updating = true;
        progressBar.setVisibility(View.VISIBLE);
        Disposable disposable = Observable.fromCallable(() -> YoutubeDL.getInstance().updateYoutubeDL(getApplication()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(status -> {
                    progressBar.setVisibility(View.GONE);
                    switch (status) {
                        case DONE:
                            Toast.makeText(MainActivity.this, "update successful", Toast.LENGTH_LONG).show();
                            break;
                        case ALREADY_UP_TO_DATE:
                            Toast.makeText(MainActivity.this, "already up to date", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(MainActivity.this, status.toString(), Toast.LENGTH_LONG).show();
                            break;
                    }
                    updating = false;
                }, e -> {
                    if(BuildConfig.DEBUG) Log.e(TAG, "failed to update", e);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "update failed", Toast.LENGTH_LONG).show();
                    updating = false;
                });
        compositeDisposable.add(disposable);
    }


}

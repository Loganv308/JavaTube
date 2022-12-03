package com.Lakeland.JavaTube;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.devbrackets.android.exomedia.BuildConfig;
import com.Lakeland.youtubedl_android.DownloadProgressCallback;
import com.Lakeland.youtubedl_android.YoutubeDL;
import com.Lakeland.youtubedl_android.YoutubeDLRequest;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class DownloadingExampleActivity extends AppCompatActivity implements View.OnClickListener {

    private Video currentVideo = new Video();
    private Button btnStartDownload;
    private Button btnStopDownload;
    private EditText etUrl;
    private Switch useConfigFile;
    private ProgressBar progressBar;
    private TextView tvDownloadStatus;
    private TextView tvCommandOutput;
    private ProgressBar pbLoading;
    private EditText videoTitleText, videoLengthText, videoAuthorText, videoLinkText;

    private boolean downloading = false;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String processId = "MyDlProcess";

    VideoDataSource db = new VideoDataSource(DownloadingExampleActivity.this);

    private final DownloadProgressCallback callback = new DownloadProgressCallback()
    {
        @Override
        public void onProgressUpdate(float progress, long etaInSeconds, String line)
        {
            runOnUiThread(() ->
                    {
                        progressBar.setProgress((int) progress);
                        tvDownloadStatus.setText(line);
                    }
            );
        }
    };

    private static final String TAG = DownloadingExampleActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloading_example);

        initViews();
        initListeners();
        initTextChangeEvents();
    }

    private void initViews() {
        btnStartDownload = findViewById(R.id.btn_start_download);
        btnStopDownload = findViewById(R.id.btn_stop_download);
        etUrl = findViewById(R.id.et_url);
        useConfigFile = findViewById(R.id.use_config_file);
        progressBar = findViewById(R.id.progress_bar);
        tvDownloadStatus = findViewById(R.id.tv_status);
        pbLoading = findViewById(R.id.pb_status);
        tvCommandOutput = findViewById(R.id.tv_command_output);
        videoTitleText = findViewById(R.id.video_title_edit);
        videoLengthText = findViewById(R.id.video_length_edit);
        videoAuthorText = findViewById(R.id.video_author_edit);
        videoLinkText = findViewById(R.id.video_link_edit);
    }

    private void initListeners() {
        btnStartDownload.setOnClickListener(this);
        btnStopDownload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.btn_start_download:
                startDownload();
                break;
            case R.id.btn_stop_download:
                try {
                    YoutubeDL.getInstance().destroyProcessById(processId);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                break;
        }
        try
        {

            VideoDataSource ds = new VideoDataSource(DownloadingExampleActivity.this);
            ds.open();

            String titleText = videoTitleText.getText().toString();
            float lengthText = Float.valueOf(videoLengthText.getText().toString());
            String authortext = videoAuthorText.getText().toString();
            String linkText = videoLinkText.getText().toString();

            currentVideo.setVideoID(ds.getLastVideoID() + 1);
            currentVideo.setVideoTitle(titleText);
            currentVideo.setVideoLength(lengthText);
            currentVideo.setVideoAuthor(authortext);
            currentVideo.setVideoLink(linkText);

            ds.insertVideo(currentVideo);
            Toast.makeText(DownloadingExampleActivity.this, "Data has been inserted!", Toast.LENGTH_LONG).show();

            ds.close();
        }
        catch(Exception e)
        {
            Toast.makeText(DownloadingExampleActivity.this,"Please fill in all the text", Toast.LENGTH_LONG).show();
        }
    }

    private void startDownload()
    {
        boolean wasSuccessful = true;
        FileIO fileIO = new FileIO();
        if (downloading) {
            Toast.makeText(DownloadingExampleActivity.this, "cannot start download. a download is already in progress", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isStoragePermissionGranted()) {
            Toast.makeText(DownloadingExampleActivity.this, "grant storage permission and retry", Toast.LENGTH_LONG).show();
            return;
        }

        String url = etUrl.getText().toString().trim();
        if (TextUtils.isEmpty(url)) {
            etUrl.setError(getString(R.string.url_error));
            return;
        }

        YoutubeDLRequest request = new YoutubeDLRequest(url);
        File youtubeDLDir = getDownloadLocation();
        File config = new File(youtubeDLDir, "config.txt");

        if (useConfigFile.isChecked() && config.exists()) {
            request.addOption("--config-location", config.getAbsolutePath());
        } else {
            request.addOption("--no-mtime");
            request.addOption("--downloader", "libaria2c.so");
            request.addOption("--external-downloader-args", "aria2c:\"--summary-interval=1\"");
            request.addOption("-f", "bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best");
            request.addOption("-o", youtubeDLDir.getAbsolutePath() + "/sdcard/Download/%(title)s.%(ext)s");
        }

        showStart();

        downloading = true;
        Disposable disposable = Observable.fromCallable(() -> YoutubeDL.getInstance().execute(request, processId, callback))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(youtubeDLResponse -> {
                    pbLoading.setVisibility(View.GONE);
                    progressBar.setProgress(100);
                    tvDownloadStatus.setText(getString(R.string.download_complete));
                    tvCommandOutput.setText(youtubeDLResponse.getOut());
                    Toast.makeText(DownloadingExampleActivity.this, "download successful", Toast.LENGTH_LONG).show();
                    downloading = false;
                }, e -> {
                    if (BuildConfig.DEBUG) Log.e(TAG, "failed to download", e);
                    pbLoading.setVisibility(View.GONE);
                    tvDownloadStatus.setText(getString(R.string.download_failed));
                    tvCommandOutput.setText(e.getMessage());
                    Toast.makeText(DownloadingExampleActivity.this, "download failed", Toast.LENGTH_LONG).show();
                    downloading = false;
                });
        compositeDisposable.add(disposable);

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @NonNull
    private File getDownloadLocation() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File youtubeDLDir = new File(downloadsDir, "JavaTube");
        if (!youtubeDLDir.exists()) youtubeDLDir.mkdir();
        return youtubeDLDir;
    }

    private void showStart() {
        tvDownloadStatus.setText(getString(R.string.download_start));
        progressBar.setProgress(0);
        pbLoading.setVisibility(View.VISIBLE);
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    private void initTextChangeEvents()
    {
        final TextView tvVidTitle = findViewById(R.id.video_title_text);
        tvVidTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //  Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //  Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                currentVideo.setVideoTitle(tvVidTitle.getText().toString());
            }
        });
        final TextView tvVidLength = findViewById(R.id.video_title_text);
        tvVidTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //  Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //  Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                currentVideo.setVideoTitle(tvVidLength.getText().toString());
            }
        });
        final TextView tvVidAuthor = findViewById(R.id.video_title_text);
        tvVidTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //  Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //  Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                currentVideo.setVideoTitle(tvVidAuthor.getText().toString());
            }
        });
        final TextView tvVidLink = findViewById(R.id.video_title_text);
        tvVidTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //  Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //  Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                currentVideo.setVideoTitle(tvVidLink.getText().toString());
            }
        });
    }
}
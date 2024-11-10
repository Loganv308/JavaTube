# JavaTube Mobile Application

JavaTube is a Java developed mobile application that can download your favorite Youtube videos and save them locally on your device, along with saving the designated video metadata to a local Database. 

This project utilizes ffmpeg to download videos from YouTubes. 

## Features

- Ability to download YouTube videos
- Save videos to an internal database
- Gather video metadata and stores to an internal database

## Prerequisites

- Android Studio (Development)
- Latest Java SDK

## Usage

- Here's an example code snippet that handles the Download

```java

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

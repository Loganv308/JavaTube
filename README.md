# Project Title

JavaTube is a Java developed mobile application that can download your favorite Youtube videos and save them locally on your device, along with saving the designated video metadata to a local Database. 

## Features

- Ability to download YouTube videos
- Save videos to an internal database
- Gather video metadata and store to an internal database

## Prerequisites

- Example: Java Development Kit (JDK) version X.X.X
- Any external libraries or frameworks used

## Usage

- Here's an example code snippet from my project

```java

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

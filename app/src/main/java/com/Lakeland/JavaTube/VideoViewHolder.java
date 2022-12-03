package com.Lakeland.JavaTube;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class VideoViewHolder extends RecyclerView.ViewHolder
{
    TextView videoName, videoLink;

    VideoViewHolder(View itemView)
    {
        super(itemView);
        videoName = itemView.findViewById(R.id.vid_tit_text);
        videoLink = itemView.findViewById(R.id.vid_link_text);
    }
}

package com.Lakeland.JavaTube;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter
{
    private ArrayList<Video> videoData;
    private View.OnClickListener mOnItemClickListener;
    private boolean isDeleting;
    private Context parentContext;

    public class VideoViewHolder extends RecyclerView.ViewHolder
    {

        public TextView textVidName;
        public TextView textVidLink;
        public TextView textVidID;


        public VideoViewHolder(@NonNull View itemView)
        {
            super(itemView);

            textVidName = itemView.findViewById(R.id.vid_tit_text);

            textVidLink = itemView.findViewById(R.id.vid_link_text);

            textVidID = itemView.findViewById(R.id.vid_ID_text);
            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }

        public TextView getTextVidID() { return textVidID; }

        public TextView getVideoText()
        {
            return textVidName;
        }

        public TextView getVideoLink()
        {
            return textVidLink;
        }

    }
        public VideoAdapter(ArrayList<Video> arrayList, Context context)
        {
            videoData = arrayList;
            parentContext = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new VideoViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position1)
        {
            VideoViewHolder vvh = (VideoViewHolder) holder;
            vvh.getVideoLink().setText(videoData.get(position1).getVideoLink());
            vvh.getVideoText().setText(videoData.get(position1).getVideoTitle());
            vvh.getTextVidID().setText(String.valueOf(videoData.get(position1).getVideoID()));
        }

        private void deleteItem(int position)
        {
            Video video = videoData.get(position);
            VideoDataSource ds = new VideoDataSource(parentContext);

            try
            {
                ds.open();
                boolean didDelete = ds.deleteVideo(video.getVideoID());
                ds.close();
            }
            catch(Exception e)
            {

            }
        }
        @Override
        public int getItemCount()
        {
            return videoData.size();
        }

        public void setDelete(boolean b)
        {
            isDeleting = b;
        }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }
}


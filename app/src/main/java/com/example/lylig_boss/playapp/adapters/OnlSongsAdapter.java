package com.example.lylig_boss.playapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.lylig_boss.playapp.R;
import com.example.lylig_boss.playapp.models.OnlineSong;
import com.example.lylig_boss.playapp.utils.TimeUtil;

import java.util.List;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 20/06/2016.
 */
public class OnlSongsAdapter extends RecyclerView.Adapter<OnlSongsAdapter.ViewHolder> {
    private final Context mContext;
    private final List<OnlineSong> mOnlineSongs;
    private OnItemClickListener mOnItemClickListener;

    public OnlSongsAdapter(Context context, List<OnlineSong> onlineSongs) {
        mContext = context;
        mOnlineSongs = onlineSongs;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClicklistener) {
        mOnItemClickListener = onItemClicklistener;
    }

    public interface OnItemClickListener {
        void Onclick(View view, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_song_online_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final OnlineSong songObject = mOnlineSongs.get(position);
        holder.mTvNameOffSong.setText(songObject.getTitle());
        holder.mTvDurationOffSong.setText(TimeUtil.getInstance().setTimeForDuration(songObject.getDuration()));
    }

    @Override
    public int getItemCount() {
        return mOnlineSongs != null ? mOnlineSongs.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener {
        private final TextView mTvNameOffSong;
        private final TextView mTvDurationOffSong;


        public ViewHolder(View itemView) {
            super(itemView);
            mTvNameOffSong = (TextView) itemView.findViewById(R.id.tvNameOnlSong);
            mTvDurationOffSong = (TextView) itemView.findViewById(R.id.tvDurationOnlSong);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.Onclick(v, getAdapterPosition());
            }
        }
    }
}

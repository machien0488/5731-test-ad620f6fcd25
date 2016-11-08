package com.example.lylig_boss.playapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lylig_boss.playapp.R;
import com.example.lylig_boss.playapp.models.PlaySong;
import com.example.lylig_boss.playapp.utils.TimeUtil;

import java.util.List;


/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 20/06/2016.
 */
public class PlaySongsAdapter extends RecyclerView.Adapter<PlaySongsAdapter.ViewHolder> {
    private final Context mContext;
    private final List<PlaySong> mPlaySongs;
    private OnItemClickListener mOnItemClickListener;
    private boolean mIsOffline;

    public PlaySongsAdapter(Context context, List<PlaySong> playSongs, boolean isOffline) {
        mContext = context;
        mPlaySongs = playSongs;
        mIsOffline = isOffline;
    }

    public void setIsPlaying(int position) {
        for (PlaySong song : mPlaySongs) {
            song.setPlaying(false);
        }
        mPlaySongs.get(position).setPlaying(true);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClicklistener) {
        mOnItemClickListener = onItemClicklistener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_song_play, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PlaySong songUtil = mPlaySongs.get(position);
        if (mIsOffline) {
            holder.mTvNameSong.setText(songUtil.getOfflineSong().getTitle());
            holder.mTvDuration.setText(TimeUtil.getInstance().setTimeForDuration(songUtil.getOfflineSong().getDuration()));
        } else {
            holder.mTvNameSong.setText(songUtil.getOnlineSong().getTitle());
            holder.mTvDuration.setText(TimeUtil.getInstance().setTimeForDuration(songUtil.getOnlineSong().getDuration()));
        }
        holder.mTvNumSong.setText(String.valueOf(position + 1));
        holder.mImgPlay.setVisibility(songUtil.isPlaying() ? View.VISIBLE : View.GONE);
        holder.mTvNumSong.setVisibility(songUtil.isPlaying() ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mPlaySongs != null ? mPlaySongs.size() : 0;
    }

    public interface OnItemClickListener {
        void Onclick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mTvNameSong;
        private final TextView mTvDuration;
        private final TextView mTvNumSong;
        private final ImageView mImgPlay;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvNameSong = (TextView) itemView.findViewById(R.id.tvNamePlaySong);
            mTvDuration = (TextView) itemView.findViewById(R.id.tvDurationSong);
            mTvNumSong = (TextView) itemView.findViewById(R.id.tvNumSong);
            mImgPlay = (ImageView) itemView.findViewById(R.id.imgListPlay);
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

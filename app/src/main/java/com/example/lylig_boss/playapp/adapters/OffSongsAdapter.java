package com.example.lylig_boss.playapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lylig_boss.playapp.R;
import com.example.lylig_boss.playapp.models.OfflineSong;
import com.example.lylig_boss.playapp.utils.TimeUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 20/06/2016.
 */
public class OffSongsAdapter extends RecyclerView.Adapter<OffSongsAdapter.ViewHolder> {
    private final Context mContext;
    private final List<OfflineSong> mOfflineSongs;
    private OnItemClickListener mOnItemClickListener;

    public OffSongsAdapter(Context context, List<OfflineSong> offlineSongs) {
        mContext = context;
        this.mOfflineSongs = offlineSongs;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClicklistener) {
        mOnItemClickListener = onItemClicklistener;
    }

    public interface OnItemClickListener {
        void Onclick(View view, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_song_offline_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final OfflineSong songObject = mOfflineSongs.get(position);
        holder.mTvNameOffSong.setText(songObject.getTitle());
        holder.mTvDurationOffSong.setText(TimeUtil.getInstance().setTimeForDuration(songObject.getDuration()));
        holder.mTvGenreOffSong.setText(songObject.getGenre());
        Picasso.with(mContext).load(new File(songObject.getArtworkUri())).placeholder(R.drawable.ic_disk).into(holder.mImgOffSong);
    }

    @Override
    public int getItemCount() {
        return mOfflineSongs != null ? mOfflineSongs.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener {
        private final TextView mTvNameOffSong;
        private final TextView mTvDurationOffSong;
        private final TextView mTvGenreOffSong;
        private final ImageView mImgOffSong;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvNameOffSong = (TextView) itemView.findViewById(R.id.tvNameOffSong);
            mTvDurationOffSong = (TextView) itemView.findViewById(R.id.tvDurationOffSong);
            mTvGenreOffSong = (TextView) itemView.findViewById(R.id.tvGenreOffSong);
            mImgOffSong = (ImageView) itemView.findViewById(R.id.imgOffSong);
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

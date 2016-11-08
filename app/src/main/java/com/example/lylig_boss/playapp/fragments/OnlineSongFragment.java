package com.example.lylig_boss.playapp.fragments;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lylig_boss.playapp.R;
import com.example.lylig_boss.playapp.adapters.OnlSongsAdapter;
import com.example.lylig_boss.playapp.common.Constant;
import com.example.lylig_boss.playapp.eventBus.BusProvider;
import com.example.lylig_boss.playapp.eventBus.event.ReceiveOnlineSongEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiveStringRequestEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiverDataOnlineEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReloadDataEvent;
import com.example.lylig_boss.playapp.models.OnlineSong;
import com.example.lylig_boss.playapp.utils.InternetUtil;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 26/07/2016.
 */

@EFragment(R.layout.fragment_online_song)
public class OnlineSongFragment extends BaseFragment {
    private static final String TAG = OnlineSongFragment.class.getSimpleName();
    private static final int DELAY_TIME = 400;

    @ViewById(R.id.tvTotalOnlSong)
    TextView mTvTotalOnlSong;
    @ViewById(R.id.recycleViewOnlSong)
    RecyclerView mRecycleViewOnlSong;
    @ViewById(R.id.progressBarOnlineSongFragment)
    ProgressBar mProgressBarOnlineSongFragment;
    private Firebase mFireBaseRef;
    private OnlSongsAdapter mListOnlSongAdapter;
    private List<OnlineSong> mListOnlSongs;
    private long mDelayTime;
    private int mPositionSong = -1;
    private Bus mBus;

    @AfterViews
    protected void afterView() {
        mBus = BusProvider.getInstance();
        mProgressBarOnlineSongFragment.setVisibility(View.VISIBLE);
        Firebase.setAndroidContext(getContext());
        mFireBaseRef = new Firebase(Constant.LINK_FIRE_BASE);
        mListOnlSongs = new ArrayList<>();
        loadDataMusic();
        mListOnlSongAdapter = new OnlSongsAdapter(getContext(), mListOnlSongs);
        mRecycleViewOnlSong.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecycleViewOnlSong.setAdapter(mListOnlSongAdapter);
        mListOnlSongAdapter.setOnItemClickListener(new OnlSongsAdapter.OnItemClickListener() {
            @Override
            public void Onclick(View view, int position) {
                playOnlSong(position);
                mBus.post(new ReceiveStringRequestEvent(Constant.REQUEST_START_ACTIVITY));
            }
        });
    }

    @Subscribe
    public void updateData(ReloadDataEvent event) {
        if (mListOnlSongs.size() > 0 && event.isOnlineLoading()) {
            loadDataMusic();
        }
    }

    private void loadDataMusic() {
        if (!InternetUtil.getInstance().checkInternet(getContext())) {
            mRecycleViewOnlSong.setBackgroundResource(R.drawable.connection_fail);
            mProgressBarOnlineSongFragment.setVisibility(View.GONE);
            return;
        }
        mRecycleViewOnlSong.setBackgroundColor(Color.TRANSPARENT);
        mFireBaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mListOnlSongs.clear();
                for (DataSnapshot obj : snapshot.getChildren()) {
                    for (DataSnapshot obj1 : obj.getChildren()) {
                        mListOnlSongs.add(obj1.getValue(OnlineSong.class));
                    }
                }
                Log.d("song",""+mListOnlSongs.get(1).getDownloadUrl());
                loadTracks();
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
    }

    private void loadTracks() {
        mProgressBarOnlineSongFragment.setVisibility(View.GONE);
        mBus.post(new ReceiverDataOnlineEvent(mListOnlSongs));
        mTvTotalOnlSong.setText(String.format("%s%s", getString(R.string.textview_text_total_song), mListOnlSongs.size()));
        mListOnlSongAdapter.notifyDataSetChanged();
    }

    private void playOnlSong(int position) {
        if (System.currentTimeMillis() - mDelayTime < DELAY_TIME) {
            return;
        }
        mPositionSong = position;
        mDelayTime = System.currentTimeMillis();
        OnlineSong song = mListOnlSongs.get(position);
        mBus.post(new ReceiveOnlineSongEvent(song, mPositionSong));
    }

}

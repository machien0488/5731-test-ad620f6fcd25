package com.example.lylig_boss.playapp.fragments;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.lylig_boss.playapp.R;
import com.example.lylig_boss.playapp.adapters.OffSongsAdapter;
import com.example.lylig_boss.playapp.common.Constant;
import com.example.lylig_boss.playapp.databases.RealmHelper;
import com.example.lylig_boss.playapp.eventBus.BusProvider;
import com.example.lylig_boss.playapp.eventBus.event.ReceiveOfflineSongEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiveStringRequestEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiverDataOfflineEvent;
import com.example.lylig_boss.playapp.models.OfflineSong;
import com.squareup.otto.Bus;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 26/07/2016.
 */
@EFragment(R.layout.fragment_offline_song)
public class OfflineSongFragment extends BaseFragment implements RealmChangeListener<RealmResults<OfflineSong>> {
    private static final String TAG = OfflineSongFragment.class.getSimpleName();
    private static final int DELAY_TIME = 400;

    @ViewById(R.id.tvTotalOfflineSong)
    TextView mTvTotalOfflineSong;
    @ViewById(R.id.recyclerViewOffSong)
    RecyclerView mRecyclerViewOfflineSong;

    private RealmHelper mRealmHelper;
    private List<OfflineSong> mOfflineSongs;
    private OffSongsAdapter mOffSongsAdapter;
    private long mDelayTime;
    private int mPositionSong = -1;
    private RealmResults<OfflineSong> mResultSongs;
    private Bus mBus;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mOfflineSongs = new ArrayList<>();
        mRealmHelper = RealmHelper.with(this);
        loadData();
    }

    protected void afterView() {
        BusProvider.getInstance().post(new ReceiverDataOfflineEvent(mOfflineSongs));
        mTvTotalOfflineSong.setText(String.format("%s %s", getString(R.string.textview_text_total_song), mOfflineSongs.size()));
        mRecyclerViewOfflineSong.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mOffSongsAdapter = new OffSongsAdapter(getContext(), mOfflineSongs);
        mRecyclerViewOfflineSong.setAdapter(mOffSongsAdapter);
        mOffSongsAdapter.setOnItemClickListener(new OffSongsAdapter.OnItemClickListener() {
            @Override
            public void Onclick(View view, int position) {
                playOfflineSong(position);
                BusProvider.getInstance().post(new ReceiveStringRequestEvent(Constant.REQUEST_START_ACTIVITY));
            }
        });
    }

    private void loadData() {
        mResultSongs = mRealmHelper.queryData();
        mResultSongs.addChangeListener(this);
        mOfflineSongs.addAll(mResultSongs);
    }

    private void playOfflineSong(int position) {
        if (System.currentTimeMillis() - mDelayTime < DELAY_TIME) {
            return;
        }
        mPositionSong = position;
        mDelayTime = System.currentTimeMillis();
        OfflineSong song = mOfflineSongs.get(position);
        BusProvider.getInstance().post(new ReceiveOfflineSongEvent(song, mPositionSong));
    }

    @Override
    public void onChange(RealmResults<OfflineSong> element) {
        mOfflineSongs.clear();
        mOfflineSongs.addAll(mResultSongs);
        BusProvider.getInstance().post(new ReceiverDataOfflineEvent(mOfflineSongs));
        mTvTotalOfflineSong.setText(String.format("%s %s", getString(R.string.textview_text_total_song), mOfflineSongs.size()));
        mOffSongsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mBus.unregister(this);
    }
}

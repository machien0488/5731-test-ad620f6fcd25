package com.example.lylig_boss.playapp.fragments;

import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.lylig_boss.playapp.R;
import com.example.lylig_boss.playapp.adapters.PlaySongsAdapter;
import com.example.lylig_boss.playapp.common.Constant;
import com.example.lylig_boss.playapp.databases.RealmHelper;
import com.example.lylig_boss.playapp.eventBus.BusProvider;
import com.example.lylig_boss.playapp.eventBus.event.ReceivePositionSongEvent;
import com.example.lylig_boss.playapp.models.OfflineSong;
import com.example.lylig_boss.playapp.models.PlaySong;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

@EFragment(R.layout.fragment_list_song)
public class ListSongFragment extends BaseFragment {

    @ViewById(R.id.recycleViewPlaySong)
    RecyclerView mRecyclerView;

    @FragmentArg(Constant.LIST_ONL)
    ArrayList<PlaySong> mPlaySongs;
    @FragmentArg(Constant.POSITION)
    int mPositionSong;
    @FragmentArg(Constant.OFFLINE_MODE)
    boolean mIsOffline;

    private PlaySongsAdapter mPlaySongAdapter;
    private List<OfflineSong> mOfflineSongs;

    @Override
    protected void afterView() {
        BusProvider.getInstance().register(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder(getContext()).build();
        Realm.setDefaultConfiguration(configuration);
        if (mIsOffline) {
            mPlaySongs = new ArrayList<>();
            mOfflineSongs = RealmHelper.with(this).queryData();
            for (OfflineSong song : mOfflineSongs) {
                mPlaySongs.add(new PlaySong(song, false));
            }
        }
        mPlaySongAdapter = new PlaySongsAdapter(getContext(), mPlaySongs, mIsOffline);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mPlaySongAdapter);
        mPlaySongAdapter.setOnItemClickListener(new PlaySongsAdapter.OnItemClickListener() {

            @Override
            public void Onclick(View view, int position) {
                mPlaySongAdapter.setIsPlaying(position);
                mPositionSong = position;
                BusProvider.getInstance().post(new ReceivePositionSongEvent(mPositionSong));
            }
        });
    }

    public static ListSongFragment newInstance(List<PlaySong> list, int positionSong) {
        return ListSongFragment_.builder()
                .parcelableArrayListArg(Constant.LIST_ONL, (ArrayList<? extends Parcelable>) list)
                .arg(Constant.POSITION, positionSong)
                .build();
    }

    public static ListSongFragment newInstance(int positionSong, boolean isOffline) {
        return ListSongFragment_.builder()
                .arg(Constant.POSITION, positionSong)
                .arg(Constant.OFFLINE_MODE, isOffline)
                .build();
    }

    public void smoothScrollRecycleView(int mPosition) {
        mPositionSong = mPosition;
        mRecyclerView.smoothScrollToPosition(mPositionSong);
        mPlaySongAdapter.setIsPlaying(mPositionSong);
    }

    public void reloadDataLocal() {
        mOfflineSongs.clear();
        mOfflineSongs = RealmHelper.with(this).queryData();
        mPlaySongAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
    }
}

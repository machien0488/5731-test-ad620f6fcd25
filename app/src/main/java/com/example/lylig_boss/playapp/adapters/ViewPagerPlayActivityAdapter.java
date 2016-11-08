package com.example.lylig_boss.playapp.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.example.lylig_boss.playapp.fragments.ListSongFragment;
import com.example.lylig_boss.playapp.fragments.PlaySongFragment;
import com.example.lylig_boss.playapp.models.OnlineSong;
import com.example.lylig_boss.playapp.models.PlaySong;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 23/06/2016.
 */
public class ViewPagerPlayActivityAdapter extends FragmentPagerAdapter {

    public static final int TAG_LIST = 0;
    public static final int TAB_PLAY = 1;

    private SparseArray<Fragment> mFragments;
    private final Context mContext;
    private final List<OnlineSong> mOnlineSongs;
    private List<PlaySong> playSongs;
    private int mPosition;
    private boolean mIsOffline;
    private boolean mIsPlaying;

    public ViewPagerPlayActivityAdapter(FragmentManager fm, Context context, List<OnlineSong> onlineSongs, int position, boolean isOffline, boolean isPlaying) {
        super(fm);
        mFragments = new SparseArray<>();
        mContext = context;
        mOnlineSongs = onlineSongs;
        mPosition = position;
        mIsOffline = isOffline;
        mIsPlaying = isPlaying;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mFragments.put(position, fragment);
        return fragment;
    }

    public Fragment getFragmentByPosition(int position) {
        return mFragments.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case TAG_LIST:
                if (!mIsOffline) {
                    playSongs = new ArrayList<>();
                    for (OnlineSong song : mOnlineSongs) {
                        playSongs.add(new PlaySong(song, false));
                    }
                    return ListSongFragment.newInstance(playSongs, mPosition);
                } else {
                    return ListSongFragment.newInstance(mPosition, true);
                }
            case TAB_PLAY:
                if (!mIsOffline) {
                    return PlaySongFragment.newInstance(mOnlineSongs.get(mPosition), mIsPlaying);
                } else {
                    return PlaySongFragment.newInstance(mPosition, true, mIsPlaying);
                }
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}

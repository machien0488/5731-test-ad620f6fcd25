package com.example.lylig_boss.playapp.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lylig_boss.playapp.R;
import com.example.lylig_boss.playapp.fragments.OfflineSongFragment_;
import com.example.lylig_boss.playapp.fragments.OnlineSongFragment_;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 23/06/2016.
 */
public class ViewPagerMainActivityAdapter extends FragmentPagerAdapter {

    private static final int TAG_LIST_ONLINE = 0;
    private static final int TAB_LIST_OFFLINE = 1;
    private static final String[] TAB = {"Online", "Offline"};
    private final SparseArray<Fragment> mListFragments;
    private final Context mContext;

    public ViewPagerMainActivityAdapter(FragmentManager fm, Context context) {
        super(fm);
        mListFragments = new SparseArray<>();
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mListFragments.put(position, fragment);
        return fragment;
    }

    public Fragment getFragmentByPosition(int position) {
        return mListFragments.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case TAG_LIST_ONLINE:
                return OnlineSongFragment_.builder().build();

            case TAB_LIST_OFFLINE:
                return OfflineSongFragment_.builder().build();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return TAB.length;
    }

    public View getTabView(int position) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.tab_item, null);
        TextView tvTabName = (TextView) v.findViewById(R.id.tvTabName);
        tvTabName.setText(TAB[position]);
        return v;
    }
}

package com.example.lylig_boss.playapp.fragments;

import android.support.v4.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 28/07/2016.
 */
@EFragment
public abstract class BaseFragment extends Fragment {
    @AfterViews
    public void init() {
        afterView();
    }

    protected abstract void afterView();
}

package com.example.lylig_boss.playapp.activities;

import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 21/06/2016.
 */
@EActivity
public abstract class  BaseActivity  extends AppCompatActivity {

    @AfterViews
    public void init() {
        afterView();
    }

    protected abstract void afterView();
}
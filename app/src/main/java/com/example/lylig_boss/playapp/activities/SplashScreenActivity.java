package com.example.lylig_boss.playapp.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lylig_boss.playapp.R;
import com.example.lylig_boss.playapp.common.Constant;
import com.example.lylig_boss.playapp.services.ServiceMusic_;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 16/08/2016.
 */
@EActivity(R.layout.activity_splash)
public class SplashScreenActivity extends BaseActivity {
    private static final String TAG = SplashScreenActivity.class.getSimpleName();
    private final long SPLASH_DISPLAY_LENGTH = 3000;
    @ViewById(R.id.rlSplashLayout)
    RelativeLayout mRlSplashLayout;

    @ViewById(R.id.tvAppTitle)
    TextView mTvTitleApp;
    @ViewById(R.id.tvAppContent)
    TextView mTvContentApp;

    @Override
    protected void afterView() {
        Typeface tpTitle = Typeface.createFromAsset(getAssets(), Constant.TITLE_FONT);
        Typeface tpContent = Typeface.createFromAsset(getAssets(), Constant.CONTENT_FONT);
        mTvTitleApp.setTypeface(tpTitle);
        mTvContentApp.setTypeface(tpContent);
        startWithAnimation();
        startService();
    }

    private void startService() {
        if (!isMyServiceRunning(ServiceMusic_.class)) {
            ServiceMusic_.intent(getApplication()).start();
        }
    }

    private void startWithAnimation() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
        anim.reset();
        mRlSplashLayout.startAnimation(anim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity_.intent(SplashScreenActivity.this).start();
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

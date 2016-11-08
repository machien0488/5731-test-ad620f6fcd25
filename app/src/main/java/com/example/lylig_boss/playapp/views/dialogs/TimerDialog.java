package com.example.lylig_boss.playapp.views.dialogs;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.lylig_boss.playapp.R;
import com.example.lylig_boss.playapp.broadcasts.PlayBroadcast_;
import com.example.lylig_boss.playapp.common.Constant;
import com.example.lylig_boss.playapp.eventBus.BusProvider;
import com.example.lylig_boss.playapp.eventBus.event.ReceiverTimerValueEvent;
import com.example.lylig_boss.playapp.utils.ScreenUtil;
import com.example.lylig_boss.playapp.utils.TimeUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.SeekBarProgressChange;
import org.androidannotations.annotations.SeekBarTouchStop;
import org.androidannotations.annotations.ViewById;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 25/07/2016.
 */
@EFragment(R.layout.dialog_timer)
public class TimerDialog extends DialogFragment {
    private static final String TAG = TimerDialog.class.getSimpleName();
    private static final int CONVERT_TIME_VALUE = 60000;
    @ViewById(R.id.tvTimerTitle)
    TextView mTvTimerTitle;
    @ViewById(R.id.seekBarTimer)
    SeekBar mSeekBarTimer;
    PendingIntent mTimerPendingIntent;
    @FragmentArg
    long mTimeValue;
    private AlarmManager mAlarmManager;
    private Intent mTimerIntent;
    private int mTimer;

    @SeekBarProgressChange(R.id.seekBarTimer)
    public void onProgressChangeOnSeekBar(int progress) {
        mTimer = progress;
        mTvTimerTitle.setText(mTimer == 0 ? Constant.TIMER_DEFAULT_STRING : TimeUtil.getInstance().setStringTimeForTimerDialog(mTimer));
    }

    @SeekBarTouchStop(R.id.seekBarTimer)
    public void onProgressStopOnSeekBar() {
        if (mTimer == 0) {
            mAlarmManager.cancel(mTimerPendingIntent);
        } else {
            long tempValue = System.currentTimeMillis() + ((long) mTimer * CONVERT_TIME_VALUE);
            BusProvider.getInstance().post(new ReceiverTimerValueEvent(tempValue));
            mAlarmManager.set(AlarmManager.RTC, tempValue, mTimerPendingIntent);
        }
    }

    @AfterViews
    public void afterView() {
        mTvTimerTitle.setText(mTimeValue == 0 ? Constant.TIMER_DEFAULT_STRING : TimeUtil.getInstance().setTimeForTimerDialog(mTimeValue));
        mTimer = TimeUtil.getInstance().setValueTimeForTimerDialog(mTimeValue) == 0 ? 0 : TimeUtil.getInstance().setValueTimeForTimerDialog(mTimeValue);
        mSeekBarTimer.setProgress(mTimer);
        mAlarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        mTimerIntent = new Intent(getActivity(), PlayBroadcast_.class);
        mTimerIntent.setAction(Constant.TIMER);
        mTimerPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, mTimerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (mTimeValue == 0) {
            mAlarmManager.cancel(mTimerPendingIntent);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_timer);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.getWindow().setLayout((int) (ScreenUtil.getInstance().getWidthScreen(getActivity()) * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }
}

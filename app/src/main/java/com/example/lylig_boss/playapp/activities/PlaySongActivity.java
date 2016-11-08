package com.example.lylig_boss.playapp.activities;


import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.lylig_boss.playapp.R;
import com.example.lylig_boss.playapp.adapters.ViewPagerPlayActivityAdapter;
import com.example.lylig_boss.playapp.common.Constant;
import com.example.lylig_boss.playapp.eventBus.BusProvider;
import com.example.lylig_boss.playapp.eventBus.event.CheckExistingActivityEvent;
import com.example.lylig_boss.playapp.eventBus.event.PlaySongEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiveOfflineSongEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiveOnlineSongEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiveStringRequestEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiverCurrentDurationSongEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiverDurationSeekBarEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiverTimerValueEvent;
import com.example.lylig_boss.playapp.fragments.ListSongFragment;
import com.example.lylig_boss.playapp.fragments.ListSongFragment_;
import com.example.lylig_boss.playapp.models.OfflineSong;
import com.example.lylig_boss.playapp.models.OnlineSong;
import com.example.lylig_boss.playapp.utils.TimeUtil;
import com.example.lylig_boss.playapp.views.dialogs.TimerDialog;
import com.example.lylig_boss.playapp.views.dialogs.TimerDialog_;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.SeekBarProgressChange;
import org.androidannotations.annotations.SeekBarTouchStop;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 27/06/2016.
 */
@EActivity(R.layout.activity_play_song)
public class PlaySongActivity extends BaseActivity {
    private static final int DELAY_TIME = 1000;
    private static final String DEFAULT_TIME = "00:00";
    @ViewById(R.id.toolBarPlayActivity)
    Toolbar mToolBarPlayActivity;
    @ViewById(R.id.tvNameSongPlayActivity)
    TextView mTvNameSongPlayActivity;
    @ViewById(R.id.imgBtnPlay)
    ImageButton mImgPlay;
    @ViewById(R.id.seekBar)
    SeekBar mSeekBar;
    @ViewById(R.id.tvDurationTime)
    TextView mTvDuration;
    @ViewById(R.id.tvTimeSong)
    TextView mTvTimeSong;
    @ViewById(R.id.viewPagerPlayActivity)
    ViewPager mViewPager;

    @Extra
    ArrayList<OnlineSong> mOnlineSongs;
    @Extra
    int mPosition;
    @Extra
    boolean mIsPlaying;
    @Extra
    int mCurrentDuration;
    @Extra
    boolean mIsOffline;
    @Extra
    OfflineSong mOfflineSong;
    @Extra
    long mTimerValue;

    private Bus mBus;
    private Timer mTimer;
    private Handler mHandlerUpdateUi;
    private OnlineSong mOnlineSong;
    private ViewPagerPlayActivityAdapter mViewPagerAdapter;
    private TimeUtil mTimeUtil;

    @Override
    protected void afterView() {
        overridePendingTransition(R.anim.trans_up_in, R.anim.noanimation);
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mTimeUtil = TimeUtil.getInstance();
        mBus.post(new CheckExistingActivityEvent(true));
        setSupportActionBar(mToolBarPlayActivity);
        mTvNameSongPlayActivity.setText(mIsOffline ? mOfflineSong.getTitle() : mOnlineSongs.get(mPosition).getTitle());
        mHandlerUpdateUi = new Handler();
        mViewPagerAdapter = new ViewPagerPlayActivityAdapter(getSupportFragmentManager(), getApplication(), mOnlineSongs, mPosition, mIsOffline, mIsPlaying);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(ViewPagerPlayActivityAdapter.TAB_PLAY);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == ViewPagerPlayActivityAdapter.TAG_LIST) {
                    remoteIconPlay();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mSeekBar.post(new Runnable() {
            @Override
            public void run() {
                mSeekBar.setProgress(mCurrentDuration);
            }
        });
        changeSeekBarForTypePlayMode(mIsOffline);
        changePlayButton(mIsPlaying);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBus.post(new ReceiveStringRequestEvent(Constant.UPLOAD_UI_PLAY_ACTIVITY));
    }

    @Click(R.id.imgBtnBack)
    void imgBtnExitClick() {
        onBackPressed();
    }

    @Click(R.id.imgBtnTimer)
    void imgBtnTimerClick() {
        TimerDialog dialog = TimerDialog_.builder().mTimeValue(mTimerValue == 0 ? 0 : mTimerValue - System.currentTimeMillis()).build();
        dialog.show(getFragmentManager(), Constant.TIMER_DIALOG_TAG);
    }

    @Click(R.id.imgBtnPlay)
    void imgBtnPlayClick() {
        if (mIsPlaying) {
            mIsPlaying = false;
            mBus.post(new ReceiveStringRequestEvent(Constant.PAUSE));
        } else {
            mIsPlaying = true;
            mBus.post(new ReceiveStringRequestEvent(Constant.REPLAY));
        }
        changePlayButton(mIsPlaying);
        runTimeOnSeekBar();
    }

    @Click(R.id.imgBtnNext)
    void imgBtnNextClick() {
        mBus.post(new ReceiveStringRequestEvent(Constant.NEXT));
        resetUi(mIsOffline);
    }

    @Click(R.id.imgBtnPrevious)
    void imgBtnPrevious() {
        mBus.post(new ReceiveStringRequestEvent(Constant.PREVIOUS));
        if (mPosition == 0) {
            return;
        }
        resetUi(mIsOffline);
    }

    @SeekBarProgressChange(R.id.seekBar)
    void onProgressChangeOnSeekBar(int progress) {
        mCurrentDuration = progress;
        mTvDuration.setText(mTimeUtil.setTimeForDuration(mCurrentDuration));
        if (mIsOffline) {
            mTvTimeSong.setText(mTimeUtil.setTimeForDuration(mOfflineSong.getDuration() - mCurrentDuration));
        } else {
            mTvTimeSong.setText(mTimeUtil.setTimeForDuration(mOnlineSong.getDuration() - mCurrentDuration));
        }
    }

    @SeekBarTouchStop(R.id.seekBar)
    void onProgressStopOnSeekBar() {
        mBus.post(new ReceiverDurationSeekBarEvent(mCurrentDuration));
    }

    @Subscribe
    public void getTimerValue(ReceiverTimerValueEvent valueEvent) {
        mTimerValue = valueEvent.getTimerValue();
    }

    @Subscribe
    public void receiveValueOfPositionOnlineSong(ReceiveOnlineSongEvent event) {
        mIsOffline = false;
        mPosition = event.getPositionSong();
        resetUi(mIsOffline);
    }

    @Subscribe
    public void receiveValueOfDurationOfflineSong(final ReceiveOfflineSongEvent event) {
        mIsOffline = true;
        mOfflineSong = event.getOfflineSong();
        mPosition = event.getPositionSong();
        resetUi(mIsOffline);
    }

    @Subscribe
    public void updateUi(final PlaySongEvent ms) {
        if (mIsPlaying == ms.isPlayed() && mPosition == 0) {
            return;
        }
        mIsPlaying = ms.isPlayed();
        mHandlerUpdateUi.post(new Runnable() {
            @Override
            public void run() {
                changePlayButton(mIsPlaying);
            }
        });
        runTimeOnSeekBar();
    }

    @Subscribe
    public void startRunTime(ReceiverCurrentDurationSongEvent obj) {
        mCurrentDuration = obj.getCurrentDuration();
        mHandlerUpdateUi.post(new Runnable() {
            @Override
            public void run() {
                mSeekBar.setProgress(mCurrentDuration);
            }
        });
    }

    @Subscribe
    public void receiveErrorMessage(ReceiveStringRequestEvent event) {
        if (event.getTitleRequest().equals(Constant.ERROR)) {
            mCurrentDuration = 0;
            mIsPlaying = false;
            mSeekBar.setProgress(mCurrentDuration);
            changePlayButton(mIsPlaying);
        }
    }

    @Subscribe
    public void reloadDataLocal(ReceiveStringRequestEvent event) {
        if (event.getTitleRequest().equals(Constant.RELOAD_DATA_LOCAL)) {
            Fragment fragment = mViewPagerAdapter.getFragmentByPosition(ViewPagerPlayActivityAdapter.TAG_LIST);
            if (fragment instanceof ListSongFragment_) {
                ((ListSongFragment) fragment).reloadDataLocal();
            }
        }
    }

    @Subscribe
    public void closeActivity(ReceiveStringRequestEvent event) {
        if (event.getTitleRequest().equals(Constant.CLOSE_NOTIFICATION)) {
            onBackPressed();
        }
    }

    private void runTimeOnSeekBar() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.cancel();
        mTimer = new Timer();
        if (mIsPlaying) {
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mHandlerUpdateUi.post(new Runnable() {
                        @Override
                        public void run() {
                            mSeekBar.setProgress(mCurrentDuration);
                            mCurrentDuration = mCurrentDuration + DELAY_TIME;
                        }
                    });
                }
            }, 0, DELAY_TIME);
        } else {
            mTimer.cancel();
        }
    }

    private void remoteIconPlay() {
        Fragment fragment = mViewPagerAdapter.getFragmentByPosition(ViewPagerPlayActivityAdapter.TAG_LIST);
        if (fragment instanceof ListSongFragment_) {
            ((ListSongFragment) fragment).smoothScrollRecycleView(mPosition);
        }
    }

    private void changePlayButton(boolean isPlaying) {
        mImgPlay.setBackgroundResource(isPlaying ? R.drawable.ic_pause_press : R.drawable.ic_play_play_activity);
    }

    private void resetUi(boolean isOffline) {
        mCurrentDuration = 0;
        mIsPlaying = true;
        mTvDuration.setText(DEFAULT_TIME);
        mSeekBar.setProgress(mCurrentDuration);
        mViewPagerAdapter.notifyDataSetChanged();
        if (!isOffline) {
            resetUiDurationSongTextOnlineMode();
        } else {
            resetUiDurationSongTextOfflineMode();
        }
        remoteIconPlay();
        changePlayButton(mIsPlaying);
    }

    private void resetUiDurationSongTextOnlineMode() {
        mOnlineSong = mOnlineSongs.get(mPosition);
        mTvTimeSong.setText(mTimeUtil.setTimeForDuration(mOnlineSong.getDuration()));
        mTvNameSongPlayActivity.setText(mOnlineSong.getTitle());
        mSeekBar.setMax(mOnlineSong.getDuration());
        runTimeOnSeekBar();
    }

    private void resetUiDurationSongTextOfflineMode() {
        mTvTimeSong.setText(mTimeUtil.setTimeForDuration(mOfflineSong.getDuration()));
        mSeekBar.setMax(mOfflineSong.getDuration());
        mTvNameSongPlayActivity.setText(mOfflineSong.getTitle());
        runTimeOnSeekBar();
    }

    private void changeSeekBarForTypePlayMode(boolean isOffline) {
        runTimeOnSeekBar();
        if (isOffline) {
            mSeekBar.setMax(mOfflineSong.getDuration());
            mTvTimeSong.setText(mTimeUtil.setTimeForDuration(mOfflineSong.getDuration()));
        } else {
            mOnlineSong = mOnlineSongs.get(mPosition);
            mTvTimeSong.setText(mTimeUtil.setTimeForDuration(mOnlineSong.getDuration()));
            mSeekBar.setMax(mOnlineSong.getDuration());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.noanimation, R.anim.trans_down_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
        mBus.post(new CheckExistingActivityEvent(false));
        mBus.unregister(this);
    }
}

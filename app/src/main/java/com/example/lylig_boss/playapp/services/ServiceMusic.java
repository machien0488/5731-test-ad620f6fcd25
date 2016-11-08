package com.example.lylig_boss.playapp.services;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.lylig_boss.playapp.R;
import com.example.lylig_boss.playapp.activities.PlaySongActivity_;
import com.example.lylig_boss.playapp.common.Constant;
import com.example.lylig_boss.playapp.databases.RealmHelper;
import com.example.lylig_boss.playapp.eventBus.BusProvider;
import com.example.lylig_boss.playapp.eventBus.event.CheckExistingActivityEvent;
import com.example.lylig_boss.playapp.eventBus.event.PlaySongEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiveDownloadRequestCodeEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiveOfflineSongEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiveOnlineSongEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceivePositionSongEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiveStringRequestEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiverCurrentDurationSongEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiverDataOfflineEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiverDataOnlineEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiverDurationSeekBarEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiverTimerValueEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReloadDataEvent;
import com.example.lylig_boss.playapp.eventBus.event.StartActivityEvent;
import com.example.lylig_boss.playapp.models.OfflineSong;
import com.example.lylig_boss.playapp.models.OnlineSong;
import com.example.lylig_boss.playapp.models.PlaySong;
import com.example.lylig_boss.playapp.notifications.PlaySongNotification;
import com.example.lylig_boss.playapp.utils.FileUtil;
import com.example.lylig_boss.playapp.utils.InternetUtil;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.Receiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 21/06/2016.
 */
@EService
public class ServiceMusic extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener {
    private static final String TAG = ServiceMusic.class.getSimpleName();
    private static final int ZERO_VALUE = 0;
    private static final int OVER_TIME = 9000;

    private DownloadManager mDownloadManager;
    private NotificationManager mNotificationManager;
    private MediaPlayer mMediaPlayer;
    private List<OnlineSong> mOnlineSongs;
    private List<OfflineSong> mOfflineSongs;
    private int mPositionSong;
    private int mDurationTemp;
    private int mSizeList;
    private long mTimerValue = 0;
    private long mDownloadRequestCode;
    private boolean mIsOffline;
    private boolean mIsRequestTimeOut;
    private boolean mIsPlaying;
    private boolean mIsShowNotification;
    private boolean mIsCreatedActivity;
    private Handler mHandlerDelay;
    private String mLinkTemp;
    private RealmHelper mRealmHelper;
    private Runnable mRunnable;
    private Bus mBus;
    private PlaySongNotification mNotification;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Receiver(actions = {ConnectivityManager.CONNECTIVITY_ACTION}, registerAt = Receiver.RegisterAt.OnCreateOnDestroy)
    void onActionConnection() {
        checkInternet(getApplication());
    }

    @Receiver(actions = {DownloadManager.ACTION_DOWNLOAD_COMPLETE}, registerAt = Receiver.RegisterAt.OnCreateOnDestroy)
    void onReceiverRequestCode(Intent intent) {
        if (mDownloadRequestCode == intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)) {
            mDownloadRequestCode = 0;
            mRealmHelper.setDataRealm(getApplication(), mOnlineSongs.get(mPositionSong),
                    String.format("%s%s%s", Constant.PATH_DOWNLOAD_MUSIC_FILE, mOnlineSongs.get(mPositionSong).getId(), Constant.TYPE_MEDIA_FILE),
                    String.format("%s%s%s", Constant.PATH_DOWNLOAD_IMAGE_FILE, mOnlineSongs.get(mPositionSong).getId(), Constant.TYPE_IMAGE_FILE));
        }
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: ");
        super.onCreate();
        RealmConfiguration configuration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(configuration);
        mBus = BusProvider.getInstance();
        mBus.register(this);
        mNotification = PlaySongNotification.getInstance();
        mRealmHelper = RealmHelper.with(getApplication());
        createMediaPlayerObject();
        mHandlerDelay = new Handler();
        mOnlineSongs = new ArrayList<>();
        mOfflineSongs = new ArrayList<>();
        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        return START_STICKY;
    }


    @Subscribe
    public void receiveStringRequest(ReceiveStringRequestEvent request) {
        mDurationTemp = (mMediaPlayer.getCurrentPosition() != 0 ? mMediaPlayer.getCurrentPosition() : ZERO_VALUE);
        switch (request.getTitleRequest()) {
            case Constant.PLAY:
                if (mOnlineSongs.size() == ZERO_VALUE && mOfflineSongs.size() == ZERO_VALUE) {
                    mBus.post(new PlaySongEvent(mIsPlaying));
                } else {
                    mPositionSong = ZERO_VALUE;
                    mIsOffline = mOnlineSongs.size() == ZERO_VALUE;
                    choiceTypePlayMode(mIsOffline);
                }
                break;
            case Constant.PAUSE:
                mIsPlaying = false;
                mBus.post(new PlaySongEvent(false));
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mBus.post(new ReceiverCurrentDurationSongEvent(mDurationTemp));
                }
                if (mIsShowNotification) {
                    updateNotification(mIsOffline, mIsPlaying);
                }
                break;
            case Constant.REPLAY:
                if (!mIsPlaying) {
                    mIsPlaying = true;
                    mIsRequestTimeOut = false;
                    mMediaPlayer.start();
                    sendUpdateUi(mIsPlaying);
                } else {
                    checkRequestTimeOut();
                }
                if (mIsShowNotification) {
                    updateNotification(mIsOffline, mIsPlaying);
                } else {
                    showNotification(mIsOffline);
                }
                break;
            case Constant.NEXT:
                if (mSizeList == 1) {
                    mPositionSong = ZERO_VALUE;
                } else {
                    mPositionSong = mPositionSong == (mSizeList - 1) ? ZERO_VALUE : ++mPositionSong;
                }
                choiceTypePlayMode(mIsOffline);
                break;
            case Constant.PREVIOUS:
                if (mPositionSong == ZERO_VALUE) {
                    mBus.post(new PlaySongEvent(mIsPlaying));
                    return;
                }
                mPositionSong--;
                choiceTypePlayMode(mIsOffline);
                break;
            case Constant.REQUEST_START_ACTIVITY:
                boolean status = mMediaPlayer.isPlaying();
                mDurationTemp = mMediaPlayer.getCurrentPosition();
                mBus.post(new StartActivityEvent(status, mDurationTemp));
                mBus.post(new PlaySongEvent(status));
                break;
            case Constant.RELOAD_DATA_LOCAL:
                mOfflineSongs.clear();
                mOfflineSongs = mRealmHelper.queryData();
                break;
            case Constant.CLOSE_NOTIFICATION:
                mIsShowNotification = false;
                stopForeground(true);
                mMediaPlayer.pause();
                mIsPlaying = false;
                sendUpdateUi(false);
                break;
            case Constant.SHOW_ACTIVITY:
                getApplication().sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
                startPlayActivity(mIsPlaying);
                break;
            case Constant.UPLOAD_UI_PLAY_ACTIVITY:
                sendUpdateUi(mIsPlaying);
                break;
            case Constant.DOWNLOAD:
                if (mDownloadRequestCode == ZERO_VALUE) {
                    FileUtil.getInstance().downloadImage(getApplication(), mDownloadManager, mOnlineSongs.get(mPositionSong));
                }
                break;
            case Constant.REQUEST_TIMER:
                mBus.post(new ReceiverTimerValueEvent(mTimerValue));
                break;
            case Constant.RESET_TIMER:
                mTimerValue = ZERO_VALUE;
                mBus.post(new ReceiverTimerValueEvent(mTimerValue));
        }
    }

    private void choiceTypePlayMode(boolean isOffline) {
        if (isOffline) {
            mBus.post(new ReceiveOfflineSongEvent(mOfflineSongs.get(mPositionSong), mPositionSong));
            if (mIsShowNotification) {
                mNotification.updateUiNotification(getApplication(), mNotificationManager, true, new PlaySong(mOfflineSongs.get(mPositionSong), true));
            }
            playOffSong(mPositionSong);
        } else {
            mBus.post(new ReceiveOnlineSongEvent(mOnlineSongs.get(mPositionSong), mPositionSong));
            if (mIsShowNotification) {
                mNotification.updateUiNotification(getApplication(), mNotificationManager, false, new PlaySong(mOnlineSongs.get(mPositionSong), true));
            }
            playOnlSong(mPositionSong);
        }
    }

    private void showNotification(boolean isOffline) {
        if (isOffline) {
            mNotification.showNotification(getApplication(), this, new PlaySong(mOfflineSongs.get(mPositionSong), mIsPlaying), mIsOffline);
        } else {
            mNotification.showNotification(getApplication(), this, new PlaySong(mOnlineSongs.get(mPositionSong), mIsPlaying), mIsOffline);
        }
    }

    private void updateNotification(boolean isOffline, boolean isPlaying) {
        if (isOffline) {
            mNotification.updateUiNotification(getApplication(), mNotificationManager, true, new PlaySong(mOfflineSongs.get(mPositionSong), isPlaying));
        } else {
            mNotification.updateUiNotification(getApplication(), mNotificationManager, false, new PlaySong(mOnlineSongs.get(mPositionSong), isPlaying));
        }
    }

    @Subscribe
    public void getTimerValue(ReceiverTimerValueEvent valueEvent) {
        mTimerValue = valueEvent.getTimerValue();
    }

    @Subscribe
    public void playSongWithReceiverPosition(ReceivePositionSongEvent obj) {
        mPositionSong = obj.getPositionSong();
        choiceTypePlayMode(mIsOffline);
    }

    @Subscribe
    public void receiveOfflineSong(ReceiveOfflineSongEvent obj) {
        mPositionSong = obj.getPositionSong();
        mDurationTemp = ZERO_VALUE;
        mIsOffline = true;
        mSizeList = mOfflineSongs.size();
        if (mIsShowNotification) {
            updateNotification(mIsOffline, mIsPlaying);
        }
        playOffSong(mPositionSong);
    }

    @Subscribe
    public void receiveOnlineSong(ReceiveOnlineSongEvent obj) {
        mIsRequestTimeOut = false;
        mIsOffline = false;
        mPositionSong = obj.getPositionSong();
        mDurationTemp = ZERO_VALUE;
        mLinkTemp = obj.getOnlineSong().getStreamUrl();
        mSizeList = mOnlineSongs.size();
        if (mIsShowNotification) {
            updateNotification(mIsOffline, mIsPlaying);
        }
        playOnlSong(mPositionSong);
    }

    @Subscribe
    public void receiveOnlineData(ReceiverDataOnlineEvent data) {
        mOnlineSongs.clear();
        mOnlineSongs.addAll(data.getOnlineSongs());
    }

    @Subscribe
    public void receiveOfflineData(ReceiverDataOfflineEvent data) {
        mOfflineSongs.clear();
        mOfflineSongs.addAll(data.getOfflineSongs());
    }

    @Subscribe
    public void receiveDuration(ReceiverDurationSeekBarEvent obj) {
        mDurationTemp = obj.getDurationFromSeekBar();
        mMediaPlayer.seekTo(mDurationTemp);
    }

    @Subscribe
    public void receiveDownloadRequestCode(ReceiveDownloadRequestCodeEvent code) {
        mDownloadRequestCode = code.getDownloadRequestCode();
    }

    @Subscribe
    public void checkCreatedActivity(CheckExistingActivityEvent event) {
        mIsCreatedActivity = event.isCreated();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mIsPlaying) {
            mp.start();
            if (!mIsShowNotification) {
                showNotification(mIsOffline);
            }
            mIsShowNotification = true;
        }
        sendUpdateUi(mIsPlaying);
        mIsRequestTimeOut = false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mDurationTemp = 0;
        mBus.post(new PlaySongEvent(mIsPlaying));
        if (mIsShowNotification) {
            updateNotification(mIsOffline, mIsPlaying);
        }
        autoPlaySong(mIsOffline);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stop();
        mMediaPlayer = null;
        mBus.unregister(this);
    }

    private void sendUpdateUi(Boolean isCheck) {
        mDurationTemp = mMediaPlayer.getCurrentPosition();
        mBus.post(new PlaySongEvent(isCheck));
        mBus.post(new ReceiverCurrentDurationSongEvent(mDurationTemp));
    }

    private void checkRequestTimeOut() {
        if (mRunnable != null) {
            mHandlerDelay.removeCallbacks(mRunnable);
        }
        mHandlerDelay.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mIsRequestTimeOut) {
                    sendUpdateUi(!mIsPlaying);
                    mBus.post(new ReceiveStringRequestEvent(Constant.ERROR));
                    Toast.makeText(getApplication(), getString(R.string.toast_error_message_network_error), Toast.LENGTH_SHORT).show();
                    mIsPlaying = false;
                    mHandlerDelay.removeCallbacks(mRunnable);
                }
            }
        }, OVER_TIME);
        mIsRequestTimeOut = true;
    }

    private boolean checkInternet(Context context) {
        if (!InternetUtil.getInstance().checkInternet(context)) {
            if (mIsPlaying) {
                mMediaPlayer.pause();
                mIsPlaying = false;
            } else {
                mMediaPlayer.reset();
            }
            mBus.post(new PlaySongEvent(mIsPlaying));
            return false;
        } else {
            mBus.post(new ReloadDataEvent(true));
            return true;
        }
    }

    private void createMediaPlayerObject() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplication(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private void playOnlSong(int position) {
        mIsPlaying = true;
        OnlineSong obj = mOnlineSongs.get(position);
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        } else {
            createMediaPlayerObject();
        }
        checkRequestTimeOut();
        mLinkTemp = obj.getStreamUrl();
        mDurationTemp = ZERO_VALUE;
        try {
            mMediaPlayer.setDataSource(mLinkTemp);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }

    private void playOffSong(int positionSong) {
        mIsPlaying = true;
        OfflineSong song = mOfflineSongs.get(positionSong);
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        } else {
            createMediaPlayerObject();
        }
        try {
            mMediaPlayer.setDataSource(song.getStreamUrl());
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }

    private void startPlayActivity(final boolean isPlay) {
        mDurationTemp = mMediaPlayer.getCurrentPosition();
        if (!mIsOffline) {
            PlaySongActivity_.intent(getApplication())
                    .mPosition(mPositionSong)
                    .mOnlineSongs((ArrayList<OnlineSong>) mOnlineSongs)
                    .mIsPlaying(isPlay)
                    .mCurrentDuration(mDurationTemp)
                    .mTimerValue(mTimerValue)
                    .flags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .start();
        } else {
            PlaySongActivity_.intent(getApplication())
                    .mIsOffline(mIsOffline)
                    .mIsPlaying(isPlay)
                    .mPosition(mPositionSong)
                    .mCurrentDuration(mDurationTemp)
                    .mOfflineSong(mOfflineSongs.get(mPositionSong))
                    .mTimerValue(mTimerValue)
                    .flags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .start();
        }
    }

    private void autoPlaySong(boolean isOffline) {
        if (mMediaPlayer.getDuration() >= 0) {
            if (isOffline) {
                mPositionSong = mSizeList == 1 ? ZERO_VALUE : mPositionSong == (mSizeList - 1) ? ZERO_VALUE : ++mPositionSong;
            } else {
                if (checkInternet(getApplication())) {
                    mPositionSong = mSizeList == 1 ? ZERO_VALUE : mPositionSong == (mSizeList - 1) ? ZERO_VALUE : ++mPositionSong;
                }
            }
        }
        choiceTypePlayMode(mIsOffline);
    }
}


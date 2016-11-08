package com.example.lylig_boss.playapp.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.RemoteViews;

import com.example.lylig_boss.playapp.R;
import com.example.lylig_boss.playapp.broadcasts.PlayBroadcast_;
import com.example.lylig_boss.playapp.common.Constant;
import com.example.lylig_boss.playapp.models.PlaySong;
import com.example.lylig_boss.playapp.services.ServiceMusic;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 29/07/2016.
 */
public class PlaySongNotification {
    private static PlaySongNotification sPlaySongNotification;
    private RemoteViews mBigViews;
    private Notification mNotificationBuilder;

    public static synchronized PlaySongNotification getInstance() {
        if (sPlaySongNotification == null) {
            sPlaySongNotification = new PlaySongNotification();
        }
        return sPlaySongNotification;
    }

    public void showNotification(Context context, ServiceMusic serviceMusic, PlaySong objectSong, boolean isOffline) {
        // Using RemoteViews to bind custom layouts into Notification
        mBigViews = new RemoteViews(context.getPackageName(),
                R.layout.notification_play_song);
        // showing default album image
        mBigViews.setImageViewResource(R.id.imgNotificationPhotoSong, R.drawable.ic_disk);
        // create intent
        Intent notificationIntent = new Intent(context, PlayBroadcast_.class);
        notificationIntent.setAction(Constant.ACTION.SHOW_ACTIVITY_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent previousIntent = new Intent(context, PlayBroadcast_.class);
        previousIntent.setAction(Constant.ACTION.PREV_ACTION);
        PendingIntent pPreviousIntent = PendingIntent.getBroadcast(context, 0,
                previousIntent, 0);

        Intent pauseIntent = new Intent(context, PlayBroadcast_.class);
        pauseIntent.setAction(Constant.ACTION.PAUSE_ACTION);
        PendingIntent pPauseIntent = PendingIntent.getBroadcast(context, 0,
                pauseIntent, 0);

        Intent nextIntent = new Intent(context, PlayBroadcast_.class);
        nextIntent.setAction(Constant.ACTION.NEXT_ACTION);
        PendingIntent pNextIntent = PendingIntent.getBroadcast(context, 0,
                nextIntent, 0);

        Intent closeIntent = new Intent(context, PlayBroadcast_.class);
        closeIntent.setAction(Constant.ACTION.STOP_FOREGROUND_ACTION);
        PendingIntent pCloseIntent = PendingIntent.getBroadcast(context, 0,
                closeIntent, 0);
        // set view
        mBigViews.setOnClickPendingIntent(R.id.imgNotificationPlay, pPauseIntent);
        mBigViews.setOnClickPendingIntent(R.id.imgNotificationNext, pNextIntent);
        mBigViews.setOnClickPendingIntent(R.id.imgNotificationPrevious, pPreviousIntent);
        mBigViews.setOnClickPendingIntent(R.id.imgNotificationExit, pCloseIntent);
        mBigViews.setOnClickPendingIntent(R.id.rlNotificationBarEx,pendingIntent);
        mBigViews.setImageViewResource(R.id.imgNotificationPlay,
                R.drawable.ic_pause_press);
        mBigViews.setTextViewText(R.id.tvNotificationSongName, isOffline ? objectSong.getOfflineSong().getTitle() : objectSong.getOnlineSong().getTitle());
        mBigViews.setTextViewText(R.id.tvNotificationSinger, isOffline ? objectSong.getOfflineSong().getSinger() : objectSong.getOnlineSong().getSinger());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mNotificationBuilder = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification))
                    .setContentIntent(pendingIntent)
                    .build();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mNotificationBuilder.bigContentView = mBigViews;
        }
        mNotificationBuilder.flags = Notification.FLAG_ONGOING_EVENT;
        serviceMusic.startForeground(Constant.NOTIFICATION_ID.FOREGROUND_SERVICE, mNotificationBuilder);
    }

    public void updateUiNotification(Context context, NotificationManager notificationManager, boolean isOffline, PlaySong objectSong) {
        // create intent
        Intent replayIntent = new Intent(context, PlayBroadcast_.class);
        replayIntent.setAction(Constant.ACTION.REPLAY_ACTION);
        PendingIntent pReplayIntent = PendingIntent.getBroadcast(context, 0,
                replayIntent, 0);
        Intent pauseIntent = new Intent(context, PlayBroadcast_.class);
        pauseIntent.setAction(Constant.ACTION.PAUSE_ACTION);
        PendingIntent pPauseIntent = PendingIntent.getBroadcast(context, 0,
                pauseIntent, 0);
        // update the icon Play/pause
        if (!objectSong.isPlaying()) {
            mBigViews.setOnClickPendingIntent(R.id.imgNotificationPlay, pReplayIntent);
            mBigViews.setImageViewResource(R.id.imgNotificationPlay,
                    R.drawable.ic_play_play_activity);
        } else {
            mBigViews.setOnClickPendingIntent(R.id.imgNotificationPlay, pPauseIntent);
            mBigViews.setImageViewResource(R.id.imgNotificationPlay,
                    R.drawable.ic_pause_press);
        }
        // update the title
        mBigViews.setTextViewText(R.id.tvNotificationSongName, isOffline ? objectSong.getOfflineSong().getTitle() : objectSong.getOnlineSong().getTitle());
        mBigViews.setTextViewText(R.id.tvNotificationSinger, isOffline ? objectSong.getOfflineSong().getSinger() : objectSong.getOnlineSong().getSinger());
        // update the art song
        if(isOffline){
            mBigViews.setImageViewBitmap(R.id.imgNotificationPhotoSong, BitmapFactory.decodeFile(objectSong.getOfflineSong().getArtworkUri()));
        }
        // update the notification
        notificationManager.notify(Constant.NOTIFICATION_ID.FOREGROUND_SERVICE, mNotificationBuilder);
    }
}

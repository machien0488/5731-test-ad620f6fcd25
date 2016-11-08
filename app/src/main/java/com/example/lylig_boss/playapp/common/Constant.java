package com.example.lylig_boss.playapp.common;

import android.os.Environment;

import java.io.File;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 20/06/2016.
 */
public class Constant {
    public static final String LINK_FIRE_BASE = "https://myapp-a88ff.firebaseio.com/Data/DataMusic";
    public static final String LINK_STORAGE_FIRE_BASE = "gs://myapp-a88ff.appspot.com/";
    public static final String TITLE_FONT = "fonts/Andy.ttf";
    public static final String CONTENT_FONT = "fonts/Amerika.ttf";
    public static final String PATH_FILE = String.format("%s%s%s", Environment.getExternalStorageDirectory().toString(), File.separator, "PlayApp/");
    public static final String PATH_DOWNLOAD_MUSIC_FILE = String.format("%s%s%s%s%s", PATH_FILE, "data", File.separator, "Music", File.separator);
    public static final String SHORT_PATH_DOWNLOAD_MUSIC_FILE = String.format("%s%s%s", File.separator, "PlayApp/data/Music", File.separator);
    public static final String PATH_DOWNLOAD_IMAGE_FILE = String.format("%s%s%s%s%s", PATH_FILE, "data", File.separator, "Image", File.separator);
    public static final String PLAY = "PlaySong";
    public static final String PAUSE = "PauseSong";
    public static final String REPLAY = "ReplaySongFromActivityToService";
    public static final String NEXT = "NextSong";
    public static final String PREVIOUS = "PreviousSong";
    public static final String LIST_ONL = "listOnline";
    public static final String PLAYING = "Playing";
    public static final String REQUEST_START_ACTIVITY = "RequestStartActivity";
    public static final String POSITION = "positionSongtoPlay";
    public static final String TYPE_MEDIA_FILE = ".mp3";
    public static final String TYPE_IMAGE_FILE = ".png";
    public static final String SONG = "CurrentSong";
    public static final String ERROR = "Service error";
    public static final String OFFLINE_MODE = "OfflineMode";
    public static final String RELOAD_DATA_LOCAL = "ReloadData";
    public static final String UPLOAD_DIALOG_TAG = "UploadFileDialog";
    public static final String TIMER_DIALOG_TAG = "TimerDialog";
    public static final String UPLOAD_UI_PLAY_ACTIVITY = "UpdateUiPlaySongActivity";
    public static final String SHOW_ACTIVITY = "HideNotificationAndShowPlaySongActivity";
    public static final String CLOSE_NOTIFICATION = "CloseNotification";
    public static final String DOWNLOAD = "DownloadFile";
    public static final String ERROR_FILE = "error file";
    public static final String TIMER = "Timer";
    public static final String REQUEST_TIMER = "RequestTimerValue";
    public static final String RESET_TIMER = "ResetTimerValue";
    public static final String UPDATING = "Updating";
    public static final String TIMER_DEFAULT_STRING = "Hẹn giờ ngừng phát nhạc.";
    public static final String TIMER_AFTER_STRING = "Ngừng phát nhạc sau:";
    public static final String NAME_SINGER_DEFAULT = "Admin đẹp trai";

    public interface ACTION {
        String SHOW_ACTIVITY_ACTION = "show";
        String PREV_ACTION = "previous";
        String PAUSE_ACTION = "pause";
        String REPLAY_ACTION = "replay";
        String NEXT_ACTION = "next";
        String STOP_FOREGROUND_ACTION = "topForeground";

    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }

}

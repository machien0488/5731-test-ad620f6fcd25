package com.example.lylig_boss.playapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 23/06/2016.
 */

@Data
public class PlaySong implements Parcelable {
    public static final Creator<PlaySong> CREATOR = new Creator<PlaySong>() {
        @Override
        public PlaySong createFromParcel(Parcel in) {
            return new PlaySong(in);
        }

        @Override
        public PlaySong[] newArray(int size) {
            return new PlaySong[size];
        }
    };
    private OnlineSong onlineSong;
    private boolean isPlaying;
    private OfflineSong offlineSong;

    public PlaySong(OnlineSong song, boolean isPlaying) {
        this.onlineSong = song;
        this.isPlaying = isPlaying;
    }


    public PlaySong(OfflineSong offlineSong, boolean isPlaying) {
        this.isPlaying = isPlaying;
        this.offlineSong = offlineSong;
    }

    protected PlaySong(Parcel in) {
        onlineSong = in.readParcelable(OnlineSong.class.getClassLoader());
        isPlaying = in.readByte() != 0;
        offlineSong = in.readParcelable(OfflineSong.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(onlineSong, flags);
        dest.writeByte((byte) (isPlaying ? 1 : 0));
        dest.writeParcelable(offlineSong, flags);
    }
}

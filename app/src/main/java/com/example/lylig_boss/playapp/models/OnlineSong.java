package com.example.lylig_boss.playapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 6/17/16.
 */
@Data
@AllArgsConstructor(suppressConstructorProperties = true)
public class OnlineSong implements Parcelable {

    private String id;
    private String title;
    private String streamUrl;
    private String artworkUrl;
    private int duration;
    private String genre;
    private String downloadUrl;
    private String singer;

    public OnlineSong() {
    }

    protected OnlineSong(Parcel in) {
        id = in.readString();
        title = in.readString();
        streamUrl = in.readString();
        artworkUrl = in.readString();
        duration = in.readInt();
        genre = in.readString();
        downloadUrl = in.readString();
        singer = in.readString();
    }

    public static final Creator<OnlineSong> CREATOR = new Creator<OnlineSong>() {
        @Override
        public OnlineSong createFromParcel(Parcel in) {
            return new OnlineSong(in);
        }

        @Override
        public OnlineSong[] newArray(int size) {
            return new OnlineSong[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(streamUrl);
        dest.writeString(artworkUrl);
        dest.writeInt(duration);
        dest.writeString(genre);
        dest.writeString(downloadUrl);
        dest.writeString(singer);
    }
}

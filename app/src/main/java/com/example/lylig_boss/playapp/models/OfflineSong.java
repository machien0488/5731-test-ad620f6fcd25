package com.example.lylig_boss.playapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 6/17/15.
 */
@Data
public class OfflineSong extends RealmObject implements Parcelable {

    @PrimaryKey
    private String id;
    private String title;
    private String streamUrl;
    private String genre;
    private String downloadUrl;
    private int duration;
    private String artworkUri;
    private String singer;

    public OfflineSong() {
    }

    protected OfflineSong(Parcel in) {
        id = in.readString();
        title = in.readString();
        streamUrl = in.readString();
        genre = in.readString();
        downloadUrl = in.readString();
        duration = in.readInt();
        artworkUri = in.readString();
        singer = in.readString();
    }

    public static final Creator<OfflineSong> CREATOR = new Creator<OfflineSong>() {
        @Override
        public OfflineSong createFromParcel(Parcel in) {
            return new OfflineSong(in);
        }

        @Override
        public OfflineSong[] newArray(int size) {
            return new OfflineSong[size];
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
        dest.writeString(genre);
        dest.writeString(downloadUrl);
        dest.writeInt(duration);
        dest.writeString(artworkUri);
        dest.writeString(singer);
    }
}

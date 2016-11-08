package com.example.lylig_boss.playapp.databases;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.example.lylig_boss.playapp.R;
import com.example.lylig_boss.playapp.models.OfflineSong;
import com.example.lylig_boss.playapp.models.OnlineSong;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 13/06/2016.
 */
public class RealmHelper {
    private static RealmHelper instance;
    private final Realm realm;

    public RealmHelper(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmHelper with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmHelper(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmHelper with(Activity activity) {
        if (instance == null) {
            instance = new RealmHelper(activity.getApplication());
        }
        return instance;
    }

    public static RealmHelper with(Application application) {

        if (instance == null) {
            instance = new RealmHelper(application);
        }
        return instance;
    }

    public static RealmHelper getInstance() {

        return instance;
    }

    public Realm getRealm() {
        return realm;
    }

    public void clearAll() {
        realm.beginTransaction();
        realm.delete(OfflineSong.class);
        realm.commitTransaction();
    }

    public void setDataRealm(final Context context, final OnlineSong obj, final String streamUri, final String artworkUri) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                OfflineSong data = realm.createObject(OfflineSong.class);
                data.setId(obj.getId());
                data.setTitle(obj.getTitle());
                data.setDuration(obj.getDuration());
                data.setGenre(obj.getGenre());
                data.setDownloadUrl(obj.getDownloadUrl());
                data.setStreamUrl(streamUri);
                data.setSinger(obj.getSinger());
                data.setArtworkUri(artworkUri);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, context.getString(R.string.text_title_download_success), Toast.LENGTH_SHORT).show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
            }
        });
    }

    public OfflineSong quertyDatafor(String titleSong) {
        return realm.where(OfflineSong.class).equalTo("title", titleSong).findFirst();
    }

    public RealmResults<OfflineSong> queryData() {
        return realm.where(OfflineSong.class).findAll();
    }

}

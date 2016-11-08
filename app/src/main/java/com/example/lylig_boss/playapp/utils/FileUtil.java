package com.example.lylig_boss.playapp.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.webkit.URLUtil;

import com.example.lylig_boss.playapp.R;
import com.example.lylig_boss.playapp.common.Constant;
import com.example.lylig_boss.playapp.eventBus.BusProvider;
import com.example.lylig_boss.playapp.eventBus.event.ReceiveDownloadRequestCodeEvent;
import com.example.lylig_boss.playapp.models.OnlineSong;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 05/07/2016.
 */
public final class FileUtil {
    private static FileUtil sFileUtil;
    private static String sPath;
    private static String sFileName;
    private static final int BYTE_VALUE = 1024;
    private static final int ZERO_VALUE = 0;
    private static final String FILE_TYPE = "file";
    private static final String CONTENT_TYPE = "content";

    public static synchronized FileUtil getInstance() {
        if (sFileUtil == null) {
            sFileUtil = new FileUtil();
        }
        return sFileUtil;
    }

    private FileUtil() {
//         TO DO : nothing....
    }

    public void downloadImage(final Context context, final DownloadManager downloadManager, final OnlineSong onlineSong) {
        class DownloadFile extends AsyncTask<String, Integer, String> {
            private boolean isCompleted = true;

            @Override
            protected String doInBackground(String... url) {
                int count;
                if (URLUtil.isValidUrl(url[ZERO_VALUE])) {
                    try {
                        URL url1 = new URL(url[ZERO_VALUE]);
                        URLConnection urlConnection = url1.openConnection();
                        urlConnection.connect();
                        InputStream input = new BufferedInputStream(url1.openStream());
                        File file = new File(String.format("%s%s%s", Constant.PATH_DOWNLOAD_IMAGE_FILE, onlineSong.getId(), Constant.TYPE_IMAGE_FILE));
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        OutputStream output = new FileOutputStream(file);
                        byte data[] = new byte[BYTE_VALUE];
                        while ((count = input.read(data)) != -1) {
                            output.write(data, ZERO_VALUE, count);
                        }
                        output.flush();
                        output.close();
                        input.close();
                    } catch (Exception e) {
                        isCompleted = false;
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (isCompleted) {
                    long code = downloadFileFromFireBaseStorage(context, downloadManager, onlineSong.getStreamUrl(), onlineSong.getId());
                    BusProvider.getInstance().post(new ReceiveDownloadRequestCodeEvent(code));
                }
            }
        }
        DownloadFile downloadFile = new DownloadFile();
        File file = new File(Constant.PATH_DOWNLOAD_IMAGE_FILE);
        boolean isOk = false;
        if (!file.exists()) {
            isOk = file.mkdirs();
        }
        downloadFile.execute(onlineSong.getArtworkUrl());
    }

    public UploadTask uploadFileFromDevice(final String nameSong, final String nameSinger, final int durationSong, final String linkImg,
                                           final String genre, String pathFile, StorageReference storageReference, final Firebase firebase, final Context context) {
        Uri file = Uri.fromFile(new File(pathFile));
        StorageReference storageRef = storageReference.child(String.format("%s%s", "Music/", file.getLastPathSegment()));
        UploadTask uploadTask = storageRef.putFile(Uri.fromFile(new File(pathFile)));

// Show the percent download
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i("Upload Task", "onProgress: " + (taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()) * 100);
            }
        });
// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                new AlertDialog.Builder(context)
                        .setTitle(context.getResources().getString(R.string.dialog_upload_title_warning))
                        .setMessage(context.getResources().getString(R.string.dialog_upload_error_message_upload_fail))
                        .setCancelable(true)
                        .show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
// TaskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                List<OnlineSong> listData = new ArrayList<>();
                assert downloadUrl != null;
                listData.add(new OnlineSong(UUID.randomUUID().toString(), nameSong, downloadUrl.toString(), linkImg, durationSong, genre, linkImg, nameSinger));
                firebase.push().setValue(listData);
                new AlertDialog.Builder(context)
                        .setTitle(context.getResources().getString(R.string.dialog_upload_title_warning))
                        .setMessage(context.getResources().getString(R.string.dialog_upload_message_upload_success))
                        .setCancelable(true)
                        .show();
            }
        });
        return uploadTask;
    }

    public long downloadFileFromFireBaseStorage(Context context, DownloadManager downloadManager, String linkUrl, String trackName) {
        Uri linkDownload = Uri.parse(linkUrl);
        DownloadManager.Request request = new DownloadManager.Request(linkDownload);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        //Set whether this download may proceed over a roaming connection.
        request.setAllowedOverRoaming(false);
        request.setTitle(trackName);
        request.setDescription(context.getString(R.string.notification_download_text_downloading));
        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalPublicDir(Constant.SHORT_PATH_DOWNLOAD_MUSIC_FILE, String.format("%s%s", trackName, Constant.TYPE_MEDIA_FILE));
        //Enqueue a new download and same the referenceId
        return downloadManager.enqueue(request);
    }

    public String getRealNameFromUri(Context context, Uri contentUri) {
        Cursor cursor;
        String projectionType = MediaStore.Audio.Media.TITLE;
        String scheme = contentUri.getScheme();
        if (scheme.equals(FILE_TYPE)) {
            sFileName = contentUri.getLastPathSegment();
        } else if (scheme.equals(CONTENT_TYPE)) {
            cursor = context.getContentResolver().query(contentUri, null, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();
            cursor = context.getContentResolver().query(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Audio.Media._ID + " = ? ", new String[]{document_id}, null);
            assert cursor != null;
            if (cursor.getCount() == 0) {
                sFileName = Constant.ERROR_FILE;
            } else {
                cursor.moveToFirst();
                sFileName = cursor.getString(cursor.getColumnIndex(projectionType));
                cursor.close();
            }
        }
        return sFileName;
    }

    public String geRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor;
        String scheme = contentUri.getScheme();
        String projectionType = MediaStore.Audio.Media.DATA;
        if (scheme.equals(FILE_TYPE)) {
            sPath = contentUri.getPath();
        } else if (scheme.equals(CONTENT_TYPE)) {
            cursor = context.getContentResolver().query(contentUri, null, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();
            cursor = context.getContentResolver().query(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Audio.Media._ID + " = ? ", new String[]{document_id}, null);
            assert cursor != null;
            if (cursor.getCount() == 0) {
                sPath = Constant.ERROR_FILE;
            } else {
                cursor.moveToFirst();
                sPath = cursor.getString(cursor.getColumnIndex(projectionType));
                cursor.close();
            }
        }
        return sPath;
    }
}

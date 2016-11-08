package com.example.lylig_boss.playapp.views.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lylig_boss.playapp.R;
import com.example.lylig_boss.playapp.common.Constant;
import com.example.lylig_boss.playapp.utils.FileUtil;
import com.example.lylig_boss.playapp.utils.ScreenUtil;
import com.firebase.client.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 25/07/2016.
 */
@EFragment(R.layout.dialog_upload_file)
public class UploadFileDialog extends DialogFragment {
    private Firebase mFireBase;
    private StorageReference mStorageReference;
    @ViewById(R.id.edtNameSong)
    EditText mEdtNameSong;
    @ViewById(R.id.edtLinkImgSong)
    EditText mEdtLinkImgSong;
    @ViewById(R.id.edtSingerUploadSong)
    EditText mEdtSingerUploadSong;
    @ViewById(R.id.edtGenreUploadSong)
    EditText mEdtGenreUploadSong;

    @FragmentArg
    int mDurationSong;
    @FragmentArg
    String mNameSong;
    @FragmentArg
    String mPathFile;

    @AfterViews
    public void afterView() {
        mEdtNameSong.setText(mNameSong);
        mEdtSingerUploadSong.setText(Constant.NAME_SINGER_DEFAULT);
        mFireBase = new Firebase(Constant.LINK_FIRE_BASE);
        mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(Constant.LINK_STORAGE_FIRE_BASE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_upload_file);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout((int) (ScreenUtil.getInstance().getWidthScreen(getActivity()) * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    @Click(R.id.imgSubmit)
    public void submitToUploadFile() {
        String nameSong;
        String linkSong;
        String singerSong;
        String genreSong;
        boolean isOk;
        isOk = !mEdtNameSong.getText().toString().isEmpty();
        nameSong = mEdtNameSong.getText().toString();
        linkSong = mEdtLinkImgSong.getText().toString().isEmpty() ? Constant.UPDATING : mEdtLinkImgSong.getText().toString();
        singerSong = mEdtSingerUploadSong.getText().toString().isEmpty() ? Constant.UPDATING : mEdtSingerUploadSong.getText().toString();
        genreSong = mEdtGenreUploadSong.getText().toString().isEmpty() ? Constant.UPDATING : mEdtGenreUploadSong.getText().toString();
        if (isOk) {
            FileUtil.getInstance().uploadFileFromDevice(nameSong, singerSong, mDurationSong, linkSong, genreSong, mPathFile, mStorageReference, mFireBase, getActivity());
            dismiss();
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_main_activity_error_message_fill_name_song), Toast.LENGTH_SHORT).show();
        }
    }

    @Click(R.id.imgCancel)
    public void cancelUploadFile() {
        dismiss();
    }

}

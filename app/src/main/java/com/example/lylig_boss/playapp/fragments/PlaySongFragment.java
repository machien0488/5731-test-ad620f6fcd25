package com.example.lylig_boss.playapp.fragments;

import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lylig_boss.playapp.R;
import com.example.lylig_boss.playapp.common.Constant;
import com.example.lylig_boss.playapp.eventBus.BusProvider;
import com.example.lylig_boss.playapp.eventBus.event.PlaySongEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiveOfflineSongEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiveOnlineSongEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiveStringRequestEvent;
import com.example.lylig_boss.playapp.models.OnlineSong;
import com.example.lylig_boss.playapp.utils.InternetUtil;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;
import io.realm.RealmConfiguration;

@EFragment(R.layout.fragment_play_song)
public class PlaySongFragment extends BaseFragment {
    private static final int TIME = 20000;
    @ViewById(R.id.imgSongPlay)
    ImageView mImgSongPlay;
    @ViewById(R.id.imgDownload)
    ImageView mImgDownload;
    @FragmentArg(Constant.SONG)
    OnlineSong mOnlineSong;
    @FragmentArg(Constant.POSITION)
    int mPositionSong;
    @FragmentArg(Constant.OFFLINE_MODE)
    boolean mIsOffline;
    @FragmentArg(Constant.PLAYING)
    boolean mIsPlaying;
    private RotateAnimation mRotate;
    private Handler mHandlerUpdateUi;

    public static PlaySongFragment newInstance(OnlineSong song, boolean isPlaying) {
        return PlaySongFragment_.builder()
                .arg(Constant.SONG, song)
                .arg(Constant.PLAYING, isPlaying)
                .build();
    }

    public static PlaySongFragment newInstance(int position, boolean isOffline, boolean isPlaying) {
        return PlaySongFragment_.builder()
                .arg(Constant.POSITION, position)
                .arg(Constant.OFFLINE_MODE, isOffline)
                .arg(Constant.PLAYING, isPlaying)
                .build();
    }

    @Override
    protected void afterView() {
        RealmConfiguration configuration = new RealmConfiguration.Builder(getContext()).build();
        Realm.setDefaultConfiguration(configuration);
        BusProvider.getInstance().register(this);
        mHandlerUpdateUi = new Handler();
        mRotate = new RotateAnimation(0, 359,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotate.setDuration(TIME);
        mRotate.setRepeatCount(Animation.INFINITE);
        if (mIsPlaying) {
            mImgSongPlay.startAnimation(mRotate);
        }
        if (mIsOffline) {
            banDownloadFunction();
            return;
        }
        if (mOnlineSong.getDownloadUrl() == null || mIsOffline) {
            banDownloadFunction();
        }
    }

    private void banDownloadFunction() {
        mImgDownload.setImageResource(R.drawable.ic_downloaded);
        mImgDownload.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().post(new ReceiveStringRequestEvent(Constant.UPLOAD_UI_PLAY_ACTIVITY));
    }

    @Subscribe
    public void updateUi(final PlaySongEvent obj) {
        mIsPlaying = obj.isPlayed();
        mHandlerUpdateUi.post(new Runnable() {
            @Override
            public void run() {
                if (!mIsPlaying) {
                    mImgSongPlay.clearAnimation();
                } else {
                    mImgSongPlay.startAnimation(mRotate);
                }
            }
        });
    }

    @Subscribe
    public void receiveValueOfPositionOnlineSong(ReceiveOnlineSongEvent event) {
        mOnlineSong = event.getOnlineSong();
        mImgDownload.setImageResource(mOnlineSong.getDownloadUrl() == null ? R.drawable.ic_downloaded : R.drawable.ic_download);
        mImgDownload.setEnabled(mOnlineSong.getDownloadUrl() != null);
    }

    @Subscribe
    public void receiveValueOfDurationOfflineSong(ReceiveOfflineSongEvent event) {
        mPositionSong = event.getPositionSong();
        mImgDownload.setImageResource(R.drawable.ic_downloaded);
        mImgDownload.setEnabled(false);
    }

    @Subscribe
    public void getControlSong(ReceiveStringRequestEvent event) {
        if (event.getTitleRequest().equals(Constant.NEXT) || event.getTitleRequest().equals(Constant.PREVIOUS)) {
            mImgSongPlay.startAnimation(mRotate);
        }
    }

    @Click(R.id.imgShare)
    void setImgShareClick() {
        Toast.makeText(getContext(), getString(R.string.toast_play_fragment_text_coming_soon), Toast.LENGTH_SHORT).show();
    }

    @Click(R.id.imgAdd)
    void setImgAddClick() {
        Toast.makeText(getContext(), getString(R.string.toast_play_fragment_text_coming_soon), Toast.LENGTH_SHORT).show();
    }

    @Click(R.id.imgDownload)
    void setImgDownloadClick() {
        Toast.makeText(getContext(), getString(R.string.toast_play_fragment_text_downloading), Toast.LENGTH_SHORT).show();
        if (InternetUtil.getInstance().checkInternet(getContext())) {
            BusProvider.getInstance().post(new ReceiveStringRequestEvent(Constant.DOWNLOAD));
        } else {
            Toast.makeText(getContext(), getString(R.string.toast_message_no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Click(R.id.imgMv)
    void setImgMvClick() {
        Toast.makeText(getContext(), getString(R.string.toast_play_fragment_text_coming_soon), Toast.LENGTH_SHORT).show();
    }

    @Click(R.id.imgVol)
    void setImgVolClick() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
    }
}

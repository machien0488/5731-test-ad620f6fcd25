package com.example.lylig_boss.playapp.activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lylig_boss.playapp.R;
import com.example.lylig_boss.playapp.adapters.MenuItemsAdapter;
import com.example.lylig_boss.playapp.adapters.ViewPagerMainActivityAdapter;
import com.example.lylig_boss.playapp.common.Constant;
import com.example.lylig_boss.playapp.databases.RealmHelper;
import com.example.lylig_boss.playapp.eventBus.BusProvider;
import com.example.lylig_boss.playapp.eventBus.event.CheckExistingActivityEvent;
import com.example.lylig_boss.playapp.eventBus.event.PlaySongEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiveOfflineSongEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiveOnlineSongEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiveStringRequestEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiverDataOfflineEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiverDataOnlineEvent;
import com.example.lylig_boss.playapp.eventBus.event.ReceiverTimerValueEvent;
import com.example.lylig_boss.playapp.eventBus.event.StartActivityEvent;
import com.example.lylig_boss.playapp.models.ItemMenu;
import com.example.lylig_boss.playapp.models.OfflineSong;
import com.example.lylig_boss.playapp.models.OnlineSong;
import com.example.lylig_boss.playapp.utils.FileUtil;
import com.example.lylig_boss.playapp.utils.InternetUtil;
import com.example.lylig_boss.playapp.views.dialogs.UploadFileDialog;
import com.example.lylig_boss.playapp.views.dialogs.UploadFileDialog_;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 27/06/2016.
 */
@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity {
    private static final int REQUEST_WRITE_STORAGE = 9;
    private static final int REQUEST_CODE = 1;
    private static final int DELAY_TIME = 500;
    private static final int DELAY_TIME_EXIT = 2000;
    private static final String TYPE = "audio/mpeg";

    @ViewById(R.id.toolBarMain)
    Toolbar mToolbarMain;
    @ViewById(R.id.viewFullScreen)
    View mViewFullScreen;
    @ViewById(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;
    @ViewById(R.id.rlConsole)
    RelativeLayout mRlConsole;
    @ViewById(R.id.recyclerViewMenu)
    RecyclerView mRecycleViewMenu;
    @ViewById(R.id.tabLayoutMain)
    TabLayout mTabLayoutMain;
    @ViewById(R.id.viewPagerMain)
    ViewPager mViewPagerMain;
    @ViewById(R.id.tvNameSongBottom)
    TextView mTvNameSongBottom;
    @ViewById(R.id.tvNameSinger)
    TextView mTvNameSinger;
    @ViewById(R.id.imgSong)
    ImageView mImgSong;
    @ViewById(R.id.imgBtnPlay)
    ImageView mImgBtnPlay;
    @ViewById(R.id.imgBtnNext)
    ImageView mImgBtnNext;

    private boolean mIsPlaying;
    private boolean mIsCreatedActivity;
    private boolean mIsBackPressToExit;
    private boolean mIsOffline = true;
    private List<OfflineSong> mOffSongs;
    private List<OnlineSong> mOnlSongs;
    private RealmHelper mRealmHelper;
    private String mTitleSong = "";
    private String mNameSinger = "";
    private Handler mHandlerUpdateUi;
    private ViewPagerMainActivityAdapter mViewPagerAdapter;
    private long mDelayTime;
    private long mTimerValue;
    private int mCurrentDurationSong;
    private int mPositionSong = -1;
    private ActionBarDrawerToggle mDrawerToggle;
    private Bus mBus;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void afterView() {
        mBus = BusProvider.getInstance();
        mBus.register(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(configuration);
        createFileApp();
        Firebase.setAndroidContext(this);
        mRealmHelper = RealmHelper.with(this);
        mBus.post(new ReceiveStringRequestEvent(Constant.REQUEST_TIMER));
        mOffSongs = new ArrayList<>();
        mOnlSongs = new ArrayList<>();
        mOffSongs.addAll(mRealmHelper.queryData());
        mHandlerUpdateUi = new Handler();

//      Draw Navigation Layout
        final List<ItemMenu> itemMenuLists = new ArrayList<>();
        String[] arrayMenuItems = getResources().getStringArray(R.array.item_list_menu);
        int[] arrayImageMenuItems = new int[]{R.drawable.ic_home, R.drawable.ic_report, R.drawable.ic_infor};
        int lengthArray = arrayMenuItems.length;
        for (int i = 0; i < lengthArray; i++) {
            itemMenuLists.add(new ItemMenu(arrayMenuItems[i], arrayImageMenuItems[i]));
        }
        final MenuItemsAdapter mListItemMenuAdapter = new MenuItemsAdapter(MainActivity.this, itemMenuLists);
        mRecycleViewMenu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecycleViewMenu.setAdapter(mListItemMenuAdapter);
        setSupportActionBar(mToolbarMain);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbarMain.setNavigationIcon(R.drawable.ic_menu);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                mDrawerLayout.bringToFront();
                mViewFullScreen.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                mViewFullScreen.setVisibility(View.GONE);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mTabLayoutMain.setTabGravity(TabLayout.GRAVITY_FILL);
        mViewPagerAdapter = new ViewPagerMainActivityAdapter(getSupportFragmentManager(), MainActivity.this);
        mViewPagerMain.setAdapter(mViewPagerAdapter);
        mTabLayoutMain.setupWithViewPager(mViewPagerMain);
        setCustomTab();
    }

    private void setCustomTab() {
        int tmp = mTabLayoutMain.getTabCount();
        for (int i = 0; i < tmp; i++) {
            View v = mViewPagerAdapter.getTabView(i);
            mTabLayoutMain.getTabAt(i).setCustomView(v);
        }
    }

    @OnActivityResult(REQUEST_CODE)
    void onResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uriFile = data.getData();
            String name = FileUtil.getInstance().getRealNameFromUri(this, uriFile);
            String path = FileUtil.getInstance().geRealPathFromUri(this, uriFile);
            if (TextUtils.equals(name, Constant.ERROR_FILE) && TextUtils.equals(path, Constant.ERROR_FILE)) {
                Toast.makeText(this, getString(R.string.toast_main_activity_error_message_upload_file), Toast.LENGTH_LONG).show();
                return;
            }
            int duration = MediaPlayer.create(this, Uri.parse(path)).getDuration();
            UploadFileDialog dialog = UploadFileDialog_.builder()
                    .mDurationSong(duration)
                    .mNameSong(name)
                    .mPathFile(path)
                    .build();
            dialog.show(getFragmentManager(), Constant.UPLOAD_DIALOG_TAG);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item);
    }

    @Click(R.id.imgBtnUpload)
    void imgBtnUpload() {
        if (InternetUtil.getInstance().checkInternet(this)) {
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType(TYPE);
                startActivityForResult(Intent.createChooser(intent, null), REQUEST_CODE);
            } else {
                Toast.makeText(this, getString(R.string.toast_message_no_google_service), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_message_no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Click(R.id.rlConsole)
    void linearLayoutClick() {
        if (mPositionSong < 0) {
            return;
        }
        mBus.post(new ReceiveStringRequestEvent(Constant.REQUEST_START_ACTIVITY));
    }

    @Click(R.id.imgBtnPlay)
    void imgBtnPlayClick() {
        if (mIsPlaying) {
            mIsPlaying = false;
            changeIconPlay();
            mBus.post(new ReceiveStringRequestEvent(Constant.PAUSE));
        } else {
            mIsPlaying = true;
            changeIconPlay();
            if (mPositionSong >= 0) {
                replay();
            } else {
                mBus.post(new ReceiveStringRequestEvent(Constant.PLAY));
            }
        }
    }

    @Click(R.id.imgBtnNext)
    void imgBtnNextClick() {
        if (System.currentTimeMillis() - mDelayTime > DELAY_TIME) {
            mBus.post(new ReceiveStringRequestEvent(Constant.NEXT));
        }
        mDelayTime = System.currentTimeMillis();
    }

    @Click(R.id.imgBtnPrevious)
    void imgBtnPreClick() {
        if (System.currentTimeMillis() - mDelayTime > DELAY_TIME) {
            mBus.post(new ReceiveStringRequestEvent(Constant.PREVIOUS));
        }
        mDelayTime = System.currentTimeMillis();
    }

    @Subscribe
    public void getTimerValue(ReceiverTimerValueEvent valueEvent) {
        mTimerValue = valueEvent.getTimerValue();
    }

    @Subscribe
    public void updateUi(final PlaySongEvent obj) {
        mHandlerUpdateUi.post(new Runnable() {
            @Override
            public void run() {
                mIsPlaying = obj.isPlayed();
                changeIconPlay();
            }
        });
    }

    @Subscribe
    public void getOnlineSong(final ReceiveOnlineSongEvent obj) {
        mIsPlaying = true;
        mIsOffline = false;
        changeIconPlay();
        mPositionSong = obj.getPositionSong();
        showBottomConsoleLayout();
        mHandlerUpdateUi.post(new Runnable() {
            @Override
            public void run() {
                OnlineSong song = obj.getOnlineSong();
                mTitleSong = song.getTitle();
                mTvNameSongBottom.setText(mTitleSong);
                mNameSinger = song.getSinger();
                mTvNameSinger.setText(mNameSinger);
                Picasso.with(getApplicationContext()).load(song.getArtworkUrl()).placeholder(R.drawable.ic_disk).into(mImgSong);
            }
        });

    }

    @Subscribe
    public void getOfflineSong(final ReceiveOfflineSongEvent obj) {
        mIsPlaying = true;
        mIsOffline = true;
        changeIconPlay();
        mPositionSong = obj.getPositionSong();
        showBottomConsoleLayout();
        mHandlerUpdateUi.post(new Runnable() {
            @Override
            public void run() {
                OfflineSong song = obj.getOfflineSong();
                mTitleSong = song.getTitle();
                mTvNameSongBottom.setText(mTitleSong);
                mNameSinger = song.getSinger();
                mTvNameSinger.setText(mNameSinger);
                Picasso.with(MainActivity.this).load(new File(obj.getOfflineSong().getArtworkUri())).placeholder(R.drawable.ic_disk).into(mImgSong);
            }
        });

    }

    @Subscribe
    public void getDataOnlineSongs(final ReceiverDataOnlineEvent list) {
        mOnlSongs.clear();
        mOnlSongs.addAll(list.getOnlineSongs());
    }

    @Subscribe
    public void getDataOfflineSongs(final ReceiverDataOfflineEvent list) {
        mOffSongs.clear();
        mOffSongs.addAll(list.getOfflineSongs());
    }

    @Subscribe
    public void startPlaySongActivity(StartActivityEvent obj) {
        mCurrentDurationSong = obj.getCurrentTime();
        startPlayActivity(obj.isOk());
    }

    @Subscribe
    public void checkCreatedActivity(CheckExistingActivityEvent event) {
        mIsCreatedActivity = event.isCreated();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE:
                if (Arrays.equals(permissions, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
                    File file = new File(Constant.PATH_FILE);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTvNameSongBottom.setText(mTitleSong);
        mTvNameSinger.setText(mNameSinger);
        if (mIsPlaying) {
            mImgBtnPlay.setImageResource(R.drawable.ic_pause_main_activity);
        }
    }

    @Override
    public void onBackPressed() {
        if (mIsBackPressToExit) {
            super.onBackPressed();
        }
        mIsBackPressToExit = true;
        Toast.makeText(this, getString(R.string.toast_message_exit), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsBackPressToExit = false;
            }
        }, DELAY_TIME_EXIT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBus.unregister(this);
    }

    private void replay() {
        mBus.post(new ReceiveStringRequestEvent(Constant.REPLAY));
    }

    private void startPlayActivity(boolean isPlay) {
        if (!mIsCreatedActivity) {
            if (!mIsOffline) {
                PlaySongActivity_.intent(MainActivity.this)
                        .mPosition(mPositionSong)
                        .mOnlineSongs((ArrayList<OnlineSong>) mOnlSongs)
                        .mIsPlaying(isPlay)
                        .mCurrentDuration(mCurrentDurationSong)
                        .mTimerValue(mTimerValue)
                        .start();
            } else {
                PlaySongActivity_.intent(MainActivity.this)
                        .mIsOffline(mIsOffline)
                        .mIsPlaying(isPlay)
                        .mPosition(mPositionSong)
                        .mCurrentDuration(mCurrentDurationSong)
                        .mOfflineSong(mOffSongs.get(mPositionSong))
                        .mTimerValue(mTimerValue)
                        .start();
            }
        }
    }

    private void changeIconPlay() {
        mImgBtnPlay.setImageResource(mIsPlaying ? R.drawable.ic_pause_main_activity : R.drawable.ic_play_main_activity);
    }

    private void createFileApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE);
            }
        } else {
            File file = new File(Constant.PATH_FILE);
            Boolean isOK = file.mkdir();
        }
    }

    private void showBottomConsoleLayout() {
        if (mRlConsole.getVisibility() == View.GONE) {
            mRlConsole.setVisibility(View.VISIBLE);
        }
    }
}
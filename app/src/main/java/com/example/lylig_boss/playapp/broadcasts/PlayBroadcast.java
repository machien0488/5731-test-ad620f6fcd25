package com.example.lylig_boss.playapp.broadcasts;

import android.content.Context;
import android.content.Intent;

import com.example.lylig_boss.playapp.common.Constant;
import com.example.lylig_boss.playapp.eventBus.BusProvider;
import com.example.lylig_boss.playapp.eventBus.event.ReceiveStringRequestEvent;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.ReceiverAction;
import org.androidannotations.api.support.content.AbstractBroadcastReceiver;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 01/08/2016.
 */
@EReceiver
public class PlayBroadcast extends AbstractBroadcastReceiver {
    @ReceiverAction(Constant.ACTION.PAUSE_ACTION)
    void onPauseAction() {
        BusProvider.getInstance().post(new ReceiveStringRequestEvent(Constant.PAUSE));
    }

    @ReceiverAction (Constant.TIMER)
    void onTimerAction(){
        BusProvider.getInstance().post(new ReceiveStringRequestEvent(Constant.PAUSE));
        BusProvider.getInstance().post(new ReceiveStringRequestEvent(Constant.RESET_TIMER));
    }
    @ReceiverAction(Constant.ACTION.REPLAY_ACTION)
    void onReplayAction() {
        BusProvider.getInstance().post(new ReceiveStringRequestEvent(Constant.REPLAY));
    }

    @ReceiverAction(Constant.ACTION.NEXT_ACTION)
    void onNextAction() {
        BusProvider.getInstance().post(new ReceiveStringRequestEvent(Constant.NEXT));
    }

    @ReceiverAction(Constant.ACTION.PREV_ACTION)
    void onPreviousAction() {
        BusProvider.getInstance().post(new ReceiveStringRequestEvent(Constant.PREVIOUS));
    }

    @ReceiverAction(Constant.ACTION.STOP_FOREGROUND_ACTION)
    void onStopNotificationAction() {
        BusProvider.getInstance().post(new ReceiveStringRequestEvent(Constant.CLOSE_NOTIFICATION));
    }

    @ReceiverAction(Constant.ACTION.SHOW_ACTIVITY_ACTION)
    void onCreateActivityAction() {
        BusProvider.getInstance().post(new ReceiveStringRequestEvent(Constant.SHOW_ACTIVITY));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}

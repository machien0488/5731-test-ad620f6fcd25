package com.example.lylig_boss.playapp.eventBus;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 27/06/2016.
 */
public class BusProvider {
    private static Bus mBus;
    public static synchronized Bus getInstance() {
        if (mBus == null) {
            mBus = new Bus(ThreadEnforcer.ANY);
        }
        return mBus;
    }
}

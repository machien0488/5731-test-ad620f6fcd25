package com.example.lylig_boss.playapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 22/06/2016.
 */
public class InternetUtil {
    private static InternetUtil sInternetUtil;

    public static synchronized InternetUtil getInstance() {
        if (sInternetUtil == null) {
            sInternetUtil = new InternetUtil();
        }
        return sInternetUtil;
    }

    public boolean checkInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}

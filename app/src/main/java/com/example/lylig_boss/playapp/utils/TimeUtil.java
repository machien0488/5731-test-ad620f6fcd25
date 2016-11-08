package com.example.lylig_boss.playapp.utils;

import com.example.lylig_boss.playapp.common.Constant;

/**
 * Copyright @2016 AsianTech Inc.
 * Created by Bia_AST on 20/06/2016.
 */
public class TimeUtil {
    private static final int THOUSAND_VALUE = 1000;
    private static final int HOUR_VALUE = 3600;
    private static final int MINUTE_VALUE = 60;
    private static final int TEN_VALUE = 10;
    private static final int ZERO_VALUE = 0;
    private static final String ZERO = "0";
    private static final String SPACE_STRING = ":";
    private static final String MINUTE = "phút";
    private static TimeUtil sTimeUtil;

    public static synchronized TimeUtil getInstance() {
        if (sTimeUtil == null) {
            sTimeUtil = new TimeUtil();
        }
        return sTimeUtil;
    }

    public String setTimeForDuration(int duration) {
        String durationString;
        String stringHour;
        String stringMinute;
        String stringSecond;
        duration = duration / THOUSAND_VALUE;
        int hour = duration / HOUR_VALUE;
        stringHour = String.valueOf(hour < TEN_VALUE ? ZERO + hour : hour);

        int remainder = duration - hour * HOUR_VALUE;
        int minute = remainder / MINUTE_VALUE;
        stringMinute = String.valueOf(minute < TEN_VALUE ? ZERO + minute : minute);

        remainder = remainder - minute * MINUTE_VALUE;
        int second = remainder;
        stringSecond = String.valueOf(second < TEN_VALUE ? ZERO + second : second);

        durationString = String.valueOf(hour > ZERO_VALUE ? stringHour + SPACE_STRING + stringMinute + SPACE_STRING + stringSecond : stringMinute + SPACE_STRING + stringSecond);

        return durationString;
    }

    public String setTimeForTimerDialog(long timerValue) {
        String text;
        int tempTimerValue = timerValue % (THOUSAND_VALUE * MINUTE_VALUE) == 0 ? (int) timerValue / (THOUSAND_VALUE * MINUTE_VALUE) : (int) timerValue / (THOUSAND_VALUE * MINUTE_VALUE) + 1;
        text = String.format("%s %d %s", Constant.TIMER_DEFAULT_STRING, tempTimerValue, MINUTE);
        return text;
    }

    public String setStringTimeForTimerDialog(int timerValue) {
        return String.format("%s %d %s", Constant.TIMER_AFTER_STRING, timerValue, MINUTE);
    }

    public int setValueTimeForTimerDialog(long timerValue) {
        return timerValue % (THOUSAND_VALUE * MINUTE_VALUE) == 0 ? (int) timerValue / (THOUSAND_VALUE * MINUTE_VALUE) : (int) timerValue / (THOUSAND_VALUE * MINUTE_VALUE) + 1;
    }
}


package com.syh.dalilystudio;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

public class DateTimeUtil {
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat mFormatDate = new SimpleDateFormat(
            "yyyy-MM-dd");
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat mFormatDateTime = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat mFormatDateTimeForFileName = new SimpleDateFormat(
            "yyyyMMddHHmmss");
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat mFormatFullDateTime = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * yyyy.MM.dd
     */
    public static SimpleDateFormat sFormatDate1 = new SimpleDateFormat(
            "yyyy.MM.dd");

    public static String formatDate(Date date) {
        return mFormatDate.format(date);
    }

    public static String formatDateTime(Date date) {
        return mFormatDateTime.format(date);
    }

    public static String formatFullDateTime(Date date) {
        return mFormatFullDateTime.format(date);
    }

    public static String formatDateTimeForFileName(Date date) {
        return mFormatDateTimeForFileName.format(date);
    }

    public static int getDayOffset(long startTime, long endTime) {
        Calendar start = Calendar.getInstance();
        start.setTime(new Date(startTime));

        Calendar end = Calendar.getInstance();
        start.setTime(new Date(endTime));
        int startYear = start.get(Calendar.YEAR);
        int endYear = end.get(Calendar.YEAR);
        int startDay = start.get(Calendar.DAY_OF_YEAR);
        int endDay = end.get(Calendar.DAY_OF_YEAR);
        return 365 * (endYear - startYear) + (endDay - startDay);
    }
    
    public static String getTimeByDuration(int time) {
        StringBuffer sbf = new StringBuffer();
        int hour = (int) (time / (60 * 60 * 1000));
        if (hour > 0) {
            time = time % (60 * 60 * 1000);
            sbf.append((hour > 9 ? hour : ("0" + hour)) + ":");
        }
        int minute = (int) (time / (60 * 1000));
        if (minute > 0) {
            time = time % (60 * 1000);
        }
        sbf.append((minute > 9 ? minute : ("0" + minute)) + ":");
        int second = (int) (time / 1000);
        if (second > 0) {
            sbf.append(second > 9 ? second : ("0" + second));
        } else {
            sbf.append("00");
        }
        return sbf.toString();
    }
}

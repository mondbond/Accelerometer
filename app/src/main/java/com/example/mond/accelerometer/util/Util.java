package com.example.mond.accelerometer.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Util {

    static private final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd hh-mm-ss";

    private static SimpleDateFormat mDateFormat;
    private static TimeZone timeZone = TimeZone.getDefault();

    public static String makeCurrentTimeStampToDate(){
            mDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);

        return mDateFormat.format(new Date(Util.getLocalTimeStamp()));
    }

    public static String makeTimeStampToDate(long timeStamp){
        mDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);

        return mDateFormat.format(new Date(timeStamp));
    }

    public static boolean isOutOfTime(int hour, int minute){
        if(getLocalTimeStamp() % TimeUnit.DAYS.toMillis(1)  <= getTimeOfDayInMl(hour, minute)){
            return false;
        }else {
            return true;
        }
    }

    public static boolean isTimeToStart(int startTime){
        if(getLocalTimeStamp() % TimeUnit.DAYS.toMillis(1) <= startTime){
            return false;
        } else {
            return true;
        }
    }

    public static int getTimeOfDayInMl(int hour, int minute){
        return ((int) TimeUnit.HOURS.toMillis(hour)) + ((int) TimeUnit.MINUTES.toMillis(minute));
    }

    public static long getLocalTimeStamp(){
        return System.currentTimeMillis() + timeZone.getOffset(System.currentTimeMillis());
    }
}

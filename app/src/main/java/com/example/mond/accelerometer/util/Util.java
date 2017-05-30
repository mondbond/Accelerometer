package com.example.mond.accelerometer.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Util {

    private static final int ML_IN_DAY = 86400000;
    private static final int ML_IN_HOUR = 3600000;
    private static final int ML_IN_MINUTE = 60000;

    private static TimeZone timeZone = TimeZone.getDefault();

    static private final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd hh-mm-ss";

    private static SimpleDateFormat mDateFormat;

    public static String clearDots(String string){
        return string.replaceAll("\\." ,",");
    }

    public static String makeTimeStampToDate(long timestamp){
            mDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);

        return mDateFormat.format(new Date(Util.getLocalTimeStamp()));
    }

    public static boolean isOutOfTime(int hour, int minute){
        if(getLocalTimeStamp() % ML_IN_DAY  <= getTimeOfDayInMl(hour, minute)){
            return false;
        }else {
            return true;
        }
    }

    public static boolean isTimeToStart(int startTime){
        if(getLocalTimeStamp() % ML_IN_DAY <= startTime){
            return false;
        } else {
            return true;
        }
    }

    public static int getTimeOfDayInMl(int hour, int minute){
        return hour * ML_IN_HOUR + minute * ML_IN_MINUTE;
    }

    public static long getLocalTimeStamp(){
        return System.currentTimeMillis() + timeZone.getOffset(System.currentTimeMillis());
    }
}

package com.example.mond.accelerometer.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Util {

    private static final int ML_IN_DAY = 86400000;
    private static final int ML_IN_HOUR = 3600000;
    private static final int ML_IN_MINUTE = 60000;

    private static TimeZone timeZone = TimeZone.getTimeZone("Europe/Uzhgorod");

    static private final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd hh-mm-ss";

    private static SimpleDateFormat mDateFormat;

    public static String clearDots(String string){
        return string.replaceAll("\\." ,"");
    }

    public static String currenTimeStampToDate(String dateFormat){
        if(dateFormat == null){
            mDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        }else {
            mDateFormat = new SimpleDateFormat(dateFormat);
        }

        return mDateFormat.format(new Date(System.currentTimeMillis()));
    }

    public static boolean isOutOfTime(int hour, int minute){
        if(System.currentTimeMillis() % ML_IN_DAY  + timeZone.getOffset(System.currentTimeMillis())
                <= getTimeOfDayInMl(hour, minute)){
            return false;
        }else {
            return true;
        }
    }

    public static boolean isTimeToStart(int startTime){
        if(System.currentTimeMillis() % ML_IN_DAY  + timeZone.getOffset(System.currentTimeMillis())
                <= startTime){
            return false;
        } else {
            return true;
        }
    }

    public static int getTimeOfDayInMl(int hour, int minute){
        return hour * ML_IN_HOUR + minute * ML_IN_MINUTE;
    }
}

package com.example.mond.accelerometer;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

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
}

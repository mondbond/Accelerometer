package com.example.mond.accelerometer.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.mond.accelerometer.R;

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
        return ((int) TimeUnit.HOURS.toMillis(hour)) + ((int) TimeUnit.MINUTES.toMillis(minute)) - timeZone.getOffset(System.currentTimeMillis());
    }

    public static long getLocalTimeStamp(){
        return System.currentTimeMillis();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(!(activeNetworkInfo != null && activeNetworkInfo.isConnected())){
            Toast.makeText(context, context.getResources().getString(R.string.error_network_is_not_available), Toast.LENGTH_SHORT).show();
        }

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // TODO: 13/06/17 better to create separated methods to validate email & password
    public static boolean isFieldsNotNullAndEmpty(String email, String pswd){
        // TODO: 13/06/17 why you need TextUtils.equals(email, null)???
        if (!TextUtils.equals(email, null) && !TextUtils.equals(pswd, null)
                &&!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pswd)) {
            return true;
        }else{
            return false;
        }
    }
}

package com.example.mond.accelerometer.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.mond.accelerometer.R;

public class Util {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(!(activeNetworkInfo != null && activeNetworkInfo.isConnected())){
            Toast.makeText(context, context.getResources().getString(R.string.error_network_is_not_available), Toast.LENGTH_SHORT).show();
        }

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isFieldsNotEmpty(String email, String pswd){
        if (isEmailValidate(email) && isPasswordValidate(pswd)) {
            return true;
        }else{
            return false;
        }
    }

    public static boolean isEmailValidate(String email){
        return !TextUtils.isEmpty(email);
    }

    public static boolean isPasswordValidate(String pswd){
        return !TextUtils.isEmpty(pswd);
    }
}

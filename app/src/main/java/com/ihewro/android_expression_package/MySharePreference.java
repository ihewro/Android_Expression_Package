package com.ihewro.android_expression_package;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/09
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class MySharePreference {

    public static boolean setIsFistEnter(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("baby", false);
        editor.apply();
        return true;
    }

    public static boolean getIsFirstEnter(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("baby", true);
    }
}

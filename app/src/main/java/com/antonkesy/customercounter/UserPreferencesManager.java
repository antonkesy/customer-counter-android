package com.antonkesy.customercounter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserPreferencesManager {
    private static final String prefMaxCustomer = "maxKey";
    private static final String prefCustomerAmount = "customerKey";

    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static SharedPreferences.Editor getEditorSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).edit();
    }

    private static void setInt(Context context, String key, int newValue) {
        getEditorSharedPreferences(context).putInt(key, newValue).apply();
    }

    public static int getMaxCustomer(Context context) {
        return getSharedPreferences(context).getInt(prefMaxCustomer, 10);
    }

    public static void setMaxCustomer(Context context, int value) {
        setInt(context, prefMaxCustomer, value);
    }

    public static int getCustomerAmount(Context context) {
        return getSharedPreferences(context).getInt(prefCustomerAmount, 0);
    }

    public static void setCustomerAmount(Context context, int value) {
        setInt(context, prefCustomerAmount, value);
    }


}

package com.antonkesy.customercounter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserPreferencesManager {
    private static final String prefMaxCustomer = "prefMaxKey";
    private static final String prefCustomerAmount = "prefCustomerKey";
    private static final String prefVibrateOn = "prefVibrateKey";
    private static final String prefSoundOn = "prefSoundKey";
    private static final String prefVolumeControlOn = "prefVolumeControlOnKey";

    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static SharedPreferences.Editor getEditorSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).edit();
    }

    private static void setIntAsString(Context context, String key, int newValue) {
        getEditorSharedPreferences(context).putString(key, Integer.toString(newValue)).apply();
    }

    /**
     * string to int because root_preferences saves as string not int key
     *
     * @param context
     * @return
     */
    public static int getMaxCustomer(Context context) {
        String prefValue = getSharedPreferences(context).getString(prefMaxCustomer, "10");
        int maxCustomerValue = 10;
        try {
            maxCustomerValue = Integer.parseInt(prefValue);
        } catch (NumberFormatException ignore) {
        }
        return maxCustomerValue;
    }

    /**
     * string to int because root_preferences saves as string not int key
     *
     * @param context
     * @return
     */
    public static int getCustomerAmount(Context context) {
        String prefValue = getSharedPreferences(context).getString(prefCustomerAmount, "0");
        int customerValue = 0;
        try {
            customerValue = Integer.parseInt(prefValue);
        } catch (NumberFormatException ignore) {
        }
        return customerValue;
    }

    public static void setCustomerAmount(Context context, int value) {
        setIntAsString(context, prefCustomerAmount, value);
    }

    public static boolean isSoundOn(Context context) {
        return getSharedPreferences(context).getBoolean(prefSoundOn, false);
    }

    public static boolean isVibrateOn(Context context) {
        return getSharedPreferences(context).getBoolean(prefVibrateOn, true);
    }

    public static boolean isVolumeControlOn(Context context) {
        return getSharedPreferences(context).getBoolean(prefVolumeControlOn, false);
    }

}

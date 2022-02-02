package com.antonkesy.customercounter.application.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

public class UserPreferencesManager implements ICustomerCounterSettings {

    private static final String PREF_MAX_KEY = "prefMaxKey";
    private static final String PREF_CUSTOMER_KEY = "prefCustomerKey";
    private static final String PREF_VIBRATE_KEY = "prefVibrateKey";
    private static final String PREF_SOUND_KEY = "prefSoundKey";
    private static final String PREF_VOLUME_CONTROL_ON_KEY = "prefVolumeControlOnKey";
    private static final String PREF_DARK_MODE_KEY = "prefDarkModeKey";

    private final Context context;

    public UserPreferencesManager(Context context) {
        this.context = context;
    }

    private SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private SharedPreferences.Editor getEditorSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).edit();
    }

    private void setIntAsString(Context context, String key, int newValue) {
        getEditorSharedPreferences(context).putString(key, Integer.toString(newValue)).apply();
    }


    @Override
    public boolean isSoundActive() {
        return getSharedPreferences(context).getBoolean(PREF_SOUND_KEY, false);
    }


    @Override
    public boolean isVibrationActive() {
        return getSharedPreferences(context).getBoolean(PREF_VIBRATE_KEY, true);
    }

    @Override
    public void putVibrationActive(boolean isActive) {
        getEditorSharedPreferences(context).putBoolean(PREF_VIBRATE_KEY, isActive).apply();
    }

    @Override
    public void putSoundActive(boolean isActive) {
        getEditorSharedPreferences(context).putBoolean(PREF_SOUND_KEY, isActive).apply();
    }

    @Override
    public boolean isVolumeControlButton() {
        return getSharedPreferences(context).getBoolean(PREF_VOLUME_CONTROL_ON_KEY, false);
    }

    @Override
    public void putVolumeControlButton(boolean isButton) {
        getEditorSharedPreferences(context).putBoolean(PREF_VOLUME_CONTROL_ON_KEY, isButton).apply();
    }

    @Override
    public boolean isDarkMode() {
        return getSharedPreferences(context).getBoolean(PREF_DARK_MODE_KEY, false);
    }

    @Override
    public void putDarkMode(boolean isDarkMode) {
        getEditorSharedPreferences(context).putBoolean(PREF_DARK_MODE_KEY, isDarkMode).apply();
    }

    @Override
    public int getMaxCustomer() {
        String prefValue = getSharedPreferences(context).getString(PREF_MAX_KEY, "10");
        int maxCustomerValue = 10;
        try {
            maxCustomerValue = Integer.parseInt(prefValue);
        } catch (NumberFormatException ignore) {
        }
        return maxCustomerValue;
    }

    @Override
    public int getCustomerAmount() {
        String prefValue = getSharedPreferences(context).getString(PREF_CUSTOMER_KEY, "0");
        int customerValue = 0;
        try {
            customerValue = Integer.parseInt(prefValue);
        } catch (NumberFormatException ignore) {
        }
        return customerValue;
    }

    @Override
    public void putCustomerAmount(int amountOfCustomers) {
        setIntAsString(context, PREF_CUSTOMER_KEY, amountOfCustomers);
    }

    @NonNull
    @Override
    public String getDarkModeKey() {
        return PREF_DARK_MODE_KEY;
    }

    @NonNull
    @Override
    public String getCustomerKey() {
        return PREF_CUSTOMER_KEY;
    }

    @NonNull
    @Override
    public String getMaxCustomerKey() {
        return PREF_MAX_KEY;
    }
}

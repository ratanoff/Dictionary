package ru.ratanov.dictionary.util;


import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {

    public static String getStoredQuery(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(key, null);
    }

    public static String getStoredQuery(Context context, String key, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(key, defaultValue);
    }

    public static void setStoredQuery(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(key, value)
                .apply();
    }
}

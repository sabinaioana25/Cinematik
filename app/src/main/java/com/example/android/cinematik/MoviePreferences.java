package com.example.android.cinematik;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Sabina on 4/15/2018.
 */

public class MoviePreferences {

    public static String getSortByPreference(Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String sortByKey = context.getString(R.string.pref_sort_by_key);
        String defaultValue = context.getString(R.string.pref_sort_by_popularity_desc);
        return sharedPreferences.getString(sortByKey, defaultValue);
    }
}

package com.example.android.cinematik.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.android.cinematik.R;

/**
 * Created by Sabina on 4/15/2018.
 */

public class MoviePreferences {

    public static String getSortByPreference(Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String sortByKey = context.getString(R.string.pref_sort_by_key);
        String defaultValue = context.getString(R.string.pref_sort_by_popularity);
        return sharedPreferences.getString(sortByKey, defaultValue);
    }
}

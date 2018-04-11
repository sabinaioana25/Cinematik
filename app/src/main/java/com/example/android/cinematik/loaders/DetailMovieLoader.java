package com.example.android.cinematik.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.android.cinematik.pojos.MovieItem;
import com.example.android.cinematik.utilities.MovieJsonUtils;
import com.example.android.cinematik.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Sabina on 3/21/2018.
 */

public class DetailMovieLoader extends AsyncTaskLoader<MovieItem> {

    private static final String LOG_TAG = DetailMovieLoader.class.getSimpleName();
    private int id;

    public DetailMovieLoader(Context context, int id) {
        super(context);
        this.id = id;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public MovieItem loadInBackground() {
        URL url = NetworkUtils.buildUrlDetailActivity(id);

        try {
            String jsonResponse = NetworkUtils.makeHttpRequest(url, getContext());
            return MovieJsonUtils.extractDetailsFromJson(jsonResponse);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Http request failed " + e);
            return null;
        }
    }
}


package com.example.android.cinematik.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.android.cinematik.DetailMovieActivity;
import com.example.android.cinematik.utilities.MovieJsonUtils;
import com.example.android.cinematik.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Sabina on 3/21/2018.
 */

public class DetailMovieLoader extends AsyncTaskLoader<Object> {

    private static final String TAG = DetailMovieLoader.class.getSimpleName();
    private final int id;
    private final int loaderId;

    public DetailMovieLoader(Context context, int id, int loaderId) {
        super(context);
        this.id = id;
        this.loaderId = loaderId;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public Object loadInBackground() {
        URL url;
        switch (loaderId) {
            case DetailMovieActivity.ID_LOADER_DETAIL_MOVIES:
                url = NetworkUtils.buildUrlDetailActivity(id);
                try {
                    String movieJsonResponse = NetworkUtils.makeHttpRequest(url);
                    return MovieJsonUtils.extractDetailsFromJson(movieJsonResponse);
                } catch (IOException e) {
                    Log.e(TAG, "Could not male Http request", e);
                    return null;
                }

                case DetailMovieActivity.ID_VIDEO_LOADER:
                    url = NetworkUtils.buildUrlVideo(id);
                    Log.e(TAG, "bla bla bla 3 " + url);

                    try {
                        String videoJsonResponse = NetworkUtils.makeHttpRequest(url);
                        return MovieJsonUtils.extractVideoFromJson(videoJsonResponse);
                    } catch (IOException e) {
                        Log.e(TAG, "Could not make Http request", e);
                        return null;
                    }
                    default:
                        return null;
        }


//        URL url = NetworkUtils.buildUrlDetailActivity(id);
//        try {
//            String jsonResponse = NetworkUtils.makeHttpRequest(url);
//            return MovieJsonUtils.extractDetailsFromJson(jsonResponse);
//        } catch (IOException e) {
//            return null;
//        }


    }

}


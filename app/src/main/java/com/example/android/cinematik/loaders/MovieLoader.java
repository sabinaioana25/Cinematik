package com.example.android.cinematik.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.cinematik.pojos.MovieItem;
import com.example.android.cinematik.utilities.MovieJsonUtils;

import java.util.List;

/**
 * Created by Sabina on 3/19/2018.
 */

public class MovieLoader extends AsyncTaskLoader<List<MovieItem>> {

    private static final String LOG_TAG = MovieLoader.class.getSimpleName();
    private String url;

    public MovieLoader(Context context, String url) { //defines type of arguments it takes in
        super(context);
        this.url = url;
    }

    @Override
        protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<MovieItem> loadInBackground() {

        List<MovieItem> movieList = MovieJsonUtils.getMovieData(url, getContext());
        return movieList;
    }
}

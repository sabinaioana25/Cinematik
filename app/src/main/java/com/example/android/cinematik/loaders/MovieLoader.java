package com.example.android.cinematik.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.cinematik.pojos.MovieItem;
import com.example.android.cinematik.utilities.MovieJsonUtils;

import java.util.List;

public class MovieLoader extends AsyncTaskLoader<List<MovieItem>> {

    private String url;

    public MovieLoader(Context context, String url) {
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
        return MovieJsonUtils.getMovieData(url, getContext());
    }
}

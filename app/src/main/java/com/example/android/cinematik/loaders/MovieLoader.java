package com.example.android.cinematik.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.cinematik.pojos.MovieItem;
import com.example.android.cinematik.utilities.MovieJsonUtils;

import java.util.List;

@SuppressWarnings({"unused", "CanBeFinal"})
public class MovieLoader extends AsyncTaskLoader<List<MovieItem>> {

    private final String url;
    private static final String TAG = MovieLoader.class.getSimpleName();

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

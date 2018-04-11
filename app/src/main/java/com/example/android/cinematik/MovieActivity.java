package com.example.android.cinematik;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.cinematik.Adapters.MovieAdapter;
import com.example.android.cinematik.Interfaces.MovieDetailClickHandler;
import com.example.android.cinematik.loaders.MovieLoader;
import com.example.android.cinematik.pojos.MovieItem;
import com.example.android.cinematik.utilities.NetworkUtils;

import java.util.List;

public class MovieActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<MovieItem>>,
        MovieDetailClickHandler {

    private static final String TAG = MovieActivity.class.getSimpleName();
    public static final String MOVIE_ID = "id" ;

    private RecyclerView movieListRV;
    private GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
    Context context = this;

    // Loader IDs for loading the main API and the poster API, respectively
    private static final int ID_LOADER_LIST_MOVIES = 44;

    public static int pageCount = 2;
    // adapter
    private MovieAdapter adapter;

    String sortOption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieListRV = (RecyclerView) findViewById(R.id.recycler_view_movies);
        movieListRV.setLayoutManager(gridLayoutManager);
        movieListRV.setHasFixedSize(true);

        adapter = new MovieAdapter(context, this);
        movieListRV.setAdapter(adapter);

        sortOption = NetworkUtils.TOP_RATED_PARAM;

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(ID_LOADER_LIST_MOVIES, null, this).forceLoad();
    }

    @Override
    public Loader<List<MovieItem>> onCreateLoader(int i, Bundle args) {
        String urlPopularMovieActivity = NetworkUtils.buildUrlMovieActivity(sortOption);

        return new MovieLoader(this, urlPopularMovieActivity);
    }

    @Override
    public void onLoadFinished(Loader<List<MovieItem>> loader, List<MovieItem> movieItems) {
        adapter.InsertList(movieItems);
    }

    @Override
    public void onLoaderReset(Loader<List<MovieItem>> loader) {

    }

    @Override
    public void onSelectedItem(int id) {
        Intent goToDetailActivity = new Intent(this,DetailMovieActivity.class);
        goToDetailActivity.putExtra(MOVIE_ID, id);
        startActivity(goToDetailActivity);
    }
}

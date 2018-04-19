package com.example.android.cinematik;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.cinematik.Adapters.MovieAdapter;
import com.example.android.cinematik.Interfaces.MovieDetailClickHandler;
import com.example.android.cinematik.loaders.MovieLoader;
import com.example.android.cinematik.pojos.MovieItem;
import com.example.android.cinematik.utilities.NetworkUtils;

import java.util.List;

public class MovieActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<MovieItem>>,
        MovieDetailClickHandler, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MovieActivity.class.getSimpleName();
    public static final String MOVIE_ID = "id";

    public RecyclerView movieListRV;
    private GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
    Context context = this;

    // Loader IDs for loading the main API and the poster API, respectively
    private static final int ID_LOADER_LIST_MOVIES = 44;

    // adapter
    private MovieAdapter adapter;

    SwipeRefreshLayout swipeRefreshLayout;
    private boolean isLoading = false;


    // sortOption
    String sortOption = null;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.discover_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(MovieActivity.this);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_red_dark);

        movieListRV = (RecyclerView) findViewById(R.id.recycler_view_movies);
        movieListRV.setLayoutManager(gridLayoutManager);
        movieListRV.setHasFixedSize(true);

        adapter = new MovieAdapter(context, this);
        movieListRV.setAdapter(adapter);

        RecyclerViewItemDecorator itemDecorator = new RecyclerViewItemDecorator(context,
                R.dimen.item_offset);
        movieListRV.addItemDecoration(itemDecorator);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.e(TAG, "message " + key);
                if (key.equals(getString(R.string.pref_sort_by_key))) {
                    isLoading = true;
                    getLoaderManager().restartLoader(ID_LOADER_LIST_MOVIES, null, MovieActivity
                            .this);
                }

            }
        };

        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        getLoaderManager().initLoader(ID_LOADER_LIST_MOVIES, null, this).forceLoad();
    }

    @Override
    public Loader<List<MovieItem>> onCreateLoader(int i, Bundle args) {

        if (MoviePreferences.getSortByPreference(context).equals(getString(R.string
                .pref_sort_by_popularity_desc))) {
            sortOption = NetworkUtils.MOST_POPULAR_PARAM;
        } else if (MoviePreferences.getSortByPreference(context).equals(getString(R.string
                .pref_sort_by_rating_desc))) {
            sortOption = NetworkUtils.TOP_RATED_PARAM;
        }
        String urlPopularMovieActivity = NetworkUtils.buildUrlMovieActivity(context, sortOption);
        Log.e(TAG, urlPopularMovieActivity);
        return new MovieLoader(this, urlPopularMovieActivity);
    }

    @Override
    public void onLoadFinished(Loader<List<MovieItem>> loader, List<MovieItem> movieItems) {
        adapter.InsertList(movieItems);
    }

    @Override
    public void onLoaderReset(Loader<List<MovieItem>> loader) {
        adapter.InsertList(null);
    }

    @Override
    public void onSelectedItem(int id) {
        Intent goToDetailActivity = new Intent(this, DetailMovieActivity.class);
        goToDetailActivity.putExtra(MOVIE_ID, id);
        startActivity(goToDetailActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.action_general_settings) {
            Intent goToSetting = new Intent(this, SettingsActivity.class);
            startActivity(goToSetting);
            return true;
        } else if (id == R.id.action_refresh) {
            Toast.makeText(context, "bla bla reload", Toast.LENGTH_SHORT).show();
            getLoaderManager().restartLoader(ID_LOADER_LIST_MOVIES, null, MovieActivity.this);
        }

        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        adapter.deleteItemsInList();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 500);
        getLoaderManager().restartLoader(ID_LOADER_LIST_MOVIES,  null,this);
        Log.e(TAG, "Swipe refresh");
    }
}
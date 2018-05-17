package com.example.android.cinematik;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.cinematik.Adapters.MovieAdapter;
import com.example.android.cinematik.data.MoviePreferences;
import com.example.android.cinematik.data.MoviesContract;
import com.example.android.cinematik.loaders.MovieLoader;
import com.example.android.cinematik.pojos.MovieItem;
import com.example.android.cinematik.utilities.NetworkDetection;
import com.example.android.cinematik.utilities.NetworkUtils;
import com.facebook.stetho.Stetho;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks,
        MovieAdapter.MovieDetailClickHandler, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String MOVIE_ID = "movieId";

    public RecyclerView movieListRV;
    private GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
    Context context = this;

    // Loader IDs for loading the main API and the poster API, respectively
    private static final int ID_LOADER_LIST_MOVIES = 1;
    private static final int ID_LOADER_CURSOR = 2;

    // adapter
    private MovieAdapter adapter;

    // detect internet connection
    NetworkDetection networkDetection;

    // swipe to refresh
    SwipeRefreshLayout swipeRefreshLayout;

    // sortOption
    String sortOption = null;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    // movie projection
    private final String[] projection = new String[]{
            MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER,
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);

        Toolbar toolbar = findViewById(R.id.settings_activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        networkDetection = new NetworkDetection(this);

        swipeRefreshLayout = findViewById(R.id.discover_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(MainActivity.this);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_red_dark);

        movieListRV = findViewById(R.id.recycler_view_movies);
        movieListRV.setLayoutManager(gridLayoutManager);
        movieListRV.setHasFixedSize(true);

        adapter = new MovieAdapter(this, this);
        movieListRV.setAdapter(adapter);

        RecyclerViewItemDecorator itemDecorator = new RecyclerViewItemDecorator(context,
                R.dimen.item_offset);
        movieListRV.addItemDecoration(itemDecorator);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(getString(R.string.pref_sort_by_key))) {
                    initializeloader();
                }
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        initializeloader();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String urlMovieActivity = null;

        switch (id) {
            case ID_LOADER_CURSOR:

                return new CursorLoader(context, MoviesContract.MovieEntry.MOVIES_CONTENT_URI,
                        projection, null, null, null);

            case ID_LOADER_LIST_MOVIES:
                urlMovieActivity = NetworkUtils.buildUrlMovieActivity(context, sortOption);
                return new MovieLoader(this, urlMovieActivity);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        TextView noMoviesMessage = findViewById(R.id.no_movies_found_tv);
        switch (loader.getId()) {
            case ID_LOADER_CURSOR:
                adapter.InsertList(data);
                getLoaderManager().destroyLoader(ID_LOADER_CURSOR);
                break;

            case ID_LOADER_LIST_MOVIES:
                //noinspection unchecked
                List<MovieItem> movieItems = (List<MovieItem>) data;
                if (networkDetection.isConnected()) {
                    noMoviesMessage.setVisibility(View.GONE);
                    adapter.InsertList(movieItems);
                } else {
                    noMoviesMessage.setVisibility(View.VISIBLE);
                }

                getLoaderManager().destroyLoader(ID_LOADER_LIST_MOVIES);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        switch (loader.getId()) {
            case ID_LOADER_CURSOR:
                break;
            case ID_LOADER_LIST_MOVIES:
                adapter.InsertList(null);
                break;
        }
    }

    @Override
    public void onSelectedItem(int movieId) {
        Intent goToDetailActivity = new Intent(this, DetailMovieActivity.class);
        goToDetailActivity.putExtra(MOVIE_ID, movieId);
        startActivity(goToDetailActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            onRefresh();
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
        }, 0);
        restartloader();
        adapter.notifyDataSetChanged();
    }

    private void restartloader() {
        if (MoviePreferences.getSortByPreference(context).equals(getString(R.string
                .pref_sort_by_favourite))) {
            getLoaderManager().restartLoader(ID_LOADER_CURSOR, null, MainActivity
                    .this);
        }

        if (MoviePreferences.getSortByPreference(context).equals(getString(R.string
                .pref_sort_by_popularity))) {
            sortOption = NetworkUtils.MOST_POPULAR_PARAM;
            getLoaderManager().restartLoader(ID_LOADER_LIST_MOVIES, null,
                    MainActivity.this);
        }

        if (MoviePreferences.getSortByPreference(context).equals(getString(R.string
                .pref_sort_by_rating))) {
            sortOption = NetworkUtils.TOP_RATED_PARAM;
            getLoaderManager().restartLoader(ID_LOADER_LIST_MOVIES, null,
                    MainActivity.this);
        }
    }

    public void initializeloader() {
        if (MoviePreferences.getSortByPreference(context).equals(getString(R.string
                .pref_sort_by_favourite))) {
            getLoaderManager().initLoader(ID_LOADER_CURSOR, null, MainActivity
                    .this);
        }

        if (MoviePreferences.getSortByPreference(context).equals(getString(R.string
                .pref_sort_by_popularity))) {
            sortOption = NetworkUtils.MOST_POPULAR_PARAM;
            getLoaderManager().initLoader(ID_LOADER_LIST_MOVIES, null,
                    MainActivity.this);
        }

        if (MoviePreferences.getSortByPreference(context).equals(getString(R.string
                .pref_sort_by_rating))) {
            sortOption = NetworkUtils.TOP_RATED_PARAM;
            getLoaderManager().initLoader(ID_LOADER_LIST_MOVIES, null,
                    MainActivity.this);
        }
    }
}
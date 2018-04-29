package com.example.android.cinematik;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.cinematik.Adapters.MovieAdapter;
import com.example.android.cinematik.Interfaces.MovieDetailClickHandler;
import com.example.android.cinematik.data.MoviePreferences;
import com.example.android.cinematik.data.MoviesDbHelper;
import com.example.android.cinematik.loaders.MovieLoader;
import com.example.android.cinematik.pojos.MovieItem;
import com.example.android.cinematik.utilities.NetworkDetection;
import com.example.android.cinematik.utilities.NetworkUtils;
import com.facebook.stetho.Stetho;

import java.util.List;

import static com.example.android.cinematik.data.MoviesContract.MovieEntry;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<MovieItem>>,
        MovieDetailClickHandler, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String MOVIE_ID = "id";

    public RecyclerView movieListRV;
    private GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
    Context context = this;

    // Loader IDs for loading the main API and the poster API, respectively
    private static final int ID_LOADER_LIST_MOVIES = 44;

    // adapter
    private MovieAdapter adapter;

    // detect internet connection
    NetworkDetection networkDetection;

    // swipe to refresh
    SwipeRefreshLayout swipeRefreshLayout;

    // sortOption
    String sortOption = null;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    private final String[] projection = new String[]{
            MovieEntry.COLUMN_MOVIE_ID,
            MovieEntry.COLUMN_MOVIE_POSTER
    };

    // local filed member of type SQLiteDatabase called mDb
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);

        Toolbar toolbar = findViewById(R.id.settings_activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        // MoviesDbHelper instance with "this" as context
        MoviesDbHelper dbHelper = new MoviesDbHelper(this);
        dbHelper.getReadableDatabase();


        networkDetection = new NetworkDetection(this);

        swipeRefreshLayout = findViewById(R.id.discover_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(MainActivity.this);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_red_dark);

        movieListRV = findViewById(R.id.recycler_view_movies);
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

                    getLoaderManager().restartLoader(ID_LOADER_LIST_MOVIES, null, MainActivity
                            .this);
                }
            }
        };

        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        getLoaderManager().initLoader(ID_LOADER_LIST_MOVIES, null, this).forceLoad();
    }

    @Override
    public Loader<List<MovieItem>> onCreateLoader(int i, Bundle args) {
        String urlMovieActivity = null;

        if (MoviePreferences.getSortByPreference(context).equals(
                getString(R.string.pref_sort_by_popularity))) {
            sortOption = NetworkUtils.MOST_POPULAR_PARAM;
            urlMovieActivity = NetworkUtils.buildUrlMovieActivity(context, sortOption);
        } else if (MoviePreferences.getSortByPreference(context).equals(
                getString(R.string.pref_sort_by_rating))) {
            sortOption = NetworkUtils.TOP_RATED_PARAM;
            urlMovieActivity = NetworkUtils.buildUrlMovieActivity(context, sortOption);
        }
        return new MovieLoader(this, urlMovieActivity);
    }

    @Override
    public void onLoadFinished(Loader<List<MovieItem>> loader, List<MovieItem> movieItems) {

        TextView noMoviesMessage = findViewById(R.id.no_movies_found_tv);

        if (networkDetection.isConnected()) {
            adapter.InsertList(movieItems);
            noMoviesMessage.setVisibility(View.GONE);
        } else {
            noMoviesMessage.setVisibility(View.VISIBLE);
        }
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
        getLoaderManager().restartLoader(ID_LOADER_LIST_MOVIES, null, this);
        adapter.notifyDataSetChanged();
    }

    private Cursor getAllMovies() {
        return mDb.query(
                MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MovieEntry._ID
        );
    }
}

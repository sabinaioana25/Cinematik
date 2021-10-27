package com.main.android.cinematik;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import 	androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import 	androidx.appcompat.app.AppCompatActivity;
import 	androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.main.android.cinematik.Adapters.MovieAdapter;
import com.example.android.cinematik.R;
import com.main.android.cinematik.data.MoviePreferences;
import com.main.android.cinematik.data.MoviesContract;
import com.main.android.cinematik.loaders.MovieLoader;
import com.main.android.cinematik.pojos.MovieItem;
import com.main.android.cinematik.utilities.NetworkDetection;
import com.main.android.cinematik.utilities.NetworkUtils;
import com.facebook.stetho.Stetho;

import java.util.List;

import static com.example.android.cinematik.R.*;

public class MainActivity extends AppCompatActivity implements
    LoaderManager.LoaderCallbacks,
        MovieAdapter.MovieDetailClickHandler, SwipeRefreshLayout.OnRefreshListener {

    public static final String MOVIE_ID = "movieId";

    private final static String LIFECYCLE_CALLBACKS_LAYOUT_MANAGER_KEY = "KeyForLayoutManagerState";
    Parcelable savedLayoutManagerState;

    public RecyclerView movieListRV;
    private final GridLayoutManager gridLayoutManager =
            new GridLayoutManager(this, 1);
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

    // movie projection
    private final String[] projection = new String[]{
            MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER,
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        Stetho.initializeWithDefaults(this);

        Toolbar toolbar = findViewById(id.settings_activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        networkDetection = new NetworkDetection(this);

        swipeRefreshLayout = findViewById(id.discover_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(MainActivity.this);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_red_dark);

        movieListRV = findViewById(id.recycler_view_movies);
        movieListRV.setLayoutManager(gridLayoutManager);
        movieListRV.setHasFixedSize(true);

        ViewTreeObserver viewTreeObserver = movieListRV.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                calculateSize();
            }
        });
        adapter = new MovieAdapter(this, this);
        movieListRV.setAdapter(adapter);

        ItemDecoration decoration = new ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }
        };
        movieListRV.addItemDecoration(decoration);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences
                (context);
        SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = (sharedPreferences, key) -> {
                adapter.deleteItemsInList();
                onRefresh();
            if (key.equals(getString(string.pref_sort_by_key))) {
                initializeLoader();
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        initializeLoader();
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(ID_LOADER_CURSOR, null, this);
        getLoaderManager().restartLoader(ID_LOADER_LIST_MOVIES, null, this);
    }

    private static final int sColumnWidth = 200;

    private void calculateSize() {
        int spanCount = (int) Math.floor(movieListRV.getWidth() / convertDPToPixels(sColumnWidth));
        ((GridLayoutManager) movieListRV.getLayoutManager()).setSpanCount(spanCount);
    }

    @SuppressWarnings("SameParameterValue")
    private float convertDPToPixels(int dp) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float logicalDensity = metrics.density;
        return dp * logicalDensity;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LIFECYCLE_CALLBACKS_LAYOUT_MANAGER_KEY, gridLayoutManager
                .onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            savedLayoutManagerState = savedInstanceState.getParcelable
                    (LIFECYCLE_CALLBACKS_LAYOUT_MANAGER_KEY);
            movieListRV.getLayoutManager().onRestoreInstanceState(savedLayoutManagerState);
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        adapter.deleteItemsInList();
        String urlMovieActivity;
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
        adapter.deleteItemsInList();
        TextView noMoviesMessage = findViewById(id.no_movies_found_tv);
        switch (loader.getId()) {
            case ID_LOADER_CURSOR:
                adapter.deleteItemsInList();
                adapter.InsertList(data);
                break;
            case ID_LOADER_LIST_MOVIES:
                //noinspection unchecked
                List<MovieItem> movieItems = (List<MovieItem>) data;
                if (networkDetection.isConnected()) {
                    noMoviesMessage.setVisibility(View.GONE);
                    adapter.InsertList(movieItems);
                    movieListRV.getLayoutManager().onRestoreInstanceState(savedLayoutManagerState);
                } else {
                    noMoviesMessage.setVisibility(View.VISIBLE);
                }
                break;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader loader) {
        switch (loader.getId()) {
            case ID_LOADER_CURSOR:
            case ID_LOADER_LIST_MOVIES:
                adapter.InsertList(null);
                break;
        }
    }

    @Override
    public void onPostResume(Loader loader) {
        super.onPostResume();
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
        swipeRefreshLayout.setRefreshing(false);

        // commented out for functionality purposes
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        }, 0);
        restartLoader();
        adapter.notifyDataSetChanged();
    }

    private void restartLoader() {
        adapter.deleteItemsInList();
        if (MoviePreferences.getSortByPreference(context).equals(getString(string
                .pref_sort_by_favourite))) {
            getLoaderManager().restartLoader(ID_LOADER_CURSOR, null, MainActivity
                    .this);
        }

        if (MoviePreferences.getSortByPreference(context).equals(getString(string
                .pref_sort_by_popularity))) {
            sortOption = NetworkUtils.MOST_POPULAR_PARAM;
            getLoaderManager().restartLoader(ID_LOADER_LIST_MOVIES, null,
                    MainActivity.this);
        }

        if (MoviePreferences.getSortByPreference(context).equals(getString(string
                .pref_sort_by_rating))) {
            sortOption = NetworkUtils.TOP_RATED_PARAM;
            getLoaderManager().restartLoader(ID_LOADER_LIST_MOVIES, null,
                    MainActivity.this);
        }
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void initializeLoader() {
        restartLoader();
        if (MoviePreferences.getSortByPreference(context).equals(getString(string
                .pref_sort_by_favourite))) {
            getLoaderManager().initLoader(ID_LOADER_CURSOR, null, MainActivity
                    .this);
        }

        if (MoviePreferences.getSortByPreference(context).equals(getString(string
                .pref_sort_by_popularity))) {
            onRefresh();

            sortOption = NetworkUtils.MOST_POPULAR_PARAM;
            getLoaderManager().initLoader(ID_LOADER_LIST_MOVIES, null,
                    MainActivity.this);
        }

        if (MoviePreferences.getSortByPreference(context).equals(getString(string
                .pref_sort_by_rating))) {
            onRefresh();

            sortOption = NetworkUtils.TOP_RATED_PARAM;
            getLoaderManager().initLoader(ID_LOADER_LIST_MOVIES, null,
                    MainActivity.this);
        }

        adapter.notifyDataSetChanged();
    }
}
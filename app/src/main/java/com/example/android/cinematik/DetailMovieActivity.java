package com.example.android.cinematik;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.cinematik.Adapters.CastAdapter;
import com.example.android.cinematik.Adapters.ReviewAdapter;
import com.example.android.cinematik.loaders.DetailMovieLoader;
import com.example.android.cinematik.pojos.MovieItem;
import com.example.android.cinematik.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.android.cinematik.data.MoviesContract.MovieEntry;

public class DetailMovieActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks {

    private static final String TAG = DetailMovieActivity.class.getSimpleName();
    private static final int ID_LOADER_DETAIL_MOVIES = 45;
    private static final int ID_CURSOR_LOADER = 47;
//    private static final int ID_CAST_CURSOR_LOADER = 2;
//    private static final int ID_REVIEW_CURSOR_LOADER = 3;

    public static int movieId = 0;

    private RecyclerView castListRecyclerView;
    private LinearLayoutManager castLinearLayoutManager = new LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false);
    private RecyclerView reviewListRecyclerView;
    private LinearLayoutManager reviewLinearLayoutManager = new LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false);

    // adapters
    private CastAdapter castAdapter;
    private ReviewAdapter reviewAdapter;

    // add to favourites button
    Button buttonFavouriteMovies;
    Button buttonPlayTrailer;
    boolean buttonIsSelected = false;

    // detail activity components
    private String movieBackdrop = null;
    private String movieTitle = null;
    private String movieReleaseDate = null;
    private String movieRuntime = null;
    private String movieGenres = null;
    private String movieVote = null;
    private String movieOverview = null;
    private String movieDirector = null;
    private String movieProducer = null;
    private String movieVideoUrl = null;
    private String moviePoster = null;

    private final String[] movieProjection = new String[]{
            MovieEntry.COLUMN_MOVIE_ID,
            MovieEntry.COLUMN_MOVIE_BACKDROP,
            MovieEntry.COLUMN_MOVIE_TITLE,
            MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
            MovieEntry.COLUMN_MOVIE_RUNTIME,
            MovieEntry.COLUMN_MOVIE_GENRES,
            MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
            MovieEntry.COLUMN_MOVIE_OVERVIEW,
            MovieEntry.COLUMN_MOVIE_DIRECTOR,
            MovieEntry.COLUMN_MOVIE_PRODUCER,
            MovieEntry.COLUMN_MOVIE_VIDEO_URL,
            MovieEntry.COLUMN_MOVIE_POSTER
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        Toolbar toolbar = findViewById(R.id.detail_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);

        Intent getMovieDetails = getIntent();
        movieId = getMovieDetails.getExtras().getInt(MainActivity.MOVIE_ID);

        buttonFavouriteMovies = findViewById(R.id.detail_activity_button_favourites);
        buttonFavouriteMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // clear state
                if (!buttonIsSelected) {
                    buttonFavouriteMovies.setSelected(true);
                    buttonIsSelected = true;
                    addToDatabaseTable();

                } else {
                    // change state
                    buttonFavouriteMovies.setSelected(false);
                    buttonIsSelected = false;
                    deleteFromTable();
                }
            }
        });

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_18dp);
        upArrow.setColorFilter(getResources().getColor(R.color.colorUpArrow), PorterDuff.Mode
                .DST_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        // CastAdapter
        castListRecyclerView = findViewById(R.id.detail_activity_recycler_view_cast_id);
        castListRecyclerView.setLayoutManager(castLinearLayoutManager);
        castListRecyclerView.setHasFixedSize(true);
        castAdapter = new CastAdapter(this, movieId);
        castListRecyclerView.setAdapter(castAdapter);

        // ReviewAdapter
        reviewListRecyclerView = findViewById(R.id.detail_activity_recycler_view_reviews_id);
        reviewListRecyclerView.setLayoutManager(reviewLinearLayoutManager);
        reviewListRecyclerView.setHasFixedSize(false);
        reviewAdapter = new ReviewAdapter(this, movieId);
        reviewListRecyclerView.setAdapter(reviewAdapter);

//        getLoaderManager().initLoader(ID_LOADER_DETAIL_MOVIES, null, this);
        getLoaderManager().initLoader(ID_CURSOR_LOADER, null, this);
    }

    @Override
    public Loader onCreateLoader(int loaderID, Bundle bundle) {

        switch (loaderID) {
            case ID_CURSOR_LOADER:
                String movieSelection = MovieEntry.COLUMN_MOVIE_ID + " = ?";
                String[] movieSelectionArgs = new String[]{String.valueOf(movieId)};
                return new CursorLoader(getApplicationContext(),
                        MovieEntry.MOVIES_CONTENT_URI,
                        movieProjection,
                        movieSelection,
                        movieSelectionArgs,
                        null);

            case ID_LOADER_DETAIL_MOVIES:
                return new DetailMovieLoader(this, movieId);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished
            (Loader loader, Object data) {

        switch (loader.getId()) {
            case ID_CURSOR_LOADER:
                Cursor movieCursor = (Cursor) data;
                if (movieCursor.getCount() > 0) {
                    movieCursor.moveToFirst();
                    int currentMovieId;
                    currentMovieId = movieCursor.getInt(movieCursor.getColumnIndex(MovieEntry
                            .COLUMN_MOVIE_ID));
                    if (currentMovieId == movieId) {
                        populateMovieItems(loader.getContext(), movieCursor, ID_CURSOR_LOADER);
                        buttonFavouriteMovies.setSelected(true);
                        buttonIsSelected = true;
                    }
                } else {
                    getLoaderManager().initLoader(ID_LOADER_DETAIL_MOVIES, null, this);
                }
                return;

            case ID_LOADER_DETAIL_MOVIES:
                populateMovieItems(loader.getContext(), data, ID_LOADER_DETAIL_MOVIES);
                break;
        }


//        // cast
//        castAdapter.addList(.getMovieCastMembers());
//
//        // reviews
//        if (data.getMovieReviewItems() != null) {
//            reviewAdapter.addList(data.getMovieReviewItems());
//        } else {
//            reviewListRecyclerView.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    @SuppressLint("SetTextI18n")
    private void populateMovieItems(Context context, Object object, int type) {

        switch (type) {
            case ID_CURSOR_LOADER:
                Cursor movieCursor = (Cursor) object;
                movieBackdrop = movieCursor.getString(movieCursor.getColumnIndex
                        (MovieEntry.COLUMN_MOVIE_BACKDROP));
                movieTitle = movieCursor.getString(movieCursor.getColumnIndex(MovieEntry
                        .COLUMN_MOVIE_TITLE));
                movieReleaseDate = movieCursor.getString(movieCursor.getColumnIndex
                        (MovieEntry.COLUMN_MOVIE_RELEASE_DATE));
                movieRuntime = movieCursor.getString(movieCursor.getColumnIndex(MovieEntry
                        .COLUMN_MOVIE_RUNTIME));
                movieGenres = movieCursor.getString(movieCursor.getColumnIndex(MovieEntry
                        .COLUMN_MOVIE_GENRES));
                movieVote = movieCursor.getString(movieCursor.getColumnIndex(MovieEntry
                        .COLUMN_MOVIE_VOTE_AVERAGE));
                movieOverview = movieCursor.getString(movieCursor.getColumnIndex
                        (MovieEntry.COLUMN_MOVIE_OVERVIEW));
                movieDirector = movieCursor.getString(movieCursor.getColumnIndex
                        (MovieEntry.COLUMN_MOVIE_DIRECTOR));
                movieProducer = movieCursor.getString(movieCursor.getColumnIndex
                        (MovieEntry.COLUMN_MOVIE_PRODUCER));
                movieVideoUrl = movieCursor.getString(movieCursor.getColumnIndex
                        (MovieEntry.COLUMN_MOVIE_VIDEO_URL));
                moviePoster = movieCursor.getString(movieCursor.getColumnIndex
                        (MovieEntry.COLUMN_MOVIE_POSTER));
                break;

            case ID_LOADER_DETAIL_MOVIES:
                MovieItem movieItem = (MovieItem) object;

                movieBackdrop = movieItem.getBackdropPath();
                movieTitle = movieItem.getTitle();
                movieReleaseDate = movieItem.getReleaseDate();
                movieRuntime = movieItem.getRuntime();
                List<String> movieGenresList = movieItem.getGenres();
                StringBuilder genresBuilder = new StringBuilder();
                if (movieGenresList != null) {
                    for (String s : movieGenresList) {
                        genresBuilder.append(s).append(", ");
                    }
                    movieGenres = genresBuilder.toString();
                    movieGenres = movieGenres.substring(0, movieGenres.length() - 2);

                }
                movieVote = movieItem.getVoteAverage();
                movieOverview = movieItem.getOverview();
                movieDirector = movieItem.getMovieDirector();
                movieProducer = movieItem.getMovieProducer();
                movieVideoUrl = movieItem.getVideoId();
                moviePoster = movieItem.getPoster();
                break;
        }

        buttonPlayTrailer = findViewById(R.id.detail_activity_button_play_trailer);
        buttonPlayTrailer.getBackground().setAlpha(128);
        buttonPlayTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(NetworkUtils
                        .buildUrlVideo(movieVideoUrl)));
                startActivity(goToTrailer);
            }
        });

        ImageView backdropPhoto = findViewById(R.id.detail_activity_backdrop_image_id);
        Picasso.with(context)
                .load(movieBackdrop)
                .into(backdropPhoto);

        TextView title = findViewById(R.id.detail_activity_title_movie_id);
        title.setText(movieTitle);

        TextView releaseDate = findViewById(R.id.detail_activity_release_date_id);
        releaseDate.setText(movieReleaseDate.substring(0, 4));

        TextView runtime = findViewById(R.id.detail_activity_runtime_id);
        runtime.setText(movieRuntime + " min");

        TextView movieGenresTV = findViewById(R.id.detail_activity_genres_id);
        if (movieGenres != null) {
            movieGenresTV.setText(movieGenres);
        } else {
            movieGenresTV.setText(R.string.detail_activity_no_genres_found_tv);
        }

        TextView voteAverage = findViewById(R.id.detail_activity_imdb_rating_id);
        if (movieVote != null) {
            voteAverage.setText(movieVote);
        } else {
            voteAverage.setText("");
        }
        TextView overview = findViewById(R.id.detail_activity_overview_text_id);
        if (movieOverview != null) {
            overview.setText(movieOverview);
        } else {
            overview.setText("");
        }

        TextView director = findViewById(R.id.detail_activity_director_value_id);
        if (movieDirector != null) {
            director.setText(movieDirector);
        } else {
            director.setText("");
        }

        TextView producer = findViewById(R.id.detail_activity_producer_value_id);
        if (movieProducer != null) {
            producer.setText(movieProducer);
        } else {
            producer.setText("");
        }
    }

    private void addToDatabaseTable() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieEntry.COLUMN_MOVIE_ID, movieId);
        contentValues.put(MovieEntry.COLUMN_MOVIE_BACKDROP, movieBackdrop);
        contentValues.put(MovieEntry.COLUMN_MOVIE_TITLE, movieTitle);
        contentValues.put(MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movieReleaseDate);
        contentValues.put(MovieEntry.COLUMN_MOVIE_RUNTIME, movieRuntime);
        contentValues.put(MovieEntry.COLUMN_MOVIE_GENRES, movieGenres);
        contentValues.put(MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, movieVote);
        contentValues.put(MovieEntry.COLUMN_MOVIE_OVERVIEW, movieOverview);
        contentValues.put(MovieEntry.COLUMN_MOVIE_DIRECTOR, movieDirector);
        contentValues.put(MovieEntry.COLUMN_MOVIE_PRODUCER, movieProducer);
        contentValues.put(MovieEntry.COLUMN_MOVIE_VIDEO_URL, movieVideoUrl);
        contentValues.put(MovieEntry.COLUMN_MOVIE_POSTER, moviePoster);

        getContentResolver().insert(MovieEntry.MOVIES_CONTENT_URI, contentValues);
    }

    private void deleteFromTable() {
        String selection = MovieEntry.COLUMN_MOVIE_ID + " = ? ";
        String[] selArgs = new String[]{String.valueOf(movieId)};
        getContentResolver().delete(MovieEntry.MOVIES_CONTENT_URI, selection, selArgs);
    }
}



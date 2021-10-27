package com.main.android.cinematik;

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
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import 	androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.main.android.cinematik.Adapters.CastAdapter;
import com.main.android.cinematik.Adapters.ReviewAdapter;
import com.example.android.cinematik.R;
import com.main.android.cinematik.loaders.DetailMovieLoader;
import com.main.android.cinematik.pojos.CastMember;
import com.main.android.cinematik.pojos.MovieItem;
import com.main.android.cinematik.pojos.ReviewItem;
import com.main.android.cinematik.utilities.NetworkUtils;
import com.main.android.cinematik.data.MoviesContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"WeakerAccess", "unused"})
public class DetailMovieActivity  extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks {

    private static final String TAG = DetailMovieActivity.class.getSimpleName();
    public static final int ID_LOADER_DETAIL_MOVIES = 45;
    public static final int ID_VIDEO_LOADER = 47;
    public static final int ID_REVIEW_LOADER = 46;
    private static final int ID_CURSOR_LOADER = 48;
    private static final int ID_CAST_CURSOR_LOADER = 2;
    private static final int ID_REVIEW_CURSOR_LOADER = 3;
    private int movieId;
    private static String urlVideo;
    private static String urlTwoVideo; //temporarily placed here; will modify the app sometime in the very near future hopefully; not design to be pretty, designed to pass the course

    private RecyclerView castListRecyclerView;
    @SuppressWarnings("CanBeFinal")
    private final LinearLayoutManager castLinearLayoutManager = new LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false);
    private RecyclerView reviewListRecyclerView;
    @SuppressWarnings("CanBeFinal")
    private final LinearLayoutManager reviewLinearLayoutManager = new LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false);

    // adapters
    private CastAdapter castAdapter;
    private ReviewAdapter reviewAdapter;

    // add to favourites button
    Button buttonFavouriteMovies;
    Button buttonPlayTrailer;
    Button buttonPlaySecondTrailer;
    ImageView secondTrailerImage;
    boolean buttonIsSelected = false;

    // detail activity components
    private String movieBackdrop = null;
    private String trailerImage = null;
    private String movieTitle = null;
    private String movieReleaseDate = null;
    private String movieRuntime = null;
    private String movieGenres = null;
    private String movieVote = null;
    private String movieOverview = null;
    private String movieDirector = null;
    private String movieProducer = null;
    public String movieVideoUrl = null;
    public String movieTwoVideoUrl = null;
    private String moviePoster = null;
    private List<CastMember> castMembers = null;
    private List<ReviewItem> reviewsList = null;

    private final String[] movieProjection = new String[]{
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE_BACKDROP,
            MoviesContract.MovieEntry.COLUMN_TRAILER_IMAGE,
            MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_MOVIE_RUNTIME,
            MoviesContract.MovieEntry.COLUMN_MOVIE_GENRES,
            MoviesContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
            MoviesContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
            MoviesContract.MovieEntry.COLUMN_MOVIE_DIRECTOR,
            MoviesContract.MovieEntry.COLUMN_MOVIE_PRODUCER,
            MoviesContract.MovieEntry.COLUMN_MOVIE_VIDEO_URL,
            MoviesContract.MovieEntry.COLUMN_MOVIE_VIDEO_TWO_URL,
            MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER
    };

    private final String[] castProjection = new String[]{
            MoviesContract.CastEntry.COLUMN_CAST_MOVIE_ID,
            MoviesContract.CastEntry.COLUMN_CAST_PROFILE,
            MoviesContract.CastEntry.COLUMN_CAST_NAME,
            MoviesContract.CastEntry.COLUMN_CAST_SUBTITLE
    };

    private final String[] reviewProjection = new String[]{
            MoviesContract.ReviewsEntry.COLUMN_REVIEWS_MOVIE_ID,
            MoviesContract.ReviewsEntry.COLUMN_REVIEWS_AUTHOR,
            MoviesContract.ReviewsEntry.COLUMN_REVIEWS_CONTENT
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        Toolbar toolbar = findViewById(R.id.detail_activity_toolbar);
        setSupportActionBar(toolbar);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
//        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);

        Intent getMovieDetails = getIntent();
        movieId = getMovieDetails.getExtras().getInt(MainActivity.MOVIE_ID);

        buttonFavouriteMovies = findViewById(R.id.detail_activity_button_favourites);
        buttonFavouriteMovies.setOnClickListener(view -> {

            // clear state
            if (!buttonIsSelected) {
                buttonFavouriteMovies.setSelected(true);
                buttonIsSelected = true;
                addToDatabaseTable(ID_CURSOR_LOADER);
                addToDatabaseTable(ID_VIDEO_LOADER);
                addToDatabaseTable(ID_CAST_CURSOR_LOADER);
                addToDatabaseTable(ID_REVIEW_CURSOR_LOADER);

            } else {
                // change state
                buttonFavouriteMovies.setSelected(false);
                buttonIsSelected = false;
                deleteFromTable();
            }
        });

        @SuppressLint("UseCompatLoadingForDrawables") final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_18dp);
        upArrow.setColorFilter(getResources().getColor(R.color.colorUpArrow), PorterDuff.Mode
                .DST_IN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(upArrow);
        }

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

        NetworkUtils.buildUrlVideo(movieId);
        NetworkUtils.buildUrlReviewList(movieId);
        getLoaderManager().initLoader(ID_CURSOR_LOADER, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            String message = savedInstanceState.getString("message");
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Loader onCreateLoader(int loaderID, Bundle bundle) {
        switch (loaderID) {
            case ID_CURSOR_LOADER:
                String movieSelection = MoviesContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
                String[] movieSelectionArgs = new String[]{String.valueOf(movieId)};
                return new CursorLoader(getApplicationContext(),
                        MoviesContract.MovieEntry.MOVIES_CONTENT_URI,
                        movieProjection,
                        movieSelection,
                        movieSelectionArgs,
                        null);
            case ID_CAST_CURSOR_LOADER:
                String castSel = MoviesContract.CastEntry.COLUMN_CAST_MOVIE_ID + "=?";
                String[] castSelArg = new String[]{String.valueOf(movieId)};
                return new CursorLoader(this, MoviesContract.CastEntry.CAST_CONTENT_URI,
                        castProjection, castSel, castSelArg, null);
            case ID_REVIEW_CURSOR_LOADER:
                String reviewSel = MoviesContract.ReviewsEntry.COLUMN_REVIEWS_MOVIE_ID + "=?";
                String[] reviewSelArgs = new String[]{String.valueOf(movieId)};
                return new CursorLoader(this, MoviesContract.ReviewsEntry.REVIEWS_CONTENT_URI,
                        reviewProjection, reviewSel, reviewSelArgs, null);
            case ID_LOADER_DETAIL_MOVIES:
                return new DetailMovieLoader(this, movieId, ID_LOADER_DETAIL_MOVIES);
            case ID_VIDEO_LOADER:
                return new DetailMovieLoader(this, movieId, ID_VIDEO_LOADER);
            case ID_REVIEW_LOADER:
                return new DetailMovieLoader(this, movieId, ID_REVIEW_LOADER);
            default:
                return null;
        }
    }

    @SuppressLint("Range")
    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case ID_CURSOR_LOADER:
                Cursor movieCursor = (Cursor) data;
                if (movieCursor.getCount() > 0) {
                    movieCursor.moveToFirst();
                    int currentMovieId;
                    currentMovieId = movieCursor.getInt(movieCursor.getColumnIndex(MoviesContract.MovieEntry
                            .COLUMN_MOVIE_ID));
                    if (currentMovieId == movieId) {
                        buttonFavouriteMovies.setSelected(true);
                        buttonIsSelected = true;
                        populateMovieItems(loader.getContext(), movieCursor, ID_CURSOR_LOADER);
                        getLoaderManager().initLoader(ID_CAST_CURSOR_LOADER, null, this);
                        getLoaderManager().initLoader(ID_REVIEW_CURSOR_LOADER, null, this);
                    }
                } else {
//                    deleteFromTable();
                    getLoaderManager().restartLoader(ID_LOADER_DETAIL_MOVIES, null, this);
                    getLoaderManager().restartLoader(ID_VIDEO_LOADER, null, this);
                    getLoaderManager().restartLoader(ID_REVIEW_LOADER, null, this);
                }
                break;
            case ID_CAST_CURSOR_LOADER:
                Cursor cursorCast = (Cursor) data;
                populateCastItems(cursorCast, ID_CAST_CURSOR_LOADER);
                break;
            case ID_REVIEW_CURSOR_LOADER:
                Cursor cursorReviews = (Cursor) data;
                populateReviewsItems(cursorReviews, ID_REVIEW_CURSOR_LOADER);
                break;
            case ID_LOADER_DETAIL_MOVIES:
                populateMovieItems(loader.getContext(), data, ID_LOADER_DETAIL_MOVIES);
                populateCastItems(data, ID_LOADER_DETAIL_MOVIES);
                break;
            case ID_VIDEO_LOADER:
                MovieItem movieItem = (MovieItem) data;
                urlVideo = NetworkUtils.buildUrlVideoFromYoutube(movieItem.getVideoId());
                urlTwoVideo = NetworkUtils.buildUrlVideoFromYoutube(movieItem.getVideoIdTwo());
                break;
            case ID_REVIEW_LOADER:
                MovieItem movieItem2 = (MovieItem) data;
                populateReviewsItems(movieItem2, ID_REVIEW_LOADER);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    @SuppressLint({"SetTextI18n", "Range"})
    private void populateMovieItems(Context context, Object object, int type) {

        switch (type) {
            case ID_CURSOR_LOADER:
                Cursor movieCursor = (Cursor) object;
                movieBackdrop = movieCursor.getString(movieCursor.getColumnIndex
                        (MoviesContract.MovieEntry.COLUMN_MOVIE_BACKDROP));
                trailerImage = movieCursor.getString(movieCursor.getColumnIndex(
                        MoviesContract.MovieEntry.COLUMN_TRAILER_IMAGE));
                movieTitle = movieCursor.getString(movieCursor.getColumnIndex(MoviesContract.MovieEntry
                        .COLUMN_MOVIE_TITLE));
                movieReleaseDate = movieCursor.getString(movieCursor.getColumnIndex
                        (MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE));
                movieRuntime = movieCursor.getString(movieCursor.getColumnIndex(MoviesContract.MovieEntry
                        .COLUMN_MOVIE_RUNTIME));
                movieGenres = movieCursor.getString(movieCursor.getColumnIndex(MoviesContract.MovieEntry
                        .COLUMN_MOVIE_GENRES));
                movieVote = movieCursor.getString(movieCursor.getColumnIndex(MoviesContract.MovieEntry
                        .COLUMN_MOVIE_VOTE_AVERAGE));
                movieOverview = movieCursor.getString(movieCursor.getColumnIndex
                        (MoviesContract.MovieEntry.COLUMN_MOVIE_OVERVIEW));
                movieDirector = movieCursor.getString(movieCursor.getColumnIndex
                        (MoviesContract.MovieEntry.COLUMN_MOVIE_DIRECTOR));
                movieProducer = movieCursor.getString(movieCursor.getColumnIndex
                        (MoviesContract.MovieEntry.COLUMN_MOVIE_PRODUCER));
                movieVideoUrl = movieCursor.getString(movieCursor.getColumnIndex
                        (MoviesContract.MovieEntry.COLUMN_MOVIE_VIDEO_URL));
                movieTwoVideoUrl = movieCursor.getString(movieCursor.getColumnIndex(
                        MoviesContract.MovieEntry.COLUMN_MOVIE_VIDEO_TWO_URL));
                moviePoster = movieCursor.getString(movieCursor.getColumnIndex
                        (MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER));
                break;

            case ID_LOADER_DETAIL_MOVIES:
                MovieItem movieItem = (MovieItem) object;
                movieBackdrop = movieItem.getBackdropPath();
                trailerImage = movieItem.getTrailerImagePath();
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
                Intent goToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(urlVideo));
                startActivity(goToTrailer);
            }
        });

        secondTrailerImage = findViewById(R.id.detail_activity_backdrop_second_image_id);
        secondTrailerImage.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                Intent goToSecondTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(urlTwoVideo));
                startActivity(goToSecondTrailer);
            }
        });

        ImageView backdropPhoto = findViewById(R.id.detail_activity_backdrop_image_id);
        Picasso.get()
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

    @SuppressLint("Range")
    private void populateCastItems(Object castObject, int idType) {
        switch (idType) {
            case ID_CAST_CURSOR_LOADER:
                Cursor castCursor = (Cursor) castObject;
                String castProfile;
                String castActorName;
                String castCharName;
                castMembers = new ArrayList<>();
                for (int i = 0; i < castCursor.getCount(); i++) {
                    castCursor.moveToPosition(i);
                    castProfile = castCursor.getString(castCursor.getColumnIndex(MoviesContract.CastEntry
                            .COLUMN_CAST_PROFILE));
                    castActorName = castCursor.getString(castCursor.getColumnIndex(MoviesContract.CastEntry
                            .COLUMN_CAST_NAME));
                    castCharName = castCursor.getString(castCursor.getColumnIndex(MoviesContract.CastEntry
                            .COLUMN_CAST_SUBTITLE));
                    castMembers.add(new CastMember(castProfile, castActorName, castCharName));
                }
                break;

            case ID_LOADER_DETAIL_MOVIES:
                MovieItem movieItem = (MovieItem) castObject;
                castMembers = movieItem.getMovieCastMembers();
                break;
        }
        if (castMembers != null) {
            castAdapter.addMembers(castMembers);

        } else {
            castListRecyclerView.setVisibility(View.GONE);

        }
    }

    @SuppressLint("Range")
    private void populateReviewsItems(Object object, int idType) {
        switch (idType) {

            case ID_REVIEW_CURSOR_LOADER:
                Cursor reviewsCursor = (Cursor) object;
                String reviewAuthor;
                String reviewContent;
                reviewsList = new ArrayList<>();
                for (int i = 0; i < reviewsCursor.getCount(); i++) {
                    reviewsCursor.moveToPosition(i);
                    reviewAuthor = reviewsCursor.getString(reviewsCursor.getColumnIndex
                            (MoviesContract.ReviewsEntry.COLUMN_REVIEWS_AUTHOR));
                    reviewContent = reviewsCursor.getString(reviewsCursor.getColumnIndex
                            (MoviesContract.ReviewsEntry.COLUMN_REVIEWS_CONTENT));
                    reviewsList.add(new ReviewItem(reviewAuthor, reviewContent));
                }
                break;
            case ID_REVIEW_LOADER:
                MovieItem movieItem = (MovieItem) object;
                reviewsList = movieItem.getMovieReviewItems();
                break;
        }

        if (reviewsList != null) {
            reviewAdapter.addList(reviewsList);
        } else {
            reviewListRecyclerView.setVisibility(View.GONE);
        }
    }

    private void addToDatabaseTable(int loaderType) {
        ContentValues contentValues = new ContentValues();

        switch (loaderType) {
            case ID_CURSOR_LOADER:
                contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
                contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_BACKDROP, movieBackdrop);
                contentValues.put(MoviesContract.MovieEntry.COLUMN_TRAILER_IMAGE, trailerImage);
                contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE, movieTitle);
                contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movieReleaseDate);
                contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RUNTIME, movieRuntime);
                contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_GENRES, movieGenres);
                contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, movieVote);
                contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movieOverview);
                contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_DIRECTOR, movieDirector);
                contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_PRODUCER, movieProducer);
                contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_VIDEO_URL, urlVideo);
                contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_VIDEO_TWO_URL, urlTwoVideo);
                contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER, moviePoster);
                getContentResolver().insert(MoviesContract.MovieEntry.MOVIES_CONTENT_URI, contentValues);

                break;

            case ID_CAST_CURSOR_LOADER:
                String castActorNm;
                String castCharNm;
                String castProfilePic;
                if (castMembers != null) {
                    for (int i = 0; i < castMembers.size(); i++) {
                        castProfilePic = castMembers.get(i).getCastProfile();
                        castActorNm = castMembers.get(i).getCastActorName();
                        castCharNm = castMembers.get(i).getCastCharName();
                        contentValues.put(MoviesContract.CastEntry.COLUMN_CAST_MOVIE_ID, movieId);
                        contentValues.put(MoviesContract.CastEntry.COLUMN_CAST_PROFILE, castProfilePic);
                        contentValues.put(MoviesContract.CastEntry.COLUMN_CAST_NAME, castActorNm);
                        contentValues.put(MoviesContract.CastEntry.COLUMN_CAST_SUBTITLE, castCharNm);
                        getContentResolver().insert(MoviesContract.CastEntry.CAST_CONTENT_URI, contentValues);
                    }
                }
                break;

            case ID_REVIEW_CURSOR_LOADER:
                String reviewsAuthor;
                String reviewsContent;
                if (reviewsList != null) {
                    for (int j = 0; j < reviewsList.size(); j++) {
                        reviewsAuthor = reviewsList.get(j).getReviewAuthor();
                        reviewsContent = reviewsList.get(j).getReviewContent();
                        contentValues.put(MoviesContract.ReviewsEntry.COLUMN_REVIEWS_MOVIE_ID, movieId);
                        contentValues.put(MoviesContract.ReviewsEntry.COLUMN_REVIEWS_AUTHOR, reviewsAuthor);
                        contentValues.put(MoviesContract.ReviewsEntry.COLUMN_REVIEWS_CONTENT, reviewsContent);
                        getContentResolver().insert(MoviesContract.ReviewsEntry.REVIEWS_CONTENT_URI,
                                contentValues);
                    }
                }
                break;
        }
    }

    private void deleteFromTable() {
        String selection = MoviesContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selArgs = new String[]{String.valueOf(movieId)};
        getContentResolver().delete(MoviesContract.MovieEntry.MOVIES_CONTENT_URI, selection, selArgs);
        getContentResolver().delete(MoviesContract.CastEntry.CAST_CONTENT_URI, selection, selArgs);
        getContentResolver().delete(MoviesContract.ReviewsEntry.REVIEWS_CONTENT_URI, selection, selArgs);
    }
}



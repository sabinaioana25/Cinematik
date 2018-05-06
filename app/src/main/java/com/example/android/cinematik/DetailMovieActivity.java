package com.example.android.cinematik;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
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

import static com.example.android.cinematik.data.MoviesContract.MovieEntry;

public class DetailMovieActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<MovieItem> {

    private static final String TAG = DetailMovieActivity.class.getSimpleName();
    private static final int ID_LOADER_DETAIL_MOVIES = 45;
    private static final int ID_VIDEO_LOADER = 46;
    private static final int ID_MOVIE_CURSOR_LOADER = 1;
    private static final int ID_CAST_CURSOR_LOADER = 2;
    private static final int ID_REVIEW_CURSOR_LOADER = 3;

    private static final String LOG_TAG = DetailMovieActivity.class.getSimpleName();
    private static String url;
    public static int id = 0;
    public static final String KEY_STRING_MIN = "min";

    private RecyclerView castListRecyclerView;
    private LinearLayoutManager castLinearLayoutManager = new LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false);

    private RecyclerView reviewListRecyclerView;
    private LinearLayoutManager reviewLinearLayoutManager = new LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false);

    // adapters
    private CastAdapter castAdapter;
    private ReviewAdapter reviewAdapter;

    // Play Button
    Button buttonPlayTrailer;

    // add to favourites button
    Button buttonFavouriteMovies;

//    // movie projection
//    private final String[] movieProjection = new String[] {
//            MovieEntry.COLUMN_MOVIE_ID,
//            MovieEntry.COLUMN_MOVIE_BACKDROP,
//            MovieEntry.COLUMN_MOVIE_TITLE,
//            MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
//            MovieEntry.COLUMN_MOVIE_RUNTIME,
//            MovieEntry.COLUMN_MOVIE_GENRES,
//            MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
//            MovieEntry.COLUMN_MOVIE_OVERVIEW,
//            MovieEntry.COLUMN_MOVIE_DIRECTOR,
//            MovieEntry.COLUMN_MOVIE_PRODUCER,
//            MovieEntry.COLUMN_MOVIE_VIDEO_URL};
//
//    // cast projection
//    private final String[] castProjection = new String[] {
//            CastEntry.COLUMN_CAST_MOVIE_ID,
//            CastEntry.COLUMN_CAST_TYPE,
//            CastEntry.COLUMN_CAST_NAME,
//            CastEntry.COLUMN_CAST_SUBTITLE,
//            CastEntry.COLUMN_CAST_PROFILE};
//
//    // reviews projection
//    private final String[] reviewsProjection = new String[] {
//            ReviewsEntry.COLUMN_REVIEWS_MOVIE_ID,
//            ReviewsEntry.COLUMN_REVIEWS_AUTHOR,
//            ReviewsEntry.COLUMN_REVIEWS_CONTENT};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        Toolbar toolbar = findViewById(R.id.detail_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);

        buttonFavouriteMovies = findViewById(R.id.detail_activity_button_favourites);
        buttonFavouriteMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // clear state
                buttonFavouriteMovies.setSelected(false);
                buttonFavouriteMovies.setPressed(true);

                // change state
                buttonFavouriteMovies.setSelected(true);
                buttonFavouriteMovies.setPressed(true);
            }
        });

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_18dp);
        upArrow.setColorFilter(getResources().getColor(R.color.colorUpArrow), PorterDuff.Mode
                .DST_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        Intent getMovieDetails = getIntent();

        id = getMovieDetails.getExtras().getInt(MainActivity.MOVIE_ID);

        // CastAdapter
        castListRecyclerView = findViewById(R.id.detail_activity_recycler_view_cast_id);
        castListRecyclerView.setLayoutManager(castLinearLayoutManager);
        castListRecyclerView.setHasFixedSize(true);
        castAdapter = new CastAdapter(this, id);
        castListRecyclerView.setAdapter(castAdapter);

        // ReviewAdapter
        reviewListRecyclerView = findViewById(R.id.detail_activity_recycler_view_reviews_id);
        reviewListRecyclerView.setLayoutManager(reviewLinearLayoutManager);
        reviewListRecyclerView.setHasFixedSize(false);
        reviewAdapter = new ReviewAdapter(this, id);
        reviewListRecyclerView.setAdapter(reviewAdapter);

        getLoaderManager().initLoader(ID_LOADER_DETAIL_MOVIES, null, this);
    }

    @Override
    public Loader<MovieItem> onCreateLoader(int i, Bundle bundle) {
        return new DetailMovieLoader(this, id);
    }

    @Override
    public void onLoadFinished
            (Loader<MovieItem> loader, MovieItem data) {

        // backdropImage
        ImageView backdropPhoto = findViewById(R.id.detail_activity_backdrop_image_id);

        Picasso.with(this)
                .load(data.getBackdropPath())
                .into(backdropPhoto);

        // title
        TextView title = findViewById(R.id.detail_activity_title_movie_id);
        title.setText(data.getTitle());

        // releaseDate
        TextView releaseDate = findViewById(R.id.detail_activity_release_date_id);
        releaseDate.setText(data.getReleaseDate().substring(0, 4));

        // voteAverage
        TextView voteAverage = findViewById(R.id.detail_activity_imdb_rating_id);
        voteAverage.setText(data.getVoteAverage());

        // overview
        TextView overview = findViewById(R.id.detail_activity_overview_text_id);
        overview.setText(data.getOverview());

        // genres
        TextView genresTextView = findViewById(R.id.detail_activity_genres_id);
        StringBuilder genreStringBuilder = new StringBuilder();
        String genres = null;
        if (data.getGenres().size() != 0) {
            for (String string : data.getGenres()) {
                genreStringBuilder.append(string).append(", ");
            }
            genres = genreStringBuilder.toString();
            genres = genres.substring(0, genres.length() - 2);
            genresTextView.setText(genres);
        }

        // runtime
        TextView runtime = findViewById(R.id.detail_activity_runtime_id);
        StringBuilder minStringBuilder = new StringBuilder();
        runtime.setText(minStringBuilder.append(data.getRuntime()).append(" ").append
                (KEY_STRING_MIN).toString());

        // crew director
        TextView director = findViewById(R.id.detail_activity_director_value_id);
        director.setText(data.getMovieDirector());

        // crew producer
        TextView producer = findViewById(R.id.detail_activity_producer_value_id);
        producer.setText(data.getMovieProducer());

        // cast
        castAdapter.addList(data.getMovieCastMembers());

        // reviews
        if (data.getMovieReviewItems() != null) {
            reviewAdapter.addList(data.getMovieReviewItems());
        } else {
            reviewListRecyclerView.setVisibility(View.GONE);
        }

        final String movieTrailerKey = data.getVideoId();

        buttonPlayTrailer = findViewById(R.id.detail_activity_button_play_trailer);
        buttonPlayTrailer.getBackground().setAlpha(128);
        buttonPlayTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(NetworkUtils
                        .buildUrlVideo(movieTrailerKey)));
                startActivity(goToTrailer);
            }
        });

        ContentValues values = new ContentValues();

        values.put(MovieEntry.COLUMN_MOVIE_ID, data.getMovieId());
        values.put(MovieEntry.COLUMN_MOVIE_BACKDROP, id);
        values.put(MovieEntry.COLUMN_MOVIE_TITLE, data.getTitle());
        values.put(MovieEntry.COLUMN_MOVIE_RELEASE_DATE, data.getReleaseDate());
        values.put(MovieEntry.COLUMN_MOVIE_RUNTIME, data.getRuntime());
        values.put(MovieEntry.COLUMN_MOVIE_GENRES, genres);
        values.put(MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, data.getVoteAverage());
        values.put(MovieEntry.COLUMN_MOVIE_OVERVIEW, data.getOverview());
        values.put(MovieEntry.COLUMN_MOVIE_DIRECTOR, data.getMovieDirector());
        values.put(MovieEntry.COLUMN_MOVIE_PRODUCER, data.getMovieProducer());
        values.put(MovieEntry.COLUMN_MOVIE_VIDEO_URL, movieTrailerKey);

        getContentResolver().insert(MovieEntry.MOVIES_CONTENT_URI, values);
    }

    @Override
    public void onLoaderReset(Loader<MovieItem> loader) {
    }

}

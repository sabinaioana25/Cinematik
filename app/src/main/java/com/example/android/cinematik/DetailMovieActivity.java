package com.example.android.cinematik;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class DetailMovieActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<MovieItem> {

    private static final String TAG = DetailMovieActivity.class.getSimpleName();
    private static final int ID_LOADER_DETAIL_MOVIES = 45;
    public static int id = 0;
    public static final String KEY_STRING_MIN = "min";

    private RecyclerView castListRecyclerView;
    private LinearLayoutManager castLinearLayoutManager = new LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false);

    private RecyclerView reviewListRecyclerView;
    private LinearLayoutManager reviewLinearLayoutManager = new LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false);

    Context context;
    // adapters
    private CastAdapter castAdapter;
    private ReviewAdapter reviewAdapter;

    // Play Button
    Button buttonPlayTrailer;
    String videoKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        Intent getMovieDetails = getIntent();
        id = getMovieDetails.getExtras().getInt(MovieActivity.MOVIE_ID);

        // CastAdapter
        castListRecyclerView = (RecyclerView) findViewById(R.id.id_recycler_view_cast);
        castListRecyclerView.setLayoutManager(castLinearLayoutManager);
        castListRecyclerView.setHasFixedSize(true);
        castAdapter = new CastAdapter(this, id);
        castListRecyclerView.setAdapter(castAdapter);
//
//        RecyclerViewItemDecorator itemDecoratorCast = new
//                RecyclerViewItemDecorator(context, R.dimen.item_offset);
//        castListRecyclerView.addItemDecoration(itemDecoratorCast);

        // ReviewAdapter
        reviewListRecyclerView = (RecyclerView) findViewById(R.id.id_recycler_view_reviews);
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
        ImageView backdropPhoto = (ImageView) findViewById(R.id.backdrop_image);

        Picasso.with(this)
                .load(data.getBackdropPath())
                .into(backdropPhoto);

        // title
        TextView title = (TextView) findViewById(R.id.id_title_movie);
        title.setText(data.getTitle());

        // releaseDate
        TextView releaseDate = (TextView) findViewById(R.id.id_release_date);
        releaseDate.setText(data.getReleaseDate().substring(0, 4));

        // voteAverage
        TextView voteAverage = (TextView) findViewById(R.id.id_imdb_rating);
        voteAverage.setText(data.getVoteAverage());

        // overview
        TextView overview = (TextView) findViewById(R.id.id_overview_text);
        overview.setText(data.getOverview());

        // genres
        TextView genresTextView = (TextView) findViewById(R.id.id_genres);
        StringBuilder genreStringBuilder = new StringBuilder();
        if (data.getGenres().size() != 0) {
            for (String string : data.getGenres()) {
                genreStringBuilder.append(string).append(", ");
            }
            String genres = genreStringBuilder.toString();
            genres = genres.substring(0, genres.length() - 2);
            genresTextView.setText(genres);
        }

        // runtime
        TextView runtime = (TextView) findViewById(R.id.id_runtime);
        StringBuilder minStringBuilder = new StringBuilder();
        runtime.setText(minStringBuilder.append(data.getRuntime()).append(" ").append
                (KEY_STRING_MIN).toString());

        // crew director
        TextView director = (TextView) findViewById(R.id.id_director_value);
        director.setText(data.getMovieDirector());

        // crew producer
        TextView producer = (TextView) findViewById(R.id.id_producer_value);
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

        buttonPlayTrailer = findViewById(R.id.button_play_trailer);
        buttonPlayTrailer.getBackground().setAlpha(128);
        buttonPlayTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(NetworkUtils
                        .buildUrlVideo(movieTrailerKey)));
                startActivity(goToTrailer);
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<MovieItem> loader) {

    }

    public void playTrailer(View view) {}
}

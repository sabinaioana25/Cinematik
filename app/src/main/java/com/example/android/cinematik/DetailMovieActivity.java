package com.example.android.cinematik;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.android.cinematik.Adapters.CastAdapter;
import com.example.android.cinematik.loaders.DetailMovieLoader;
import com.example.android.cinematik.pojos.MovieItem;

public class DetailMovieActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<MovieItem> {

    private static final String TAG = DetailMovieActivity.class.getSimpleName();
    private static final int ID_LOADER_DETAIL_MOVIES = 45;
    public static int id = 0;
    public static final String KEY_STRING_MIN = "min";
    private RecyclerView castListRecyclerView;
    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false);

    // adapter
    private CastAdapter castAdapter;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        Intent getMovieDetails = getIntent();
        id = getMovieDetails.getExtras().getInt(MovieActivity.MOVIE_ID);

//        castListRecyclerView = (RecyclerView) findViewById(R.id.id_recycler_view_cast);
//        castListRecyclerView.setLayoutManager(linearLayoutManager);
//        castListRecyclerView.setHasFixedSize(true);
//        castAdapter = new CastAdapter(context, id);
//        castListRecyclerView.setAdapter(castAdapter);

        getLoaderManager().initLoader(ID_LOADER_DETAIL_MOVIES, null, this);


    }

    @Override
    public Loader<MovieItem> onCreateLoader(int i, Bundle bundle) {
        return new DetailMovieLoader(this, id);

    }

    @Override
    public void onLoadFinished(Loader<MovieItem> loader, MovieItem movieItem) {

        // title
        TextView title = (TextView) findViewById(R.id.id_title_movie);
        title.setText(movieItem.getTitle());

        // releaseDate
        TextView releaseDate = (TextView) findViewById(R.id.id_title_release_date);
        releaseDate.setText(movieItem.getReleaseDate());

        // voteAverage
        TextView voteAverage = (TextView) findViewById(R.id.id_imdb_rating);
        voteAverage.setText(movieItem.getVoteAverage());

        // overview
        TextView overview = (TextView) findViewById(R.id.id_overview_text);
        overview.setText(movieItem.getOverview());

        // genres
        TextView genresTextView = (TextView) findViewById(R.id.id_genres);
        StringBuilder genreStringBuilder = new StringBuilder();
        if (movieItem.getGenres() != null) {
            for (String string : movieItem.getGenres()) {
                genreStringBuilder.append(string).append(", ");
            }
            String genres = genreStringBuilder.toString();
            genres = genres.substring(0, genres.length() - 2);
            genresTextView.setText(genres);
        }

        // runtime
        TextView runtime = (TextView) findViewById(R.id.id_runtime);
        StringBuilder minStringBuilder = new StringBuilder();
        runtime.setText(minStringBuilder.append(movieItem.getRuntime()).append(" ").append
                (KEY_STRING_MIN).toString());

        // crew director
        TextView director = (TextView) findViewById(R.id.id_director_value);
        director.setText(movieItem.getMovieDirector());

        // crew producer
        TextView producer = (TextView) findViewById(R.id.id_producer_value);
        producer.setText(movieItem.getMovieProducer());
    }

    @Override
    public void onLoaderReset(Loader<MovieItem> loader) {

    }

}

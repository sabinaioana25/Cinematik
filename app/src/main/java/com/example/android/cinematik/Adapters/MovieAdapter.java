package com.example.android.cinematik.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.cinematik.R;
import com.example.android.cinematik.pojos.MovieItem;
import com.example.android.cinematik.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.cinematik.data.MoviesContract.MovieEntry;

/**
 * Created by Sabina on 3/11/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final static String TAG = MovieAdapter.class.getSimpleName();
    private final MovieDetailClickHandler onClickHandler;
    private Context context;
    private final List<MovieItem> list = new ArrayList<>();
    private Cursor cursor;
    private boolean mCursor = true;


    public MovieAdapter(Context context, MovieDetailClickHandler onClickHandler) {
        this.context = context;
        this.onClickHandler = onClickHandler;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        //noinspection unchecked
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_movie,
                null);
        return new MovieViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Log.e(TAG, "cursor " + cursor);
        String imgUrl = null;
        if (mCursor) {

            this.cursor.moveToPosition(position);
            Log.e(TAG, "position " + position);
            imgUrl = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_POSTER));
            Log.e(TAG, "Cursor image eee " + imgUrl);
            Picasso.with(context)
                    .load(imgUrl)
                    .into(holder.posterImageView);
        } else if (!mCursor) {
            imgUrl = NetworkUtils.buildUrlImage(list.get(position).getPoster()
                    .substring(1), NetworkUtils.URL_POSTER_SIZE_VALUE);}
        Picasso.with(context)
                .load(imgUrl)
                .into(holder.posterImageView);
    }

    @Override
    public int getItemCount() {
        if (mCursor) {
            if (cursor != null) {
                return cursor.getCount();
            }
        } else {
            return list.size();
        }
        return 0;
    }

    public void InsertList(Object movies) {
        list.clear();
        if (movies != null) {
            if (movies instanceof Cursor) {
                mCursor = true;
                this.cursor = (Cursor) movies;
            } else {
                mCursor = false;
                //noinspection unchecked
                this.list.addAll((List<MovieItem>) movies);
            }
        }
        notifyDataSetChanged();
    }

    public void deleteItemsInList() {
        list.clear();
        notifyDataSetChanged();
    }

    public interface MovieDetailClickHandler {
        void onSelectedItem(int id);
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private final ImageView posterImageView;
        MovieViewHolder(View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.grid_view_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id;
            if (mCursor) {
                cursor.moveToPosition(getAdapterPosition());
                id = cursor.getInt(cursor.getColumnIndex(MovieEntry
                        .COLUMN_MOVIE_ID));
            } else {
                id = list.get(getAdapterPosition()).getMovieId();
            }
            onClickHandler.onSelectedItem(id);
        }
    }
}

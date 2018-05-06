package com.example.android.cinematik.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.cinematik.Interfaces.MovieDetailClickHandler;
import com.example.android.cinematik.R;
import com.example.android.cinematik.pojos.MovieItem;
import com.example.android.cinematik.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sabina on 3/11/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final static String TAG = MovieAdapter.class.getSimpleName();
    public static MovieDetailClickHandler onClickHandler;
    Context context;
    private static List<MovieItem> list = new ArrayList<>();


    public MovieAdapter(Context context, MovieDetailClickHandler onClickHandler) {
        this.context = context;
        MovieAdapter.onClickHandler = onClickHandler;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_movie,
                null);
        MovieViewHolder movieViewHolder = null;

        movieViewHolder = new MovieViewHolder(layout);
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, final int position) {

        final String imgUrl = NetworkUtils.buildUrlImage(list.get(position).getPoster()
                .substring(1), NetworkUtils.URL_POSTER_SIZE_VALUE);

        Picasso.with(context).load(imgUrl).into(holder.posterImageView);
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public void InsertList(List<MovieItem> movies) {
        list.clear();
        list.addAll(movies);
        notifyDataSetChanged();
    }

    public void deleteItemsInList() {
        list.clear();
        notifyDataSetChanged();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView posterImageView;
        MovieViewHolder(View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.grid_view_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = list.get(getAdapterPosition()).getMovieId();
            onClickHandler.onSelectedItem(id);
        }
    }
}

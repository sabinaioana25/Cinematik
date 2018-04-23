package com.example.android.cinematik.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.cinematik.R;
import com.example.android.cinematik.pojos.ReviewItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sabina on 4/12/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final static String TAG = ReviewAdapter.class.getSimpleName();
    private List<ReviewItem> reviewList = new ArrayList<>();
    Context context;
    public int id = 0;

    public ReviewAdapter(Context context, int id) {
        this.context = context;
        this.id = id;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .grid_item_reviews, parent, false);
        ReviewViewHolder reviewViewHolder = null;
        reviewViewHolder = new ReviewViewHolder(layout);
        return reviewViewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {

        holder.reviewAuthor.setText(reviewList.get(position).getReviewAuthor());
        holder.reviewContent.setText(reviewList.get(position).getReviewContent());
    }

    @Override
    public int getItemCount() {
        if (reviewList != null) {
            return reviewList.size();
        }
        return 0;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView reviewAuthor;
        private TextView reviewContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            reviewAuthor = itemView.findViewById(R.id.review_author_id);
            reviewContent = itemView.findViewById(R.id.review_content_id);
        }
    }

    public void addList(List<ReviewItem> reviewItems) {
        this.reviewList = reviewItems;
        notifyDataSetChanged();
    }
}

package com.example.android.cinematik.pojos;

/**
 * Created by Sabina on 4/7/2018.
 */

public class ReviewItem {

    private String reviewAuthor;
    private String reviewContent;

    public ReviewItem(String reviewAuthor, String reviewContent) {
        this.reviewAuthor = reviewAuthor;
        this.reviewContent = reviewContent;
    }

    public String getReviewAuthor() { return reviewAuthor; }

    public String getReviewContent() { return reviewContent; }
}

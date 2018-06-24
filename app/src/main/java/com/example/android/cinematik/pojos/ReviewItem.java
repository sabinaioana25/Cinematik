package com.example.android.cinematik.pojos;

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

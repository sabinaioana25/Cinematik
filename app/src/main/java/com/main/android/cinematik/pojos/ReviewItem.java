package com.main.android.cinematik.pojos;

@SuppressWarnings("CanBeFinal")
public class ReviewItem {

    private final String reviewAuthor;
    private final String reviewContent;

    public ReviewItem(String reviewAuthor, String reviewContent) {
        this.reviewAuthor = reviewAuthor;
        this.reviewContent = reviewContent;
    }

    public String getReviewAuthor() { return reviewAuthor; }

    public String getReviewContent() { return reviewContent; }
}

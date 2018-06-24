package com.example.android.cinematik.pojos;

import java.util.List;

public class MovieItem {

    // pojos
    private String poster;
    private int movieId;
    private String backdropPath;
    private String trailerImagePath;
    private String title;
    private String releaseDate;
    private List<String> genres;
    private String voteAverage;
    private String overview;
    private String runtime;
    private List<CastMember> movieCastMembers;
    private String movieDirector;
    private String movieProducer;
    private List<ReviewItem> movieReviewItems;
    private String videoId;
    private String videoIdTwo;

    // constructor
    public MovieItem(String poster,
                     int movieId,
                     String backdropPath,
                     String trailerImagePath,
                     String title,
                     String releaseDate,
                     List<String> genres,
                     String voteAverage,
                     String overview,
                     String runtime,
                     List<CastMember> movieCastMembers,
                     String movieDirector,
                     String movieProducer,
                     List<ReviewItem> movieReviewItems,
                     String videoId,
                     String videoIdTwo) {

        this.poster = poster;
        this.movieId = movieId;
        this.backdropPath = backdropPath;
        this.trailerImagePath = trailerImagePath;
        this.title = title;
        this.releaseDate = releaseDate;
        this.genres = genres;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.runtime = runtime;
        this.movieCastMembers = movieCastMembers;
        this.movieDirector = movieDirector;
        this.movieProducer = movieProducer;
        this.movieReviewItems = movieReviewItems;
        this.videoId = videoId;
        this.videoIdTwo = videoIdTwo;
    }

    // getter methods
    public String getPoster() {
        return poster;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getBackdropPath() { return backdropPath; }

    public String getTrailerImagePath() { return trailerImagePath; }

    public String getTitle() { return title; }

    public String getReleaseDate() { return releaseDate; }

    public List<String> getGenres() { return genres; }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public String getRuntime() { return runtime; }

    public List<CastMember> getMovieCastMembers() { return movieCastMembers; }

    public String getMovieDirector() { return movieDirector; }

    public String getMovieProducer() { return movieProducer; }

    public List<ReviewItem> getMovieReviewItems() { return movieReviewItems; }

    public String getVideoId() { return videoId; }

    public String getVideoIdTwo() { return videoIdTwo; }

}

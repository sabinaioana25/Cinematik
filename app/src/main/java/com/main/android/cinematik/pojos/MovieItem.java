package com.main.android.cinematik.pojos;

import java.util.List;

@SuppressWarnings("CanBeFinal")
public class MovieItem {

    // pojos
    private final String poster;
    private final int movieId;
    private final String backdropPath;
    private final String trailerImagePath;
    private final String title;
    private final String releaseDate;
    private final List<String> genres;
    private final String voteAverage;
    private final String overview;
    private final String runtime;
    private final List<CastMember> movieCastMembers;
    private final String movieDirector;
    private final String movieProducer;
    private final List<ReviewItem> movieReviewItems;
    private final String videoId;
    private final String videoIdTwo;

    // constructor
    @SuppressWarnings("SameParameterValue")
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

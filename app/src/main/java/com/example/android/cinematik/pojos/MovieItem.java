package com.example.android.cinematik.pojos;

import java.util.List;

/**
 * Created by Sabina on 3/19/2018.
 */

public class MovieItem {

    // pojos
    private String poster;
    private int movieId;
    private String backdropPath;
    private String title;
    private String releaseDate;
    private List<String> genres;
    private int budget;
    private String voteAverage;
    private String overview;
    private int revenue;
    private String runtime;
    private List<CastMember> movieCastMembers;
    private String movieDirector;
    private String movieProducer;
    private List<CastMember> movieCrewMembers;
    private List<ReviewItem> movieReviewItems;
    private String videoId;

    // constructor
    public MovieItem(String poster,
                     int movieId,
                     String backdropPath,
                     String title,
                     String releaseDate,
                     List<String> genres,
                     int budget,
                     String voteAverage,
                     String overview,
                     int revenue,
                     String runtime,
                     List<CastMember> movieCastMembers,
                     String movieDirector,
                     String movieProducer,
                     List<CastMember> movieCrewMembers,
                     List<ReviewItem> movieReviewItems,
                     String videoId) {
        this.poster = poster;
        this.movieId = movieId;
        this.backdropPath = backdropPath;
        this.title = title;
        this.releaseDate = releaseDate;
        this.genres = genres;
        this.budget = budget;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.revenue = revenue;
        this.runtime = runtime;
        this.movieCastMembers = movieCastMembers;
        this.movieDirector = movieDirector;
        this.movieProducer = movieProducer;
        this.movieCrewMembers = movieCrewMembers;
        this.movieReviewItems = movieReviewItems;
        this.videoId = videoId;
    }

    // getter methods
    public String getPoster() {
        return poster;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getBackdropPath() { return backdropPath; }

    public String getTitle() { return title; }

    public String getReleaseDate() { return releaseDate; }

    public List<String> getGenres() { return genres; }

    public int getBudget() { return budget; }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public int getRevenue() {
        return revenue;
    }

    public String getRuntime() { return runtime; }

    public List<CastMember> getMovieCastMembers() { return movieCastMembers; }

    public String getMovieDirector() { return movieDirector; }

    public String getMovieProducer() { return movieProducer; }

    public List<CastMember> getMovieCrewMembers() { return movieCrewMembers; }

    public List<ReviewItem> getMovieReviewItems() { return movieReviewItems; }

    public String getVideoId() { return videoId; }

}

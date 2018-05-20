package com.example.android.cinematik.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesContract {
    public static final String MOVIES_CONTENT_SCHEME = "content://";
    public static final String MOVIES_CONTENT_AUTHORTY = "com.example.android.cinematik";
    public static final String MOVIES_PATH = "movies";
    public static final String MOVIES_PATH_CAST = "cast";
    public static final String MOVIES_PATH_REVIEWS = "reviews";
    public static final String MOVIES_CONTENT_LIST_TYPE = "";
    public static final String MOVIES_CONTENT_ITEM_TYPE = "";
    public static final Uri MOVIES_BASE_CONTENT_URI = Uri.parse(MOVIES_CONTENT_SCHEME + MOVIES_CONTENT_AUTHORTY);

    public static final class MovieEntry implements BaseColumns {
        public static final Uri MOVIES_CONTENT_URI = MOVIES_BASE_CONTENT_URI.buildUpon()
                .appendPath(MOVIES_PATH)
                .build();

        public static final String TABLE_NAME = "favourites";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_BACKDROP = "backdrop";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_MOVIE_GENRES = "genres";
        public static final String COLUMN_MOVIE_RUNTIME = "runtime";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_MOVIE_DIRECTOR = "director";
        public static final String COLUMN_MOVIE_PRODUCER = "producer";
        public static final String COLUMN_MOVIE_VIDEO_URL = "videoKey";
        public static final String COLUMN_MOVIE_POSTER = "poster";
    }

    public static final class CastEntry implements BaseColumns {
        public static final Uri CAST_CONTENT_URI = MOVIES_BASE_CONTENT_URI.buildUpon()
                .appendPath(MOVIES_PATH_CAST)
                .build();

        public static final String CAST_TABLE_NAME = "cast";
        public static final String COLUMN_CAST_MOVIE_ID = "movieId";
        public static final String COLUMN_CAST_PROFILE = "profile";
        public static final String COLUMN_CAST_NAME = "name";
        public static final String COLUMN_CAST_SUBTITLE = "subtitle";

    }

    public static final class ReviewsEntry implements BaseColumns {
        public static final Uri REVIEWS_CONTENT_URI = MOVIES_BASE_CONTENT_URI.buildUpon()
                .appendPath(MOVIES_PATH_REVIEWS)
                .build();

        public static final String REVIEWS_TABLE_NAME = "reviews";
        public static final String COLUMN_REVIEWS_MOVIE_ID = "movieId";
        public static final String COLUMN_REVIEWS_AUTHOR = "author";
        public static final String COLUMN_REVIEWS_CONTENT = "content";
    }

}

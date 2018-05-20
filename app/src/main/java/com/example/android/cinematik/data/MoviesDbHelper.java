package com.example.android.cinematik.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.cinematik.data.MoviesContract.CastEntry;
import static com.example.android.cinematik.data.MoviesContract.MovieEntry;
import static com.example.android.cinematik.data.MoviesContract.ReviewsEntry;

public class MoviesDbHelper extends SQLiteOpenHelper  {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIES_TABLE =
                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                        MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieEntry.COLUMN_MOVIE_ID + " INTEGER, " +
                        MovieEntry.COLUMN_MOVIE_BACKDROP + " TEXT, " +
                        MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT, " +
                        MovieEntry.COLUMN_MOVIE_RUNTIME + " TEXT, " +
                        MovieEntry.COLUMN_MOVIE_GENRES + " TEXT, " +
                        MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE + " TEXT, " +
                        MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT, " +
                        MovieEntry.COLUMN_MOVIE_DIRECTOR + " TEXT, " +
                        MovieEntry.COLUMN_MOVIE_PRODUCER + " TEXT, " +
                        MovieEntry.COLUMN_MOVIE_VIDEO_URL + " TEXT, " +
                        MovieEntry.COLUMN_MOVIE_POSTER + " TEXT);";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);

        final String SQL_CREATE_CAST_TABLE =
                "CREATE TABLE " + CastEntry.CAST_TABLE_NAME + " (" +
                        CastEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        CastEntry.COLUMN_CAST_MOVIE_ID + " INTEGER, " +
                        CastEntry.COLUMN_CAST_PROFILE + " TEXT, " +
                        CastEntry.COLUMN_CAST_NAME + " TEXT, " +
                        CastEntry.COLUMN_CAST_SUBTITLE + " TEXT);";
        sqLiteDatabase.execSQL(SQL_CREATE_CAST_TABLE);

        final String SQL_CREATE_REVIEWS_TABLE =
                "CREATE TABLE " + ReviewsEntry.REVIEWS_TABLE_NAME + " (" +
                        ReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ReviewsEntry.COLUMN_REVIEWS_MOVIE_ID + " INTEGER, " +
                        ReviewsEntry.COLUMN_REVIEWS_AUTHOR + " TEXT, " +
                        ReviewsEntry.COLUMN_REVIEWS_CONTENT + " TEXT);";
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

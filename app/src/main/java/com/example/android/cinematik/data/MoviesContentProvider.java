package com.example.android.cinematik.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


import com.example.android.cinematik.data.MoviesContract.CastEntry;

import static com.example.android.cinematik.data.MoviesContract.MOVIES_CONTENT_AUTHORTY;
import static com.example.android.cinematik.data.MoviesContract.MOVIES_PATH;
import static com.example.android.cinematik.data.MoviesContract.MOVIES_PATH_CAST;
import static com.example.android.cinematik.data.MoviesContract.MOVIES_PATH_REVIEWS;
import static com.example.android.cinematik.data.MoviesContract.MovieEntry;
import static com.example.android.cinematik.data.MoviesContract.ReviewsEntry;

public class MoviesContentProvider extends ContentProvider {

    private static final int CODE_MOVIES = 200;
    private static final int CODE_MOVIES_ID = 201;
    private static final int CODE_CAST = 300;
    private static final int CODE_REVIEW = 400;
    private static final String LOG_TAG = MoviesContentProvider.class.getSimpleName();
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private MoviesDbHelper dbHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MOVIES_CONTENT_AUTHORTY, MOVIES_PATH,
                CODE_MOVIES);
        uriMatcher.addURI(MOVIES_CONTENT_AUTHORTY, MOVIES_PATH +
                "/#", CODE_MOVIES_ID);
        uriMatcher.addURI(MOVIES_CONTENT_AUTHORTY,
                MOVIES_PATH_CAST, CODE_CAST);
        uriMatcher.addURI(MOVIES_CONTENT_AUTHORTY,
                MOVIES_PATH_REVIEWS, CODE_REVIEW);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIES:
                cursor = database.query(MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_MOVIES_ID:
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_CAST:
                cursor = database.query(CastEntry.CAST_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_REVIEW:
                cursor = database.query(ReviewsEntry.REVIEWS_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Could not query URI " + uri);
        }
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case CODE_MOVIES:
                return MoviesContract.MOVIES_CONTENT_LIST_TYPE;

            case CODE_MOVIES_ID:
                return MoviesContract.MOVIES_CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long id;

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIES:
                id = database.insert(MovieEntry.TABLE_NAME, null, contentValues);
                if (id == -1) {
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                }
                break;

            case CODE_CAST:
                id = database.insert(CastEntry.CAST_TABLE_NAME, null, contentValues);
                if (id == -1) {
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                }
                break;

            case CODE_REVIEW:
                id = database.insert(ReviewsEntry.REVIEWS_TABLE_NAME, null, contentValues);
                if (id == -1) {
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                }
                break;

            default:
                throw new IllegalArgumentException("Insert is not possible for " + uri);
        }
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsDeleted;

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIES:
                rowsDeleted = database.delete(MovieEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;

            case CODE_MOVIES_ID:
                rowsDeleted = database.delete(MovieEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;

            case CODE_CAST:
                rowsDeleted = database.delete(CastEntry.CAST_TABLE_NAME,
                        selection, selectionArgs);
                break;
//
//            case CODE_REVIEW:
//                rowsDeleted = database.delete(ReviewsEntry.REVIEWS_TABLE_NAME,
//                        selection, selectionArgs);
//                break;

            default:
                throw new IllegalArgumentException("Delete is not possible for " + uri);
        }

        if (rowsDeleted != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String
            selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsUpdated;

        if (contentValues == null || contentValues.size() == 0) {
            return 0;
        }

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIES:
                rowsUpdated = database.update(MovieEntry.TABLE_NAME, contentValues,
                        selection, selectionArgs);
                break;

            case CODE_MOVIES_ID:
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = database.update(MovieEntry.TABLE_NAME, contentValues, selection,
                        selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Update is not possible for " + uri);
        }

        if (rowsUpdated != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public void shutdown() {
        dbHelper.close();
    }
}
package com.main.android.cinematik.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.util.Log;

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
        uriMatcher.addURI(MoviesContract.MOVIES_CONTENT_AUTHORTY, MoviesContract.MOVIES_PATH,
                CODE_MOVIES);
        uriMatcher.addURI(MoviesContract.MOVIES_CONTENT_AUTHORTY, MoviesContract.MOVIES_PATH +
                "/#", CODE_MOVIES_ID);
        uriMatcher.addURI(MoviesContract.MOVIES_CONTENT_AUTHORTY,
                MoviesContract.MOVIES_PATH_CAST, CODE_CAST);
        uriMatcher.addURI(MoviesContract.MOVIES_CONTENT_AUTHORTY,
                MoviesContract.MOVIES_PATH_REVIEWS, CODE_REVIEW);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIES:
                cursor = database.query(MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_MOVIES_ID:
                selection = MoviesContract.MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_CAST:
                cursor = database.query(MoviesContract.CastEntry.CAST_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_REVIEW:
                cursor = database.query(MoviesContract.ReviewsEntry.REVIEWS_TABLE_NAME,
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

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
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
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long id;

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIES:
                id = database.insert(MoviesContract.MovieEntry.TABLE_NAME, null, contentValues);
                if (id == -1) {
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                }
                break;

            case CODE_CAST:
                id = database.insert(MoviesContract.CastEntry.CAST_TABLE_NAME, null, contentValues);
                if (id == -1) {
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                }
                break;

            case CODE_REVIEW:
                id = database.insert(MoviesContract.ReviewsEntry.REVIEWS_TABLE_NAME, null, contentValues);
                if (id == -1) {
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                }
                break;

            default:
                throw new IllegalArgumentException("Insert is not possible for " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsDeleted;

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIES:

            case CODE_MOVIES_ID:
                rowsDeleted = database.delete(MoviesContract.MovieEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;

            case CODE_CAST:
                rowsDeleted = database.delete(MoviesContract.CastEntry.CAST_TABLE_NAME,
                        selection, selectionArgs);
                break;

            case CODE_REVIEW:
                rowsDeleted = database.delete(MoviesContract.ReviewsEntry.REVIEWS_TABLE_NAME,
                        selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Delete is not possible for " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String
            selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsUpdated;

        if (contentValues == null || contentValues.size() == 0) {
            return 0;
        }

        switch (uriMatcher.match(uri)) {
            case CODE_MOVIES:
                rowsUpdated = database.update(MoviesContract.MovieEntry.TABLE_NAME, contentValues,
                        selection, selectionArgs);
                break;

            case CODE_MOVIES_ID:
                selection = MoviesContract.MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = database.update(MoviesContract.MovieEntry.TABLE_NAME, contentValues, selection,
                        selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Update is not possible for " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public void shutdown() {
        dbHelper.close();
    }
}
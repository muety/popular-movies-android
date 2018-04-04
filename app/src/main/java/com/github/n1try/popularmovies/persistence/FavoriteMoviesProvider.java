package com.github.n1try.popularmovies.persistence;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FavoriteMoviesProvider extends ContentProvider {
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private MoviesDbHelper dbHelper;

    private static final int FAVORITE_MOVIE = 100;
    private static final int FAVORITE_MOVIE_WITH_ID = 200;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoriteMoviesContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES, FAVORITE_MOVIE);
        matcher.addURI(authority, FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES + "/#", FAVORITE_MOVIE_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        switch (uriMatcher.match(uri)) {
            case FAVORITE_MOVIE:
                return dbHelper.getReadableDatabase().query(
                        FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            case FAVORITE_MOVIE_WITH_ID:
                return dbHelper.getReadableDatabase().query(
                        FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES,
                        projection,
                        FavoriteMoviesContract.FavoriteMovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case FAVORITE_MOVIE:
                return FavoriteMoviesContract.FavoriteMovieEntry.CONTENT_DIR_TYPE;
            case FAVORITE_MOVIE_WITH_ID:
                return FavoriteMoviesContract.FavoriteMovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri resultUri;
        switch (uriMatcher.match(uri)) {
            case FAVORITE_MOVIE:
                long id = db.insert(FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES, null, values);
                if (id > 0)
                    resultUri = FavoriteMoviesContract.FavoriteMovieEntry.buildFavoriteMoviesUri(id);
                else throw new SQLException("Failed to insert row: " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return resultUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case FAVORITE_MOVIE:
                return db.delete(
                        FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES,
                        selection,
                        selectionArgs);
            case FAVORITE_MOVIE_WITH_ID:
                return db.delete(
                        FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES,
                        FavoriteMoviesContract.FavoriteMovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Not implemented.");
    }
}

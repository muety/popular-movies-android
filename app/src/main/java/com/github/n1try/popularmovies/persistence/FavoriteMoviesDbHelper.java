package com.github.n1try.popularmovies.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public FavoriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_STATEMENT = new StringBuilder()
                .append("CREATE TABLE ")
                .append(FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES)
                .append("(")
                .append(FavoriteMoviesContract.FavoriteMovieEntry._ID)
                .append(" INTEGER PRIMARY KEY, ")
                .append(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_TITLE)
                .append(" TEXT NON NULL")
                .append(")")
                .toString();
        db.execSQL(CREATE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(getClass().getSimpleName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ". OLD DATA WILL BE DESTROYED");
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITE_MOVIES + "'");
        onCreate(db);
    }
}

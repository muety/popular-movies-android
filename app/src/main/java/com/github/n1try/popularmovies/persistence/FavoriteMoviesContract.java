package com.github.n1try.popularmovies.persistence;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.github.n1try.popularmovies.model.Genre;
import com.github.n1try.popularmovies.model.Movie;
import com.github.n1try.popularmovies.serialization.GsonHolder;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.List;

public class FavoriteMoviesContract {
    public static final String CONTENT_AUTHORITY = "com.github.n1try.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class FavoriteMovieEntry implements BaseColumns {
        public static final String TABLE_FAVORITE_MOVIES = "favorite_movies";
        public static final String _ID = "_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_GENRE_IDS = "genre_ids";
        public static final String COLUMN_GENRES = "genres";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_POPULARITY = "popularity";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_FAVORITE_MOVIES).build();
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_FAVORITE_MOVIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_FAVORITE_MOVIES;

        public static Uri buildFavoriteMoviesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static ContentValues movieToContentValues(Movie movie) {
            ContentValues values = new ContentValues();
            values.put(_ID, movie.getId());
            values.put(COLUMN_TITLE, movie.getTitle());
            values.put(COLUMN_OVERVIEW, movie.getOverview());
            values.put(COLUMN_GENRE_IDS, GsonHolder.getInstance().getGson().toJson(movie.getGenreIds()));
            values.put(COLUMN_GENRES, GsonHolder.getInstance().getGson().toJson(movie.getGenres()));
            values.put(COLUMN_RELEASE_DATE, movie.getReleaseDate().getTime());
            values.put(COLUMN_POSTER_PATH, movie.getPosterPath());
            values.put(COLUMN_BACKDROP_PATH, movie.getBackdropPath());
            values.put(COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
            values.put(COLUMN_POPULARITY, movie.getPopularity());
            return values;
        }

        public static Movie movieFromCursor(Cursor cursor) {
            Movie movie = Movie.builder()
                    .id(cursor.getDouble(cursor.getColumnIndex(_ID)))
                    .title(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)))
                    .overview(cursor.getString(cursor.getColumnIndex(COLUMN_OVERVIEW)))
                    .releaseDate(new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_RELEASE_DATE))))
                    .posterPath(cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_PATH)))
                    .backdropPath(cursor.getString(cursor.getColumnIndex(COLUMN_BACKDROP_PATH)))
                    .voteAverage(cursor.getDouble(cursor.getColumnIndex(COLUMN_VOTE_AVERAGE)))
                    .popularity(cursor.getDouble(cursor.getColumnIndex(COLUMN_POPULARITY)))
                    .build();

            String serializedGenreIds = cursor.getString(cursor.getColumnIndex(COLUMN_GENRE_IDS));
            String serializedGenres = cursor.getString(cursor.getColumnIndex(COLUMN_GENRES));

            TypeToken<List<Integer>> t1 = new TypeToken<List<Integer>>() {
            };
            TypeToken<List<Genre>> t2 = new TypeToken<List<Genre>>() {
            };
            movie.setGenreIds((List<Double>) GsonHolder.getInstance().getGson().fromJson(serializedGenreIds, t1.getType()));
            movie.setGenres((List<Genre>) GsonHolder.getInstance().getGson().fromJson(serializedGenres, t2.getType()));

            return movie;
        }
    }
}

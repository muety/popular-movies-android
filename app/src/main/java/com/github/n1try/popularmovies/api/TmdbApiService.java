/*
 * Copyright (C) 2018 Ferdinand Mütsch
 */

package com.github.n1try.popularmovies.api;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.github.n1try.popularmovies.BuildConfig;
import com.github.n1try.popularmovies.model.Genre;
import com.github.n1try.popularmovies.model.Movie;
import com.github.n1try.popularmovies.model.MovieReview;
import com.github.n1try.popularmovies.model.MovieTrailer;
import com.github.n1try.popularmovies.model.TmdbGenresResult;
import com.github.n1try.popularmovies.model.TmdbMovieReviewsResult;
import com.github.n1try.popularmovies.model.TmdbMovieVideosResult;
import com.github.n1try.popularmovies.model.TmdbMoviesResult;
import com.github.n1try.popularmovies.serialization.GsonHolder;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TmdbApiService {
    public static final String API_BASE_URL = "https://api.themoviedb.org/3";
    public static final String API_IMAGE_SM_BASE_URL = "http://image.tmdb.org/t/p/w185";
    public static final String API_IMAGE_LG_BASE_URL = "http://image.tmdb.org/t/p/w780";
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;
    private static TmdbApiService ourInstance;
    private OkHttpClient httpClient;
    private Gson gson;

    private Map<Double, Genre> genres;

    public static TmdbApiService getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new TmdbApiService(context);
        }
        return ourInstance;
    }

    private TmdbApiService(Context context) {
        httpClient = new OkHttpClient.Builder()
                .cache(new Cache(context.getCacheDir(), 1024 * 1014 * 10))
                .build();
        gson = GsonHolder.getInstance().getGson();
        genres = getGenreMap();
    }

    /**
     * Fetches a list of all available movie genres from TMDB.
     */
    public List<Genre> getGenres() {
        Uri uri = Uri.parse(API_BASE_URL + "/genre/movie/list").buildUpon().appendQueryParameter("api_key", API_KEY).build();
        Request request = new Request.Builder().url(uri.toString()).build();

        try {
            Response response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException(response.message());
            ResponseBody body = response.body();
            List<Genre> genres = gson.fromJson(body.string(), TmdbGenresResult.class).getGenres();
            body.close();
            return genres;
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Could not fetch or deserialize genres.\n" + e.getMessage());
        }

        return new ArrayList<>();
    }

    /**
     * Fetches all available movie genres from TMBD as a map of their id's to the respective Genre objects for faster lookup.
     */
    public Map<Double, Genre> getGenreMap() {
        Map genreMap = new HashMap();
        for (Genre g : getGenres()) {
            genreMap.put(g.getId(), g);
        }
        return genreMap;
    }

    /**
     * Fetches a list of popular movies from TMDB.
     * @param page Page number to fetch (one page consists of 20 items)
     */
    public List<Movie> getPopularMovies(int page) {
        return fetchMovieList(Uri.parse(API_BASE_URL + "/movie/popular").buildUpon().appendQueryParameter("page", String.valueOf(page)).build());
    }

    /**
     * Fetches a list of highest rated movies from TMDB.
     * @param page Page number to fetch (one page consists of 20 items)
     */
    public List<Movie> getTopRatedMovies(int page) {
        return fetchMovieList(Uri.parse(API_BASE_URL + "/movie/top_rated").buildUpon().appendQueryParameter("page", String.valueOf(page)).build());
    }

    public List<MovieTrailer> getVideosByMovie(double movieId) {
        Uri uri = Uri.parse(API_BASE_URL + "/movie/" + String.valueOf(movieId) + "/videos");
        uri = uri.buildUpon().appendQueryParameter("api_key", API_KEY).build();
        Request request = new Request.Builder().url(uri.toString()).build();

        try {
            Response response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException(response.message());
            ResponseBody body = response.body();
            List<MovieTrailer> trailers = gson.fromJson(body.string(), TmdbMovieVideosResult.class).getResults();
            for (MovieTrailer t : trailers) {
                if (!TextUtils.equals(t.getSite().toLowerCase(), "YouTube".toLowerCase())) {
                    trailers.remove(t);
                }
            }
            body.close();
            return trailers;
        } catch (IOException e) {
            Log.w(getClass().getSimpleName(), "Could not fetch trailers.\n" + e.getMessage());
        }

        return new ArrayList<>();
    }

    public List<MovieReview> getReviewsByMovie(double movieId) {
        Uri uri = Uri.parse(API_BASE_URL + "/movie/" + String.valueOf(movieId) + "/reviews");
        uri = uri.buildUpon().appendQueryParameter("api_key", API_KEY).build();
        Request request = new Request.Builder().url(uri.toString()).build();

        try {
            Response response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException(response.message());
            ResponseBody body = response.body();
            List<MovieReview> reviews = gson.fromJson(body.string(), TmdbMovieReviewsResult.class).getResults();
            body.close();
            return reviews;
        } catch (IOException e) {
            Log.w(getClass().getSimpleName(), "Could not fetch reviews.\n" + e.getMessage());
        }

        return new ArrayList<>();
    }

    private List<Movie> fetchMovieList(Uri uri) {
            Uri fetchUri = uri.buildUpon().appendQueryParameter("api_key", API_KEY).build();
            Request request = new Request.Builder().url(fetchUri.toString()).build();

            try {
                Response response = httpClient.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException(response.message());
                ResponseBody body = response.body();
                List<Movie> movies = gson.fromJson(body.string(), TmdbMoviesResult.class).getResults();
                for (Movie m : movies) {
                    m.enrich(genres);
                    if (!m.getPosterPath().startsWith("http://"))
                        m.setPosterPath(API_IMAGE_SM_BASE_URL + m.getPosterPath());
                    if (!m.getBackdropPath().startsWith("http://"))
                        m.setBackdropPath(API_IMAGE_SM_BASE_URL + m.getBackdropPath());
                }
                body.close();
                return movies;
            } catch (IOException e) {
                Log.w(getClass().getSimpleName(), "Could not fetch movies.\n" + e.getMessage());
            }

            return new ArrayList<>();
    }

}

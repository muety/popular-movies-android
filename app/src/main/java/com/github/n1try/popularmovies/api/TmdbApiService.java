package com.github.n1try.popularmovies.api;

import android.net.Uri;
import android.util.Log;

import com.github.n1try.popularmovies.BuildConfig;
import com.github.n1try.popularmovies.deserialization.TmdbGenresResultDeserializer;
import com.github.n1try.popularmovies.deserialization.TmdbMovieDeserializer;
import com.github.n1try.popularmovies.deserialization.TmdbMoviesResultDeserializer;
import com.github.n1try.popularmovies.model.Genre;
import com.github.n1try.popularmovies.model.Movie;
import com.github.n1try.popularmovies.model.TmdbGenresResult;
import com.github.n1try.popularmovies.model.TmdbMoviesResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TmdbApiService {
    public static final String API_BASE_URL = "https://api.themoviedb.org/3";
    public static final String API_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;
    private static final TmdbApiService ourInstance = new TmdbApiService();
    private OkHttpClient httpClient;
    private Gson gson;

    public static TmdbApiService getInstance() {
        return ourInstance;
    }

    private TmdbApiService() {
        httpClient = new OkHttpClient();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Movie.class, new TmdbMovieDeserializer());
        gsonBuilder.registerTypeAdapter(TmdbMoviesResult.class, new TmdbMoviesResultDeserializer());
        gsonBuilder.registerTypeAdapter(TmdbGenresResult.class, new TmdbGenresResultDeserializer());
        gson = gsonBuilder.create();

    }

    public List<Genre> getGenres() {
        Uri uri = Uri.parse(API_BASE_URL + "/genre/movie/list").buildUpon().appendQueryParameter("api_key", API_KEY).build();
        Request request = new Request.Builder().url(uri.toString()).build();
        Response response;
        String body;

        try {
            response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException(response.message());
            body = response.body().string();
            return gson.fromJson(body, TmdbGenresResult.class).getGenres();
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Could not fetch or deserialize genres.\n" + e.getMessage());
        }

        return new ArrayList<>();
    }

    public Map<Double, Genre> getGenreMap() {
        Map genreMap = new HashMap();
        for (Genre g : getGenres()) {
            genreMap.put(g.getId(), g);
        }
        return genreMap;
    }

    public List<Movie> getPopularMovies() {
        Map<Double, Genre> genres = getGenreMap();

        Uri uri = Uri.parse(API_BASE_URL + "/movie/popular").buildUpon().appendQueryParameter("api_key", API_KEY).build();
        Request request = new Request.Builder().url(uri.toString()).build();
        Response response;
        String body;

        try {
            response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException(response.message());
            body = response.body().string();
            List<Movie> movies = gson.fromJson(body, TmdbMoviesResult.class).getResults();
            for (Movie m : movies) {
                m.enrich(genres);
            }
            return movies;
        } catch (IOException e) {
            Log.w(getClass().getSimpleName(), "Could not fetch popular movies.\n" + e.getMessage());
        }

        return new ArrayList<>();
    }

    public static Date parseDate(String dateString) {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return parser.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }
}
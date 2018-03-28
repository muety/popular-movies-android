package com.github.n1try.popularmovies.service;

import android.net.Uri;
import android.util.Log;

import com.github.n1try.popularmovies.BuildConfig;
import com.github.n1try.popularmovies.model.Genre;
import com.github.n1try.popularmovies.model.Movie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
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
    private static final String API_BASE_URL = "https://api.themoviedb.org/3";
    private static final String API_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;
    private static final TmdbApiService ourInstance = new TmdbApiService();
    private OkHttpClient httpClient;
    private Gson gson;

    public static TmdbApiService getInstance() {
        return ourInstance;
    }

    private TmdbApiService() {
        httpClient = new OkHttpClient();
        gson = new Gson();
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
        } catch (IOException e) {
            Log.w(getClass().getSimpleName(), "Could not fetch genres.\n" + e.getMessage());
            return new ArrayList<>();
        }


        Type genreListType = new TypeToken<Map<String, List<Genre>>>() {
        }.getType();
        Map<String, List<Genre>> parsedResult = gson.fromJson(body, genreListType);
        return parsedResult.containsKey("genres")
                ? parsedResult.get("genres")
                : new ArrayList<Genre>();
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
        } catch (IOException e) {
            Log.w(getClass().getSimpleName(), "Could not fetch popular movies.\n" + e.getMessage());
            return new ArrayList<>();
        }


        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> parsedResult = gson.fromJson(body, mapType);
        return parsedResult.containsKey("results")
                ? deserializeMoviesList((List<Map<String, Object>>) parsedResult.get("results"), genres)
                : new ArrayList<Movie>();
    }

    private static List<Movie> deserializeMoviesList(List<Map<String, Object>> data, Map<Double, Genre> genres) {
        List<Movie> movies = new ArrayList<>();
        for (Map entry : data) {
            movies.add(Movie.builder()
                    .id((Double) entry.get("id"))
                    .voteAverage((Double) entry.get("vote_average"))
                    .title((String) entry.get("title"))
                    .popularity((Double) entry.get("popularity"))
                    .posterPath(API_IMAGE_BASE_URL + entry.get("poster_path"))
                    .backdropPath(API_IMAGE_BASE_URL + entry.get("backdrop_path"))
                    .adult((Boolean) entry.get("adult"))
                    .releaseDate(parseDate((String) entry.get("release_date")))
                    .overview((String) entry.get("overview"))
                    .genres(resolveMovieGenres((List) entry.get("genre_ids"), genres))
                    .build()
            );
        }
        return movies;
    }

    private static List<Genre> resolveMovieGenres(List<Double> genreIds, Map<Double, Genre> allGenres) {
        List<Genre> genres = new ArrayList<>();
        for (Double id : genreIds) {
            genres.add(allGenres.get(id));
        }
        return genres;
    }

    private static Date parseDate(String dateString) {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return parser.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }
}

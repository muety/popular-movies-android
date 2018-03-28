package com.github.n1try.popularmovies.service;

import com.github.n1try.popularmovies.BuildConfig;
import com.github.n1try.popularmovies.model.Movie;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import okhttp3.OkHttpClient;

public class TmdbApiService {
    private static final String API_BASE_URL = "https://api.themoviedb.org/3";
    private static final String API_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;
    private static final TmdbApiService ourInstance = new TmdbApiService();

    private OkHttpClient httpClient;

    public static TmdbApiService getInstance() {
        return ourInstance;
    }

    private TmdbApiService() {
        httpClient = new OkHttpClient();
    }

    public List<Movie> getPopularMovies() {
        return getMockMovies();
    }

    private static List<Movie> getMockMovies() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2017, 01, 01);

        return Arrays.asList(Movie.builder()
                        .id(1L)
                        .adult(false)
                        .overview("Believing they have left behind shadowy figures from their past, newlyweds Christian and Ana fully embrace an inextricable connection and shared life of luxury. But just as she steps into her role as Mrs. Grey and he relaxes into an unfamiliar stability, new threats could jeopardize their happy ending before it even begins.")
                        .popularity(542.892558)
                        .posterPath(API_IMAGE_BASE_URL + "/jjPJ4s3DWZZvI4vw8Xfi4Vqa1Q8.jpg")
                        .releaseDate(cal1.getTime())
                        .title("Fifty Shades Freed")
                        .voteAverage(6.1)
                        .build(),
                Movie.builder()
                        .id(2L)
                        .adult(false)
                        .overview("Determined to prove herself, Officer Judy Hopps, the first bunny on Zootopia's police force, jumps at the chance to crack her first case - even if it means partnering with scam-artist fox Nick Wilde to solve the mystery.")
                        .popularity(340.221)
                        .posterPath(API_IMAGE_BASE_URL + "/sM33SANp9z6rXW8Itn7NnG1GOEs.jpg")
                        .releaseDate(cal1.getTime())
                        .title("Zootopia")
                        .voteAverage(7.7)
                        .build());
    }
}

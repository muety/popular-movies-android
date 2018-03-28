package popularmovies.n1try.github.com.popularmovies.service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import okhttp3.OkHttpClient;
import popularmovies.n1try.github.com.popularmovies.BuildConfig;
import popularmovies.n1try.github.com.popularmovies.model.Movie;

public class TmdbApiService {
    private static final String API_BASE_URL = "https://api.themoviedb.org/3";
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;
    private static final TmdbApiService ourInstance = new TmdbApiService();

    private OkHttpClient httpClient;

    public static TmdbApiService getInstance() {
        return ourInstance;
    }

    private TmdbApiService() {
        httpClient = new OkHttpClient();
    }

    private static List<Movie> getPopularMovies() {
        return getMockMovies();
    }

    private static List<Movie> getMockMovies() {
        Calendar cal = Calendar.getInstance();
        cal.set(2017, 01, 01);

        return Arrays.asList(Movie.builder()
                .id(1L)
                .adult(false)
                .overview("A really great Movie")
                .popularity(352.6)
                .posterPath("http://via.placeholder.com/185x300")
                .releaseDate(cal.getTime())
                .title("Best Movie")
                .voteAverage(5.4)
                .build());
    }
}

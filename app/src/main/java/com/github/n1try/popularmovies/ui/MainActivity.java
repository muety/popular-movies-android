package com.github.n1try.popularmovies.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import com.github.n1try.popularmovies.R;
import com.github.n1try.popularmovies.model.Movie;
import com.github.n1try.popularmovies.service.TmdbApiService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.main_movies_gv)
    GridView moviesGv;
    private MovieItemAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setTitle(R.string.main_title);

        List<Movie> popularMovies = TmdbApiService.getInstance().getPopularMovies();
        movieAdapter = new MovieItemAdapter(this, popularMovies);
        moviesGv.setAdapter(movieAdapter);
    }
}

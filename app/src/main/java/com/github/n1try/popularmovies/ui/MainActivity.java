package com.github.n1try.popularmovies.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import com.github.n1try.popularmovies.R;
import com.github.n1try.popularmovies.api.TmdbApiService;
import com.github.n1try.popularmovies.model.Movie;

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

        new AsyncTask<Void, Void, List<Movie>>() {
            @Override
            protected List<Movie> doInBackground(Void... voids) {
                return TmdbApiService.getInstance().getPopularMovies();
            }

            @Override
            protected void onPostExecute(List<Movie> movies) {
                movieAdapter = new MovieItemAdapter(getApplicationContext(), movies);
                moviesGv.setAdapter(movieAdapter);
            }
        }.execute();
    }
}

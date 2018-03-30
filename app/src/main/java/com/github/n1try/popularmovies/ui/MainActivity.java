package com.github.n1try.popularmovies.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;

import com.github.n1try.popularmovies.R;
import com.github.n1try.popularmovies.api.TmdbApiService;
import com.github.n1try.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.main_movies_gv)
    GridView moviesGv;
    private MovieItemAdapter movieAdapter;

    enum MovieSortOrder {POPULAR, TOP_RATED}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setData(MovieSortOrder.POPULAR);
    }

    private void setData(final MovieSortOrder order) {
        if (order == MovieSortOrder.TOP_RATED) setTitle(R.string.title_top_rated_movies);
        else setTitle(R.string.title_popular_movies);

        new AsyncTask<Void, Void, List<Movie>>() {
            @Override
            protected List<Movie> doInBackground(Void... voids) {
                switch (order) {
                    case POPULAR:
                        return TmdbApiService.getInstance().getPopularMovies();
                    case TOP_RATED:
                        return TmdbApiService.getInstance().getTopRatedMovies();
                    default:
                        return new ArrayList<>();
                }
            }

            @Override
            protected void onPostExecute(List<Movie> movies) {
                movieAdapter = new MovieItemAdapter(getApplicationContext(), movies);
                moviesGv.setAdapter(movieAdapter);
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sort_popular:
                setData(MovieSortOrder.POPULAR);
                return true;
            case R.id.action_sort_rating:
                setData(MovieSortOrder.TOP_RATED);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

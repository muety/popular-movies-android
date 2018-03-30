/*
    TODO: JavaDocs
    TODO: Infinite scroll
    TODO: Tablet layout
    TODO: Offline message
 */

package com.github.n1try.popularmovies.ui;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.github.n1try.popularmovies.R;
import com.github.n1try.popularmovies.api.TmdbApiService;
import com.github.n1try.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>> {
    public static final String KEY_SORT_ORDER = "sort_order";
    public static final String KEY_MOVIE_ID = "movie_id";
    private static final int LOADER_ID = 0;

    @BindView(R.id.main_movies_gv)
    GridView moviesGv;

    private MovieItemAdapter movieAdapter;
    private SharedPreferences prefs;

    enum MovieSortOrder {POPULAR, TOP_RATED}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        prefs = getPreferences(Context.MODE_PRIVATE);
        MovieSortOrder order = MovieSortOrder.valueOf(prefs.getString(KEY_SORT_ORDER, MovieSortOrder.POPULAR.name()));

        Bundle bundle = new Bundle();
        bundle.putString(KEY_SORT_ORDER, order.name());
        getLoaderManager().initLoader(LOADER_ID, bundle, this);

        moviesGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movieItem = (Movie) adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra(KEY_MOVIE_ID, movieItem);
                startActivity(intent);
            }
        });
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
        MovieSortOrder order;
        Bundle bundle;

        switch (id) {
            case R.id.action_sort_popular:
                order = MovieSortOrder.POPULAR;
                prefs.edit().putString(KEY_SORT_ORDER, order.name()).apply();
                bundle = new Bundle();
                bundle.putString(KEY_SORT_ORDER, order.name());
                getLoaderManager().restartLoader(LOADER_ID, bundle, this);
                return true;
            case R.id.action_sort_rating:
                order = MovieSortOrder.TOP_RATED;
                prefs.edit().putString(KEY_SORT_ORDER, order.name()).apply();
                bundle = new Bundle();
                bundle.putString(KEY_SORT_ORDER, order.name());
                getLoaderManager().restartLoader(LOADER_ID, bundle, this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
        final MovieSortOrder order = MovieSortOrder.valueOf(bundle.getString(KEY_SORT_ORDER));
        if (order == MovieSortOrder.TOP_RATED) setTitle(R.string.title_top_rated_movies);
        else setTitle(R.string.title_popular_movies);

        return new AsyncTaskLoader<List<Movie>>(getApplicationContext()) {
            @Override
            public List<Movie> loadInBackground() {
                switch (order) {
                    case POPULAR:
                        return TmdbApiService.getInstance(getApplicationContext()).getPopularMovies();
                    case TOP_RATED:
                        return TmdbApiService.getInstance(getApplicationContext()).getTopRatedMovies();
                    default:
                        return new ArrayList<>();
                }
            }

            @Override
            protected void onStartLoading() {
                forceLoad();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        movieAdapter = new MovieItemAdapter(getApplicationContext(), movies);
        moviesGv.setAdapter(movieAdapter);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        movieAdapter.clear();
    }
}

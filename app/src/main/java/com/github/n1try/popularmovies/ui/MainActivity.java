/*
    TODO: JavaDocs
    TODO: Infinite scroll
    TODO: Tablet layout
    TODO: Offline message
    TODO: Default backdrop image
 */

package com.github.n1try.popularmovies.ui;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.github.n1try.popularmovies.R;
import com.github.n1try.popularmovies.api.TmdbApiService;
import com.github.n1try.popularmovies.model.Movie;
import com.github.n1try.popularmovies.utils.AndroidUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>> {
    enum MovieSortOrder {POPULAR, TOP_RATED}

    public static final String KEY_SORT_ORDER = "sort_order";
    public static final String KEY_PAGE = "page";
    public static final String KEY_HIDE_LOADING_DIALOG = "hide_loading_dialog";
    public static final String KEY_MOVIE_ID = "movie_id";
    private static final int LOADER_ID = 0;

    @BindView(R.id.main_movies_gv)
    GridView moviesContainer;
    @BindView(R.id.main_offline_indicator_container)
    ViewGroup offlineContainer;
    @BindView(R.id.main_offline_indicator_iv)
    ImageView offlineIndicatorIv;

    private MovieItemAdapter movieAdapter;
    private SharedPreferences prefs;
    private ProgressDialog loadingDialog;
    private MovieSortOrder currentOrder;
    private MovieSortOrder currentLoaderState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        prefs = getPreferences(Context.MODE_PRIVATE);
        currentOrder = MovieSortOrder.valueOf(prefs.getString(KEY_SORT_ORDER, MovieSortOrder.POPULAR.name()));
        updateTitle(currentOrder);

        getLoaderManager().initLoader(LOADER_ID, createLoaderBundle(), this);

        moviesContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movieItem = (Movie) adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra(KEY_MOVIE_ID, movieItem);
                startActivity(intent);
            }
        });

        resetScrollListener();

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setTitle(getResources().getString(R.string.main_loading_title));
        loadingDialog.setMessage(getResources().getString(R.string.main_loading_text));
        loadingDialog.setCancelable(false);

        Drawable mIcon = ContextCompat.getDrawable(this, R.drawable.ic_cloud_off_black_48dp);
        mIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorTextMedium), PorterDuff.Mode.SRC_IN);
        offlineIndicatorIv.setImageDrawable(mIcon);
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
                currentOrder = MovieSortOrder.POPULAR;
                prefs.edit().putString(KEY_SORT_ORDER, currentOrder.name()).apply();
                getLoaderManager().restartLoader(LOADER_ID, createLoaderBundle(), this);
                resetScrollListener();
                return true;
            case R.id.action_sort_rating:
                currentOrder = MovieSortOrder.TOP_RATED;
                prefs.edit().putString(KEY_SORT_ORDER, currentOrder.name()).apply();
                getLoaderManager().restartLoader(LOADER_ID, createLoaderBundle(), this);
                resetScrollListener();
                return true;
        }

        if (currentOrder != null) updateTitle(currentOrder);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
        final MovieSortOrder order = MovieSortOrder.valueOf(bundle.getString(KEY_SORT_ORDER));
        final int page = bundle.getInt(KEY_PAGE, 1);
        final boolean hideLoadingDialog = bundle.getBoolean(KEY_HIDE_LOADING_DIALOG, false);

        return new AsyncTaskLoader<List<Movie>>(getApplicationContext()) {
            @Override
            public List<Movie> loadInBackground() {
                switch (order) {
                    case POPULAR:
                        return TmdbApiService.getInstance(getApplicationContext()).getPopularMovies(page);
                    case TOP_RATED:
                        return TmdbApiService.getInstance(getApplicationContext()).getTopRatedMovies(page);
                    default:
                        return new ArrayList<>();
                }
            }

            @Override
            protected void onStartLoading() {
                if (!hideLoadingDialog) loadingDialog.show();
                forceLoad();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        loadingDialog.dismiss();

        if (currentOrder != currentLoaderState) {
            // Initial load
            if (movies.isEmpty() && !AndroidUtils.isNetworkAvailable(this)) {
                moviesContainer.setVisibility(View.GONE);
                offlineContainer.setVisibility(View.VISIBLE);
            } else {
                moviesContainer.setVisibility(View.VISIBLE);
                offlineContainer.setVisibility(View.GONE);
                movieAdapter = new MovieItemAdapter(getApplicationContext(), movies);
                moviesContainer.setAdapter(movieAdapter);
            }
        } else {
            // Load caused by infinite scrolling
            movieAdapter.addAll(movies);
            movieAdapter.notifyDataSetChanged();
        }

        currentLoaderState = currentOrder;
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        movieAdapter.clear();
    }

    private void updateTitle(MovieSortOrder currentSortOrder) {
        if (currentSortOrder == MovieSortOrder.TOP_RATED) setTitle(R.string.title_top_rated_movies);
        else setTitle(R.string.title_popular_movies);
    }

    private void resetScrollListener() {
        moviesContainer.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                getLoaderManager().restartLoader(LOADER_ID, createLoaderBundle(page, true), MainActivity.this);
                return true;
            }
        });
    }

    private Bundle createLoaderBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SORT_ORDER, currentOrder.name());
        return bundle;
    }

    private Bundle createLoaderBundle(int pageToLoad, boolean hideLoadingDialog) {
        Bundle bundle = createLoaderBundle();
        bundle.putInt(KEY_PAGE, pageToLoad);
        bundle.putBoolean(KEY_HIDE_LOADING_DIALOG, hideLoadingDialog);
        return bundle;
    }
}

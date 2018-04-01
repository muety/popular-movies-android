/*
 * Copyright (C) 2018 Ferdinand Mütsch
 */

package com.github.n1try.popularmovies.serialization;

import com.github.n1try.popularmovies.model.Movie;
import com.github.n1try.popularmovies.model.TmdbGenresResult;
import com.github.n1try.popularmovies.model.TmdbMoviesResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.Getter;

public class GsonHolder {
    private static final GsonHolder ourInstance = new GsonHolder();

    @Getter
    private Gson gson;

    public static GsonHolder getInstance() {
        return ourInstance;
    }

    private GsonHolder() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Movie.class, new TmdbMovieDeserializer());
        gsonBuilder.registerTypeAdapter(Movie.class, new TmdbMovieSerializer());
        gsonBuilder.registerTypeAdapter(TmdbMoviesResult.class, new TmdbMoviesResultDeserializer());
        gsonBuilder.registerTypeAdapter(TmdbGenresResult.class, new TmdbGenresResultDeserializer());
        gson = gsonBuilder.create();
    }
}

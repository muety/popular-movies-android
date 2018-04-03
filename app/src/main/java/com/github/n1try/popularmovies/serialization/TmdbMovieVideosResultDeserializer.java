/*
 * Copyright (C) 2018 Ferdinand Mütsch
 */

package com.github.n1try.popularmovies.serialization;

import com.github.n1try.popularmovies.model.MovieTrailer;
import com.github.n1try.popularmovies.model.TmdbMovieVideosResult;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class TmdbMovieVideosResultDeserializer implements JsonDeserializer<TmdbMovieVideosResult> {
    @Override
    public TmdbMovieVideosResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject entry = json.getAsJsonObject();
        Type trailerListType = new TypeToken<List<MovieTrailer>>() {
        }.getType();
        return TmdbMovieVideosResult.builder()
                .id(entry.get("id").getAsDouble())
                .results((List<MovieTrailer>) context.deserialize(entry.get("results"), trailerListType))
                .build();
    }
}

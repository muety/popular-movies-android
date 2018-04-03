/*
 * Copyright (C) 2018 Ferdinand Mütsch
 */

package com.github.n1try.popularmovies.serialization;

import com.github.n1try.popularmovies.api.TmdbApiService;
import com.github.n1try.popularmovies.model.MovieTrailer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class TmdbMovieTrailerDeserializer implements JsonDeserializer<MovieTrailer> {
    @Override
    public MovieTrailer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject entry = json.getAsJsonObject();
        MovieTrailer trailer = MovieTrailer.builder()
                .id(entry.get("id").getAsString())
                .title(entry.get("name").getAsString())
                .url(TmdbApiService.API_TRAILER_URL_PREFIX + entry.get("title").getAsString())
                .build();
        return trailer;
    }
}

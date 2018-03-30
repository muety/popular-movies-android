package com.github.n1try.popularmovies.deserialization;

import com.github.n1try.popularmovies.model.Movie;
import com.github.n1try.popularmovies.model.TmdbMoviesResult;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class TmdbMoviesResultDeserializer implements JsonDeserializer<TmdbMoviesResult> {
    @Override
    public TmdbMoviesResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject entry = json.getAsJsonObject();
        Type movieListType = new TypeToken<List<Movie>>() {
        }.getType();
        return TmdbMoviesResult.builder()
                .page(entry.get("page").getAsInt())
                .totalPages(entry.get("total_pages").getAsInt())
                .totalResults(entry.get("total_results").getAsInt())
                .results((List<Movie>) context.deserialize(entry.get("results"), movieListType))
                .build();
    }
}

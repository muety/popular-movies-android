/*
 * Copyright (C) 2018 Ferdinand Mütsch
 */

package com.github.n1try.popularmovies.serialization;

import com.github.n1try.popularmovies.api.TmdbApiService;
import com.github.n1try.popularmovies.model.Genre;
import com.github.n1try.popularmovies.model.Movie;
import com.github.n1try.popularmovies.utils.Utils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TmdbMovieDeserializer implements JsonDeserializer<Movie> {
    @Override
    public Movie deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject entry = json.getAsJsonObject();
        Movie movie = Movie.builder()
                .id(entry.get("id").getAsDouble())
                .voteAverage(entry.get("vote_average").getAsDouble())
                .title(entry.get("title").getAsString())
                .popularity(entry.get("popularity").getAsDouble())
                .posterPath(TmdbApiService.API_IMAGE_SM_BASE_URL + entry.get("poster_path").getAsString())
                .backdropPath(TmdbApiService.API_IMAGE_LG_BASE_URL + entry.get("backdrop_path").getAsString())
                .adult(entry.get("adult").getAsBoolean())
                .releaseDate(Utils.parseDate((entry.get("release_date").getAsString()), "yyyy-MM-dd"))
                .overview(entry.get("overview").getAsString())
                .genres(new ArrayList<Genre>())
                .build();

        for (JsonElement idElement : entry.get("genre_ids").getAsJsonArray()) {
            if (idElement.isJsonObject()) {
                movie.getGenres().add(Genre.builder()
                        .id(idElement.getAsJsonObject().get("id").getAsDouble())
                        .name(idElement.getAsJsonObject().get("name").getAsString())
                        .build());
            }
            else {
                movie.getGenres().add(Genre.builder().id(idElement.getAsDouble()).build());
            }
        }

        return movie;
    }
}

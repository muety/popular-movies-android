package com.github.n1try.popularmovies.serialization;

import com.github.n1try.popularmovies.model.Genre;
import com.github.n1try.popularmovies.model.Movie;
import com.github.n1try.popularmovies.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class TmdbMovieSerializer implements JsonSerializer<Movie> {
    @Override
    public JsonElement serialize(Movie src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject el = new JsonObject();
        el.addProperty("id", src.getId());
        el.addProperty("vote_average", src.getVoteAverage());
        el.addProperty("title", src.getTitle());
        el.addProperty("popularity", src.getPopularity());
        el.addProperty("poster_path", src.getPosterPath().substring(src.getPosterPath().lastIndexOf("/")));
        el.addProperty("backdrop_path", src.getBackdropPath());
        el.addProperty("adult", src.isAdult());
        el.addProperty("release_date", Utils.dumpDate(src.getReleaseDate()));
        el.addProperty("overview", src.getOverview());

        JsonArray genres = new JsonArray();
        for (Genre g : src.getGenres()) {
            JsonObject genre = new JsonObject();
            genre.addProperty("id", g.getId());
            genre.addProperty("name", g.getName());
            genres.add(genre);
        }

        el.add("genre_ids", genres);
        return el;
    }
}

package com.github.n1try.popularmovies.serialization;

import com.github.n1try.popularmovies.model.Genre;
import com.github.n1try.popularmovies.model.TmdbGenresResult;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class TmdbGenresResultDeserializer implements JsonDeserializer<TmdbGenresResult> {
    @Override
    public TmdbGenresResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject entry = json.getAsJsonObject();
        Type genreListType = new TypeToken<List<Genre>>() {}.getType();
        return TmdbGenresResult.builder()
                .genres((List<Genre>) context.deserialize(entry.get("genres"), genreListType))
                .build();
    }
}

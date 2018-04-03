/*
 * Copyright (C) 2018 Ferdinand Mütsch
 */

package com.github.n1try.popularmovies.serialization;

import com.google.gson.FieldNamingPolicy;
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
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gson = gsonBuilder.create();
    }
}

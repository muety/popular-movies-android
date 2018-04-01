/*
 * Copyright (C) 2018 Ferdinand Mütsch
 */

package com.github.n1try.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.n1try.popularmovies.serialization.GsonHolder;
import com.google.gson.Gson;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movie implements Parcelable {
    private double id;
    private String title;
    private String overview;
    private List<Genre> genres;
    private Date releaseDate;
    private String posterPath;
    private String backdropPath;
    private double voteAverage;
    private double popularity;
    private boolean adult;

    public void enrich(Map<Double, Genre> genreMap) {
        for (Genre genre : genres) {
            genre.setName(genreMap.get(genre.getId()).getName());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Gson gson = GsonHolder.getInstance().getGson();
        parcel.writeString(gson.toJson(this));
    }

    private static Movie readFromParcel(Parcel parcel) {
        Gson gson = GsonHolder.getInstance().getGson();
        return gson.fromJson(parcel.readString(), Movie.class);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return readFromParcel(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}

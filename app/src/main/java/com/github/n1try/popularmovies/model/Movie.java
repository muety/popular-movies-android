package com.github.n1try.popularmovies.model;

import java.util.Date;
import java.util.List;

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
public class Movie {
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
}

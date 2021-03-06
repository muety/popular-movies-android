package com.github.n1try.popularmovies.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TmdbMovieReviewsResult {
    private double id;
    private List<MovieReview> results;
}

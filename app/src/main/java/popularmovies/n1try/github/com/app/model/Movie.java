package popularmovies.n1try.github.com.app.model;

import java.util.Date;

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
    private long id;
    private double voteAverage;
    private double popularity;
    private String posterPath;
    private String title;
    private boolean adult;
    private String overview;
    private Date releaseDate;
}

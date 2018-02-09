package com.pimenta.bestv.connectors;

import com.pimenta.bestv.models.Genre;
import com.pimenta.bestv.models.Movie;

import java.util.List;

/**
 * Created by marcus on 08-02-2018.
 */
public interface TmdbConnector {

    List<Genre> getGenres();

    List<Movie> getMoviesByGenre(Genre genre);

}
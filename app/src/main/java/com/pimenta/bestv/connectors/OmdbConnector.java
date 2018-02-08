package com.pimenta.bestv.connectors;

import com.pimenta.bestv.models.Movie;

import java.util.List;
import java.util.Map;

/**
 * Created by marcus on 08-02-2018.
 */
public interface OmdbConnector {

    Map<String, List<Movie>> getTopMovies();

}
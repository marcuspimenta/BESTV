package com.pimenta.bestv.workdetail.data.local.mapper

import com.pimenta.bestv.data.local.model.MovieDbModel
import com.pimenta.bestv.data.local.model.TvShowDbModel
import com.pimenta.bestv.model.domain.WorkDomainModel

internal fun WorkDomainModel.toMovieDbModel() =
    MovieDbModel(
        id = id,
        title = title,
        originalTitle = originalTitle,
        releaseDate = releaseDate,
        originalLanguage = originalLanguage,
        overview = overview,
        source = source.name,
        backdropPath = backdropPath,
        posterPath = posterPath,
        popularity = popularity,
        voteAverage = voteAverage,
        voteCount = voteCount,
        isAdult = isAdult,
        isFavorite = isFavorite,
    )

internal fun WorkDomainModel.toTvShowDbModel() =
    TvShowDbModel(
        id = id,
        title = title,
        originalTitle = originalTitle,
        releaseDate = releaseDate,
        originalLanguage = originalLanguage,
        overview = overview,
        source = source.name,
        backdropPath = backdropPath,
        posterPath = posterPath,
        popularity = popularity,
        voteAverage = voteAverage,
        voteCount = voteCount,
        isAdult = isAdult,
        isFavorite = isFavorite,
    )

package com.pimenta.bestv.workbrowse.data.local.mapper

import com.pimenta.bestv.data.local.model.MovieDbModel
import com.pimenta.bestv.data.local.model.TvShowDbModel
import com.pimenta.bestv.model.domain.ContentSource
import com.pimenta.bestv.model.domain.WorkDomainModel

internal fun MovieDbModel.toDomainModel() =
    WorkDomainModel(
        id = id,
        title = title,
        originalTitle = originalTitle,
        releaseDate = releaseDate,
        originalLanguage = originalLanguage,
        overview = overview,
        source = ContentSource.valueOf(source),
        backdropPath = backdropPath,
        posterPath = posterPath,
        popularity = popularity,
        voteAverage = voteAverage,
        voteCount = voteCount,
        isAdult = isAdult,
        type = WorkDomainModel.Type.MOVIE,
        isFavorite = isFavorite,
    )

internal fun TvShowDbModel.toDomainModel() =
    WorkDomainModel(
        id = id,
        title = title,
        originalTitle = originalTitle,
        releaseDate = releaseDate,
        originalLanguage = originalLanguage,
        overview = overview,
        source = ContentSource.valueOf(source),
        backdropPath = backdropPath,
        posterPath = posterPath,
        popularity = popularity,
        voteAverage = voteAverage,
        voteCount = voteCount,
        isAdult = isAdult,
        type = WorkDomainModel.Type.TV_SHOW,
        isFavorite = isFavorite,
    )

/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.pimenta.bestv.common.presentation.mapper

import com.pimenta.bestv.common.presentation.model.GenreViewModel
import com.pimenta.bestv.common.presentation.model.Source
import com.pimenta.bestv.data.entity.Genre
import com.pimenta.bestv.data.entity.MovieGenre
import com.pimenta.bestv.data.entity.TvShowGenre
import io.reactivex.Single

fun Genre.toViewModel() = GenreViewModel(
        id = id,
        name = name,
        source = Source.MOVIE.takeIf { source == Genre.Source.MOVIE } ?: Source.TV_SHOW
)

fun GenreViewModel.toGenre() = when (source) {
    Source.MOVIE -> MovieGenre(id = id, name = name)
    Source.TV_SHOW -> TvShowGenre(id = id, name = name)
}

fun Genre.toSingle() = Single.fromCallable { this }
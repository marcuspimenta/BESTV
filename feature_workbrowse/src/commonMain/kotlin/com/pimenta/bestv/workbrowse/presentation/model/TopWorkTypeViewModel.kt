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

package com.pimenta.bestv.workbrowse.presentation.model

import com.pimenta.bestv.workbrowse.resources.Res
import org.jetbrains.compose.resources.StringResource
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel.AIRING_TODAY_TV_SHOWS
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel.NOW_PLAYING_MOVIES
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel.ON_THE_AIR_TV_SHOWS
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel.POPULAR_MOVIES
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel.POPULAR_TV_SHOWS
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel.TOP_RATED_MOVIES
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel.TOP_RATED_TV_SHOWS
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel.UP_COMING_MOVIES
import com.pimenta.bestv.workbrowse.resources.airing_today
import com.pimenta.bestv.workbrowse.resources.favorites
import com.pimenta.bestv.workbrowse.resources.now_playing
import com.pimenta.bestv.workbrowse.resources.on_the_air
import com.pimenta.bestv.workbrowse.resources.popular
import com.pimenta.bestv.workbrowse.resources.top_rated
import com.pimenta.bestv.workbrowse.resources.up_coming

/**
 * Created by marcus on 24-10-2019.
 */
enum class TopWorkTypeViewModel(
    val resource: StringResource,
) {
    FAVORITES_MOVIES(Res.string.favorites),
    NOW_PLAYING_MOVIES(Res.string.now_playing),
    POPULAR_MOVIES(Res.string.popular),
    TOP_RATED_MOVIES(Res.string.top_rated),
    UP_COMING_MOVIES(Res.string.up_coming),
    AIRING_TODAY_TV_SHOWS(Res.string.airing_today),
    ON_THE_AIR_TV_SHOWS(Res.string.on_the_air),
    POPULAR_TV_SHOWS(Res.string.popular),
    TOP_RATED_TV_SHOWS(Res.string.top_rated),
}

val topMoviesTypes: List<TopWorkTypeViewModel> =
    listOf(
        NOW_PLAYING_MOVIES,
        POPULAR_MOVIES,
        TOP_RATED_MOVIES,
        UP_COMING_MOVIES,
    )

val topTvShowTypes: List<TopWorkTypeViewModel> =
    listOf(
        AIRING_TODAY_TV_SHOWS,
        ON_THE_AIR_TV_SHOWS,
        TOP_RATED_TV_SHOWS,
        POPULAR_TV_SHOWS,
    )

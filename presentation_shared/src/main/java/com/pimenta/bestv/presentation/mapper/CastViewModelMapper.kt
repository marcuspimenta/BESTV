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

package com.pimenta.bestv.presentation.mapper

import com.pimenta.bestv.model.domain.CastDomainModel
import com.pimenta.bestv.model.domain.ContentSource
import com.pimenta.bestv.model.presentation.mapper.TmdbImageUrlBuilder
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.presentation.R
import com.pimenta.bestv.presentation.platform.Resource

fun CastDomainModel.toViewModel(resource: Resource): CastViewModel? {
    val displayName = name ?: return null
    val displayProfilePath = profilePath ?: return null
    return CastViewModel(
        id = id,
        name = displayName,
        character = character.orEmpty(),
        birthday = birthday.orEmpty(),
        source = when (source) {
            ContentSource.TMDB -> resource.getStringResource(R.string.source_tmdb)
        },
        deathDay = deathDay.orEmpty(),
        biography = biography.orEmpty(),
        thumbnailUrl = TmdbImageUrlBuilder.profile(displayProfilePath)
    )
}

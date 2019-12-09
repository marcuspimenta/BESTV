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

package com.pimenta.bestv.model.presentation.mapper

import com.pimenta.bestv.model.BuildConfig
import com.pimenta.bestv.model.domain.CastDomainModel
import com.pimenta.bestv.model.presentation.model.CastViewModel

fun CastDomainModel.toViewModel() = CastViewModel(
        id = id,
        name = name,
        character = character,
        birthday = birthday,
        source = source,
        deathDay = deathDay,
        biography = biography,
        thumbnailUrl = profilePath?.let { String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, it) }
)
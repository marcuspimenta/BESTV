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

package com.pimenta.bestv.common.domain.model

/**
 * Created by marcus on 29-10-2019.
 */
data class CastDomainModel(
        val castId: Int = 0,
        val creditId: String? = null,
        val gender: Int = 0,
        val id: Int = 0,
        val order: Int = 0,
        val name: String? = null,
        val character: String? = null,
        val profilePath: String? = null,
        val birthday: String? = null,
        val deathDay: String? = null,
        val biography: String? = null,
        val popularity: Double? = null,
        val placeOfBirth: String? = null
)
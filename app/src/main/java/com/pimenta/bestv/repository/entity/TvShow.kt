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

package com.pimenta.bestv.repository.entity

import com.google.gson.annotations.SerializedName
import com.j256.ormlite.table.DatabaseTable
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by marcus on 06/07/18.
 */
@DatabaseTable(tableName = TvShow.TABLE)
class TvShow(
        id: Int = 0,
        @SerializedName("name") override var title: String? = null,
        @SerializedName("original_name") override var originalTitle: String? = null,
        @SerializedName("first_air_date") private var firstAirDate: String? = null
) : Work(id) {

    override var releaseDate: Date?
        get() {
            return try {
                dateFormat.parse(firstAirDate)
            } catch (e: ParseException) {
                Timber.e(e, "Error to get the release data")
                null
            }
        }
        set(value) {
            firstAirDate = value.toString()
        }

    companion object {

        const val TABLE = "tv_show"
    }
}
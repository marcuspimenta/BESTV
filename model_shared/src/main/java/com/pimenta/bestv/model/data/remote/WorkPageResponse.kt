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

package com.pimenta.bestv.model.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Created by marcus on 06/07/18.
 */
abstract class WorkPageResponse<T : WorkResponse>(
    @SerializedName("page") var page: Int = 0,
    @SerializedName("total_pages") var totalPages: Int = 0,
    @SerializedName("total_results") var totalResults: Int = 0,
    @SerializedName("results") var works: List<T>? = null
)

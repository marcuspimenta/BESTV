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

package com.pimenta.bestv.presentation.ui.compose

/** Shared content type tokens used by Compose lazy layouts to reuse compatible item compositions. */
object LazyContentTypes {
    const val WORK = "work"
    const val LOADING = "loading"
    const val CAST = "cast"
    const val REVIEW = "review"
    const val VIDEO = "video"
    const val PROVIDER = "provider"
    const val HEADER = "header"
    const val CASTS = "casts"
    const val RECOMMENDED = "recommended"
    const val REVIEWS = "reviews"
    const val SIMILAR = "similar"
    const val VIDEOS = "videos"
    const val DIVIDER = "divider"
    const val ERROR = "error"
    const val METADATA = "metadata"
    const val GENRE = "genre"
    const val TOP_CONTENT = "top-content"
    const val SEARCH_MOVIES = "movies"
    const val SEARCH_TV_SHOWS = "tv-shows"

    const val ID_KEY_PREFIX = "id:"
    const val URL_KEY_PREFIX = "url:"
    const val VIDEO_KEY_PREFIX = "video:"
    const val REVIEW_KEY_PREFIX = "review:"
    const val GENRE_KEY_PREFIX = "genre:"
    const val TOP_KEY_PREFIX = "top:"
    const val INDEX_KEY_PREFIX = "index:"
    const val PAGE_KEY_PREFIX = "page:"
}

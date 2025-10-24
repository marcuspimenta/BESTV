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

package com.pimenta.bestv.search.presentation.model

import com.pimenta.bestv.model.presentation.model.WorkViewModel

/**
 * Represents user intents/actions in the Search screen.
 * These are the events that the user can trigger.
 */
sealed interface SearchEvent {

    /**
     * User changed the search query text
     */
    data class SearchQueryChanged(val query: String) : SearchEvent

    /**
     * User submitted the search query
     */
    data class SearchQuerySubmitted(val query: String) : SearchEvent

    /**
     * Clear search results
     */
    data object ClearSearch : SearchEvent

    /**
     * Load more movies (pagination)
     */
    data object LoadMoreMovies : SearchEvent

    /**
     * Load more TV shows (pagination)
     */
    data object LoadMoreTvShows : SearchEvent

    /**
     * User selected a work item (for backdrop loading)
     */
    data class WorkItemSelected(val work: WorkViewModel?) : SearchEvent

    /**
     * User clicked on a work item to view details
     */
    data class WorkClicked(val work: WorkViewModel) : SearchEvent
}

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

package com.pimenta.bestv.search.presentation.viewmodel

import com.pimenta.bestv.model.presentation.model.WorkViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

private const val BACKGROUND_UPDATE_DELAY = 300L

class SelectedWorkRequestProcessor {

    private val selectedWorkRequests = MutableSharedFlow<WorkViewModel?>(replay = 1, extraBufferCapacity = 1)

    @OptIn(FlowPreview::class)
    fun observe(): Flow<SelectedWorkAction> = merge(
        selectedWorkRequests
            .debounce(BACKGROUND_UPDATE_DELAY)
            .filterNotNull()
            .map { SelectedWorkAction.Select(it) },
        selectedWorkRequests
            .filter { it == null }
            .map { SelectedWorkAction.Clear }
    )

    suspend fun emitSelectedWorkRequest(work: WorkViewModel?) {
        selectedWorkRequests.emit(work)
    }

    sealed interface SelectedWorkAction {
        data class Select(val work: WorkViewModel) : SelectedWorkAction
        data object Clear : SelectedWorkAction
    }
}

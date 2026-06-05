/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.pimenta.bestv.search.presentation.viewmodel

import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.search.presentation.viewmodel.SelectedWorkRequestProcessor.SelectedWorkAction
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Test

private val WORK = WorkViewModel(
    id = 1,
    title = "Batman",
    originalTitle = "Batman",
    originalLanguage = "en",
    overview = "A superhero movie",
    source = "tmdb",
    backdropUrl = "https://image.tmdb.org/t/p/original/backdrop.jpg",
    posterUrl = "https://image.tmdb.org/t/p/original/poster.jpg",
    releaseDate = "Jan 01, 2023",
    type = WorkType.MOVIE,
    voteAverage = 8.0f
)

private val UPDATED_WORK = WORK.copy(id = 2, title = "Batman Begins", originalTitle = "Batman Begins")

class SelectedWorkRequestProcessorTest {

    private val processor = SelectedWorkRequestProcessor()

    @Test
    fun `should emit selected work after debounce`() = runBlocking {
        val action = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(800) { processor.observe().first() }
        }

        processor.emitSelectedWorkRequest(WORK)

        assertEquals(SelectedWorkAction.Select(WORK), action.await())
    }

    @Test
    fun `should emit only latest selected work when requests change quickly`() = runBlocking {
        val action = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(1000) { processor.observe().first() }
        }

        processor.emitSelectedWorkRequest(WORK)
        delay(150)
        processor.emitSelectedWorkRequest(UPDATED_WORK)

        assertEquals(SelectedWorkAction.Select(UPDATED_WORK), action.await())
    }

    @Test
    fun `should emit clear immediately and cancel pending selection`() = runBlocking {
        val action = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(500) { processor.observe().first() }
        }

        processor.emitSelectedWorkRequest(WORK)
        delay(150)
        processor.emitSelectedWorkRequest(null)

        assertEquals(SelectedWorkAction.Clear, action.await())
    }

    @Test
    fun `should not emit previous selection after clear`() = runBlocking {
        val actions = mutableListOf<SelectedWorkAction>()

        val collector = launch(start = CoroutineStart.UNDISPATCHED) {
            processor.observe().collect(actions::add)
        }

        processor.emitSelectedWorkRequest(WORK)
        delay(150)
        processor.emitSelectedWorkRequest(null)
        delay(400)

        assertEquals(listOf(SelectedWorkAction.Clear), actions)
        collector.cancel()
    }
}

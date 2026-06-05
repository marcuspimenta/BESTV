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

import com.pimenta.bestv.search.presentation.viewmodel.SearchRequestProcessor.SearchAction
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

private const val QUERY = "Batman"
private const val UPDATED_QUERY = "Batman Begins"

class SearchRequestProcessorTest {

    private val processor = SearchRequestProcessor()

    @Test
    fun `should emit search action after debounce`() = runBlocking {
        val action = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(1000) { processor.observe().first() }
        }

        processor.emitSearchRequest(QUERY)

        assertEquals(SearchAction.Search(QUERY), action.await())
    }

    @Test
    fun `should emit only latest search query when requests change quickly`() = runBlocking {
        val action = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(1500) { processor.observe().first() }
        }

        processor.emitSearchRequest(QUERY)
        delay(250)
        processor.emitSearchRequest(UPDATED_QUERY)

        assertEquals(SearchAction.Search(UPDATED_QUERY), action.await())
    }

    @Test
    fun `should not emit duplicate search actions for same query`() = runBlocking {
        val actions = mutableListOf<SearchAction>()

        val collector = launch(start = CoroutineStart.UNDISPATCHED) {
            processor.observe().collect(actions::add)
        }

        processor.emitSearchRequest(QUERY)
        delay(600)
        processor.emitSearchRequest(QUERY)
        delay(600)

        assertEquals(listOf(SearchAction.Search(QUERY)), actions)
        collector.cancel()
    }

    @Test
    fun `should emit clear immediately and cancel pending search`() = runBlocking {
        val action = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(500) { processor.observe().first() }
        }

        processor.emitSearchRequest(QUERY)
        delay(250)
        processor.emitSearchRequest("")

        assertEquals(SearchAction.Clear, action.await())
    }
}

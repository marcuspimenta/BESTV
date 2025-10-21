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

package com.pimenta.bestv.presentation.presenter

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

/**
 * Created by marcus on 21-10-2025.
 */
abstract class BaseViewModel<ViewState, ViewEffect>(initialState: ViewState) : ViewModel() {

    private val mutableStateFlow = MutableStateFlow(initialState)
    private val effectChannel = Channel<ViewEffect>(Channel.BUFFERED)

    protected val currentState: ViewState
        get() = mutableStateFlow.value

    val state: StateFlow<ViewState>
        get() = mutableStateFlow.asStateFlow()

    val effects: Flow<ViewEffect>
        get() = effectChannel.receiveAsFlow()

    protected fun updateState(function: (ViewState) -> ViewState) {
        mutableStateFlow.update(function)
    }

    protected fun emitEvent(effect: ViewEffect) {
        effectChannel.trySend(effect)
    }
}
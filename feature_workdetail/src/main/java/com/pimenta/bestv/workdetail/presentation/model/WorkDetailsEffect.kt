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

package com.pimenta.bestv.workdetail.presentation.model

import android.content.Intent
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel

/**
 * Represents one-time side effects that should happen in the Work Details screen.
 * These are consumed once and don't persist in the state.
 */
sealed interface WorkDetailsEffect {

    /**
     * Open an Intent
     */
    data class OpenIntent(val intent: Intent, val shareTransition: Boolean) : WorkDetailsEffect

    /**
     * Show an error message to the user
     */
    data class ShowError(val message: String) : WorkDetailsEffect
}

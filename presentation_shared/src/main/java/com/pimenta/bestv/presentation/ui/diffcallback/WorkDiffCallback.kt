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

package com.pimenta.bestv.presentation.ui.diffcallback

import androidx.leanback.widget.DiffCallback
import com.pimenta.bestv.model.presentation.model.WorkViewModel

/**
 * Created by marcus on 01-06-2019.
 */
class WorkDiffCallback : DiffCallback<WorkViewModel>() {

    override fun areItemsTheSame(oldItem: WorkViewModel, newItem: WorkViewModel) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: WorkViewModel, newItem: WorkViewModel) =
        oldItem == newItem
}

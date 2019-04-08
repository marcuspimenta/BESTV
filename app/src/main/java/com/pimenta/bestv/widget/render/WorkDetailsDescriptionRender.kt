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

package com.pimenta.bestv.widget.render

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import com.pimenta.bestv.repository.entity.Work
import java.text.SimpleDateFormat

/**
 * Created by marcus on 28-01-2018.
 */
class WorkDetailsDescriptionRender : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(viewHolder: AbstractDetailsDescriptionPresenter.ViewHolder, item: Any) {
        val work = item as Work
        viewHolder.title.text = work.title
        viewHolder.subtitle.text = dateFormat.format(work.releaseDate)
        viewHolder.body.text = work.overview
    }

    companion object {

        private val dateFormat = SimpleDateFormat("MMM dd, yyyy")
    }
}
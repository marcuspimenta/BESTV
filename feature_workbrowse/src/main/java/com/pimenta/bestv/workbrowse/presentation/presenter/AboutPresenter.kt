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

package com.pimenta.bestv.workbrowse.presentation.presenter

import com.pimenta.bestv.presentation.di.annotation.FragmentScope
import com.pimenta.bestv.workbrowse.R
import javax.inject.Inject

/**
 * Created by marcus on 10-12-2019.
 */
private const val TERMS_ID = 1L
private const val API_TERMS_ID = 2L
private const val TERMS_LINK = "https://www.themoviedb.org/terms-of-use"
private const val TERMS_API_LINK = "https://www.themoviedb.org/documentation/api/terms-of-use"
private val GUIDED_ACTIONS = listOf(
        TERMS_ID to R.string.tmdb_terms,
        API_TERMS_ID to R.string.tmdb_api_terms
)

@FragmentScope
class AboutPresenter @Inject constructor(
    private val view: View
) {

    fun getGuidedActions() =
            GUIDED_ACTIONS

    fun guidedActionClicked(id: Long) {
        when (id) {
            TERMS_ID -> view.openLink(TERMS_LINK)
            API_TERMS_ID -> view.openLink(TERMS_API_LINK)
        }
    }

    interface View {

        fun openLink(link: String)
    }
}

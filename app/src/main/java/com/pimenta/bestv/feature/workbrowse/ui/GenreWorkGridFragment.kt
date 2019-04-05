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

package com.pimenta.bestv.feature.workbrowse.ui

import android.content.Context
import android.os.Bundle

import com.pimenta.bestv.BesTV
import com.pimenta.bestv.repository.entity.Genre

/**
 * Created by marcus on 11-02-2018.
 */
class GenreWorkGridFragment : AbstractWorkGridFragment() {

    private lateinit var genre: Genre

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        BesTV.applicationComponent.getGenreWorkGridFragmentComponent()
                .view(this)
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            genre = it.getSerializable(GENRE) as Genre
            showProgress = it.getBoolean(AbstractWorkGridFragment.SHOW_PROGRESS)
        }
    }

    override fun loadData() {
        presenter.loadWorkByGenre(genre)
    }

    override fun refreshDada() {
        // DO ANYTHING
    }

    companion object {

        private const val GENRE = "GENRE"

        fun newInstance(genre: Genre, showProgress: Boolean) =
                GenreWorkGridFragment().apply {
                    this.arguments = Bundle().apply {
                        putSerializable(GENRE, genre)
                        putBoolean(AbstractWorkGridFragment.SHOW_PROGRESS, showProgress)
                    }
                    this.genre = genre
                    this.showProgress = showProgress
                }
    }
}
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

package com.pimenta.bestv.search.presentation.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.ProgressBarManager
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.model.presentation.model.loadBackdrop
import com.pimenta.bestv.presentation.extension.addFragment
import com.pimenta.bestv.presentation.extension.popBackStack
import com.pimenta.bestv.presentation.ui.diffcallback.WorkDiffCallback
import com.pimenta.bestv.presentation.ui.fragment.ErrorFragment
import com.pimenta.bestv.presentation.ui.render.WorkCardRenderer
import com.pimenta.bestv.presentation.ui.setting.SettingShared
import com.pimenta.bestv.presentation.R as presentationR
import com.pimenta.bestv.search.R as searchR
import com.pimenta.bestv.search.presentation.model.SearchEffect
import com.pimenta.bestv.search.presentation.model.SearchEvent
import com.pimenta.bestv.search.presentation.model.SearchState
import com.pimenta.bestv.search.presentation.ui.activity.SearchActivity
import com.pimenta.bestv.search.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SEARCH_FRAGMENT_REQUEST_CODE = 1
private const val ERROR_FRAGMENT_REQUEST_CODE = 2
private const val NO_RESULT_ID = 0L
private const val MOVIE_HEADER_ID = 1L
private const val TV_SHOW_HEADER_ID = 2L

/**
 * Created by marcus on 12-03-2018.
 */
class SearchFragment : SearchSupportFragment() {

    private val rowsAdapter by lazy { ArrayObjectAdapter(ListRowPresenter()) }
    private val movieRowAdapter by lazy { ArrayObjectAdapter(WorkCardRenderer()) }
    private val tvShowRowAdapter by lazy { ArrayObjectAdapter(WorkCardRenderer()) }
    private val backgroundManager by lazy { BackgroundManager.getInstance(activity) }
    private val progressBarManager by lazy { ProgressBarManager() }
    private val workDiffCallback by lazy { WorkDiffCallback() }

    @Inject
    lateinit var viewModel: SearchViewModel

    private var query: String? = null

    override fun onAttach(context: Context) {
        (requireActivity() as SearchActivity).searchActivityComponent
            .searchFragmentComponent()
            .create()
            .inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(resources.getColor(androidx.leanback.R.color.lb_playback_controls_background_light, null))
        progressBarManager.apply {
            enableProgressBar()
            setProgressBarView(
                LayoutInflater.from(context).inflate(presentationR.layout.view_load, null).also {
                    (view.parent as ViewGroup).addView(it)
                }
            )
            initialDelay = 0
        }

        setupUI()
        observeViewModel()
    }

    private fun observeViewModel() {
        // Observe state changes
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    renderState(state)
                }
            }
        }

        // Observe effects
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effects.collect { effect ->
                    handleEffect(effect)
                }
            }
        }
    }

    private fun renderState(state: SearchState) {
        with(state) {
            if (isSearching) {
                progressBarManager.show()
            } else {
                progressBarManager.hide()
            }

            if (hasResults) {
                if (isHeaderPresent(NO_RESULT_ID)) {
                    rowsAdapter.clear()
                }
                if (movies.isNotEmpty()) {
                    movieRowAdapter.setItems(movies, workDiffCallback)
                    if (!isHeaderPresent((MOVIE_HEADER_ID))) {
                        val header = HeaderItem(MOVIE_HEADER_ID, getString(presentationR.string.movies))
                        rowsAdapter.add(ListRow(header, movieRowAdapter))
                    }
                }
                if (tvShows.isNotEmpty()) {
                    tvShowRowAdapter.setItems(tvShows, workDiffCallback)
                    if (!isHeaderPresent((TV_SHOW_HEADER_ID))) {
                        val header = HeaderItem(TV_SHOW_HEADER_ID, getString(presentationR.string.tv_shows))
                        rowsAdapter.add(ListRow(header, tvShowRowAdapter))
                    }
                }
                selectedWork?.loadBackdrop(requireContext()) {
                    if (!isHeaderPresent(NO_RESULT_ID)) {
                        backgroundManager.setBitmap(it)
                    }
                }
            } else if (!isHeaderPresent(NO_RESULT_ID)) {
                backgroundManager.setBitmap(null)
                rowsAdapter.clear()
                rowsAdapter.add(
                    ListRow(
                        HeaderItem(NO_RESULT_ID, getString(searchR.string.no_results)),
                        ArrayObjectAdapter(WorkCardRenderer())
                    )
                )
            }
        }
    }

    private fun handleEffect(effect: SearchEffect) {
        when (effect) {
            is SearchEffect.OpenWorkDetails -> {
                view?.let { fragmentView ->
                    val selectedView = fragmentView.findFocus() as? ImageCardView
                    selectedView?.mainImageView?.let { imageView ->
                        val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            requireActivity(),
                            imageView,
                            SettingShared.SHARED_ELEMENT_NAME
                        ).toBundle()
                        startActivity(effect.intent, bundle)
                    }
                }
            }
            is SearchEffect.ShowError -> {
                val fragment = ErrorFragment.newInstance().apply {
                    setTargetFragment(this@SearchFragment, ERROR_FRAGMENT_REQUEST_CODE)
                }
                requireActivity().addFragment(fragment, ErrorFragment.TAG)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SEARCH_FRAGMENT_REQUEST_CODE -> {
                view?.requestFocus()
            }
            ERROR_FRAGMENT_REQUEST_CODE -> {
                requireActivity().popBackStack(ErrorFragment.TAG, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                when (resultCode) {
                    Activity.RESULT_OK -> viewModel.handleEvent(SearchEvent.SearchQuerySubmitted(query ?: ""))
                    else -> requireActivity().finish()
                }
            }
        }
    }

    private fun setupUI() {
        setSearchResultProvider(
            object : SearchResultProvider {
                override fun getResultsAdapter() = rowsAdapter

                override fun onQueryTextChange(text: String): Boolean {
                    query = text
                    viewModel.handleEvent(SearchEvent.SearchQueryChanged(query ?: ""))
                    return true
                }

                override fun onQueryTextSubmit(text: String): Boolean {
                    query = text
                    viewModel.handleEvent(SearchEvent.SearchQuerySubmitted(query ?: ""))
                    return true
                }
            }
        )
        setOnItemViewSelectedListener { _, item, _, row ->
            val workSelected = item as? WorkViewModel
            workSelected?.let { work ->
                viewModel.handleEvent(SearchEvent.WorkItemSelected(workSelected))

                row?.let {
                    when (it.id) {
                        MOVIE_HEADER_ID -> if (movieRowAdapter.indexOf(work) >= movieRowAdapter.size() - 1) {
                            viewModel.handleEvent(SearchEvent.LoadMoreMovies)
                        }
                        TV_SHOW_HEADER_ID -> if (tvShowRowAdapter.indexOf(work) >= tvShowRowAdapter.size() - 1) {
                            viewModel.handleEvent(SearchEvent.LoadMoreTvShows)
                        }
                    }
                }
            }
        }
        setOnItemViewClickedListener { _, item, _, _ ->
            if (item is WorkViewModel) {
                viewModel.handleEvent(SearchEvent.WorkClicked(item))
            }
        }
    }

    private fun isHeaderPresent(id: Long): Boolean {
        for (i in 0 until rowsAdapter.size()) {
            val action = rowsAdapter.get(i) as? ListRow
            if (action?.headerItem?.id == id) {
                return true
            }
        }
        return false
    }

    companion object {

        fun newInstance(): SearchFragment = SearchFragment()
    }
}

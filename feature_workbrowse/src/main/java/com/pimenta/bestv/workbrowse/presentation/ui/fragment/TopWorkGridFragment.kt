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

package com.pimenta.bestv.workbrowse.presentation.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.os.bundleOf
import androidx.leanback.R
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.model.presentation.model.loadBackdrop
import com.pimenta.bestv.presentation.extension.addFragment
import com.pimenta.bestv.presentation.ui.fragment.ErrorFragment
import com.pimenta.bestv.presentation.ui.setting.SettingShared
import com.pimenta.bestv.route.Route
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel
import com.pimenta.bestv.workbrowse.presentation.presenter.TopWorkGridPresenter
import com.pimenta.bestv.workbrowse.presentation.ui.activity.MainActivity
import javax.inject.Inject

/**
 * Created by marcus on 11-02-2018.
 */
private const val TYPE = "TYPE"
private const val ERROR_FRAGMENT_REQUEST_CODE = 1
private const val WORK_DETAILS_REQUEST_CODE = 2

class TopWorkGridFragment : BaseWorkGridFragment(), TopWorkGridPresenter.View {

    private val topWorkTypeViewModel by lazy { arguments?.getSerializable(TYPE) as TopWorkTypeViewModel }

    @Inject
    lateinit var presenter: TopWorkGridPresenter

    override fun onAttach(context: Context) {
        (requireActivity() as MainActivity).mainActivityComponent
            .topWorkGridFragmentComponent()
            .create(this)
            .inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.bindTo(lifecycle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.loadWorkPageByType(topWorkTypeViewModel)
    }

    override fun lastRowLoaded() {
        presenter.loadWorkPageByType(topWorkTypeViewModel)
    }

    override fun workSelected(workSelected: WorkViewModel) {
        presenter.countTimerLoadBackdropImage(workSelected)
    }

    override fun workClicked(itemViewHolder: Presenter.ViewHolder, workViewModel: WorkViewModel) {
        presenter.workClicked(itemViewHolder, workViewModel)
    }

    override fun onShowProgress() {
        progressBarManager.show()
    }

    override fun onHideProgress() {
        progressBarManager.hide()
    }

    override fun onWorksLoaded(works: List<WorkViewModel>) {
        rowsAdapter.setItems(works, workDiffCallback)
        mainFragmentAdapter.fragmentHost.notifyDataReady(mainFragmentAdapter)
    }

    override fun loadBackdropImage(workViewModel: WorkViewModel) {
        workViewModel.loadBackdrop(requireContext()) {
            backgroundManager?.setBitmap(it)
        }
    }

    override fun onErrorWorksLoaded() {
        val fragment = ErrorFragment.newInstance().apply {
            setTargetFragment(this@TopWorkGridFragment, ERROR_FRAGMENT_REQUEST_CODE)
        }
        parentFragmentManager.addFragment(R.id.scale_frame, fragment, ErrorFragment.TAG)
    }

    override fun openWorkDetails(itemViewHolder: Presenter.ViewHolder, route: Route) {
        val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            (itemViewHolder.view as ImageCardView).mainImageView,
            SettingShared.SHARED_ELEMENT_NAME
        ).toBundle()
        startActivityForResult(
            route.intent,
            WORK_DETAILS_REQUEST_CODE,
            bundle
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ERROR_FRAGMENT_REQUEST_CODE -> {
                parentFragmentManager.popBackStack(ErrorFragment.TAG, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                when (resultCode) {
                    Activity.RESULT_OK -> presenter.loadWorkPageByType(topWorkTypeViewModel)
                    else -> requireActivity().finish()
                }
            }
            WORK_DETAILS_REQUEST_CODE -> presenter.refreshPage(topWorkTypeViewModel)
        }
    }

    companion object {

        fun newInstance(topWorkTypeViewModel: TopWorkTypeViewModel) =
            TopWorkGridFragment().apply {
                arguments = bundleOf(
                    TYPE to topWorkTypeViewModel
                )
            }
    }
}

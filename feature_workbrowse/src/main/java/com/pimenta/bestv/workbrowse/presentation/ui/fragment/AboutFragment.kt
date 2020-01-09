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

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import com.pimenta.bestv.workbrowse.R
import com.pimenta.bestv.workbrowse.di.AboutFragmentComponent
import com.pimenta.bestv.workbrowse.presentation.presenter.AboutPresenter
import javax.inject.Inject

/**
 * Created by marcus on 10-12-2019.
 */
class AboutFragment : GuidedStepSupportFragment(), AboutPresenter.View,
        BrowseSupportFragment.MainFragmentAdapterProvider {

    private val fragmentAdapter by lazy { BrowseSupportFragment.MainFragmentAdapter(this) }

    @Inject
    lateinit var presenter: AboutPresenter

    override fun onAttach(context: Context) {
        AboutFragmentComponent.create(this).inject(this)
        super.onAttach(context)
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        val title = getString(R.string.app_name)
        val breadcrumb = getString(R.string.about)
        val description = getString(R.string.about_description)
        val icon = requireActivity().getDrawable(R.drawable.app_icon)

        return GuidanceStylist.Guidance(title, description, breadcrumb, icon)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        presenter.getGuidedActions().forEach { (id, title) ->
            addAction(actions, id, title)
        }
        super.onCreateActions(actions, savedInstanceState)
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {
        action?.let {
            presenter.guidedActionClicked(action.id)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainFragmentAdapter.fragmentHost.notifyViewCreated(mainFragmentAdapter)
    }

    override fun openLink(link: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), R.string.failed_open_link, Toast.LENGTH_LONG).show()
        }
    }

    override fun getMainFragmentAdapter() = fragmentAdapter

    private fun addAction(actions: MutableList<GuidedAction>, id: Long, @StringRes title: Int) {
        actions.add(
                GuidedAction.Builder(requireContext())
                        .id(id)
                        .title(title)
                        .build()
        )
    }

    companion object {

        fun newInstance() = AboutFragment()
    }
}

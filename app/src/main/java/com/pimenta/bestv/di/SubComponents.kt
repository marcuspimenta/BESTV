package com.pimenta.bestv.di

import com.pimenta.bestv.feature.castdetail.presenter.CastDetailsPresenter
import com.pimenta.bestv.feature.castdetail.ui.CastDetailsFragment
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent
interface CastDetailsFragmentComponent {

    fun inject(castDetailsFragment: CastDetailsFragment)

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        fun view(view: CastDetailsPresenter.View): Builder

        fun build(): CastDetailsFragmentComponent
    }
}
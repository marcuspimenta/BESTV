package com.pimenta.bestv.di

import com.pimenta.bestv.feature.castdetail.presenter.CastDetailsPresenter
import com.pimenta.bestv.feature.castdetail.ui.CastDetailsFragment
import com.pimenta.bestv.feature.search.presenter.SearchPresenter
import com.pimenta.bestv.feature.search.ui.SearchFragment
import com.pimenta.bestv.feature.splash.presenter.SplashPresenter
import com.pimenta.bestv.feature.splash.ui.SplashFragment
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent
interface SplashFragmentComponent {

    fun inject(splashFragment: SplashFragment)

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        fun view(view: SplashPresenter.View): Builder

        fun build(): SplashFragmentComponent
    }
}

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

@Subcomponent
interface SearchFragmentComponent {

    fun inject(searchFragment: SearchFragment)

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        fun view(view: SearchPresenter.View): Builder

        fun build(): SearchFragmentComponent
    }
}
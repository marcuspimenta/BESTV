package com.pimenta.bestv.di

import com.pimenta.bestv.feature.castdetail.presenter.CastDetailsPresenter
import com.pimenta.bestv.feature.castdetail.ui.CastDetailsFragment
import com.pimenta.bestv.feature.search.presenter.SearchPresenter
import com.pimenta.bestv.feature.search.ui.SearchFragment
import com.pimenta.bestv.feature.splash.presenter.SplashPresenter
import com.pimenta.bestv.feature.splash.ui.SplashFragment
import com.pimenta.bestv.feature.workbrowse.presenter.WorkBrowsePresenter
import com.pimenta.bestv.feature.workbrowse.presenter.WorkGridPresenter
import com.pimenta.bestv.feature.workbrowse.ui.GenreWorkGridFragment
import com.pimenta.bestv.feature.workbrowse.ui.TopWorkGridFragment
import com.pimenta.bestv.feature.workbrowse.ui.WorkBrowseFragment
import com.pimenta.bestv.feature.workdetail.presenter.WorkDetailsPresenter
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsFragment
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
interface WorkBrowseFragmentComponent {

    fun inject(workBrowseFragment: WorkBrowseFragment)

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        fun view(view: WorkBrowsePresenter.View): Builder

        fun build(): WorkBrowseFragmentComponent
    }
}

@Subcomponent
interface TopWorkGridFragmentComponent {

    fun inject(topWorkGridFragment: TopWorkGridFragment)

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        fun view(view: WorkGridPresenter.View): Builder

        fun build(): TopWorkGridFragmentComponent
    }
}

@Subcomponent
interface GenreWorkGridFragmentComponent {

    fun inject(genreWorkGridFragment: GenreWorkGridFragment)

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        fun view(view: WorkGridPresenter.View): Builder

        fun build(): GenreWorkGridFragmentComponent
    }
}

@Subcomponent
interface WorkDetailsFragmentComponent {

    fun inject(workDetailsFragment: WorkDetailsFragment)

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        fun view(view: WorkDetailsPresenter.View): Builder

        fun build(): WorkDetailsFragmentComponent
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
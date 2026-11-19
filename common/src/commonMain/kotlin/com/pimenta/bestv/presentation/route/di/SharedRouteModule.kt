package com.pimenta.bestv.presentation.route.di

import com.pimenta.bestv.presentation.route.castdetail.CastDetailsRoute
import com.pimenta.bestv.presentation.route.search.SearchRoute
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedRouteModule = module {
    singleOf(::CastDetailsRoute)
    singleOf(::SearchRoute)
}

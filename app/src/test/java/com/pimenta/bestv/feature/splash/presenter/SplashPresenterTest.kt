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

package com.pimenta.bestv.feature.splash.presenter

import android.Manifest
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.manager.permission.PermissionManager
import com.pimenta.bestv.scheduler.RxScheduler
import com.pimenta.bestv.scheduler.RxSchedulerTest
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Created by marcus on 26-05-2018.
 */
class SplashPresenterTest {

    private val view: SplashPresenter.View = mock()
    private val permissionManager: PermissionManager = mock()
    private val rxScheduler: RxScheduler = RxSchedulerTest()

    private val presenter = SplashPresenter(
            view,
            permissionManager,
            rxScheduler
    )

    @Test
    fun `should load the permissions that were not accepted before`() {
        val testScheduler = TestScheduler()
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }

        val permissions = listOf(Manifest.permission.RECORD_AUDIO)
        whenever(permissionManager.loadPermissions()).thenReturn(Single.just(permissions))

        presenter.loadPermissions()

        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS)

        verify(view).onRequestPermissions(permissions)
    }

    @Test
    fun `should return true if has all permissions`() {
        val testScheduler = TestScheduler()
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }

        whenever(permissionManager.loadPermissions()).thenReturn(Single.just(emptyList()))

        presenter.loadPermissions()

        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS)

        verify(view).onHasAllPermissions(true)
    }

    @Test
    fun `should return false if an exception happens while loading the permissions`() {
        val testScheduler = TestScheduler()
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }

        whenever(permissionManager.loadPermissions()).thenReturn(Single.error(Throwable()))

        presenter.loadPermissions()

        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS)

        verify(view).onHasAllPermissions(false)
    }

    @Test
    fun `should return true if has all permissions accepted`() {
        whenever(permissionManager.loadPermissions()).thenReturn(Single.just(emptyList()))

        presenter.hasAllPermissions()

        verify(view).onHasAllPermissions(true)
    }

    @Test
    fun `should return false if has not all permissions accepted`() {
        val permissions = listOf(Manifest.permission.RECORD_AUDIO)
        whenever(permissionManager.loadPermissions()).thenReturn(Single.just(permissions))

        presenter.hasAllPermissions()

        verify(view).onHasAllPermissions(false)
    }

    @Test
    fun `should return false if an exception happens while checking if has all permissions accepted`() {
        whenever(permissionManager.loadPermissions()).thenReturn(Single.error(Throwable()))

        presenter.hasAllPermissions()

        verify(view).onHasAllPermissions(false)
    }

}
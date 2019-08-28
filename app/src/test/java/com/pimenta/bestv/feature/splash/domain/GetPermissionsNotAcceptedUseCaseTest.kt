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

package com.pimenta.bestv.feature.splash.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.only
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.data.local.permission.LocalPermissions
import io.reactivex.Single
import org.junit.Test

/**
 * Created by marcus on 2019-08-28.
 */
class GetPermissionsNotAcceptedUseCaseTest {

    private val localPermissions: LocalPermissions = mock()
    private val useCase = GetPermissionsNotAcceptedUseCase(
            localPermissions
    )

    @Test
    fun `should return the right data when loading the permissions`() {
        whenever(localPermissions.loadPermissionsNotAccepted()).thenReturn(Single.just(emptyList()))

        useCase()
                .test()
                .assertComplete()

        verify(localPermissions, only()).loadPermissionsNotAccepted()
    }

    @Test
    fun `should return an error when some exception happens`() {
        whenever(localPermissions.loadPermissionsNotAccepted()).thenReturn(Single.error(Throwable()))

        useCase()
                .test()
                .assertError(Throwable::class.java)
    }

}
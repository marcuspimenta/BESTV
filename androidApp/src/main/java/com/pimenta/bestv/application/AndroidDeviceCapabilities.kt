/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.application

import android.content.Context
import android.content.pm.PackageManager
import com.pimenta.bestv.presentation.platform.DeviceCapabilities

class AndroidDeviceCapabilities(context: Context) : DeviceCapabilities {

    override val isMobileDevice: Boolean =
        !context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
}

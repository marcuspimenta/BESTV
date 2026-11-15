/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.presentation.platform

interface DeviceCapabilities {
    val isMobileDevice: Boolean
}

object TvDeviceCapabilities : DeviceCapabilities {
    override val isMobileDevice: Boolean = false
}

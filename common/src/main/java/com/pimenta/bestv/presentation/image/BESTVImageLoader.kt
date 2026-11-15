/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.presentation.image

import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader

fun configureBESTVImageLoader(context: Context) {
    SingletonImageLoader.setSafe {
        ImageLoader.Builder(context.applicationContext).build()
    }
}

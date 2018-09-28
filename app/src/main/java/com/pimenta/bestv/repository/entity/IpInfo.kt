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

package com.pimenta.bestv.repository.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by marcus on 11-02-2018.
 */
class IpInfo {

    @SerializedName("as")
    var `as`: String? = null
    @SerializedName("city")
    var city: String? = null
    @SerializedName("country")
    var country: String? = null
    @SerializedName("countryCode")
    var countryCode: String? = null
    @SerializedName("isp")
    var isp: String? = null
    @SerializedName("lat")
    var lat: Float = 0.toFloat()
    @SerializedName("lon")
    var lon: Float = 0.toFloat()
    @SerializedName("org")
    var org: String? = null
    @SerializedName("query")
    var query: String? = null
    @SerializedName("region")
    var region: String? = null
    @SerializedName("regionName")
    var regionName: String? = null
    @SerializedName("status")
    var status: String? = null
    @SerializedName("timezone")
    var timeZone: String? = null
    @SerializedName("zip")
    var zip: String? = null
}
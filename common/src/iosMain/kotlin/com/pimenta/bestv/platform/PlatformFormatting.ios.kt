package com.pimenta.bestv.platform

import platform.Foundation.NSLocale
import platform.Foundation.NSLocaleLanguageCode
import platform.Foundation.countryCode
import platform.Foundation.currentLocale
import platform.Foundation.localeIdentifier

actual fun currentLocaleTag(): String = NSLocale.currentLocale.localeIdentifier

actual fun currentCountryCode(): String = NSLocale.currentLocale.countryCode.orEmpty()

actual fun localizedLanguageName(languageCode: String, localeTag: String): String =
    NSLocale(localeIdentifier = localeTag)
        .displayNameForKey(NSLocaleLanguageCode, value = languageCode)
        ?.takeIf(String::isNotBlank)
        ?: languageCode

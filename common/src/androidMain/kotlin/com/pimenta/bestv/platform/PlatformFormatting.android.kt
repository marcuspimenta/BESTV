package com.pimenta.bestv.platform

import java.util.Locale

actual fun currentLocaleTag(): String = Locale.getDefault().toLanguageTag()

actual fun currentCountryCode(): String = Locale.getDefault().country

actual fun localizedLanguageName(languageCode: String, localeTag: String): String {
    val displayLocale = Locale.forLanguageTag(localeTag)
    return Locale
        .forLanguageTag(languageCode)
        .getDisplayLanguage(displayLocale)
        .ifBlank { languageCode }
}

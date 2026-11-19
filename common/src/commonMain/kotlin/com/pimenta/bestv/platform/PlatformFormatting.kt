package com.pimenta.bestv.platform

expect fun currentLocaleTag(): String

expect fun currentCountryCode(): String

expect fun localizedLanguageName(languageCode: String, localeTag: String): String

fun formatUsdAmount(amount: Long): String {
    val digits = amount.toString()
    val grouped = digits
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
    return "\$$grouped"
}

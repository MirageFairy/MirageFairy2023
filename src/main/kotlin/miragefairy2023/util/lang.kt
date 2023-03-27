package miragefairy2023.util

import miragefairy2023.InitializationScope

class Translation(val key: String, val en: String, val ja: String)

fun InitializationScope.translation(translation: Translation) = enJa(translation.key, translation.en, translation.ja)
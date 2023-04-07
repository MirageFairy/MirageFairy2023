@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.init

import miragefairy2023.InitializationScope
import miragefairy2023.util.enJa

class Translation(val key: String, val en: String, val ja: String)

fun InitializationScope.translation(translation: Translation) = enJa(translation.key, translation.en, translation.ja)

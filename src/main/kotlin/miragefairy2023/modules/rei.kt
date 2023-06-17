package miragefairy2023.modules

import miragefairy2023.BLOCK_FAIRY_RELATION_KEY
import miragefairy2023.ENTITY_TYPE_FAIRY_RELATION_KEY
import miragefairy2023.module
import miragefairy2023.util.init.Translation
import miragefairy2023.util.init.translation

val COLLECTED_KEY = Translation("gui.collected", "Collected", "収集済み")
val UNCOLLECTED_KEY = Translation("gui.uncollected", "Uncollected", "未収集")

val reiModule = module {
    translation(COLLECTED_KEY)
    translation(UNCOLLECTED_KEY)
}

package miragefairy2023.modules

import miragefairy2023.BLOCK_FAIRY_RELATION_KEY
import miragefairy2023.ENTITY_TYPE_FAIRY_RELATION_KEY
import miragefairy2023.module
import miragefairy2023.util.Translation
import miragefairy2023.util.datagen.enJa

val COLLECTED_KEY = Translation("gui.collected", "Collected", "収集済み")
val UNCOLLECTED_KEY = Translation("gui.uncollected", "Uncollected", "未収集")

val reiModule = module {
    enJa(BLOCK_FAIRY_RELATION_KEY)
    enJa(ENTITY_TYPE_FAIRY_RELATION_KEY)

    enJa(COLLECTED_KEY)
    enJa(UNCOLLECTED_KEY)
}

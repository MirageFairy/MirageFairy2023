package miragefairy2023.modules

import miragefairy2023.module
import miragefairy2023.util.formatted
import miragefairy2023.util.init.FeatureSlot
import miragefairy2023.util.init.enJa
import miragefairy2023.util.text
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.item.Item
import net.minecraft.util.Formatting

val poemModule = module {
    ItemTooltipCallback.EVENT.register { stack, _, lines ->
        val poem = poemRegistry[stack.item] ?: return@register
        poem.forEachIndexed { index, poemLine ->
            lines.add(1 + index, text { translate("${stack.translationKey}.${poemLine.key}").formatted(poemLine.color) })
        }
    }
}

class PoemLine(val key: String, val color: Formatting, val en: String, val ja: String)

fun FeatureSlot<Item>.generatePoem(poem: List<PoemLine>) {
    poem.forEach {
        initializationScope.enJa({ "${feature.translationKey}.${it.key}" }, it.en, it.ja)
    }
}

private val poemRegistry = mutableMapOf<Item, List<PoemLine>>()

fun registerPoem(item: Item, poem: List<PoemLine>) {
    check(item !in poemRegistry)
    poemRegistry[item] = poem
}

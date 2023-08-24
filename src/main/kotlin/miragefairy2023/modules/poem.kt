package miragefairy2023.modules

import miragefairy2023.InitializationScope
import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.formatted
import miragefairy2023.util.init.enJa
import miragefairy2023.util.text
import net.minecraft.item.Item
import net.minecraft.util.Formatting

val poemModule = module {
    onInitializeClient {
        MirageFairy2023.clientProxy!!.registerItemTooltipCallback { stack, lines ->
            val poemList = poemListRegistry[stack.item] ?: return@registerItemTooltipCallback
            poemList.forEachIndexed { index, poemLine ->
                lines.add(1 + index, text { translate("${stack.translationKey}.${poemLine.key}").formatted(poemLine.color) })
            }
        }
    }
}

class Poem(val key: String, val color: Formatting, val en: String, val ja: String)

fun Poem(en: String, ja: String) = Poem("poem", Formatting.DARK_AQUA, en, ja)

fun Poem(key: String, en: String, ja: String) = Poem(key, Formatting.DARK_AQUA, en, ja)

@Suppress("FunctionName")
fun Description(en: String, ja: String) = Poem("description", Formatting.YELLOW, en, ja)

@Suppress("FunctionName")
fun Description(key: String, en: String, ja: String) = Poem(key, Formatting.YELLOW, en, ja)

@Suppress("FunctionName")
fun Penalty(en: String, ja: String) = Poem("penalty", Formatting.RED, en, ja)

@Suppress("FunctionName")
fun Penalty(key: String, en: String, ja: String) = Poem(key, Formatting.RED, en, ja)

fun InitializationScope.generatePoemList(item: Item, poemList: List<Poem>) {
    poemList.forEach {
        enJa({ "${item.translationKey}.${it.key}" }, it.en, it.ja)
    }
}

private val poemListRegistry = mutableMapOf<Item, List<Poem>>()

fun InitializationScope.registerPoemList(item: Item, poemList: List<Poem>) = onInitialize {
    check(item !in poemListRegistry)
    poemListRegistry[item] = poemList
}

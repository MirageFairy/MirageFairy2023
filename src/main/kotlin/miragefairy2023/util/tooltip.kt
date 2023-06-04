package miragefairy2023.util

import miragefairy2023.util.init.FeatureSlot
import miragefairy2023.util.init.enJa
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class TooltipText(val key: String, val color: Formatting, val ja: String, val en: String)

fun FeatureSlot<Item>.initItemTooltipTexts(tooltipTexts: List<TooltipText>) {
    tooltipTexts.forEach {
        initializationScope.enJa({ "${feature.translationKey}.${it.key}" }, it.en, it.ja)
    }
}

fun FeatureSlot<Block>.initBlockTooltipTexts(tooltipTexts: List<TooltipText>) {
    tooltipTexts.forEach {
        initializationScope.enJa({ "${feature.translationKey}.${it.key}" }, it.en, it.ja)
    }
}

fun List<TooltipText>.appendTooltip(item: Item, tooltip: MutableList<Text>) {
    this.forEach {
        tooltip += text { translate("${item.translationKey}.${it.key}").formatted(it.color) }
    }
}

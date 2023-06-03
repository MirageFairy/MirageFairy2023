package miragefairy2023.modules

import miragefairy2023.TrinketsSlotDataProvider
import miragefairy2023.module
import miragefairy2023.util.init.enJa
import net.minecraft.item.Item
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

enum class TrinketsSlotCard(val groupName: String, val slotName: String) {
    HEAD_FAIRY("head", "fairy"),
    CHEST_NECKLACE("chest", "necklace"),
    ;

    val path = "$groupName/$slotName"
    val tag: TagKey<Item> = TagKey.of(Registry.ITEM_KEY, Identifier("trinkets", path))
}

val trinketsSlotModule = module {

    // デフォルトのメッセージの翻訳
    onGenerateJapaneseTranslations { it.add("trinkets.tooltip.slots.any", "§9任意§rのTrinketスロットに装備可能") }
    onGenerateJapaneseTranslations { it.add("trinkets.tooltip.slots.list", "Trinketスロットに装備可能:") }
    onGenerateJapaneseTranslations { it.add("trinkets.tooltip.slots.single", "%dのTrinketスロットに装備可能") }
    onGenerateJapaneseTranslations { it.add("trinkets.tooltip.attributes.all", "装備時:") }
    onGenerateJapaneseTranslations { it.add("trinkets.tooltip.attributes.single", "%dTrinketスロットに装備時:") }
    onGenerateJapaneseTranslations { it.add("trinkets.tooltip.attributes.slots", "%dスロット") }
    onGenerateJapaneseTranslations { it.add("trinkets.slot.chest.necklace", "ネックレス") }

    // 全体
    TrinketsSlotCard.values().forEach { card ->
        onGenerateTrinketsEntities {
            it.slots += card.path
        }
    }

    // 妖精スロット
    onGenerateTrinketsSlot {
        it.slots += TrinketsSlotCard.HEAD_FAIRY.path to TrinketsSlotDataProvider.TrinketsSlotEntry(
            Identifier("trinkets", "gui/slots/${TrinketsSlotCard.HEAD_FAIRY.slotName}"),
            quickMovePredicates = listOf("trinkets:none"),
        )
    }
    enJa("trinkets.slot.head.fairy", "Fairy", "妖精")

}

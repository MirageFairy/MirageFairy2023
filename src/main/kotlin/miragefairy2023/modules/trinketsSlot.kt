package miragefairy2023.modules

import miragefairy2023.datagen.TrinketsSlotProvider
import miragefairy2023.module
import miragefairy2023.util.init.enJa
import net.minecraft.item.Item
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

enum class TrinketsSlotCard(val groupName: String, val slotName: String) {
    HEAD_FAIRY("head", "fairy"),
    CHEST_NECKLACE("chest", "necklace"),
    HAND_GLOVE("hand", "glove"),
    OFFHAND_GLOVE("offhand", "glove"),
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
    onGenerateJapaneseTranslations { it.add("trinkets.slot.head.face", "顔") }
    onGenerateJapaneseTranslations { it.add("trinkets.slot.head.hat", "帽子") }
    onGenerateJapaneseTranslations { it.add("trinkets.slot.chest.necklace", "ネックレス") }
    onGenerateJapaneseTranslations { it.add("trinkets.slot.chest.back", "背中") }
    onGenerateJapaneseTranslations { it.add("trinkets.slot.chest.cape", "マント") }
    onGenerateJapaneseTranslations { it.add("trinkets.slot.feet.aglet", "靴紐") }
    onGenerateJapaneseTranslations { it.add("trinkets.slot.feet.shoes", "靴") }
    onGenerateJapaneseTranslations { it.add("trinkets.slot.legs.belt", "ベルト") }
    onGenerateJapaneseTranslations { it.add("trinkets.slot.hand.glove", "グローブ") }
    onGenerateJapaneseTranslations { it.add("trinkets.slot.hand.ring", "指輪") }
    onGenerateJapaneseTranslations { it.add("trinkets.slot.offhand.glove", "グローブ") }
    onGenerateJapaneseTranslations { it.add("trinkets.slot.offhand.ring", "指輪") }

    // 全体
    TrinketsSlotCard.values().forEach { card ->
        onGenerateTrinketsEntities {
            it.slots += card.path
        }
    }

    // 妖精スロット
    onGenerateTrinketsSlot {
        it.slots += TrinketsSlotCard.HEAD_FAIRY.path to TrinketsSlotProvider.TrinketsSlotEntry(
            Identifier("trinkets", "gui/slots/${TrinketsSlotCard.HEAD_FAIRY.slotName}"),
            quickMovePredicates = listOf("trinkets:none"),
        )
    }
    enJa("trinkets.slot.head.fairy", "Fairy", "妖精")

}

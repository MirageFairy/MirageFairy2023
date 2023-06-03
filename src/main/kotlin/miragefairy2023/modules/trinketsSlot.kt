package miragefairy2023.modules

import miragefairy2023.TrinketsSlotDataProvider
import miragefairy2023.module
import miragefairy2023.util.init.enJa
import net.minecraft.item.Item
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

enum class TrinketsSlot(val groupName: String, val slotName: String) {
    HEAD_FAIRY("head", "fairy"),
    CHEST_NECKLACE("chest", "necklace"),
    ;

    val path = "$groupName/$slotName"
    val tag: TagKey<Item> = TagKey.of(Registry.ITEM_KEY, Identifier("trinkets", path))
}

val trinketsSlotModule = module {

    // 妖精スロット
    onGenerateTrinketsSlot {
        it.slots += TrinketsSlot.HEAD_FAIRY.path to TrinketsSlotDataProvider.TrinketsSlotEntry(
            Identifier("trinkets", "gui/slots/${TrinketsSlot.HEAD_FAIRY.slotName}"),
            quickMovePredicates = listOf("trinkets:none"),
        )
    }
    enJa("trinkets.slot.head.fairy", "Fairy", "妖精")

    // 全体
    onGenerateTrinketsEntities {
        it.slots += TrinketsSlot.HEAD_FAIRY.path
        it.slots += TrinketsSlot.CHEST_NECKLACE.path
    }

}

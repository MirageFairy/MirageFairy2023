package miragefairy2023.modules.toolitem

import dev.emi.trinkets.api.Trinket
import dev.emi.trinkets.api.TrinketsApi
import miragefairy2023.InitializationScope
import miragefairy2023.modules.TrinketsSlotCard
import miragefairy2023.modules.commonItemGroup
import miragefairy2023.util.init.generateItemTag
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.data.client.Model
import net.minecraft.data.client.Models
import net.minecraft.item.Item

abstract class ToolItemCardType<T : Item>(val model: Model) {
    abstract fun createItem(): T
    abstract fun init(scope: InitializationScope, card: ToolItemCard<T>)
}

fun <T : Item> ToolItemCard<T>.init(scope: InitializationScope) = type.init(scope, this)


class TrinketAccessoryType<I>(
    private val trinketsSlotCards: List<TrinketsSlotCard>,
    private val itemCreator: (Item.Settings) -> I,
) : ToolItemCardType<I>(Models.GENERATED) where I : Item, I : Trinket {
    override fun createItem() = itemCreator(FabricItemSettings().maxCount(1).group(commonItemGroup))
    override fun init(scope: InitializationScope, card: ToolItemCard<I>) = scope.run {
        trinketsSlotCards.forEach { trinketsSlotCard ->
            generateItemTag(trinketsSlotCard.tag, card.item)
        }
        onInitialize { TrinketsApi.registerTrinket(card.item, card.item) }
    }
}

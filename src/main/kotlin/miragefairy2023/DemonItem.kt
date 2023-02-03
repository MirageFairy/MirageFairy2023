package miragefairy2023

import miragefairy2023.core.init.Slot
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import net.fabricmc.fabric.api.loot.v2.LootTableSource
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.loot.LootManager
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.function.LootingEnchantLootFunction
import net.minecraft.loot.function.SetCountLootFunction
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

enum class DemonItemCard(
    val itemId: String,
    val enName: String,
    val jaName: String,
) {
    XARPITE("xarpite", "Xarpite", "紅天石"),
}

private val demonItems = DemonItemCard.values().associateWith { Slot<Item>() }
operator fun DemonItemCard.invoke() = demonItems[this]!!

fun initDemonItem() {

    DemonItemCard.values().forEach { card ->

        itemRegistration += {
            val item = Item(FabricItemSettings().group(ItemGroup.MATERIALS))
            card().item = item
            Registry.register(Registry.ITEM, Identifier(MirageFairy2023.modId, card.itemId), item)
        }

    }

    recipeRegistration += {

        val tableId = EntityType.WITCH.lootTableId
        LootTableEvents.MODIFY.register(LootTableEvents.Modify { resourceManager: ResourceManager?, lootManager: LootManager?, id: Identifier, tableBuilder: LootTable.Builder?, source: LootTableSource ->
            if (source.isBuiltin) {
                if (id == tableId) {
                    val poolBuilder = LootPool.builder()
                        .with(
                            ItemEntry.builder(DemonItemCard.XARPITE())
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(-1.0f, 1.0f), false))
                                .apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0f, 1.0f)))
                        )
                    tableBuilder!!.pool(poolBuilder)
                }
            }
        })

        FuelRegistry.INSTANCE.add(DemonItemCard.XARPITE(), 1600)

    }

}

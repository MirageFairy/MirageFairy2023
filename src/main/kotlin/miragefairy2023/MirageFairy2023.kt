package miragefairy2023

import miragefairy2023.core.init.Slot
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import net.fabricmc.fabric.api.loot.v2.LootTableEvents.Modify
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
import org.slf4j.LoggerFactory

enum class DemonItemCard(
    val itemId: String,
) {
    XARPITE("xarpite"),
}

private val daemonItems = DemonItemCard.values().associateWith { Slot<Item>() }
operator fun DemonItemCard.invoke() = daemonItems[this]!!

object MirageFairy2023 : ModInitializer {
    val modId = "miragefairy2023"
    val logger = LoggerFactory.getLogger("miragefairy2023")

    override fun onInitialize() {

        DemonItemCard.values().forEach { card ->
            val item = Item(FabricItemSettings().group(ItemGroup.MATERIALS))
            card().item = item
            Registry.register(Registry.ITEM, Identifier(modId, card.itemId), item)
        }

        val tableId = EntityType.WITCH.lootTableId
        LootTableEvents.MODIFY.register(Modify { resourceManager: ResourceManager?, lootManager: LootManager?, id: Identifier, tableBuilder: LootTable.Builder?, source: LootTableSource ->
            if (source.isBuiltin && tableId == id) {
                val poolBuilder = LootPool.builder()
                    .with(
                        ItemEntry.builder(DemonItemCard.XARPITE())
                            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(-1.0f, 1.0f), false))
                            .apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0f, 1.0f)))
                    )
                tableBuilder!!.pool(poolBuilder)
            }
        })

        FuelRegistry.INSTANCE.add(DemonItemCard.XARPITE(), 1600)

    }
}

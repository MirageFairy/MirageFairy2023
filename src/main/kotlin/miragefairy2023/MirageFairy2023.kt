package miragefairy2023

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import net.fabricmc.fabric.api.loot.v2.LootTableEvents.Modify
import net.fabricmc.fabric.api.loot.v2.LootTableSource
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

object MirageFairy2023 : ModInitializer {
    val modId = "miragefairy2023"
    val logger = LoggerFactory.getLogger("miragefairy2023")

    val XARPITE = Item(FabricItemSettings().group(ItemGroup.MATERIALS))

    override fun onInitialize() {

        Registry.register(Registry.ITEM, Identifier(modId, "xarpite"), XARPITE)

        val tableId = EntityType.WITCH.lootTableId
        LootTableEvents.MODIFY.register(Modify { resourceManager: ResourceManager?, lootManager: LootManager?, id: Identifier, tableBuilder: LootTable.Builder?, source: LootTableSource ->
            if (source.isBuiltin && tableId == id) {
                val poolBuilder = LootPool.builder()
                    .with(
                        ItemEntry.builder(XARPITE)
                            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(-1.0f, 1.0f), false))
                            .apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0f, 1.0f)))
                    )
                tableBuilder!!.pool(poolBuilder)
            }
        })

    }
}

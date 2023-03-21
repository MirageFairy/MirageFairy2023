@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import miragefairy2023.core.init.InitializationScope
import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.minecraft.block.Blocks
import net.minecraft.enchantment.Enchantments
import net.minecraft.item.ItemConvertible
import net.minecraft.loot.LootPool
import net.minecraft.loot.condition.LocationCheckLootCondition
import net.minecraft.loot.condition.RandomChanceLootCondition
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.function.ApplyBonusLootFunction
import net.minecraft.predicate.entity.LocationPredicate
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.biome.Biome

fun InitializationScope.registerGrassDrop(
    item: () -> ItemConvertible,
    amount: Double = 1.0,
    biome: (() -> RegistryKey<Biome>)? = null,
) {
    recipeRegistration {
        val lootTableId = Blocks.GRASS.lootTableId
        LootTableEvents.MODIFY.register { _, _, id, tableBuilder, source ->
            if (source.isBuiltin) {
                if (id == lootTableId) {
                    val itemEntry = ItemEntry.builder(item())
                    itemEntry.conditionally(RandomChanceLootCondition.builder((0.125 * amount).toFloat()))
                    if (biome != null) itemEntry.conditionally(LocationCheckLootCondition.builder(LocationPredicate.Builder.create().biome(biome())))
                    itemEntry.apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE, 2))
                    tableBuilder!!.pool(LootPool.builder().with(itemEntry))
                }
            }
        }
    }
}

/** @param ticks coal is `200 * 8 = 1600` */
fun InitializationScope.registerFuel(item: () -> ItemConvertible, ticks: Int) {
    recipeRegistration {
        FuelRegistry.INSTANCE.add(item(), ticks)
    }
}

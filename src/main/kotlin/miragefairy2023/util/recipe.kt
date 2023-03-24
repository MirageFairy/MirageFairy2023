@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import miragefairy2023.core.init.InitializationScope
import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.minecraft.block.Blocks
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.EntityType
import net.minecraft.item.ItemConvertible
import net.minecraft.item.Items
import net.minecraft.loot.condition.KilledByPlayerLootCondition
import net.minecraft.loot.condition.LocationCheckLootCondition
import net.minecraft.loot.condition.MatchToolLootCondition
import net.minecraft.loot.condition.RandomChanceLootCondition
import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition
import net.minecraft.loot.function.ApplyBonusLootFunction
import net.minecraft.loot.function.ExplosionDecayLootFunction
import net.minecraft.loot.function.LootingEnchantLootFunction
import net.minecraft.loot.function.SetCountLootFunction
import net.minecraft.loot.provider.number.LootNumberProvider
import net.minecraft.predicate.entity.LocationPredicate
import net.minecraft.predicate.item.ItemPredicate
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.biome.Biome

fun InitializationScope.registerGrassDrop(
    item: () -> ItemConvertible,
    amount: Double = 1.0,
    biome: (() -> RegistryKey<Biome>)? = null,
) {
    onRegisterRecipes {
        val lootTableId = Blocks.GRASS.lootTableId
        LootTableEvents.MODIFY.register { _, _, id, tableBuilder, source ->
            if (source.isBuiltin) {
                if (id == lootTableId) {
                    configure(tableBuilder!!) {
                        pool(lootPool {
                            with(alternativeEntry {
                                alternatively(itemEntry(Items.AIR) {
                                    conditionally(MatchToolLootCondition.builder(ItemPredicate.Builder.create().items(Items.SHEARS)))
                                })
                                alternatively(itemEntry(item()) {
                                    conditionally(RandomChanceLootCondition.builder((0.125 * amount).toFloat()))
                                    if (biome != null) conditionally(LocationCheckLootCondition.builder(LocationPredicate.Builder.create().biome(biome())))
                                    apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE, 2))
                                    apply(ExplosionDecayLootFunction.builder())
                                })
                            })
                        })
                    }
                }
            }
        }
    }
}

fun InitializationScope.registerMobDrop(
    entityType: () -> EntityType<*>,
    item: () -> ItemConvertible,
    onlyKilledByPlayer: Boolean = false,
    dropRate: Pair<Float, Float>? = null,
    amount: (LootNumberProvider)? = null,
    fortuneFactor: (LootNumberProvider)? = null,
) {
    onRegisterRecipes {
        val lootTableId = entityType().lootTableId
        LootTableEvents.MODIFY.register { _, _, id, tableBuilder, source ->
            if (source.isBuiltin) {
                if (id == lootTableId) {
                    configure(tableBuilder!!) {
                        pool(lootPool {
                            if (onlyKilledByPlayer) conditionally(KilledByPlayerLootCondition.builder())
                            if (dropRate != null) conditionally(RandomChanceWithLootingLootCondition.builder(dropRate.first, dropRate.second))
                            with(itemEntry(item()) {
                                if (amount != null) apply(SetCountLootFunction.builder(amount, false))
                                if (fortuneFactor != null) apply(LootingEnchantLootFunction.builder(fortuneFactor))
                            })
                        })
                    }
                }
            }
        }
    }
}

/** @param ticks coal is `200 * 8 = 1600` */
fun InitializationScope.registerFuel(item: () -> ItemConvertible, ticks: Int) {
    onRegisterRecipes {
        FuelRegistry.INSTANCE.add(item(), ticks)
    }
}

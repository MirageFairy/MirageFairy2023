@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.init

import miragefairy2023.InitializationScope
import miragefairy2023.modules.ApplyLuckBonusLootFunction
import miragefairy2023.util.datagen.AlternativeLootPoolEntry
import miragefairy2023.util.datagen.ItemLootPoolEntry
import miragefairy2023.util.datagen.LootPool
import miragefairy2023.util.datagen.configure
import miragefairy2023.util.identifier
import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.ComposterBlock
import net.minecraft.data.server.RecipeProvider
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
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
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.loot.provider.number.LootNumberProvider
import net.minecraft.predicate.NumberRange
import net.minecraft.predicate.entity.LocationPredicate
import net.minecraft.predicate.item.EnchantmentPredicate
import net.minecraft.predicate.item.ItemPredicate
import net.minecraft.tag.TagKey
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.biome.Biome

fun InitializationScope.registerGrassDrop(
    item: Item,
    amount: Double = 1.0,
    biome: (() -> RegistryKey<Biome>)? = null,
) = onInitialize {
    val lootTableId = Blocks.GRASS.lootTableId
    LootTableEvents.MODIFY.register { _, _, id, tableBuilder, source ->
        if (source.isBuiltin) {
            if (id == lootTableId) {
                tableBuilder.configure {
                    pool(LootPool(AlternativeLootPoolEntry {
                        alternatively(ItemLootPoolEntry(Items.AIR) {
                            conditionally(MatchToolLootCondition.builder(ItemPredicate.Builder.create().items(Items.SHEARS)))
                        })
                        alternatively(ItemLootPoolEntry(item) {
                            conditionally(RandomChanceLootCondition.builder((0.125 * amount).toFloat()))
                            if (biome != null) conditionally(LocationCheckLootCondition.builder(LocationPredicate.Builder.create().biome(biome())))
                            apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE, 2))
                            apply(ExplosionDecayLootFunction.builder())
                        })
                    }))
                }
            }
        }
    }
}

fun InitializationScope.registerBlockDrop(
    block: Block,
    item: Item,
    dropRate: Float? = null,
    amount: Int? = null,
    fortuneOreDrops: Boolean = false,
    suppressIfSilkTouch: Boolean = false,
    luckBonus: Double? = null,
) = onInitialize {
    val lootTableId = block.lootTableId
    LootTableEvents.MODIFY.register { _, _, id, tableBuilder, source ->
        if (source.isBuiltin) {
            if (id == lootTableId) {
                tableBuilder.configure {
                    pool(LootPool {
                        val itemEntry = ItemLootPoolEntry(item) {
                            if (dropRate != null) conditionally(RandomChanceLootCondition.builder(dropRate))
                            if (amount != null) apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(amount.toFloat())))
                            if (fortuneOreDrops) apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))
                            if (luckBonus != null) apply { ApplyLuckBonusLootFunction(luckBonus) }
                            apply(ExplosionDecayLootFunction.builder())
                        }
                        if (suppressIfSilkTouch) {
                            with(AlternativeLootPoolEntry {
                                alternatively(ItemLootPoolEntry(Items.AIR) {
                                    conditionally(MatchToolLootCondition.builder(ItemPredicate.Builder.create().enchantment(EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(1)))))
                                })
                                alternatively(itemEntry)
                            })
                        } else {
                            with(itemEntry)
                        }
                    })
                }
            }
        }
    }
}

fun InitializationScope.registerMobDrop(
    entityType: EntityType<*>,
    item: Item,
    onlyKilledByPlayer: Boolean = false,
    dropRate: Pair<Float, Float>? = null,
    amount: (LootNumberProvider)? = null,
    fortuneFactor: (LootNumberProvider)? = null,
) = onInitialize {
    val lootTableId = entityType.lootTableId
    LootTableEvents.MODIFY.register { _, _, id, tableBuilder, source ->
        if (source.isBuiltin) {
            if (id == lootTableId) {
                tableBuilder.configure {
                    pool(LootPool(ItemLootPoolEntry(item) {
                        if (amount != null) apply(SetCountLootFunction.builder(amount, false))
                        if (fortuneFactor != null) apply(LootingEnchantLootFunction.builder(fortuneFactor))
                    }) {
                        if (onlyKilledByPlayer) conditionally(KilledByPlayerLootCondition.builder())
                        if (dropRate != null) conditionally(RandomChanceWithLootingLootCondition.builder(dropRate.first, dropRate.second))
                    })
                }
            }
        }
    }
}

/** @param ticks coal is `200 * 8 = 1600` */
fun InitializationScope.registerFuel(item: Item, ticks: Int) = onInitialize {
    FuelRegistry.INSTANCE.add(item, ticks)
}

fun InitializationScope.registerComposterInput(item: Item, chance: Float) = onInitialize {
    ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(item, chance)
}

fun CraftingRecipeJsonBuilder.criterion(item: Item): CraftingRecipeJsonBuilder = this.criterion("has_${item.identifier.path}", RecipeProvider.conditionsFromItem(item))
fun CraftingRecipeJsonBuilder.criterion(tagKey: TagKey<Item>): CraftingRecipeJsonBuilder = this.criterion("has_${tagKey.id.path}", RecipeProvider.conditionsFromTag(tagKey))
fun CraftingRecipeJsonBuilder.group(item: Item): CraftingRecipeJsonBuilder = this.group("${item.identifier}")

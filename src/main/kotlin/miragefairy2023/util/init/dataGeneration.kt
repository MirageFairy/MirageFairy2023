@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.init

import miragefairy2023.InitializationScope
import miragefairy2023.util.jsonObjectOf
import miragefairy2023.util.jsonPrimitive
import miragefairy2023.util.string
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.block.Block
import net.minecraft.data.server.BlockLootTableGenerator
import net.minecraft.item.ItemConvertible
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.condition.BlockStatePropertyLootCondition
import net.minecraft.loot.condition.LootCondition
import net.minecraft.loot.entry.AlternativeEntry
import net.minecraft.loot.entry.GroupEntry
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.entry.LeafEntry
import net.minecraft.loot.entry.LootPoolEntry
import net.minecraft.loot.entry.SequenceEntry
import net.minecraft.loot.function.LootFunctionConsumingBuilder
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.predicate.StatePredicate
import net.minecraft.state.property.Property
import net.minecraft.util.registry.Registry

inline fun <T> configure(receiver: T, block: T.() -> Unit) = receiver.apply(block)

fun <T : Block> InitializationScope.generateDefaultBlockLootTable(block: T) = generateBlockLootTable(block) { BlockLootTableGenerator.drops(block) }

fun <T : Block> FeatureSlot<T>.generateDefaultBlockLootTable() = generateBlockLootTable { BlockLootTableGenerator.drops(feature) }

fun <T : Block> InitializationScope.generateBlockLootTable(block: T, block2: () -> LootTable.Builder) {
    onGenerateBlockLootTables {
        addDrop(block, block2())
    }
}

fun <T : Block> FeatureSlot<T>.generateBlockLootTable(block: () -> LootTable.Builder) {
    initializationScope.onGenerateBlockLootTables {
        addDrop(feature, block())
    }
}

fun <T : LootFunctionConsumingBuilder<T>> T.applyExplosionDecay(drop: ItemConvertible): T {
    return FabricBlockLootTableProvider.applyExplosionDecay(drop, this)!!
}

fun lootTable(block: (LootTable.Builder.() -> Unit)? = null): LootTable.Builder {
    return configure(LootTable.builder()!!) { block?.invoke(this) }
}

fun lootPool(block: (LootPool.Builder.() -> Unit)? = null): LootPool.Builder {
    return configure(LootPool.builder()!!) { block?.invoke(this) }
}

fun blockStatePropertyLootCondition(targetBlock: Block, block: (BlockStatePropertyLootCondition.Builder.() -> Unit)? = null): BlockStatePropertyLootCondition.Builder {
    return configure(BlockStatePropertyLootCondition.builder(targetBlock)!!) { block?.invoke(this) }
}

fun exactMatchBlockStatePropertyLootCondition(block: Block, property: Property<Int>, value: Int) = LootCondition.Builder {
    BlockStatePropertyLootCondition.Serializer().fromJson(
        jsonObjectOf(
            "block" to Registry.BLOCK.getId(block).string.jsonPrimitive,
            "condition" to "minecraft:block_state_property".jsonPrimitive,
            "properties" to jsonObjectOf(
                property.name to value.jsonPrimitive,
            )
        ), null
    )
}

fun rangedMatchBlockStatePropertyLootCondition(block: Block, property: Property<Int>, min: Int, max: Int) = LootCondition.Builder {
    BlockStatePropertyLootCondition.Serializer().fromJson(
        jsonObjectOf(
            "block" to Registry.BLOCK.getId(block).string.jsonPrimitive,
            "condition" to "minecraft:block_state_property".jsonPrimitive,
            "properties" to jsonObjectOf(
                property.name to jsonObjectOf(
                    "min" to min.jsonPrimitive,
                    "max" to max.jsonPrimitive,
                ),
            )
        ), null
    )
}

fun statePredicate(block: (StatePredicate.Builder.() -> Unit)? = null): StatePredicate.Builder {
    return configure(StatePredicate.Builder.create()!!) { block?.invoke(this) }
}

fun itemLootPoolEntry(item: ItemConvertible, block: (LeafEntry.Builder<*>.() -> Unit)? = null): LeafEntry.Builder<*> {
    return configure(ItemEntry.builder(item)!!) { block?.invoke(this) }
}

fun alternativeLootPoolEntry(vararg children: LootPoolEntry.Builder<*>, block: (AlternativeEntry.Builder.() -> Unit)? = null): AlternativeEntry.Builder {
    return configure(AlternativeEntry.builder(*children)!!) { block?.invoke(this) }
}

fun groupLootPoolEntry(vararg children: LootPoolEntry.Builder<*>, block: (GroupEntry.Builder.() -> Unit)? = null): GroupEntry.Builder {
    return configure(GroupEntry.create(*children)!!) { block?.invoke(this) }
}

fun sequenceLootPoolEntry(vararg children: LootPoolEntry.Builder<*>, block: (SequenceEntry.Builder.() -> Unit)? = null): SequenceEntry.Builder {
    return configure(SequenceEntry.create(*children)!!) { block?.invoke(this) }
}

fun constantLootNumberProvider(value: Float) = ConstantLootNumberProvider.create(value)!!
fun uniformLootNumberProvider(min: Float, max: Float) = UniformLootNumberProvider.create(min, max)!!

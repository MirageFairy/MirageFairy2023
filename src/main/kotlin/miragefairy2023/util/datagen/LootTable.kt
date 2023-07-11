@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.datagen

import miragefairy2023.util.init.configure
import miragefairy2023.util.jsonObjectOf
import miragefairy2023.util.jsonPrimitive
import miragefairy2023.util.string
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.block.Block
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
import net.minecraft.state.property.Property
import net.minecraft.util.registry.Registry

@Suppress("FunctionName")
fun LootTable(vararg pools: LootPool.Builder, initializer: LootTable.Builder.() -> Unit = {}): LootTable.Builder = configure(LootTable.builder()) {
    pools.forEach {
        this.pool(it)
    }
    initializer.invoke(this)
}


@Suppress("FunctionName")
fun LootPool(vararg entries: LootPoolEntry.Builder<*>, initializer: LootPool.Builder.() -> Unit = {}): LootPool.Builder = configure(LootPool.builder()) {
    entries.forEach {
        this.with(it)
    }
    initializer.invoke(this)
}


@Suppress("FunctionName")
fun ItemLootPoolEntry(item: ItemConvertible, initializer: LeafEntry.Builder<*>.() -> Unit = {}): LeafEntry.Builder<*> = configure(ItemEntry.builder(item)) {
    initializer.invoke(this)
}

@Suppress("FunctionName")
fun AlternativeLootPoolEntry(vararg children: LootPoolEntry.Builder<*>, initializer: AlternativeEntry.Builder.() -> Unit = {}): AlternativeEntry.Builder = configure(AlternativeEntry.builder(*children)) {
    initializer.invoke(this)
}

@Suppress("FunctionName")
fun GroupLootPoolEntry(vararg children: LootPoolEntry.Builder<*>, initializer: GroupEntry.Builder.() -> Unit = {}): GroupEntry.Builder = configure(GroupEntry.create(*children)) {
    initializer.invoke(this)
}

@Suppress("FunctionName")
fun SequenceLootPoolEntry(vararg children: LootPoolEntry.Builder<*>, initializer: SequenceEntry.Builder.() -> Unit = {}): SequenceEntry.Builder = configure(SequenceEntry.create(*children)) {
    initializer.invoke(this)
}


fun ConstantLootNumberProvider(value: Float): ConstantLootNumberProvider = ConstantLootNumberProvider.create(value)

fun UniformLootNumberProvider(min: Float, max: Float): UniformLootNumberProvider = UniformLootNumberProvider.create(min, max)


@Suppress("FunctionName")
fun ExactMatchBlockStatePropertyLootCondition(block: Block, property: Property<Int>, value: Int) = LootCondition.Builder {
    BlockStatePropertyLootCondition.Serializer().fromJson( // Rangedの方に合わせるためにjsonを使用
        jsonObjectOf(
            "block" to Registry.BLOCK.getId(block).string.jsonPrimitive,
            "condition" to "minecraft:block_state_property".jsonPrimitive,
            "properties" to jsonObjectOf(
                property.name to value.jsonPrimitive,
            )
        ), null
    )
}

@Suppress("FunctionName")
fun RangedMatchBlockStatePropertyLootCondition(block: Block, property: Property<Int>, min: Int, max: Int) = LootCondition.Builder {
    BlockStatePropertyLootCondition.Serializer().fromJson(
        jsonObjectOf(
            "block" to Registry.BLOCK.getId(block).string.jsonPrimitive,
            "condition" to "minecraft:block_state_property".jsonPrimitive,
            "properties" to jsonObjectOf(
                property.name to jsonObjectOf(
                    "min" to min.jsonPrimitive, // Builderを使うとexactMatchしか利用できない
                    "max" to max.jsonPrimitive,
                ),
            )
        ), null
    )
}


fun LootFunctionConsumingBuilder<*>.applyExplosionDecay(drop: ItemConvertible) {
    FabricBlockLootTableProvider.applyExplosionDecay(drop, this)
}

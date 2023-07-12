@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.datagen

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
import net.minecraft.util.Identifier


inline fun <T> T.configure(block: T.() -> Unit) = this.apply(block)


// LootTable

@Suppress("FunctionName")
fun LootTable(vararg pools: LootPool.Builder, initializer: LootTable.Builder.() -> Unit = {}): LootTable.Builder = LootTable.builder().configure {
    pools.forEach {
        this.pool(it)
    }
    initializer.invoke(this)
}


// LootPool

@Suppress("FunctionName")
fun LootPool(vararg entries: LootPoolEntry.Builder<*>, initializer: LootPool.Builder.() -> Unit = {}): LootPool.Builder = LootPool.builder().configure {
    entries.forEach {
        this.with(it)
    }
    initializer.invoke(this)
}


// LootPoolEntry

@Suppress("FunctionName")
fun ItemLootPoolEntry(item: ItemConvertible, initializer: LeafEntry.Builder<*>.() -> Unit = {}): LeafEntry.Builder<*> = ItemEntry.builder(item).configure {
    initializer.invoke(this)
}

@Suppress("FunctionName")
fun AlternativeLootPoolEntry(vararg children: LootPoolEntry.Builder<*>, initializer: AlternativeEntry.Builder.() -> Unit = {}): AlternativeEntry.Builder = AlternativeEntry.builder(*children).configure {
    initializer.invoke(this)
}

@Suppress("FunctionName")
fun GroupLootPoolEntry(vararg children: LootPoolEntry.Builder<*>, initializer: GroupEntry.Builder.() -> Unit = {}): GroupEntry.Builder = GroupEntry.create(*children).configure {
    initializer.invoke(this)
}

@Suppress("FunctionName")
fun SequenceLootPoolEntry(vararg children: LootPoolEntry.Builder<*>, initializer: SequenceEntry.Builder.() -> Unit = {}): SequenceEntry.Builder = SequenceEntry.create(*children).configure {
    initializer.invoke(this)
}


// LootNumberProvider

fun ConstantLootNumberProvider(value: Float): ConstantLootNumberProvider = ConstantLootNumberProvider.create(value)

fun UniformLootNumberProvider(min: Float, max: Float): UniformLootNumberProvider = UniformLootNumberProvider.create(min, max)


// LootCondition

@Suppress("FunctionName")
fun ExactMatchBlockStatePropertyLootCondition(block: Block, identifier: Identifier, property: Property<Int>, value: Int) = LootCondition.Builder {
    val jsonElement = jsonObjectOf(
        "block" to identifier.string.jsonPrimitive,
        "condition" to "minecraft:block_state_property".jsonPrimitive,
        "properties" to jsonObjectOf(
            property.name to value.jsonPrimitive,
        ),
    )
    BlockStatePropertyLootCondition.Serializer().fromJson(jsonElement, null) // Rangedの方に合わせるためにjsonを使用
}

@Suppress("FunctionName")
fun RangedMatchBlockStatePropertyLootCondition(block: Block, identifier: Identifier, property: Property<Int>, min: Int, max: Int) = LootCondition.Builder {
    val jsonElement = jsonObjectOf(
        "block" to identifier.string.jsonPrimitive,
        "condition" to "minecraft:block_state_property".jsonPrimitive,
        "properties" to jsonObjectOf(
            property.name to jsonObjectOf(
                "min" to min.jsonPrimitive, // Builderを使うとexactMatchしか利用できない
                "max" to max.jsonPrimitive,
            ),
        ),
    )
    BlockStatePropertyLootCondition.Serializer().fromJson(jsonElement, null)
}


fun LootFunctionConsumingBuilder<*>.applyExplosionDecay(drop: ItemConvertible) {
    FabricBlockLootTableProvider.applyExplosionDecay(drop, this)
}

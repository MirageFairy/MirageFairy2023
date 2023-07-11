@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.datagen

import miragefairy2023.util.init.configure
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.item.ItemConvertible
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.entry.AlternativeEntry
import net.minecraft.loot.entry.GroupEntry
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.entry.LeafEntry
import net.minecraft.loot.entry.LootPoolEntry
import net.minecraft.loot.entry.SequenceEntry
import net.minecraft.loot.function.LootFunctionConsumingBuilder
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.loot.provider.number.UniformLootNumberProvider

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


fun LootFunctionConsumingBuilder<*>.applyExplosionDecay(drop: ItemConvertible) {
    FabricBlockLootTableProvider.applyExplosionDecay(drop, this)
}

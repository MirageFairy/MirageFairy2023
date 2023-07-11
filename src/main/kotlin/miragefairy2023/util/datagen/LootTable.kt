@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.datagen

import miragefairy2023.util.init.configure
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.item.ItemConvertible
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.entry.LootPoolEntry
import net.minecraft.loot.function.LootFunctionConsumingBuilder

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


fun LootFunctionConsumingBuilder<*>.applyExplosionDecay(drop: ItemConvertible) {
    FabricBlockLootTableProvider.applyExplosionDecay(drop, this)
}

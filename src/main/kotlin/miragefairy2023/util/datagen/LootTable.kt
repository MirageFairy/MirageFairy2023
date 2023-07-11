@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.datagen

import miragefairy2023.util.init.configure
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.entry.LootPoolEntry

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

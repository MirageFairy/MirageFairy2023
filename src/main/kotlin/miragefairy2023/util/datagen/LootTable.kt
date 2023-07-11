@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.datagen

import miragefairy2023.util.init.configure
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable

@Suppress("FunctionName")
fun LootTable(vararg pools: LootPool.Builder, initializer: LootTable.Builder.() -> Unit = {}): LootTable.Builder = configure(LootTable.builder()) {
    pools.forEach {
        this.pool(it)
    }
    initializer.invoke(this)
}

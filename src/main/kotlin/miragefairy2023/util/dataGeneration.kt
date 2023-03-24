@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import com.google.gson.JsonElement
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.block.Block
import net.minecraft.data.client.BlockStateSupplier
import net.minecraft.item.ItemConvertible
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.condition.BlockStatePropertyLootCondition
import net.minecraft.loot.entry.AlternativeEntry
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.entry.LeafEntry
import net.minecraft.loot.entry.LootPoolEntry
import net.minecraft.loot.function.LootFunctionConsumingBuilder
import net.minecraft.predicate.StatePredicate

fun <T : Block> BlockScope<T>.generateBlockState(jsonElementSupplier: () -> JsonElement) {
    initializationScope.blockStateModelGeneration { blockStateModelGenerator ->
        blockStateModelGenerator.blockStateCollector.accept(object : BlockStateSupplier {
            override fun getBlock() = item
            override fun get() = jsonElementSupplier()
        })
    }
}


private inline fun <T> T.init(block: T.() -> Unit) = this.apply(block)

fun <T : Block> BlockScope<T>.generateBlockLootTable(block: () -> LootTable.Builder) {
    initializationScope.blockLootTablesGeneration {
        addDrop(item, block())
    }
}

fun <T : LootFunctionConsumingBuilder<T>> T.applyExplosionDecay(drop: ItemConvertible): T {
    return FabricBlockLootTableProvider.applyExplosionDecay(drop, this)!!
}

fun lootTable(block: (LootTable.Builder.() -> Unit)? = null): LootTable.Builder {
    return LootTable.builder()!!.init { block?.invoke(this) }
}

fun lootPool(block: (LootPool.Builder.() -> Unit)? = null): LootPool.Builder {
    return LootPool.builder()!!.init { block?.invoke(this) }
}

fun blockStatePropertyLootCondition(targetBlock: Block, block: (BlockStatePropertyLootCondition.Builder.() -> Unit)? = null): BlockStatePropertyLootCondition.Builder {
    return BlockStatePropertyLootCondition.builder(targetBlock)!!.init { block?.invoke(this) }
}

fun statePredicate(block: (StatePredicate.Builder.() -> Unit)? = null): StatePredicate.Builder {
    return StatePredicate.Builder.create()!!.init { block?.invoke(this) }
}

fun itemEntry(item: ItemConvertible, block: (LeafEntry.Builder<*>.() -> Unit)? = null): LeafEntry.Builder<*> {
    return ItemEntry.builder(item)!!.init { block?.invoke(this) }
}

fun alternativeEntry(vararg children: LootPoolEntry.Builder<*>, block: (AlternativeEntry.Builder.() -> Unit)? = null): AlternativeEntry.Builder {
    return AlternativeEntry.builder(*children)!!.init { block?.invoke(this) }
}

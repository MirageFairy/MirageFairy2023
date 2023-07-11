@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.init

import miragefairy2023.InitializationScope
import net.minecraft.block.Block
import net.minecraft.data.server.BlockLootTableGenerator
import net.minecraft.loot.LootTable

fun InitializationScope.generateBlockLootTable(block: Block, initializer: () -> LootTable.Builder) = onGenerateBlockLootTables {
    addDrop(block, initializer())
}

@Deprecated("Removing") // TODO remove
fun InitializationScope.generateBlockLootTable(blockGetter: () -> Block, initializer: () -> LootTable.Builder) = onGenerateBlockLootTables {
    addDrop(blockGetter(), initializer())
}

fun InitializationScope.generateDefaultBlockLootTable(block: Block) = generateBlockLootTable(block) { BlockLootTableGenerator.drops(block) }

@Deprecated("Removing") // TODO remove
fun InitializationScope.generateDefaultBlockLootTable(blockGetter: () -> Block) = generateBlockLootTable({ blockGetter() }) { BlockLootTableGenerator.drops(blockGetter()) }

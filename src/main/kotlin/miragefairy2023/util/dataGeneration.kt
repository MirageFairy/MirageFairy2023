@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import com.google.gson.JsonElement
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.block.Block
import net.minecraft.data.client.BlockStateSupplier
import net.minecraft.item.ItemConvertible
import net.minecraft.loot.function.LootFunctionConsumingBuilder

inline fun <T> T.init(block: T.() -> Unit) = this.apply(block)

fun <T : LootFunctionConsumingBuilder<T>> T.applyExplosionDecay(drop: ItemConvertible) = FabricBlockLootTableProvider.applyExplosionDecay(drop, this)!!

fun <T : Block> BlockScope<T>.generateBlockState(jsonElementSupplier: () -> JsonElement) {
    initializationScope.blockStateModelGeneration { blockStateModelGenerator ->
        blockStateModelGenerator.blockStateCollector.accept(object : BlockStateSupplier {
            override fun getBlock() = item
            override fun get() = jsonElementSupplier()
        })
    }
}

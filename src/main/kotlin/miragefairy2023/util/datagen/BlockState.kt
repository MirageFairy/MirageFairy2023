@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.datagen

import com.google.gson.JsonElement
import miragefairy2023.InitializationScope
import miragefairy2023.util.concat
import miragefairy2023.util.jsonObjectOf
import miragefairy2023.util.jsonPrimitive
import net.minecraft.block.Block
import net.minecraft.data.client.BlockStateSupplier
import net.minecraft.util.Identifier

fun InitializationScope.generateBlockState(block: Block, jsonElementSupplier: () -> JsonElement) {
    onGenerateBlockStateModels { blockStateModelGenerator ->
        blockStateModelGenerator.blockStateCollector.accept(object : BlockStateSupplier {
            override fun getBlock() = block
            override fun get() = jsonElementSupplier()
        })
    }
}

@Deprecated("Removing") // TODO remove
fun InitializationScope.generateBlockState(blockGetter: () -> Block, jsonElementSupplier: () -> JsonElement) {
    onGenerateBlockStateModels { blockStateModelGenerator ->
        blockStateModelGenerator.blockStateCollector.accept(object : BlockStateSupplier {
            override fun getBlock() = blockGetter()
            override fun get() = jsonElementSupplier()
        })
    }
}

fun InitializationScope.generateHorizontalFacingBlockState(block: Block, identifier: Identifier) = generateBlockState(block) {
    jsonObjectOf(
        "variants" to jsonObjectOf(listOf(
            "north" to 0,
            "south" to 180,
            "west" to 270,
            "east" to 90,
        ).map { (facing, y) ->
            "facing=$facing" to jsonObjectOf(
                "model" to "${"block/" concat identifier}".jsonPrimitive,
                "y" to y.jsonPrimitive,
            )
        }),
    )
}

@Deprecated("Removing") // TODO remove
fun InitializationScope.generateHorizontalFacingBlockState(blockGetter: () -> Block, identifier: Identifier) = generateBlockState({ blockGetter() }) {
    jsonObjectOf(
        "variants" to jsonObjectOf(listOf(
            "north" to 0,
            "south" to 180,
            "west" to 270,
            "east" to 90,
        ).map { (facing, y) ->
            "facing=$facing" to jsonObjectOf(
                "model" to "${"block/" concat identifier}".jsonPrimitive,
                "y" to y.jsonPrimitive,
            )
        }),
    )
}

fun InitializationScope.generateSimpleCubeAllBlockState(block: Block) {
    onGenerateBlockStateModels { blockStateModelGenerator ->
        blockStateModelGenerator.registerSimpleCubeAll(block)
    }
}

@Deprecated("Removing") // TODO remove
fun InitializationScope.generateSimpleCubeAllBlockState(blockGetter: () -> Block) {
    onGenerateBlockStateModels { blockStateModelGenerator ->
        blockStateModelGenerator.registerSimpleCubeAll(blockGetter())
    }
}

@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.init

import com.google.gson.JsonElement
import miragefairy2023.InitializationScope
import miragefairy2023.util.concat
import miragefairy2023.util.jsonObjectOf
import miragefairy2023.util.jsonPrimitive
import net.minecraft.block.Block
import net.minecraft.data.client.BlockStateSupplier
import net.minecraft.util.Identifier

fun InitializationScope.generateBlockState(block: Block, jsonElementGetter: () -> JsonElement) {
    onGenerateBlockStateModels { blockStateModelGenerator ->
        blockStateModelGenerator.blockStateCollector.accept(object : BlockStateSupplier {
            override fun getBlock() = block
            override fun get() = jsonElementGetter()
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

fun InitializationScope.generateSimpleCubeAllBlockState(block: Block) {
    onGenerateBlockStateModels { blockStateModelGenerator ->
        blockStateModelGenerator.registerSimpleCubeAll(block)
    }
}

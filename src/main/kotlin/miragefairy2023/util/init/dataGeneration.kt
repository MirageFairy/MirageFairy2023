@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.init

import miragefairy2023.util.jsonObjectOf
import miragefairy2023.util.jsonPrimitive
import miragefairy2023.util.string
import net.minecraft.block.Block
import net.minecraft.loot.condition.BlockStatePropertyLootCondition
import net.minecraft.loot.condition.LootCondition
import net.minecraft.state.property.Property
import net.minecraft.util.registry.Registry

inline fun <T> configure(receiver: T, initializer: T.() -> Unit) = receiver.apply(initializer)


fun exactMatchBlockStatePropertyLootCondition(block: Block, property: Property<Int>, value: Int) = LootCondition.Builder {
    BlockStatePropertyLootCondition.Serializer().fromJson(
        jsonObjectOf(
            "block" to Registry.BLOCK.getId(block).string.jsonPrimitive,
            "condition" to "minecraft:block_state_property".jsonPrimitive,
            "properties" to jsonObjectOf(
                property.name to value.jsonPrimitive,
            )
        ), null
    )
}

fun rangedMatchBlockStatePropertyLootCondition(block: Block, property: Property<Int>, min: Int, max: Int) = LootCondition.Builder {
    BlockStatePropertyLootCondition.Serializer().fromJson(
        jsonObjectOf(
            "block" to Registry.BLOCK.getId(block).string.jsonPrimitive,
            "condition" to "minecraft:block_state_property".jsonPrimitive,
            "properties" to jsonObjectOf(
                property.name to jsonObjectOf(
                    "min" to min.jsonPrimitive,
                    "max" to max.jsonPrimitive,
                ),
            )
        ), null
    )
}

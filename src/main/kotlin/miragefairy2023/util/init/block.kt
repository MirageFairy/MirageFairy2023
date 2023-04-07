@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.init

import miragefairy2023.InitializationScope
import miragefairy2023.util.FeatureSlot
import net.minecraft.block.Block
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

fun <T : Block> InitializationScope.block(name: String, blockCreator: () -> T, block: FeatureSlot<T>.() -> Unit = {}): FeatureSlot<T> {
    val id = Identifier(modId, name)
    lateinit var feature: T
    val scope = object : FeatureSlot<T> {
        override val initializationScope get() = this@block
        override val id get() = id
        override val feature get() = feature
    }
    onRegisterBlocks {
        feature = blockCreator()
        Registry.register(Registry.BLOCK, id, feature)
    }
    block(scope)
    return scope
}
@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import miragefairy2023.InitializationScope
import net.minecraft.block.Block
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

interface FeatureSlot<out T> {
    val initializationScope: InitializationScope
    val id: Identifier
    val feature: T
}

fun <T : Block> InitializationScope.block(name: String, blockCreator: () -> T, block: FeatureSlot<T>.() -> Unit = {}): FeatureSlot<T> {
    val id = Identifier(modId, name)
    lateinit var item: T
    val scope = object : FeatureSlot<T> {
        override val initializationScope get() = this@block
        override val id get() = id
        override val feature get() = item
    }
    onRegisterBlocks {
        item = blockCreator()
        Registry.register(Registry.BLOCK, id, scope.feature)
    }
    block(scope)
    return scope
}

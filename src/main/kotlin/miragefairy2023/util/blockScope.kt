@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import miragefairy2023.InitializationScope
import net.minecraft.block.Block
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class BlockScope<T : Block>(val initializationScope: InitializationScope) {
    lateinit var item: T
}

fun <T : Block> InitializationScope.block(blockId: String, blockCreator: () -> T, block: (BlockScope<T>.() -> Unit)? = null): () -> T {
    val scope = BlockScope<T>(this)
    onRegisterBlocks {
        scope.item = blockCreator()
        Registry.register(Registry.BLOCK, Identifier(modId, blockId), scope.item)
    }
    if (block != null) block(scope)
    return { scope.item }
}

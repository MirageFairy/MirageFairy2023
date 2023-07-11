@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.init

import miragefairy2023.InitializationScope
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry

fun <T : Block> InitializationScope.block(name: String, blockCreator: () -> T, initializer: FeatureSlot<T>.() -> Unit = {}): FeatureSlot<T> {
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
    initializer(scope)
    return scope
}

fun <T : BlockEntity> InitializationScope.blockEntity(
    name: String,
    blockEntityCreator: (BlockPos, BlockState) -> T,
    blockGetter: () -> Block,
    initializer: FeatureSlot<BlockEntityType<T>>.() -> Unit = {},
): FeatureSlot<BlockEntityType<T>> {
    val id = Identifier(modId, name)
    lateinit var feature: BlockEntityType<T>
    val scope = object : FeatureSlot<BlockEntityType<T>> {
        override val initializationScope get() = this@blockEntity
        override val id get() = id
        override val feature get() = feature
    }
    onRegisterBlockEntities {
        feature = BlockEntityType(blockEntityCreator, setOf(blockGetter()), null)
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id, feature)
    }
    initializer(scope)
    return scope
}

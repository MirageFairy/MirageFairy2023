package miragefairy2023.util

import net.minecraft.block.Block
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

/** 初期化コンテキストで呼び出すことはできません。 */
val Block.identifier get(): Identifier = Registry.BLOCK.getId(this)

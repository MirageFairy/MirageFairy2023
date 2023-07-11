package miragefairy2023.util

import net.minecraft.block.Block
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

val Block.identifier get(): Identifier = Registry.BLOCK.getId(this)

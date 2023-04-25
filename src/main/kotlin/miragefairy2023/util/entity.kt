@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import net.minecraft.entity.Entity
import net.minecraft.util.math.BlockPos

val Entity.eyeBlockPos get() = BlockPos(this.eyePos)

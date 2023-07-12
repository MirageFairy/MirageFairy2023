@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.init

import net.minecraft.util.Identifier

@Deprecated("Removing") // TODO remove
interface FeatureSlot<out T> {
    val id: Identifier
    val feature: T
}

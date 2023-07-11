@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.init

import miragefairy2023.InitializationScope
import net.minecraft.util.Identifier

@Deprecated("Removing") // TODO remove
interface FeatureSlot<out T> {
    val initializationScope: InitializationScope
    val id: Identifier
    val feature: T
}

@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.init

import miragefairy2023.InitializationScope
import miragefairy2023.util.FeatureSlot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

fun <T : Item> InitializationScope.item(name: String, itemCreator: () -> T, block: FeatureSlot<T>.() -> Unit = {}): FeatureSlot<T> {
    val id = Identifier(modId, name)
    lateinit var feature: T
    val scope = object : FeatureSlot<T> {
        override val initializationScope get() = this@item
        override val id get() = id
        override val feature get() = feature
    }
    onRegisterItems {
        feature = itemCreator()
        Registry.register(Registry.ITEM, id, feature)
    }
    block(scope)
    return scope
}

fun <T : Item> FeatureSlot<T>.registerColorProvider(colorFunction: (ItemStack, Int) -> Int) = initializationScope.onRegisterColorProvider {
    it(feature, colorFunction)
}

@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.init

import miragefairy2023.InitializationScope
import miragefairy2023.MirageFairy2023
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

@Deprecated("Removing") // TODO remove
fun <T : Item> InitializationScope.item(name: String, itemCreator: () -> T, initializer: FeatureSlot<T>.() -> Unit = {}): FeatureSlot<T> {
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
    initializer(scope)
    return scope
}

@Deprecated("Removing") // TODO remove
fun InitializationScope.registerColorProvider(itemGetter: () -> Item, colorFunction: (ItemStack, Int) -> Int) = onInitializeClient {
    MirageFairy2023.clientProxy!!.registerItemColorProvider(itemGetter(), colorFunction)
}

fun InitializationScope.registerColorProvider(item: Item, colorFunction: (ItemStack, Int) -> Int) = onInitializeClient {
    MirageFairy2023.clientProxy!!.registerItemColorProvider(item, colorFunction)
}

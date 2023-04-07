@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import miragefairy2023.InitializationScope
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

interface ItemScope<T> {
    val initializationScope: InitializationScope
    val id: Identifier
    val item: T
}

fun <T : Item> InitializationScope.item(name: String, itemCreator: () -> T, block: ItemScope<T>.() -> Unit = {}): ItemScope<T> {
    val id = Identifier(modId, name)
    lateinit var feature: T
    val scope = object : ItemScope<T> {
        override val initializationScope get() = this@item
        override val id get() = id
        override val item get() = feature
    }
    onRegisterItems {
        feature = itemCreator()
        Registry.register(Registry.ITEM, id, feature)
    }
    block(scope)
    return scope
}

fun <T : Item> ItemScope<T>.registerColorProvider(colorFunction: (ItemStack, Int) -> Int) = initializationScope.onRegisterColorProvider {
    it(item, colorFunction)
}

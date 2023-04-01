@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import miragefairy2023.InitializationScope
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class ItemScope<T : Item>(val initializationScope: InitializationScope) {
    lateinit var item: T
}

fun <T : Item> InitializationScope.item(itemId: String, itemCreator: () -> T, block: (ItemScope<T>.() -> Unit)? = null): () -> T {
    val scope = ItemScope<T>(this)
    onRegisterItems {
        scope.item = itemCreator()
        Registry.register(Registry.ITEM, Identifier(modId, itemId), scope.item)
    }
    if (block != null) block(scope)
    return { scope.item }
}

fun <T : Item> ItemScope<T>.registerColorProvider(colorFunction: (ItemStack, Int) -> Int) = initializationScope.onRegisterColorProvider {
    it(item, colorFunction)
}

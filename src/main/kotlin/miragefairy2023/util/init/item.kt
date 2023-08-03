@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.init

import miragefairy2023.InitializationScope
import miragefairy2023.MirageFairy2023
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

fun InitializationScope.registerColorProvider(item: Item, colorFunction: (ItemStack, Int) -> Int) = onInitializeClient {
    MirageFairy2023.clientProxy!!.registerItemColorProvider(item, colorFunction)
}

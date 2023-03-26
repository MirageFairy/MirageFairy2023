package miragefairy2023.util

import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack

fun ItemConvertible.createItemStack(count: Int = 1) = ItemStack(this, count)

package miragefairy2023.util

import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.ItemStack

fun Enchantment.getRate(itemStack: ItemStack) = EnchantmentHelper.getLevel(this, itemStack) / this.maxLevel.toDouble()

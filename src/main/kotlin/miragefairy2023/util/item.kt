@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient

fun ItemConvertible.toIngredient(): Ingredient = Ingredient.ofItems(this)
fun ItemConvertible.createItemStack(count: Int = 1) = ItemStack(this, count)


val EMPTY_ITEM_STACK: ItemStack get() = ItemStack.EMPTY

val ItemStack.isNotEmpty get() = !this.isEmpty

infix fun ItemStack.hasSameItem(other: ItemStack) = this.item === other.item
infix fun ItemStack.hasSameItemAndNbt(other: ItemStack) = this hasSameItem other && ItemStack.areNbtEqual(this, other)
infix fun ItemStack.hasSameItemAndNbtAndCount(other: ItemStack) = this hasSameItemAndNbt other && this.count == other.count

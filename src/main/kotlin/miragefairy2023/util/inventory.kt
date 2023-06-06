@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import mirrg.kotlin.hydrogen.unit
import net.minecraft.inventory.DoubleInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import kotlin.reflect.KProperty

operator fun Inventory.get(slot: Int): ItemStack = this.getStack(slot)
operator fun Inventory.set(slot: Int, stack: ItemStack) = this.setStack(slot, stack)

operator fun Inventory.plus(other: Inventory) = DoubleInventory(this, other)

fun Inventory(size: Int, maxCountPerStack: Int = 64, itemFilter: SimpleInventory.(ItemStack) -> Boolean = { true }): SimpleInventory {
    return object : SimpleInventory(size) {
        override fun isValid(slot: Int, stack: ItemStack) = itemFilter(stack)
        override fun getMaxCountPerStack() = maxCountPerStack
    }
}

fun Inventory.slot(slot: Int) = InventorySlot(this, slot)

class InventorySlot(val inventory: Inventory, val slot: Int)

operator fun InventorySlot.getValue(thisRef: Any?, property: KProperty<*>): ItemStack = this.inventory[slot]
operator fun InventorySlot.setValue(thisRef: Any?, property: KProperty<*>, value: ItemStack) = unit { this.inventory[slot] = value }

fun Inventory.toList() = (0 until this.size()).map { this[it] }

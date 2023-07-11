@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.datagen

import miragefairy2023.util.init.configure
import net.minecraft.item.ItemConvertible
import net.minecraft.loot.entry.AlternativeEntry
import net.minecraft.loot.entry.GroupEntry
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.entry.LeafEntry
import net.minecraft.loot.entry.LootPoolEntry
import net.minecraft.loot.entry.SequenceEntry

fun itemLootPoolEntry(item: ItemConvertible, initializer: LeafEntry.Builder<*>.() -> Unit = {}): LeafEntry.Builder<*> {
    return configure(ItemEntry.builder(item)!!) { initializer.invoke(this) }
}

fun alternativeLootPoolEntry(vararg children: LootPoolEntry.Builder<*>, initializer: AlternativeEntry.Builder.() -> Unit = {}): AlternativeEntry.Builder {
    return configure(AlternativeEntry.builder(*children)!!) { initializer.invoke(this) }
}

fun groupLootPoolEntry(vararg children: LootPoolEntry.Builder<*>, initializer: GroupEntry.Builder.() -> Unit = {}): GroupEntry.Builder {
    return configure(GroupEntry.create(*children)!!) { initializer.invoke(this) }
}

fun sequenceLootPoolEntry(vararg children: LootPoolEntry.Builder<*>, initializer: SequenceEntry.Builder.() -> Unit = {}): SequenceEntry.Builder {
    return configure(SequenceEntry.create(*children)!!) { initializer.invoke(this) }
}

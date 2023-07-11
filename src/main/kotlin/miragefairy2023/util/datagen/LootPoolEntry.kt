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

@Suppress("FunctionName")
fun ItemLootPoolEntry(item: ItemConvertible, initializer: LeafEntry.Builder<*>.() -> Unit = {}): LeafEntry.Builder<*> = configure(ItemEntry.builder(item)!!) {
    initializer.invoke(this)
}

@Suppress("FunctionName")
fun AlternativeLootPoolEntry(vararg children: LootPoolEntry.Builder<*>, initializer: AlternativeEntry.Builder.() -> Unit = {}): AlternativeEntry.Builder = configure(AlternativeEntry.builder(*children)!!) {
    initializer.invoke(this)
}

@Suppress("FunctionName")
fun GroupLootPoolEntry(vararg children: LootPoolEntry.Builder<*>, initializer: GroupEntry.Builder.() -> Unit = {}): GroupEntry.Builder = configure(GroupEntry.create(*children)!!) {
    initializer.invoke(this)
}

@Suppress("FunctionName")
fun SequenceLootPoolEntry(vararg children: LootPoolEntry.Builder<*>, initializer: SequenceEntry.Builder.() -> Unit = {}): SequenceEntry.Builder = configure(SequenceEntry.create(*children)!!) {
    initializer.invoke(this)
}

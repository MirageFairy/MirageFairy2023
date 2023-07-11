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

fun itemLootPoolEntry(item: ItemConvertible, block: (LeafEntry.Builder<*>.() -> Unit)? = null): LeafEntry.Builder<*> {
    return configure(ItemEntry.builder(item)!!) { block?.invoke(this) }
}

fun alternativeLootPoolEntry(vararg children: LootPoolEntry.Builder<*>, block: (AlternativeEntry.Builder.() -> Unit)? = null): AlternativeEntry.Builder {
    return configure(AlternativeEntry.builder(*children)!!) { block?.invoke(this) }
}

fun groupLootPoolEntry(vararg children: LootPoolEntry.Builder<*>, block: (GroupEntry.Builder.() -> Unit)? = null): GroupEntry.Builder {
    return configure(GroupEntry.create(*children)!!) { block?.invoke(this) }
}

fun sequenceLootPoolEntry(vararg children: LootPoolEntry.Builder<*>, block: (SequenceEntry.Builder.() -> Unit)? = null): SequenceEntry.Builder {
    return configure(SequenceEntry.create(*children)!!) { block?.invoke(this) }
}

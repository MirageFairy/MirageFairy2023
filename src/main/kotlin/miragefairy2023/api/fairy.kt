package miragefairy2023.api

import net.minecraft.item.Item
import net.minecraft.util.Identifier

interface FairyItem {
    fun getFairy(): Fairy
}

interface Fairy {
    fun getIdentifier(): Identifier
    fun getItem(): Item
}

package miragefairy2023.api

import miragefairy2023.MirageFairy2023
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.fabricmc.fabric.api.event.registry.RegistryAttribute
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.registry.SimpleRegistry

interface FairyItem {
    fun getFairy(): Fairy
}

interface Fairy {
    fun getIdentifier(): Identifier
    fun getItem(): Item
    fun getRare(): Int
}

val fairyRegistry: SimpleRegistry<Fairy> = FabricRegistryBuilder.createSimple(Fairy::class.java, Identifier(MirageFairy2023.modId, "fairy"))
    .attribute(RegistryAttribute.SYNCED)
    .buildAndRegister()

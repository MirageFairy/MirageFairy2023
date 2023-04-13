package miragefairy2023.api

import miragefairy2023.MirageFairy2023
import miragefairy2023.modules.fairy.FairyCard
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
}

val fairyRegistry: SimpleRegistry<FairyCard> = FabricRegistryBuilder.createSimple(FairyCard::class.java, Identifier(MirageFairy2023.modId, "fairy"))
    .attribute(RegistryAttribute.SYNCED)
    .buildAndRegister()

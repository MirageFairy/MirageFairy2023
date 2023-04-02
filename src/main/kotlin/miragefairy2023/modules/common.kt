package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.createItemStack
import miragefairy2023.util.enJaItemGroup
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier

val commonItemGroup: ItemGroup = FabricItemGroupBuilder.build(Identifier(MirageFairy2023.modId, "common")) { DemonItemCard.XARPITE().createItemStack() }

val commonModule = module {
    enJaItemGroup({ commonItemGroup }, "MirageFairy2023", "MirageFairy2023")
}

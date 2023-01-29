package miragefairy2023

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object MirageFairy2023 : ModInitializer {
    val modId = "miragefairy2023"

    val XARPITE = Item(FabricItemSettings().group(ItemGroup.MATERIALS))

    override fun onInitialize() {
        Registry.register(Registry.ITEM, Identifier(modId, "xarpite"), XARPITE)
    }
}

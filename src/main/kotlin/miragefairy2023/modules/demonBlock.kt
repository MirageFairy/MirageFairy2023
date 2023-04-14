package miragefairy2023.modules

import miragefairy2023.module
import miragefairy2023.util.gray
import miragefairy2023.util.init.FeatureSlot
import miragefairy2023.util.init.block
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.enJaBlock
import miragefairy2023.util.init.generateSimpleCubeAllBlockState
import miragefairy2023.util.init.item
import miragefairy2023.util.text
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.tag.BlockTags
import net.minecraft.text.Text
import net.minecraft.world.World

lateinit var creativeAuraStoneBlock: FeatureSlot<Block>
lateinit var creativeAuraStoneBlockItem: FeatureSlot<BlockItem>

val demonBlockModule = module {

    creativeAuraStoneBlock = block("creative_aura_stone", { Block(FabricBlockSettings.of(Material.STONE).strength(-1.0F, 3600000.0F).dropsNothing().allowsSpawning { _, _, _, _ -> false }) }) {
        generateSimpleCubeAllBlockState()
        enJaBlock({ feature }, "Neutronium Block", "アカーシャの霊氣石")
        enJa({ "${feature.translationKey}.poem" }, "Hypothetical substance with ideal hardness", "終末と創造の波紋。")
        onGenerateBlockTags { it(BlockTags.DRAGON_IMMUNE).add(feature) }
        onGenerateBlockTags { it(BlockTags.WITHER_IMMUNE).add(feature) }
        onGenerateBlockTags { it(BlockTags.FEATURES_CANNOT_REPLACE).add(feature) }
        onGenerateBlockTags { it(BlockTags.GEODE_INVALID_BLOCKS).add(feature) }
    }
    creativeAuraStoneBlockItem = item("creative_aura_stone", {
        object : BlockItem(creativeAuraStoneBlock.feature, FabricItemSettings().group(commonItemGroup)) {
            override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
                super.appendTooltip(stack, world, tooltip, context)
                tooltip += text { translate("$translationKey.poem").gray }
            }
        }
    })

}

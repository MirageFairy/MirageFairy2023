package miragefairy2023.modules.fairyhouse

import miragefairy2023.MirageFairy2023
import miragefairy2023.RenderingProxyBlockEntity
import miragefairy2023.module
import miragefairy2023.modules.DemonItemCard
import miragefairy2023.modules.commonItemGroup
import miragefairy2023.modules.invoke
import miragefairy2023.util.gray
import miragefairy2023.util.identifier
import miragefairy2023.util.init.FeatureSlot
import miragefairy2023.util.init.block
import miragefairy2023.util.init.blockEntity
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.enJaBlock
import miragefairy2023.util.init.generateDefaultBlockLootTable
import miragefairy2023.util.init.generateHorizontalFacingBlockState
import miragefairy2023.util.init.group
import miragefairy2023.util.init.item
import miragefairy2023.util.text
import miragefairy2023.util.yellow
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.tag.BlockTags
import net.minecraft.tag.TagKey
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class FairyHouseCard<B, BE>(
    val path: String,
    val blockCreator: (AbstractBlock.Settings) -> B,
    val blockEntityCreator: (BlockPos, BlockState) -> BE,
    val enName: String,
    val jaName: String,
    val enPoem: String,
    val jaPoem: String,
    val enDescription: String,
    val jaDescription: String,
    val material: Material,
    val soundGroup: BlockSoundGroup,
    val needsToolTag: TagKey<Block>?,
) where B : Block, BE : BlockEntity, BE : RenderingProxyBlockEntity {
    companion object {
        val FAIRY_FLUID_DRAINER = FairyHouseCard(
            "fairy_fluid_drainer", ::FairyFluidDrainerBlock, ::FairyFluidDrainerBlockEntity,
            "Fairy Fluid Drainer", "妖精の水汲み所",
            "Causes anti-Brownian motion", "覆水、盆に返る。",
            "Place a liquid fairy and a bucket", "液体系妖精と空バケツを配置",
            Material.METAL, BlockSoundGroup.METAL, BlockTags.NEEDS_STONE_TOOL,
        )
    }

    lateinit var block: FeatureSlot<B>
    lateinit var blockEntityType: FeatureSlot<BlockEntityType<BE>>
    lateinit var blockItem: FeatureSlot<BlockItem>
}

object FairyHouseModule {
    val init = module {

        fun <B, BE> registerFairyHouse(card: FairyHouseCard<B, BE>) where B : Block, BE : BlockEntity, BE : RenderingProxyBlockEntity {
            card.block = block(card.path, { card.blockCreator(FabricBlockSettings.of(card.material).sounds(card.soundGroup).requiresTool().strength(2.0F).nonOpaque()) }) {

                // レンダリング
                generateHorizontalFacingBlockState()
                onInitializeClient { MirageFairy2023.clientProxy!!.registerCutoutBlockRenderLayer(feature) }

                // 翻訳
                enJaBlock({ feature }, card.enName, card.jaName)
                enJa({ "${feature.translationKey}.poem" }, card.enPoem, card.jaPoem)
                enJa({ "${feature.translationKey}.description" }, card.enDescription, card.jaDescription)

                // レシピ
                onGenerateBlockTags { it(BlockTags.PICKAXE_MINEABLE).add(feature) }
                if (card.needsToolTag != null) onGenerateBlockTags { it(card.needsToolTag).add(feature) }
                generateDefaultBlockLootTable()

            }
            card.blockEntityType = blockEntity(card.path, card.blockEntityCreator, { card.block.feature }) {
                onInitializeClient { MirageFairy2023.clientProxy!!.registerRenderingProxyBlockEntityRendererFactory(feature) }
            }
            card.blockItem = item(card.path, {
                object : BlockItem(card.block.feature, FabricItemSettings().group(commonItemGroup)) {
                    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
                        super.appendTooltip(stack, world, tooltip, context)
                        tooltip += text { translate("$translationKey.poem").gray }
                        tooltip += text { translate("$translationKey.description").yellow }
                    }
                }
            })
        }

        // 妖精の水汲み所
        registerFairyHouse(FairyHouseCard.FAIRY_FLUID_DRAINER)
        onGenerateRecipes {
            ShapedRecipeJsonBuilder
                .create(FairyHouseCard.FAIRY_FLUID_DRAINER.blockItem.feature)
                .pattern("FMB")
                .pattern("III")
                .input('I', ConventionalItemTags.IRON_INGOTS)
                .input('F', Items.IRON_BARS)
                .input('M', DemonItemCard.MIRANAGITE())
                .input('B', Items.BUCKET)
                .criterion(DemonItemCard.MIRANAGITE())
                .group(FairyHouseCard.FAIRY_FLUID_DRAINER.blockItem.feature)
                .offerTo(it, FairyHouseCard.FAIRY_FLUID_DRAINER.blockItem.feature.identifier)
        }

    }
}

package miragefairy2023.modules.fairyhouse

import miragefairy2023.RenderingProxyBlockEntity
import miragefairy2023.module
import miragefairy2023.modules.DemonItemCard
import miragefairy2023.modules.invoke
import miragefairy2023.util.identifier
import miragefairy2023.util.init.FeatureSlot
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.group
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.BlockItem
import net.minecraft.item.Items
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.tag.BlockTags
import net.minecraft.tag.TagKey
import net.minecraft.util.math.BlockPos

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

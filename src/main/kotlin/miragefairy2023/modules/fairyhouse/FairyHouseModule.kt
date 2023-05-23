package miragefairy2023.modules.fairyhouse

import miragefairy2023.module
import miragefairy2023.modules.DemonItemCard
import miragefairy2023.modules.invoke
import miragefairy2023.util.identifier
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.group
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.block.Material
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.Items
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.tag.BlockTags

val fairyFluidDrainer = FairyHouseCard(
    "fairy_fluid_drainer", ::FairyFluidDrainerBlock, ::FairyFluidDrainerBlockEntity,
    "Fairy Fluid Drainer", "妖精の水汲み所",
    "Causes anti-Brownian motion", "覆水、盆に返る。",
    "Place a liquid fairy and a bucket", "液体系妖精と空バケツを配置",
    Material.METAL, BlockSoundGroup.METAL, BlockTags.NEEDS_STONE_TOOL,
)

object FairyHouseModule {
    val init = module {

        // 妖精の水汲み所
        registerFairyHouse(fairyFluidDrainer)
        onGenerateRecipes {
            ShapedRecipeJsonBuilder
                .create(fairyFluidDrainer.blockItem.feature)
                .pattern("FMB")
                .pattern("III")
                .input('I', ConventionalItemTags.IRON_INGOTS)
                .input('F', Items.IRON_BARS)
                .input('M', DemonItemCard.MIRANAGITE())
                .input('B', Items.BUCKET)
                .criterion(DemonItemCard.MIRANAGITE())
                .group(fairyFluidDrainer.blockItem.feature)
                .offerTo(it, fairyFluidDrainer.blockItem.feature.identifier)
        }

    }
}

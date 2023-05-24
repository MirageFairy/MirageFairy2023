package miragefairy2023.modules.fairyhouse

import miragefairy2023.module
import miragefairy2023.modules.DemonItemCard
import miragefairy2023.modules.MirageFlourCard
import miragefairy2023.modules.invoke
import miragefairy2023.modules.miranagiteBlockBlockItem
import miragefairy2023.util.identifier
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.group
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.Material
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.math.BlockPos

val fairyMetamorphosisAltar = FairyHouseCard(
    "fairy_metamorphosis_altar", ::FairyMetamorphosisAltarBlockEntity,
    "Fairy Metamorphosis Altar", "妖精の魔法の祭壇",
    "Weaken the nuclear force to resonate", "妖精と無機物が心を通わすとき。",
    "Place 4 fairies and 1 material", "4体の妖精と素材を配置",
    Material.STONE, BlockSoundGroup.STONE, null,
    Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 5.0, 15.0),
)

val fairyMetamorphosisAltarModule = module {
    registerFairyHouse(fairyMetamorphosisAltar)
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(fairyMetamorphosisAltar.blockItem.feature)
            .pattern(" B ")
            .pattern("GDG")
            .pattern("SSS")
            .input('B', miranagiteBlockBlockItem.feature)
            .input('G', DemonItemCard.MIRANAGITE())
            .input('D', MirageFlourCard.MIRAGE_FLOUR())
            .input('S', Blocks.STONE)
            .criterion(DemonItemCard.MIRANAGITE())
            .group(fairyMetamorphosisAltar.blockItem.feature)
            .offerTo(it, fairyMetamorphosisAltar.blockItem.feature.identifier)
    }
}

class FairyMetamorphosisAltarBlockEntity(pos: BlockPos, state: BlockState) : FairyHouseBlockEntity(fairyMetamorphosisAltar.blockEntityType.feature, pos, state) {

}

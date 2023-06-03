package miragefairy2023.modules

import miragefairy2023.module
import miragefairy2023.util.gray
import miragefairy2023.util.identifier
import miragefairy2023.util.init.FeatureSlot
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.enJaItem
import miragefairy2023.util.init.group
import miragefairy2023.util.init.item
import miragefairy2023.util.text
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.client.Models
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.PickaxeItem
import net.minecraft.item.ToolMaterial
import net.minecraft.tag.ItemTags
import net.minecraft.text.Text
import net.minecraft.world.World

enum class ToolItemCard(
    val path: String,
    val enName: String,
    val jaName: String,
    val enPoem: String,
    val jaPoem: String,
    val toolMaterial: ToolMaterial,
    val attackDamage: Int,
    val attackSpeed: Float,
) {
    ARTIFICIAL_FAIRY_CRYSTAL_PICKAXE(
        "artificial_fairy_crystal_pickaxe", "Crystal Pickaxe", "クリスタルのつるはし",
        "Amorphous mental body of fairies", "妖精さえ怖れる、技術の結晶。",
        DemonToolMaterials.ARTIFICIAL_FAIRY_CRYSTAL, 1, -2.8F,
    ),
    CHAOS_STONE_PICKAXE(
        "chaos_stone_pickaxe", "Chaos Pickaxe", "混沌のつるはし",
        "Is this made of metal? Or clay?", "時空結晶の交点に、古代の産業が芽吹く。",
        DemonToolMaterials.CHAOS_STONE, 1, -2.8F,
    ),
    ;

    lateinit var item: FeatureSlot<Item>
}

val toolItemModule = module {

    // 全体
    ToolItemCard.values().forEach { card ->
        card.item = item(card.path, {
            object : PickaxeItem(card.toolMaterial, card.attackDamage, card.attackSpeed, FabricItemSettings().group(commonItemGroup)) {
                override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
                    super.appendTooltip(stack, world, tooltip, context)
                    tooltip += text { translate("$translationKey.poem").gray }
                }
            }
        }) {
            onGenerateItemModels { it.register(feature, Models.HANDHELD) }
            enJaItem({ feature }, card.enName, card.jaName)
            enJa({ "${feature.translationKey}.poem" }, card.enPoem, card.jaPoem)
            onGenerateItemTags { it(ItemTags.CLUSTER_MAX_HARVESTABLES).add(feature) }
            onGenerateItemTags { it(ConventionalItemTags.PICKAXES).add(feature) }
        }
    }

    // クリスタルのつるはし
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PICKAXE.item.feature)
            .pattern("GGG")
            .pattern(" S ")
            .pattern(" S ")
            .input('G', DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL())
            .input('S', Items.STICK)
            .criterion(DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL())
            .group(ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PICKAXE.item.feature)
            .offerTo(it, ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PICKAXE.item.feature.identifier)
    }

    // 混沌のつるはし
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.CHAOS_STONE_PICKAXE.item.feature)
            .pattern("GGG")
            .pattern(" S ")
            .pattern(" S ")
            .input('G', DemonItemCard.CHAOS_STONE())
            .input('S', Items.STICK)
            .criterion(DemonItemCard.CHAOS_STONE())
            .group(ToolItemCard.CHAOS_STONE_PICKAXE.item.feature)
            .offerTo(it, ToolItemCard.CHAOS_STONE_PICKAXE.item.feature.identifier)
    }

}

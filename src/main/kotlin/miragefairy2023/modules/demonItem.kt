package miragefairy2023.modules

import miragefairy2023.SlotContainer
import miragefairy2023.module
import miragefairy2023.util.enJa
import miragefairy2023.util.enJaItem
import miragefairy2023.util.gray
import miragefairy2023.util.item
import miragefairy2023.util.registerBlockDrop
import miragefairy2023.util.registerGrassDrop
import miragefairy2023.util.registerMobDrop
import miragefairy2023.util.text
import miragefairy2023.util.uniformLootNumberProvider
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.Blocks
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.client.Models
import net.minecraft.data.server.RecipeProvider
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.tag.ItemTags
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.world.World


enum class DemonItemCard(
    val itemId: String,
    val enName: String,
    val jaName: String,
    val enPoem: String,
    val jaPoem: String,
) {
    XARPITE(
        "xarpite", "Xarpite", "紅天石",
        "Binds astral flux with magnetic force", "黒鉄の鎖は繋がれる。血腥い魂の檻へ。",
    ),
    MIRANAGITE(
        "miranagite", "Miranagite", "蒼天石",
        "Astral body crystallized by anti-entropy", "秩序の叛乱、天地創造の逆光。",
    ),

    // ミラージュの葉
    // 硝子のような触り心地。
    // 鋭利なため気を付けてください
    MIRAGE_STEM(
        "mirage_stem", "Mirage Stem", "ミラージュの茎",
        "Cell wall composed of amorphous ether", "植物が手掛ける、分子レベルの硝子細工。",
    ),
}

private val demonItems = SlotContainer<DemonItemCard, Item>()
operator fun DemonItemCard.invoke() = demonItems[this]


val demonItemModule = module {

    // 全体
    DemonItemCard.values().forEach { card ->
        item(card.itemId, { DemonItem(FabricItemSettings().group(commonItemGroup)) }) {
            onRegisterItems { demonItems[card] = item }

            onGenerateItemModels { it.register(item, Models.GENERATED) }

            enJaItem({ item }, card.enName, card.jaName)
            enJa({ "${item.translationKey}.poem" }, card.enPoem, card.jaPoem)
        }
    }

    // 魔女→紅天石
    registerMobDrop({ EntityType.WITCH }, { DemonItemCard.XARPITE() }, onlyKilledByPlayer = true, fortuneFactor = uniformLootNumberProvider(0.0f, 1.0f))

    // ゾンビ→紅天石
    registerMobDrop({ EntityType.ZOMBIE }, { DemonItemCard.XARPITE() }, onlyKilledByPlayer = true, dropRate = Pair(0.02f, 0.01f))
    registerMobDrop({ EntityType.ZOMBIE_VILLAGER }, { DemonItemCard.XARPITE() }, onlyKilledByPlayer = true, dropRate = Pair(0.02f, 0.01f))
    registerMobDrop({ EntityType.DROWNED }, { DemonItemCard.XARPITE() }, onlyKilledByPlayer = true, dropRate = Pair(0.02f, 0.01f))
    registerMobDrop({ EntityType.HUSK }, { DemonItemCard.XARPITE() }, onlyKilledByPlayer = true, dropRate = Pair(0.02f, 0.01f))

    // 雑草→紅天石
    registerGrassDrop({ DemonItemCard.XARPITE() }, 0.01)

    // エメラルド鉱石→蒼天石
    registerBlockDrop({ Blocks.EMERALD_ORE }, { DemonItemCard.MIRANAGITE() }, fortuneOreDrops = true)
    registerBlockDrop({ Blocks.DEEPSLATE_EMERALD_ORE }, { DemonItemCard.MIRANAGITE() }, fortuneOreDrops = true)

    // 銅鉱石→蒼天石
    registerBlockDrop({ Blocks.COPPER_ORE }, { DemonItemCard.MIRANAGITE() }, dropRate = 0.05f, fortuneOreDrops = true)
    registerBlockDrop({ Blocks.DEEPSLATE_COPPER_ORE }, { DemonItemCard.MIRANAGITE() }, dropRate = 0.05f, fortuneOreDrops = true)

    // 雑草→蒼天石
    registerGrassDrop({ DemonItemCard.MIRANAGITE() }, 0.01)

    // 蒼天石＋2マグマクリーム→スライムボール
    onGenerateRecipes {
        ShapelessRecipeJsonBuilder
            .create(Items.SLIME_BALL)
            .input(DemonItemCard.MIRANAGITE())
            .input(Items.MAGMA_CREAM)
            .input(Items.MAGMA_CREAM)
            .criterion("has_miranagite", RecipeProvider.conditionsFromItem(DemonItemCard.MIRANAGITE()))
            .criterion("has_magma_cream", RecipeProvider.conditionsFromItem(Items.MAGMA_CREAM))
            .offerTo(it, Identifier.of(modId, "slime_ball_from_anti_entropy"))
    }

    // 蒼天石＋4マグマクリーム→ブレイズパウダー
    onGenerateRecipes {
        ShapelessRecipeJsonBuilder
            .create(Items.BLAZE_POWDER)
            .input(DemonItemCard.MIRANAGITE())
            .input(Items.MAGMA_CREAM)
            .input(Items.MAGMA_CREAM)
            .input(Items.MAGMA_CREAM)
            .input(Items.MAGMA_CREAM)
            .criterion("has_miranagite", RecipeProvider.conditionsFromItem(DemonItemCard.MIRANAGITE()))
            .criterion("has_magma_cream", RecipeProvider.conditionsFromItem(Items.MAGMA_CREAM))
            .offerTo(it, Identifier.of(modId, "blaze_powder_from_anti_entropy"))
    }

    // 2ミラージュの茎→棒
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(Items.STICK)
            .pattern("S")
            .pattern("S")
            .input('S', DemonItemCard.MIRAGE_STEM())
            .criterion("has_mirage_stem", RecipeProvider.conditionsFromItem(DemonItemCard.MIRAGE_STEM()))
            .offerTo(it, Identifier.of(modId, "stick_from_mirage_stem"))
    }

    // ミラージュの茎＋羊毛→糸
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(Items.STRING)
            .pattern("W")
            .pattern("S")
            .input('W', ItemTags.WOOL)
            .input('S', DemonItemCard.MIRAGE_STEM())
            .criterion("has_wool", RecipeProvider.conditionsFromTag(ItemTags.WOOL))
            .criterion("has_mirage_stem", RecipeProvider.conditionsFromItem(DemonItemCard.MIRAGE_STEM()))
            .offerTo(it, Identifier.of(modId, "string_from_mirage_stem"))
    }

}


open class DemonItem(settings: Settings) : Item(settings) {
    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)
        tooltip += text { translate("$translationKey.poem").gray }
    }
}

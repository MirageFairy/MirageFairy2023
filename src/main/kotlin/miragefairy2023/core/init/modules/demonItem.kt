package miragefairy2023.core.init.modules

import miragefairy2023.core.init.SlotContainer
import miragefairy2023.core.init.module
import miragefairy2023.util.gray
import miragefairy2023.util.item
import miragefairy2023.util.registerFuel
import miragefairy2023.util.registerGrassDrop
import miragefairy2023.util.registerMobDrop
import miragefairy2023.util.text
import miragefairy2023.util.uniformLootNumberProvider
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.client.Models
import net.minecraft.data.server.RecipeProvider
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
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
        "Binds astral flux with magnetic force",
        "黒鉄の鎖は繋がれる。血腥い魂の檻へ。",
    ),
    MIRANAGITE(
        "miranagite", "Miranagite", "蒼天石",
        "Astral body crystallized by anti-entropy",
        "秩序の叛乱、天地創造の逆光。",
    ),
    TINY_MIRAGE_FLOUR(
        "tiny_mirage_flour", "Tiny Pile of Mirage Flour", "ミラージュの花粉",
        "Compose the body of Mirage fairy",
        "ささやかな温もりを、てのひらの上に。",
    ),
    MIRAGE_FLOUR(
        "mirage_flour", "Mirage Flour", "ミラージュフラワー",
        "Containing metallic organic matter",
        "創発のファンタズム",
    ),
}

private val demonItems = SlotContainer<DemonItemCard, DemonItem>()
operator fun DemonItemCard.invoke() = demonItems[this]


val demonItemModule = module {

    // 全体
    DemonItemCard.values().forEach { card ->
        item(card.itemId, { DemonItem(FabricItemSettings().group(ItemGroup.MATERIALS)) }) {
            onRegisterItems {
                demonItems[card] = item
            }

            onGenerateEnglishTranslations { it.add(item, card.enName) }
            onGenerateEnglishTranslations { it.add("${item.translationKey}.poem", card.enPoem) }
            onGenerateJapaneseTranslations { it.add(item, card.jaName) }
            onGenerateJapaneseTranslations { it.add("${item.translationKey}.poem", card.jaPoem) }

            onGenerateItemModels { it.register(item, Models.GENERATED) }
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

    // 紅天石→松明
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(Items.TORCH, 8)
            .input('A', DemonItemCard.XARPITE())
            .input('B', Items.STICK)
            .pattern("A")
            .pattern("B")
            .criterion("has_xarpite", RecipeProvider.conditionsFromItem(DemonItemCard.XARPITE()))
            .offerTo(it, Identifier.of(modId, "torch_from_xarpite"))
    }

    // 紅天石→燃料
    registerFuel({ DemonItemCard.XARPITE() }, 1600)

    // 雑草→蒼天石
    registerGrassDrop({ DemonItemCard.MIRANAGITE() }, 0.01)

    // 2マグマクリーム＋蒼天石→スライムボール
    onGenerateRecipes {
        ShapelessRecipeJsonBuilder
            .create(Items.SLIME_BALL)
            .input(Items.MAGMA_CREAM)
            .input(Items.MAGMA_CREAM)
            .input(DemonItemCard.MIRANAGITE())
            .criterion("has_magma_cream", RecipeProvider.conditionsFromItem(Items.MAGMA_CREAM))
            .criterion("has_miranagite", RecipeProvider.conditionsFromItem(DemonItemCard.MIRANAGITE()))
            .offerTo(it, Identifier.of(modId, "slime_ball_from_anti_entropy"))
    }

    // 4マグマクリーム＋蒼天石→ブレイズパウダー
    onGenerateRecipes {
        ShapelessRecipeJsonBuilder
            .create(Items.BLAZE_POWDER)
            .input(Items.MAGMA_CREAM)
            .input(Items.MAGMA_CREAM)
            .input(Items.MAGMA_CREAM)
            .input(Items.MAGMA_CREAM)
            .input(DemonItemCard.MIRANAGITE())
            .criterion("has_magma_cream", RecipeProvider.conditionsFromItem(Items.MAGMA_CREAM))
            .criterion("has_miranagite", RecipeProvider.conditionsFromItem(DemonItemCard.MIRANAGITE()))
            .offerTo(it, Identifier.of(modId, "blaze_powder_from_anti_entropy"))
    }

    // ミラージュの花粉⇔ミラージュフラワー
    onGenerateRecipes {
        ShapelessRecipeJsonBuilder
            .create(DemonItemCard.MIRAGE_FLOUR())
            .input(DemonItemCard.TINY_MIRAGE_FLOUR())
            .input(DemonItemCard.TINY_MIRAGE_FLOUR())
            .input(DemonItemCard.TINY_MIRAGE_FLOUR())
            .input(DemonItemCard.TINY_MIRAGE_FLOUR())
            .input(DemonItemCard.TINY_MIRAGE_FLOUR())
            .input(DemonItemCard.TINY_MIRAGE_FLOUR())
            .input(DemonItemCard.TINY_MIRAGE_FLOUR())
            .input(DemonItemCard.TINY_MIRAGE_FLOUR())
            .criterion("has_tiny_mirage_flour", RecipeProvider.conditionsFromItem(DemonItemCard.TINY_MIRAGE_FLOUR()))
            .offerTo(it, Identifier.of(modId, "mirage_flour"))
    }
    onGenerateRecipes {
        ShapelessRecipeJsonBuilder
            .create(DemonItemCard.TINY_MIRAGE_FLOUR(), 8)
            .input(DemonItemCard.MIRAGE_FLOUR())
            .criterion("has_mirage_flour", RecipeProvider.conditionsFromItem(DemonItemCard.MIRAGE_FLOUR()))
            .offerTo(it, Identifier.of(modId, "tiny_mirage_flour_from_mirage_flour"))
    }

}


class DemonItem(settings: Settings) : Item(settings) {
    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)
        tooltip += text { translate("$translationKey.poem").gray }
    }
}

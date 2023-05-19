package miragefairy2023.modules

import miragefairy2023.SlotContainer
import miragefairy2023.module
import miragefairy2023.util.concat
import miragefairy2023.util.gray
import miragefairy2023.util.identifier
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.enJaItem
import miragefairy2023.util.init.group
import miragefairy2023.util.init.item
import miragefairy2023.util.init.registerBlockDrop
import miragefairy2023.util.init.registerGrassDrop
import miragefairy2023.util.init.registerMobDrop
import miragefairy2023.util.init.uniformLootNumberProvider
import miragefairy2023.util.text
import miragefairy2023.util.toIngredient
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.yarn.constants.MiningLevels
import net.minecraft.block.Blocks
import net.minecraft.block.ComposterBlock
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.client.Models
import net.minecraft.data.server.RecipeProvider
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder
import net.minecraft.data.server.recipe.SingleItemRecipeJsonBuilder
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.ToolMaterial
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
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
    HONORABLE_FAIRY_CRYSTAL(
        "honorable_fairy_crystal", "Honorable Fairy Crystal", "名誉のフェアリークリスタル",
        "Appear out of nowhere", "妖精からの贈り物",
    ),
    GLORIOUS_FAIRY_CRYSTAL(
        "glorious_fairy_crystal", "Glorious Fairy Crystal", "栄光のフェアリークリスタル",
        "Not a substance formed in this world", "精霊王の表彰状",
    ),
    LEGENDARY_FAIRY_CRYSTAL(
        "legendary_fairy_crystal", "Legendary Fairy Crystal", "伝説のフェアリークリスタル",
        "Pluto exploded", "冥王が跳ね上がった",
    ),
    ARTIFICIAL_FAIRY_CRYSTAL(
        "artificial_fairy_crystal", "Artificial Fairy Crystal", "人工フェアリークリスタル",
        "Uncanny crystal not worth even 1 Minia", "妖精さえ怖れる、技術の結晶。",
    ),

    /*
    FAIRY_CRYSTAL_1(
        "fairy_crystal_1", "1 Minia Crystal", "1ミーニャクリスタル",
        "", "ガラスより硬い", // TODO
    ),
    FAIRY_CRYSTAL_5(
        "fairy_crystal_5", "5 Minia Crystal", "5ミーニャクリスタル",
        "Fairy snack", "", // TODO
    ),
    FAIRY_CRYSTAL_10(
        "fairy_crystal_10", "10 Minia Crystal", "10ミーニャクリスタル",
        "The Society failed to replicate this", "妖精の業が磨き上げる", // TODO
    ),
    */
    FAIRY_CRYSTAL_50(
        "fairy_crystal_50", "50 Minia Crystal", "50ミーニャクリスタル",
        "Has the same hardness as beryl", "世界で50番目に優美な有機結晶。",
    ),
    FAIRY_CRYSTAL_100(
        "fairy_crystal_100", "100 Minia Crystal", "100ミーニャクリスタル",
        "Created by the fairies of commerce", "妖精と人間が交差する世界。",
    ),
    FAIRY_CRYSTAL_500(
        "fairy_crystal_500", "500 Minia Crystal", "500ミーニャクリスタル",
        "Crystallized Mirage flower nectar", "ミラージュの蜜よ、永遠の宝石となれ。",
    ),
    /*
    FAIRY_CRYSTAL_1000(
        "fairy_crystal_1000", "1000 Minia Crystal", "1000ミーニャクリスタル",
        "有毒な揮発成分の味がする", "", // TODO
    ),
    FAIRY_CRYSTAL_5000(
        "fairy_crystal_5000", "5000 Minia Crystal", "5000ミーニャクリスタル",
        "", "", // TODO
    ),
    FAIRY_CRYSTAL_10000(
        "fairy_crystal_10000", "10000 Minia Crystal", "10000ミーニャクリスタル",
        "", "妖精の誇り。", // TODO
    ),
    */
}

private val demonItems = SlotContainer<DemonItemCard, Item>()
operator fun DemonItemCard.invoke() = demonItems[this]


enum class DemonToolMaterials(
    private val durability: Int,
    private val miningSpeedMultiplier: Float,
    private val attackDamage: Float,
    private val miningLevel: Int,
    private val enchantability: Int,
    private val repairIngredient: () -> Ingredient,
) : ToolMaterial {
    MIRAGE(48, 1.6F, 0.0F, MiningLevels.WOOD, 17, { DemonItemCard.MIRAGE_STEM().toIngredient() }),
    ;

    override fun getDurability() = durability
    override fun getMiningSpeedMultiplier() = miningSpeedMultiplier
    override fun getAttackDamage() = attackDamage
    override fun getMiningLevel() = miningLevel
    override fun getEnchantability() = enchantability
    override fun getRepairIngredient() = repairIngredient()
}


val demonItemModule = module {

    // 全体
    DemonItemCard.values().forEach { card ->
        item(card.itemId, { DemonItem(FabricItemSettings().group(commonItemGroup)) }) {
            onRegisterItems { demonItems[card] = feature }

            onGenerateItemModels { it.register(feature, Models.GENERATED) }

            enJaItem({ feature }, card.enName, card.jaName)
            enJa({ "${feature.translationKey}.poem" }, card.enPoem, card.jaPoem)
        }
    }

    // 魔女→紅天石
    registerMobDrop({ EntityType.WITCH }, { DemonItemCard.XARPITE() }, onlyKilledByPlayer = true, fortuneFactor = uniformLootNumberProvider(0.0F, 1.0F))

    // ゾンビ→紅天石
    registerMobDrop({ EntityType.ZOMBIE }, { DemonItemCard.XARPITE() }, onlyKilledByPlayer = true, dropRate = Pair(0.02F, 0.01F))
    registerMobDrop({ EntityType.ZOMBIE_VILLAGER }, { DemonItemCard.XARPITE() }, onlyKilledByPlayer = true, dropRate = Pair(0.02F, 0.01F))
    registerMobDrop({ EntityType.DROWNED }, { DemonItemCard.XARPITE() }, onlyKilledByPlayer = true, dropRate = Pair(0.02F, 0.01F))
    registerMobDrop({ EntityType.HUSK }, { DemonItemCard.XARPITE() }, onlyKilledByPlayer = true, dropRate = Pair(0.02F, 0.01F))

    // 雑草→紅天石
    registerGrassDrop({ DemonItemCard.XARPITE() }, 0.01)

    // エメラルド鉱石→蒼天石
    registerBlockDrop({ Blocks.EMERALD_ORE }, { DemonItemCard.MIRANAGITE() }, fortuneOreDrops = true, suppressIfSilkTouch = true)
    registerBlockDrop({ Blocks.DEEPSLATE_EMERALD_ORE }, { DemonItemCard.MIRANAGITE() }, fortuneOreDrops = true, suppressIfSilkTouch = true)

    // 銅鉱石→蒼天石
    registerBlockDrop({ Blocks.COPPER_ORE }, { DemonItemCard.MIRANAGITE() }, dropRate = 0.05F, fortuneOreDrops = true, suppressIfSilkTouch = true)
    registerBlockDrop({ Blocks.DEEPSLATE_COPPER_ORE }, { DemonItemCard.MIRANAGITE() }, dropRate = 0.05F, fortuneOreDrops = true, suppressIfSilkTouch = true)

    // 雑草→蒼天石
    registerGrassDrop({ DemonItemCard.MIRANAGITE() }, 0.01)

    // 蒼天石＋2マグマクリーム→スライムボール
    onGenerateRecipes {
        ShapelessRecipeJsonBuilder
            .create(Items.SLIME_BALL)
            .input(DemonItemCard.MIRANAGITE())
            .input(Items.MAGMA_CREAM)
            .input(Items.MAGMA_CREAM)
            .criterion(Items.MAGMA_CREAM)
            .group("slime_balls")
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
            .criterion(Items.MAGMA_CREAM)
            .group("blaze_powders")
            .offerTo(it, Identifier.of(modId, "blaze_powder_from_anti_entropy"))
    }

    // 2ミラージュの茎→棒
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(Items.STICK)
            .pattern("S")
            .pattern("S")
            .input('S', DemonItemCard.MIRAGE_STEM())
            .criterion(DemonItemCard.MIRAGE_STEM())
            .group("sticks")
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
            .criterion(DemonItemCard.MIRAGE_STEM())
            .group("strings")
            .offerTo(it, Identifier.of(modId, "string_from_mirage_stem"))
    }

    // ミラージュの茎＞コンポスター
    onRegisterRecipes {
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(DemonItemCard.MIRAGE_STEM(), 0.65F)
    }

    // 3種名誉系フェアリークリスタル→ミーニャのフェアリークリスタル
    run {
        // TODO いい感じの加工機械
        fun generateExchangeRecipe(input: () -> Item, output: () -> Item, outputCount: Int) = onGenerateRecipes {
            SingleItemRecipeJsonBuilder
                .createStonecutting(Ingredient.ofItems(input()), output(), outputCount)
                .criterion(input())
                .group(output())
                .offerTo(it, "selling_" concat input().identifier)
        }
        // TODO 成果物をシンプルに
        generateExchangeRecipe({ DemonItemCard.HONORABLE_FAIRY_CRYSTAL() }, { DemonItemCard.FAIRY_CRYSTAL_50() }, 2)
        generateExchangeRecipe({ DemonItemCard.GLORIOUS_FAIRY_CRYSTAL() }, { DemonItemCard.FAIRY_CRYSTAL_500() }, 2)
        generateExchangeRecipe({ DemonItemCard.LEGENDARY_FAIRY_CRYSTAL() }, { DemonItemCard.FAIRY_CRYSTAL_500() }, 20)
    }

    // ミラージュフラワー→人工フェアリークリスタル
    onGenerateRecipes {
        CookingRecipeJsonBuilder
            .create(Ingredient.ofItems(MirageFlourCard.MIRAGE_FLOUR()), DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL(), 0.4F, 200, RecipeSerializer.SMELTING)
            .criterion(RecipeProvider.hasItem(MirageFlourCard.MIRAGE_FLOUR()), RecipeProvider.conditionsFromItem(MirageFlourCard.MIRAGE_FLOUR()))
            .group(DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL())
            .offerTo(it, DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL().identifier)
    }

    // 両替レシピ
    run {

        fun generateExchangeRecipe(lower: () -> Item, higher: () -> Item, multiplier: Int) = onGenerateRecipes {

            // 圧縮
            ShapelessRecipeJsonBuilder
                .create(higher(), 1)
                .input(lower(), multiplier)
                .criterion(lower())
                .group(higher())
                .offerTo(it, higher().identifier concat "_from_${lower().identifier.path}")

            // 分解
            ShapelessRecipeJsonBuilder
                .create(lower(), multiplier)
                .input(higher(), 1)
                .criterion(higher())
                .group(lower())
                .offerTo(it, lower().identifier concat "_from_${higher().identifier.path}")

        }

        generateExchangeRecipe({ DemonItemCard.FAIRY_CRYSTAL_50() }, { DemonItemCard.FAIRY_CRYSTAL_100() }, 2)
        generateExchangeRecipe({ DemonItemCard.FAIRY_CRYSTAL_100() }, { DemonItemCard.FAIRY_CRYSTAL_500() }, 5)

    }

    // 購入レシピ
    onGenerateRecipes {
        fun generateBuyingRecipe(cost: Int, target: () -> Item, outputCount: Int) {
            require(cost % 50 == 0)
            ShapelessRecipeJsonBuilder
                .create(target(), outputCount + 1)
                .input(target())
                .input(DemonItemCard.FAIRY_CRYSTAL_500(), cost / 500)
                .input(DemonItemCard.FAIRY_CRYSTAL_100(), cost % 500 / 100)
                .input(DemonItemCard.FAIRY_CRYSTAL_50(), cost % 100 / 50)
                .criterion(target())
                .group(target())
                .offerTo(it, Identifier.of(modId, "buying/${target().identifier.path}"))
        }

        // MOD
        generateBuyingRecipe(50, { mirageSeedItem.feature }, 8)
        generateBuyingRecipe(50, { MirageFlourCard.VERY_RARE_MIRAGE_FLOUR() }, 3)
        generateBuyingRecipe(50, { DemonItemCard.XARPITE() }, 1)
        generateBuyingRecipe(50, { DemonItemCard.MIRANAGITE() }, 1)

        // 木材
        generateBuyingRecipe(50, { Items.OAK_LOG }, 32)
        generateBuyingRecipe(50, { Items.SPRUCE_LOG }, 32)
        generateBuyingRecipe(50, { Items.BIRCH_LOG }, 32)

        // 植物
        generateBuyingRecipe(50, { Items.SUGAR_CANE }, 16)
        generateBuyingRecipe(50, { Items.OAK_LEAVES }, 16)
        generateBuyingRecipe(50, { Items.VINE }, 16)
        generateBuyingRecipe(50, { Items.GRASS }, 16)
        generateBuyingRecipe(50, { Items.POTATO }, 12)
        generateBuyingRecipe(50, { Items.CARROT }, 12)
        generateBuyingRecipe(50, { Items.BEETROOT }, 12)
        generateBuyingRecipe(50, { Items.WHEAT }, 8)
        generateBuyingRecipe(50, { Items.CACTUS }, 8)
        generateBuyingRecipe(50, { Items.PUMPKIN }, 8)
        generateBuyingRecipe(50, { Items.APPLE }, 4)
        generateBuyingRecipe(50, { Items.LILY_PAD }, 2)

        // 動物
        generateBuyingRecipe(50, { Items.FEATHER }, 8)
        generateBuyingRecipe(50, { Items.LEATHER }, 4)
        generateBuyingRecipe(1000, { Items.WITHER_SKELETON_SKULL }, 1)
        generateBuyingRecipe(4000, { Items.NETHER_STAR }, 1)

        // 敵
        generateBuyingRecipe(50, { Items.ROTTEN_FLESH }, 8)
        generateBuyingRecipe(50, { Items.STRING }, 4)
        generateBuyingRecipe(50, { Items.GUNPOWDER }, 4)
        generateBuyingRecipe(50, { Items.BONE }, 4)
        generateBuyingRecipe(50, { Items.SLIME_BALL }, 2)

        // 鉱石
        generateBuyingRecipe(50, { Items.COAL_ORE }, 16)
        generateBuyingRecipe(50, { Items.COPPER_ORE }, 12)
        generateBuyingRecipe(50, { Items.NETHER_QUARTZ_ORE }, 12)
        generateBuyingRecipe(50, { Items.IRON_ORE }, 8)
        generateBuyingRecipe(50, { Items.REDSTONE_ORE }, 4)
        generateBuyingRecipe(50, { Items.GOLD_ORE }, 2)
        generateBuyingRecipe(50, { Items.LAPIS_ORE }, 1)
        generateBuyingRecipe(100, { Items.DIAMOND_ORE }, 1)
        generateBuyingRecipe(100, { Items.EMERALD_ORE }, 1)
        generateBuyingRecipe(500, { Items.ANCIENT_DEBRIS }, 2)

        // 岩石
        generateBuyingRecipe(50, { Items.STONE }, 32)
        generateBuyingRecipe(50, { Items.NETHERRACK }, 32)
        generateBuyingRecipe(50, { Items.DIRT }, 16)
        generateBuyingRecipe(50, { Items.SAND }, 16)
        generateBuyingRecipe(50, { Items.GRAVEL }, 16)
        generateBuyingRecipe(50, { Items.ICE }, 16)
        generateBuyingRecipe(50, { Items.DEEPSLATE }, 16)
        generateBuyingRecipe(50, { Items.SOUL_SAND }, 8)
        generateBuyingRecipe(50, { Items.MAGMA_BLOCK }, 4)
    }

}


open class DemonItem(settings: Settings) : Item(settings) {
    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)
        tooltip += text { translate("$translationKey.poem").gray }
    }
}

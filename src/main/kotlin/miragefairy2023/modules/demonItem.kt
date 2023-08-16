package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.api.Fairy
import miragefairy2023.api.fairyRegistry
import miragefairy2023.module
import miragefairy2023.modules.fairy.FairyCard
import miragefairy2023.modules.toolitem.foundFairies
import miragefairy2023.util.Chance
import miragefairy2023.util.EMPTY_ITEM_STACK
import miragefairy2023.util.Translation
import miragefairy2023.util.blue
import miragefairy2023.util.concat
import miragefairy2023.util.createItemStack
import miragefairy2023.util.datagen.UniformLootNumberProvider
import miragefairy2023.util.distinct
import miragefairy2023.util.draw
import miragefairy2023.util.get
import miragefairy2023.util.getValue
import miragefairy2023.util.hasSameItemAndNbt
import miragefairy2023.util.identifier
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.group
import miragefairy2023.util.init.registerBlockDrop
import miragefairy2023.util.init.registerGrassDrop
import miragefairy2023.util.init.registerMobDrop
import miragefairy2023.util.int
import miragefairy2023.util.obtain
import miragefairy2023.util.orDefault
import miragefairy2023.util.set
import miragefairy2023.util.setValue
import miragefairy2023.util.string
import miragefairy2023.util.text
import miragefairy2023.util.totalWeight
import miragefairy2023.util.wrapper
import miragefairy2023.util.yellow
import mirrg.kotlin.hydrogen.formatAs
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
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
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.tag.ItemTags
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.util.UseAction
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import kotlin.math.pow
import kotlin.math.roundToInt


enum class DemonItemCard(
    val path: String,
    val enName: String,
    val jaName: String,
    val poemList: List<Poem>,
) {
    XARPITE(
        "xarpite", "Xarpite", "紅天石",
        listOf(Poem("Binds astral flux with magnetic force", "黒鉄の鎖は繋がれる。血腥い魂の檻へ。")),
    ),
    MIRANAGITE(
        "miranagite", "Miranagite", "蒼天石",
        listOf(Poem("Astral body crystallized by anti-entropy", "秩序の叛乱、天地創造の逆光。")),
    ),

    // ミラージュの葉
    // 硝子のような触り心地。
    // 鋭利なため気を付けてください
    MIRAGE_STEM(
        "mirage_stem", "Mirage Stem", "ミラージュの茎",
        listOf(Poem("Cell wall composed of amorphous ether", "植物が手掛ける、分子レベルの硝子細工。")),
    ),
    HONORABLE_FAIRY_CRYSTAL(
        "honorable_fairy_crystal", "Honorable Fairy Crystal", "名誉のフェアリークリスタル",
        listOf(Poem("Appear out of nowhere", "妖精からの贈り物")),
    ),
    GLORIOUS_FAIRY_CRYSTAL(
        "glorious_fairy_crystal", "Glorious Fairy Crystal", "栄光のフェアリークリスタル",
        listOf(Poem("Not a substance formed in this world", "精霊王の表彰状")),
    ),
    LEGENDARY_FAIRY_CRYSTAL(
        "legendary_fairy_crystal", "Legendary Fairy Crystal", "伝説のフェアリークリスタル",
        listOf(Poem("Pluto exploded", "冥王が跳ね上がった")),
    ),
    ARTIFICIAL_FAIRY_CRYSTAL(
        "artificial_fairy_crystal", "Artificial Fairy Crystal", "人工フェアリークリスタル",
        listOf(Poem("Uncanny crystal not worth even 1 Minia", "20Wのかまどで10秒。")),
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
        listOf(Poem("Has the same hardness as beryl", "世界で50番目に優美な有機結晶。")),
    ),
    FAIRY_CRYSTAL_100(
        "fairy_crystal_100", "100 Minia Crystal", "100ミーニャクリスタル",
        listOf(Poem("Created by the fairies of commerce", "妖精と人間が交差する世界。")),
    ),
    FAIRY_CRYSTAL_500(
        "fairy_crystal_500", "500 Minia Crystal", "500ミーニャクリスタル",
        listOf(Poem("Crystallized Mirage flower nectar", "ミラージュの蜜よ、永遠の宝石となれ。")),
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

    CHAOS_STONE(
        "chaos_stone", "Chaos Stone", "混沌の石",
        listOf(Poem("Chemical promoting catalyst", "魔力の暴走、加速する無秩序の流れ。")),
    ),
    MIRANAGITE_ROD(
        "miranagite_rod", "Miranagite Rod", "蒼天石の棒",
        listOf(Poem("Mana flows well through the core", "蒼天に従える光条は、魔力の祝福を示す。")),
    ),
    ;

    val identifier = Identifier(MirageFairy2023.modId, path)
    val item = Item(FabricItemSettings().group(commonItemGroup))
}

enum class MirageFlourCard(
    creator: MirageFlourCard.(Item.Settings) -> Item,
    itemId: String,
    val enName: String,
    val jaName: String,
    val poemList: List<Poem>,
) {
    TINY_MIRAGE_FLOUR(
        { MirageFlourItem(this, it, null, 2, 1.0, 1) },
        "tiny_mirage_flour", "Tiny Pile of Mirage Flour", "ミラージュの花粉",
        listOf(Poem("Compose the body of Mirage fairy", "ささやかな温もりを、てのひらの上に。")),
    ),
    MIRAGE_FLOUR(
        { MirageFlourItem(this, it, 1, null, 1.0, 1) },
        "mirage_flour", "Mirage Flour", "ミラージュフラワー",
        listOf(Poem("Containing metallic organic matter", "叡智の根源、創発のファンタジア。")),
    ),
    RARE_MIRAGE_FLOUR(
        { MirageFlourItem(this, it, 3, null, 10.0, 1) },
        "rare_mirage_flour", "Rare Mirage Flour", "中級ミラージュフラワー",
        listOf(Poem("Use the difference in ether resistance", "艶やかなほたる色に煌めく鱗粉。")),
    ),
    VERY_RARE_MIRAGE_FLOUR(
        { MirageFlourItem(this, it, 5, null, 100.0, 1) },
        "very_rare_mirage_flour", "Very Rare Mirage Flour", "上級ミラージュフラワー",
        listOf(Poem("As intelligent as humans", "黄金の魂が示す、好奇心の輝き。")),
    ),
    ULTRA_RARE_MIRAGE_FLOUR(
        { MirageFlourItem(this, it, 7, null, 1_000.0, 1) },
        "ultra_rare_mirage_flour", "Ultra Rare Mirage Flour", "高純度ミラージュフラワー",
        listOf(Poem("Awaken fairies in the world and below", "1,300ケルビンの夜景。")),
    ),
    SUPER_RARE_MIRAGE_FLOUR(
        { MirageFlourItem(this, it, 9, null, 10_000.0, 1) },
        "super_rare_mirage_flour", "Super Rare Mirage Flour", "超高純度ミラージュフラワー",
        listOf(Poem("Explore atmosphere and nearby universe", "蒼淵を彷徨う影、導きの光。")),
    ),
    EXTREMELY_RARE_MIRAGE_FLOUR(
        { MirageFlourItem(this, it, 11, null, 100_000.0, 1) },
        "extremely_rare_mirage_flour", "Extremely Rare Mirage Flour", "極超高純度ミラージュフラワー",
        listOf(
            Poem("poem1", "Leap spaces by collapsing time crystals,", "運命の束、時の結晶、光速の呪いを退けよ、"),
            Poem("poem2", "capture ether beyond observable universe", "讃えよ、アーカーシャに眠る自由の頂きを。"),
        ),
    ),
    ;

    val identifier = Identifier(MirageFairy2023.modId, itemId)
    val item = creator(this, FabricItemSettings().group(commonItemGroup))
}


val demonItemModule = module {

    // 全体
    DemonItemCard.values().forEach { card ->
        Registry.register(Registry.ITEM, card.identifier, card.item)
        onGenerateItemModels { it.register(card.item, Models.GENERATED) }
        enJa(card.item, card.enName, card.jaName)
        generatePoemList(card.item, card.poemList)
        onRegisterItems { registerPoemList(card.item, card.poemList) }
    }

    // 全体
    MirageFlourCard.values().forEach { card ->

        // 登録
        Registry.register(Registry.ITEM, card.identifier, card.item)

        // モデル
        onGenerateItemModels { it.register(card.item, Models.GENERATED) }

        // 翻訳
        enJa(card.item, card.enName, card.jaName)
        generatePoemList(card.item, card.poemList)
        onRegisterItems { registerPoemList(card.item, card.poemList) }

    }


    // 翻訳

    // ミラージュフラワー
    enJa(MirageFlourItem.MIN_RARE_KEY)
    enJa(MirageFlourItem.MAX_RARE_KEY)
    enJa(MirageFlourItem.DROP_RATE_FACTOR_KEY)
    enJa(MirageFlourItem.RIGHT_CLICK_KEY)
    enJa(MirageFlourItem.SHIFT_RIGHT_CLICK_KEY)


    // レシピ

    // 魔女→紅天石
    registerMobDrop(EntityType.WITCH, DemonItemCard.XARPITE.item, onlyKilledByPlayer = true, fortuneFactor = UniformLootNumberProvider(0.0F, 1.0F))

    // ゾンビ→紅天石
    registerMobDrop(EntityType.ZOMBIE, DemonItemCard.XARPITE.item, onlyKilledByPlayer = true, dropRate = Pair(0.02F, 0.01F))
    registerMobDrop(EntityType.ZOMBIE_VILLAGER, DemonItemCard.XARPITE.item, onlyKilledByPlayer = true, dropRate = Pair(0.02F, 0.01F))
    registerMobDrop(EntityType.DROWNED, DemonItemCard.XARPITE.item, onlyKilledByPlayer = true, dropRate = Pair(0.02F, 0.01F))
    registerMobDrop(EntityType.HUSK, DemonItemCard.XARPITE.item, onlyKilledByPlayer = true, dropRate = Pair(0.02F, 0.01F))

    // 雑草→紅天石
    registerGrassDrop(DemonItemCard.XARPITE.item, 0.01)

    // エメラルド鉱石→蒼天石
    registerBlockDrop(Blocks.EMERALD_ORE, DemonItemCard.MIRANAGITE.item, fortuneOreDrops = true, suppressIfSilkTouch = true, luckBonus = 0.2)
    registerBlockDrop(Blocks.DEEPSLATE_EMERALD_ORE, DemonItemCard.MIRANAGITE.item, fortuneOreDrops = true, suppressIfSilkTouch = true, luckBonus = 0.2)

    // 銅鉱石→蒼天石
    registerBlockDrop(Blocks.COPPER_ORE, DemonItemCard.MIRANAGITE.item, dropRate = 0.05F, fortuneOreDrops = true, suppressIfSilkTouch = true, luckBonus = 0.2)
    registerBlockDrop(Blocks.DEEPSLATE_COPPER_ORE, DemonItemCard.MIRANAGITE.item, dropRate = 0.05F, fortuneOreDrops = true, suppressIfSilkTouch = true, luckBonus = 0.2)

    // レッドストーン鉱石→蒼天石
    registerBlockDrop(Blocks.REDSTONE_ORE, DemonItemCard.MIRANAGITE.item, dropRate = 0.05F, fortuneOreDrops = true, suppressIfSilkTouch = true, luckBonus = 0.2)
    registerBlockDrop(Blocks.DEEPSLATE_REDSTONE_ORE, DemonItemCard.MIRANAGITE.item, dropRate = 0.05F, fortuneOreDrops = true, suppressIfSilkTouch = true, luckBonus = 0.2)

    // 雑草→蒼天石
    registerGrassDrop(DemonItemCard.MIRANAGITE.item, 0.01)

    // 蒼天石＋2マグマクリーム→スライムボール
    onGenerateRecipes {
        ShapelessRecipeJsonBuilder
            .create(Items.SLIME_BALL)
            .input(DemonItemCard.MIRANAGITE.item)
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
            .input(DemonItemCard.MIRANAGITE.item)
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
            .input('S', DemonItemCard.MIRAGE_STEM.item)
            .criterion(DemonItemCard.MIRAGE_STEM.item)
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
            .input('S', DemonItemCard.MIRAGE_STEM.item)
            .criterion(DemonItemCard.MIRAGE_STEM.item)
            .group("strings")
            .offerTo(it, Identifier.of(modId, "string_from_mirage_stem"))
    }

    // ミラージュの茎＞コンポスター
    onRegisterRecipes {
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(DemonItemCard.MIRAGE_STEM.item, 0.65F)
    }

    // 3種名誉系フェアリークリスタル→ミーニャのフェアリークリスタル
    run {
        // TODO いい感じの加工機械
        fun generateExchangeRecipe(input: DemonItemCard, output: DemonItemCard, outputCount: Int) = onGenerateRecipes {
            SingleItemRecipeJsonBuilder
                .createStonecutting(Ingredient.ofItems(input.item), output.item, outputCount)
                .criterion(input.item)
                .group(output.item)
                .offerTo(it, "selling_" concat input.item.identifier)
        }
        // TODO 成果物をシンプルに
        generateExchangeRecipe(DemonItemCard.HONORABLE_FAIRY_CRYSTAL, DemonItemCard.FAIRY_CRYSTAL_50, 2)
        generateExchangeRecipe(DemonItemCard.GLORIOUS_FAIRY_CRYSTAL, DemonItemCard.FAIRY_CRYSTAL_500, 2)
        generateExchangeRecipe(DemonItemCard.LEGENDARY_FAIRY_CRYSTAL, DemonItemCard.FAIRY_CRYSTAL_500, 20)
    }

    // ミラージュフラワー→人工フェアリークリスタル
    onGenerateRecipes {
        CookingRecipeJsonBuilder
            .create(Ingredient.ofItems(MirageFlourCard.MIRAGE_FLOUR.item), DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL.item, 0.4F, 200, RecipeSerializer.SMELTING)
            .criterion(RecipeProvider.hasItem(MirageFlourCard.MIRAGE_FLOUR.item), RecipeProvider.conditionsFromItem(MirageFlourCard.MIRAGE_FLOUR.item))
            .group(DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL.item)
            .offerTo(it, DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL.item.identifier)
    }

    // 蒼天石の棒
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(DemonItemCard.MIRANAGITE_ROD.item)
            .pattern("  G")
            .pattern(" G ")
            .pattern("G  ")
            .input('G', DemonItemCard.MIRANAGITE.item)
            .criterion(DemonItemCard.MIRANAGITE_ROD.item)
            .group(DemonItemCard.MIRANAGITE_ROD.item)
            .offerTo(it, DemonItemCard.MIRANAGITE_ROD.item.identifier)
    }

    // 両替レシピ
    run {

        fun generateExchangeRecipe(lower: DemonItemCard, higher: DemonItemCard, multiplier: Int) = onGenerateRecipes {

            // 圧縮
            ShapelessRecipeJsonBuilder
                .create(higher.item, 1)
                .input(lower.item, multiplier)
                .criterion(lower.item)
                .group(higher.item)
                .offerTo(it, higher.identifier concat "_from_${lower.identifier.path}")

            // 分解
            ShapelessRecipeJsonBuilder
                .create(lower.item, multiplier)
                .input(higher.item, 1)
                .criterion(higher.item)
                .group(lower.item)
                .offerTo(it, lower.identifier concat "_from_${higher.identifier.path}")

        }

        generateExchangeRecipe(DemonItemCard.FAIRY_CRYSTAL_50, DemonItemCard.FAIRY_CRYSTAL_100, 2)
        generateExchangeRecipe(DemonItemCard.FAIRY_CRYSTAL_100, DemonItemCard.FAIRY_CRYSTAL_500, 5)

    }

    // 購入レシピ
    onGenerateRecipes {
        fun generateBuyingRecipe(cost: Int, target: Item, outputCount: Int) {
            require(cost % 50 == 0)
            ShapelessRecipeJsonBuilder
                .create(target, outputCount + 1)
                .input(target)
                .input(DemonItemCard.FAIRY_CRYSTAL_500.item, cost / 500)
                .input(DemonItemCard.FAIRY_CRYSTAL_100.item, cost % 500 / 100)
                .input(DemonItemCard.FAIRY_CRYSTAL_50.item, cost % 100 / 50)
                .criterion(target)
                .group(target)
                .offerTo(it, Identifier.of(modId, "buying/${target.identifier.path}"))
        }

        // MOD
        generateBuyingRecipe(50, MirageFlower.seedItem, 8)
        generateBuyingRecipe(50, MirageFlourCard.VERY_RARE_MIRAGE_FLOUR.item, 3)
        generateBuyingRecipe(50, DemonItemCard.XARPITE.item, 1)
        generateBuyingRecipe(50, DemonItemCard.MIRANAGITE.item, 1)

        // 木材
        generateBuyingRecipe(50, Items.OAK_LOG, 32)
        generateBuyingRecipe(50, Items.SPRUCE_LOG, 32)
        generateBuyingRecipe(50, Items.BIRCH_LOG, 32)

        // 植物
        generateBuyingRecipe(50, Items.SUGAR_CANE, 16)
        generateBuyingRecipe(50, Items.OAK_LEAVES, 16)
        generateBuyingRecipe(50, Items.VINE, 16)
        generateBuyingRecipe(50, Items.GRASS, 16)
        generateBuyingRecipe(50, Items.POTATO, 12)
        generateBuyingRecipe(50, Items.CARROT, 12)
        generateBuyingRecipe(50, Items.BEETROOT, 12)
        generateBuyingRecipe(50, Items.WHEAT, 8)
        generateBuyingRecipe(50, Items.CACTUS, 8)
        generateBuyingRecipe(50, Items.PUMPKIN, 8)
        generateBuyingRecipe(50, Items.APPLE, 4)
        generateBuyingRecipe(50, Items.LILY_PAD, 2)

        // 動物
        generateBuyingRecipe(50, Items.FEATHER, 8)
        generateBuyingRecipe(50, Items.LEATHER, 4)
        generateBuyingRecipe(1000, Items.WITHER_SKELETON_SKULL, 1)
        generateBuyingRecipe(4000, Items.NETHER_STAR, 1)

        // 敵
        generateBuyingRecipe(50, Items.ROTTEN_FLESH, 8)
        generateBuyingRecipe(50, Items.STRING, 4)
        generateBuyingRecipe(50, Items.GUNPOWDER, 4)
        generateBuyingRecipe(50, Items.BONE, 4)
        generateBuyingRecipe(50, Items.SLIME_BALL, 2)

        // 鉱石
        generateBuyingRecipe(50, Items.COAL_ORE, 16)
        generateBuyingRecipe(50, Items.COPPER_ORE, 12)
        generateBuyingRecipe(50, Items.NETHER_QUARTZ_ORE, 12)
        generateBuyingRecipe(50, Items.IRON_ORE, 8)
        generateBuyingRecipe(50, Items.REDSTONE_ORE, 4)
        generateBuyingRecipe(50, Items.GOLD_ORE, 2)
        generateBuyingRecipe(50, Items.LAPIS_ORE, 1)
        generateBuyingRecipe(100, Items.DIAMOND_ORE, 1)
        generateBuyingRecipe(100, Items.EMERALD_ORE, 1)
        generateBuyingRecipe(500, Items.ANCIENT_DEBRIS, 2)

        // 岩石
        generateBuyingRecipe(50, Items.STONE, 32)
        generateBuyingRecipe(50, Items.NETHERRACK, 32)
        generateBuyingRecipe(50, Items.DIRT, 16)
        generateBuyingRecipe(50, Items.SAND, 16)
        generateBuyingRecipe(50, Items.GRAVEL, 16)
        generateBuyingRecipe(50, Items.ICE, 16)
        generateBuyingRecipe(50, Items.DEEPSLATE, 16)
        generateBuyingRecipe(50, Items.SOUL_SAND, 8)
        generateBuyingRecipe(50, Items.MAGMA_BLOCK, 4)
    }

    // ミラージュフラワー相互変換
    fun registerMirageFlourRecipe(lower: MirageFlourCard, higher: MirageFlourCard) = onGenerateRecipes {
        ShapelessRecipeJsonBuilder
            .create(higher.item, 1)
            .input(lower.item, 8)
            .criterion(lower.item)
            .group(higher.item)
            .offerTo(it, higher.identifier)
        ShapelessRecipeJsonBuilder
            .create(lower.item, 8)
            .input(higher.item, 1)
            .criterion(higher.item)
            .group(lower.item)
            .offerTo(it, lower.identifier concat "_from_${higher.identifier.path}")
    }
    registerMirageFlourRecipe(MirageFlourCard.TINY_MIRAGE_FLOUR, MirageFlourCard.MIRAGE_FLOUR)
    registerMirageFlourRecipe(MirageFlourCard.MIRAGE_FLOUR, MirageFlourCard.RARE_MIRAGE_FLOUR)
    registerMirageFlourRecipe(MirageFlourCard.RARE_MIRAGE_FLOUR, MirageFlourCard.VERY_RARE_MIRAGE_FLOUR)
    registerMirageFlourRecipe(MirageFlourCard.VERY_RARE_MIRAGE_FLOUR, MirageFlourCard.ULTRA_RARE_MIRAGE_FLOUR)
    registerMirageFlourRecipe(MirageFlourCard.ULTRA_RARE_MIRAGE_FLOUR, MirageFlourCard.SUPER_RARE_MIRAGE_FLOUR)
    registerMirageFlourRecipe(MirageFlourCard.SUPER_RARE_MIRAGE_FLOUR, MirageFlourCard.EXTREMELY_RARE_MIRAGE_FLOUR)

}


class CommonFairyEntry(val fairy: Fairy, val predicate: (PlayerEntity) -> Boolean)

class MirageFlourItem(val card: MirageFlourCard, settings: Settings, private val minRare: Int?, private val maxRare: Int?, private val factor: Double, private val times: Int) : Item(settings) {
    companion object {
        private val prefix = "item.${MirageFairy2023.modId}.mirage_flour"
        val MIN_RARE_KEY = Translation("$prefix.min_rare_key", "Minimum Rare: %s", "最低レア度: %s")
        val MAX_RARE_KEY = Translation("$prefix.max_rare_key", "Maximum Rare: %s", "最高レア度: %s")
        val DROP_RATE_FACTOR_KEY = Translation("$prefix.drop_rate_factor_key", "Drop Rate Amplification: %s", "出現率倍率: %s")
        val RIGHT_CLICK_KEY = Translation("$prefix.right_click", "Right click and hold to summon fairies", "右クリック長押しで妖精を召喚")
        val SHIFT_RIGHT_CLICK_KEY = Translation("$prefix.shift_right_click", "Use while sneaking to show table", "スニーク中に使用で提供割合を表示")
        val COMMON_FAIRY_LIST = mutableListOf<CommonFairyEntry>()
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)

        // 性能
        if (minRare != null) tooltip += text { MIN_RARE_KEY(minRare).blue }
        if (maxRare != null) tooltip += text { MAX_RARE_KEY(maxRare).blue }
        tooltip += text { DROP_RATE_FACTOR_KEY(factor.roundToInt() formatAs "%,d").blue }

        // 機能説明
        tooltip += text { RIGHT_CLICK_KEY().yellow }
        tooltip += text { SHIFT_RIGHT_CLICK_KEY().yellow }

    }

    override fun getUseAction(stack: ItemStack) = UseAction.BOW
    override fun getMaxUseTime(stack: ItemStack) = 72000 // 1時間

    private fun calculateChanceTable(player: ServerPlayerEntity): List<Chance<Fairy>> {

        // コモン枠
        val commonFairyList = COMMON_FAIRY_LIST.filter { it.predicate(player) }.map { it.fairy }

        // 夢枠
        val memoryFairyList = player.foundFairies.getList().mapNotNull { fairyRegistry[it] }

        // 妖精リスト
        val actualFairyList = (commonFairyList + memoryFairyList).distinctBy { it.motif }

        // 生の提供割合
        val multiplierByRare = 0.5.pow(7.0 / 4.0)
        val rawChanceTable = actualFairyList
            .filter { minRare == null || it.rare >= minRare } // レア度フィルタ
            .filter { maxRare == null || it.rare <= maxRare } // レア度フィルタ
            .map { Chance(multiplierByRare.pow(it.rare - 1) * factor, it) } // レア度によるドロップ確率の計算

        // 内容の調整
        val actualChanceTable = run {
            val totalWeight = rawChanceTable.totalWeight
            if (totalWeight >= 1.0) {
                rawChanceTable
            } else {
                rawChanceTable + Chance(1.0 - totalWeight, FairyCard.AIR.fairy)
            }
        }

        // データの整形
        return actualChanceTable
            .distinct { a, b -> a.motif === b.motif }
            .sortedBy { it.weight }
    }

    private fun showChanceTableMessage(player: PlayerEntity, mirageFlourItemStack: ItemStack, chanceTable: List<Chance<Fairy>>) {
        player.sendMessage(text { "["() + mirageFlourItemStack.name + "]"() }, false)
        val totalWeight = chanceTable.totalWeight
        chanceTable.forEach { chance ->
            player.sendMessage(text { "${(chance.weight / totalWeight * 100 formatAs "%8.4f%%").replace(' ', '_')}: "() + chance.item.item.name }, false)
        }
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)
        if (!user.isSneaking) {

            // 使用開始
            user.setCurrentHand(hand)

        } else {

            // 提供割合表示
            if (!world.isClient) {
                val chanceTable = calculateChanceTable(user as ServerPlayerEntity)
                showChanceTableMessage(user, itemStack, chanceTable)
            }

        }
        return TypedActionResult.consume(itemStack)
    }

    override fun usageTick(world: World, user: LivingEntity, stack: ItemStack, remainingUseTicks: Int) {
        if (world.isClient) return
        if (user !is ServerPlayerEntity) return

        fun draw() {

            // 提供割合の生成
            val chanceTable = calculateChanceTable(user)

            // 消費
            if (!(user.isCreative)) {
                if (stack.count != 1) {
                    // 最後の1個でない場合

                    // 普通に消費
                    stack.decrement(1)

                } else {
                    // 最後の1個の場合

                    // リロードが可能ならリロードする
                    val isReloaded = run {
                        (0 until 36).forEach { index ->
                            val searchingItemStack = user.inventory.getStack(index)
                            if (searchingItemStack !== stack) { // 同一のアイテムスタックでなく、
                                if (searchingItemStack hasSameItemAndNbt stack) { // 両者が同一種類のアイテムスタックならば、
                                    val count = searchingItemStack.count
                                    user.inventory[index] = EMPTY_ITEM_STACK // そのアイテムスタックを消して
                                    stack.count = count // 手に持っているアイテムスタックに移動する
                                    // stack.count == 1なので、このときアイテムが1個消費される
                                    return@run true
                                }
                            }
                        }
                        false
                    }

                    // リロードできなかった場合、最後の1個を減らす
                    if (!isReloaded) stack.decrement(1)

                }
            }

            repeat(times) {

                // ガチャ
                val fairy = chanceTable.draw(world.random) ?: FairyCard.AIR.fairy

                // 入手
                user.obtain(fairy.item.createItemStack())

                // 妖精召喚履歴に追加
                val nbt = user.customData
                var count by nbt.wrapper[MirageFairy2023.modId]["fairy_count"][fairy.motif.string].int.orDefault { 0 }
                count += 1

            }

            // エフェクト
            world.playSound(null, user.x, user.y, user.z, SoundEvents.BLOCK_DEEPSLATE_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F)

        }

        val t = 72000 - remainingUseTicks
        if (t >= 280) { // 14秒以降は秒間20個
            draw()
        } else if (t >= 200 && t % 2 == 0) { // 10秒～14秒は秒間10個
            draw()
        } else if (t >= 120 && t % 5 == 0) { // 6秒～10秒は秒間4個
            draw()
        } else if (t >= 40 && t % 10 == 0) { // 最初の1個までは2秒、2秒～6秒は秒間2個
            draw()
        }

        if (stack.isEmpty) user.clearActiveItem()

    }
}

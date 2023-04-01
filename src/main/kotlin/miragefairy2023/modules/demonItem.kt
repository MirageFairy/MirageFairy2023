package miragefairy2023.modules

import com.faux.customentitydata.api.CustomDataHelper
import miragefairy2023.MirageFairy2023
import miragefairy2023.SlotContainer
import miragefairy2023.module
import miragefairy2023.util.Chance
import miragefairy2023.util.Translation
import miragefairy2023.util.blue
import miragefairy2023.util.createItemStack
import miragefairy2023.util.distinct
import miragefairy2023.util.draw
import miragefairy2023.util.enJa
import miragefairy2023.util.enJaItem
import miragefairy2023.util.get
import miragefairy2023.util.gray
import miragefairy2023.util.int
import miragefairy2023.util.item
import miragefairy2023.util.orDefault
import miragefairy2023.util.registerBlockDrop
import miragefairy2023.util.registerGrassDrop
import miragefairy2023.util.registerMobDrop
import miragefairy2023.util.text
import miragefairy2023.util.totalWeight
import miragefairy2023.util.translation
import miragefairy2023.util.uniformLootNumberProvider
import miragefairy2023.util.wrapper
import miragefairy2023.util.yellow
import mirrg.kotlin.hydrogen.formatAs
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.Blocks
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.client.Models
import net.minecraft.data.server.RecipeProvider
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import kotlin.math.pow
import kotlin.math.roundToInt


class Poem(val en: String, val ja: String)

enum class DemonItemCard(
    val creator: DemonItemCard.(Item.Settings) -> Item,
    val itemId: String,
    val enName: String,
    val jaName: String,
    val poems: List<Poem>,
) {
    XARPITE(
        { DemonItem(this, it) },
        "xarpite", "Xarpite", "紅天石",
        listOf(Poem("Binds astral flux with magnetic force", "黒鉄の鎖は繋がれる。血腥い魂の檻へ。")),
    ),
    MIRANAGITE(
        { DemonItem(this, it) },
        "miranagite", "Miranagite", "蒼天石",
        listOf(Poem("Astral body crystallized by anti-entropy", "秩序の叛乱、天地創造の逆光。")),
    ),
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
        listOf(Poem("Use the difference in ether resistance", "艶やかなほたる色に煌めく鱗粉、妖精の耽美主義。")),
    ),
    VERY_RARE_MIRAGE_FLOUR(
        { MirageFlourItem(this, it, 5, null, 100.0, 1) },
        "very_rare_mirage_flour", "Very Rare Mirage Flour", "上級ミラージュフラワー",
        listOf(Poem("As intelligent as humans", "金色の御霊示すは好奇心、朽ちた業前、明日を信じて。")),
    ),
    ULTRA_RARE_MIRAGE_FLOUR(
        { MirageFlourItem(this, it, 7, null, 1_000.0, 1) },
        "ultra_rare_mirage_flour", "Ultra Rare Mirage Flour", "高純度ミラージュフラワー",
        listOf(Poem("Awaken fairies in the world and below", "現し世と常夜のほむら、空の下。大礼の咎、火の粉に宿る。")),
    ),
    SUPER_RARE_MIRAGE_FLOUR(
        { MirageFlourItem(this, it, 9, null, 10_000.0, 1) },
        "super_rare_mirage_flour", "Super Rare Mirage Flour", "超高純度ミラージュフラワー",
        listOf(Poem("Explore atmosphere and nearby universe", "蒼淵を彷徨い歩く人々の、帰路を結える仁愛の光。")),
    ),
    EXTREMELY_RARE_MIRAGE_FLOUR(
        { MirageFlourItem(this, it, 11, null, 100_000.0, 1) },
        "extremely_rare_mirage_flour", "Extremely Rare Mirage Flour", "極超高純度ミラージュフラワー",
        listOf(
            Poem("Leap spaces by collapsing time crystals", "運命の束、広がる時間の結晶、惨憺たる光速の呪いを解放せよ、"),
            Poem("and capture ethers beyond observable universe", "讃えよ、アーカーシャに眠る自由と功徳の頂きを。"),
        ),
    ),
}

private val demonItems = SlotContainer<DemonItemCard, Item>()
operator fun DemonItemCard.invoke() = demonItems[this]


val demonItemModule = module {

    // 全体
    DemonItemCard.values().forEach { card ->
        item(card.itemId, { card.creator(card, FabricItemSettings().group(commonItemGroup)) }) {
            onRegisterItems { demonItems[card] = item }

            onGenerateItemModels { it.register(item, Models.GENERATED) }

            enJaItem({ item }, card.enName, card.jaName)
            card.poems.forEachIndexed { index, poem ->
                enJa({ "${item.translationKey}.poem${if (index + 1 == 1) "" else "${index + 1}"}" }, poem.en, poem.ja)
            }
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

    // ミラージュフラワー相互変換
    fun registerMirageFlourRecipe(lowerItemGetter: () -> Item, higherItemGetter: () -> Item) = onGenerateRecipes {
        val lowerItem = lowerItemGetter()
        val lowerName = Registry.ITEM.getId(lowerItem).path
        val higherItem = higherItemGetter()
        val higherName = Registry.ITEM.getId(higherItem).path
        ShapelessRecipeJsonBuilder
            .create(higherItem)
            .input(lowerItem)
            .input(lowerItem)
            .input(lowerItem)
            .input(lowerItem)
            .input(lowerItem)
            .input(lowerItem)
            .input(lowerItem)
            .input(lowerItem)
            .criterion("has_$lowerName", RecipeProvider.conditionsFromItem(lowerItem))
            .offerTo(it, Identifier.of(modId, higherName))
        ShapelessRecipeJsonBuilder
            .create(lowerItem, 8)
            .input(higherItem)
            .criterion("has_$higherName", RecipeProvider.conditionsFromItem(higherItem))
            .offerTo(it, Identifier.of(modId, "${lowerName}_from_$higherName"))
    }
    registerMirageFlourRecipe({ DemonItemCard.TINY_MIRAGE_FLOUR() }, { DemonItemCard.MIRAGE_FLOUR() })
    registerMirageFlourRecipe({ DemonItemCard.MIRAGE_FLOUR() }, { DemonItemCard.RARE_MIRAGE_FLOUR() })
    registerMirageFlourRecipe({ DemonItemCard.RARE_MIRAGE_FLOUR() }, { DemonItemCard.VERY_RARE_MIRAGE_FLOUR() })
    registerMirageFlourRecipe({ DemonItemCard.VERY_RARE_MIRAGE_FLOUR() }, { DemonItemCard.ULTRA_RARE_MIRAGE_FLOUR() })
    registerMirageFlourRecipe({ DemonItemCard.ULTRA_RARE_MIRAGE_FLOUR() }, { DemonItemCard.SUPER_RARE_MIRAGE_FLOUR() })
    registerMirageFlourRecipe({ DemonItemCard.SUPER_RARE_MIRAGE_FLOUR() }, { DemonItemCard.EXTREMELY_RARE_MIRAGE_FLOUR() })

    translation(MirageFlourItem.MIN_RARE_KEY)
    translation(MirageFlourItem.MAX_RARE_KEY)
    translation(MirageFlourItem.DROP_RATE_FACTOR_KEY)
    translation(MirageFlourItem.RIGHT_CLICK_KEY)
    translation(MirageFlourItem.SHIFT_RIGHT_CLICK_KEY)

}


open class DemonItem(val card: DemonItemCard, settings: Settings) : Item(settings) {
    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)
        card.poems.forEachIndexed { index, _ ->
            tooltip += text { translate("$translationKey.poem${if (index + 1 == 1) "" else "${index + 1}"}").gray }
        }
    }
}

class MirageFlourItem(card: DemonItemCard, settings: Settings, private val minRare: Int?, private val maxRare: Int?, private val factor: Double, private val times: Int) : DemonItem(card, settings) {
    companion object {
        private val prefix = "item.${MirageFairy2023.modId}.mirage_flour"
        val MIN_RARE_KEY = Translation("$prefix.min_rare_key", "Minimum Rare: %s", "最低レア度: %s")
        val MAX_RARE_KEY = Translation("$prefix.max_rare_key", "Maximum Rare: %s", "最高レア度: %s")
        val DROP_RATE_FACTOR_KEY = Translation("$prefix.drop_rate_factor_key", "Drop Rate Amplification: %s", "出現率倍率: %s")
        val RIGHT_CLICK_KEY = Translation("$prefix.right_click", "Right click to summon fairy", "右クリックで妖精召喚")
        val SHIFT_RIGHT_CLICK_KEY = Translation("$prefix.shift_right_click", "%s+right click to show fairy table", "%s+右クリックで提供割合表示")
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)

        if (!world.isClient) {

            // 提供割合生成
            val chanceTable = run {
                val chanceTable = listOf(
                    FairyCard.AIR,
                    FairyCard.FIRE,
                    FairyCard.LAVA,
                    FairyCard.MOON,
                    FairyCard.SUN,
                    FairyCard.RAIN,
                    FairyCard.DIRT,
                    FairyCard.IRON,
                    FairyCard.DIAMOND,
                    FairyCard.PLAYER,
                    FairyCard.WARDEN,
                    FairyCard.ZOMBIE,
                    FairyCard.SPRUCE,
                    FairyCard.HOE,
                    FairyCard.FOREST,
                    FairyCard.DESERT,
                    FairyCard.AVALON, // TODO イベント終了時除去
                    FairyCard.VOID,
                    FairyCard.NIGHT,
                    FairyCard.TIME,
                )
                    .map { Chance(0.1.pow((it.rare - 1) * 0.5) * factor, it) }
                    .filter { minRare == null || it.item.rare >= minRare }
                    .filter { maxRare == null || it.item.rare <= maxRare }
                val totalWeight = chanceTable.totalWeight
                if (totalWeight >= 1.0) {
                    chanceTable
                } else {
                    chanceTable + Chance(1.0 - totalWeight, FairyCard.AIR)
                }
            }
                .distinct { a, b -> a === b }
                .sortedBy { it.weight }

            if (!user.isSneaking) {

                // 消費
                itemStack.decrement(1)

                repeat(times) {

                    // ガチャ
                    val fairyCard = chanceTable.draw(world.random) ?: FairyCard.AIR

                    // 入手
                    val itemEntity = user.dropItem(fairyCard().createItemStack(), false)
                    if (itemEntity != null) {
                        itemEntity.resetPickupDelay()
                        itemEntity.owner = user.uuid
                    }

                    // 妖精召喚履歴に追加
                    val nbt = CustomDataHelper.getPersistentData(user as ServerPlayerEntity)
                    var count by nbt.wrapper[MirageFairy2023.modId]["fairy_count"][fairyCard.identifier.toString()].int.orDefault { 0 }
                    count += 1

                }

                // エフェクト
                world.playSound(null, user.x, user.y, user.z, SoundEvents.BLOCK_DEEPSLATE_BREAK, SoundCategory.NEUTRAL, 1.0f, 1.0f)

            } else {

                // 提供割合表示
                user.sendMessage(text { "["() + itemStack.name + "]"() }, false)
                val totalWeight = chanceTable.totalWeight
                chanceTable.forEach { chance ->
                    user.sendMessage(text { "${(chance.weight / totalWeight * 100 formatAs "%8.4f%%").replace(' ', '_')}: "() + chance.item().name }, false)
                }

            }
        }

        return TypedActionResult.success(itemStack, world.isClient)
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)
        if (minRare != null) tooltip += text { MIN_RARE_KEY(minRare).blue }
        if (maxRare != null) tooltip += text { MAX_RARE_KEY(maxRare).blue }
        tooltip += text { DROP_RATE_FACTOR_KEY(factor.roundToInt() formatAs "%,d").blue }
        tooltip += text { RIGHT_CLICK_KEY().yellow }
        tooltip += text { SHIFT_RIGHT_CLICK_KEY(Text.keybind("key.sneak")).yellow }
    }
}

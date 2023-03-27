package miragefairy2023.modules

import com.faux.customentitydata.api.CustomDataHelper
import miragefairy2023.MirageFairy2023
import miragefairy2023.SlotContainer
import miragefairy2023.module
import miragefairy2023.util.createItemStack
import miragefairy2023.util.enJa
import miragefairy2023.util.enJaItem
import miragefairy2023.util.get
import miragefairy2023.util.gray
import miragefairy2023.util.int
import miragefairy2023.util.item
import miragefairy2023.util.orDefault
import miragefairy2023.util.registerBlockDrop
import miragefairy2023.util.registerFuel
import miragefairy2023.util.registerGrassDrop
import miragefairy2023.util.registerMobDrop
import miragefairy2023.util.text
import miragefairy2023.util.uniformLootNumberProvider
import miragefairy2023.util.wrapper
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.Blocks
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.client.Models
import net.minecraft.data.server.RecipeProvider
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
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
import net.minecraft.world.World


enum class DemonItemCard(
    val creator: (Item.Settings) -> Item,
    val itemId: String,
    val enName: String,
    val jaName: String,
    val enPoem: String,
    val jaPoem: String,
) {
    XARPITE(
        { DemonItem(it) },
        "xarpite", "Xarpite", "紅天石",
        "Binds astral flux with magnetic force",
        "黒鉄の鎖は繋がれる。血腥い魂の檻へ。",
    ),
    MIRANAGITE(
        { DemonItem(it) },
        "miranagite", "Miranagite", "蒼天石",
        "Astral body crystallized by anti-entropy",
        "秩序の叛乱、天地創造の逆光。",
    ),
    TINY_MIRAGE_FLOUR(
        { MirageFlourItem(it, 1) },
        "tiny_mirage_flour", "Tiny Pile of Mirage Flour", "ミラージュの花粉",
        "Compose the body of Mirage fairy",
        "ささやかな温もりを、てのひらの上に。",
    ),
    MIRAGE_FLOUR(
        { MirageFlourItem(it, 8) },
        "mirage_flour", "Mirage Flour", "ミラージュフラワー",
        "Containing metallic organic matter",
        "創発のファンタズム",
    ),
}

private val demonItems = SlotContainer<DemonItemCard, Item>()
operator fun DemonItemCard.invoke() = demonItems[this]


val demonItemModule = module {

    // 全体
    DemonItemCard.values().forEach { card ->
        item(card.itemId, { card.creator(FabricItemSettings().group(commonItemGroup)) }) {
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

    // エメラルド鉱石→蒼天石
    registerBlockDrop({ Blocks.EMERALD_ORE }, { DemonItemCard.MIRANAGITE() }, fortuneOreDrops = true)
    registerBlockDrop({ Blocks.DEEPSLATE_EMERALD_ORE }, { DemonItemCard.MIRANAGITE() }, fortuneOreDrops = true)

    // 銅鉱石→蒼天石
    registerBlockDrop({ Blocks.COPPER_ORE }, { DemonItemCard.MIRANAGITE() }, dropRate = 0.05f, fortuneOreDrops = true)
    registerBlockDrop({ Blocks.DEEPSLATE_COPPER_ORE }, { DemonItemCard.MIRANAGITE() }, dropRate = 0.05f, fortuneOreDrops = true)

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


open class DemonItem(settings: Settings) : Item(settings) {
    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)
        tooltip += text { translate("$translationKey.poem").gray }
    }
}

class MirageFlourItem(settings: Settings, private val charge: Int) : DemonItem(settings) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)

        // 消費
        itemStack.decrement(1)

        if (!world.isClient) {

            repeat(charge) {

                // ガチャ
                val r = world.random.nextDouble()
                val fairyCard = when {
                    r < 0.00003 -> FairyCard.TIME
                    r < 0.0001 -> FairyCard.SUN
                    r < 0.0003 -> FairyCard.WARDEN
                    r < 0.001 -> FairyCard.NIGHT
                    r < 0.003 -> FairyCard.PLAYER
                    r < 0.01 -> FairyCard.IRON
                    r < 0.03 -> FairyCard.FOREST
                    r < 0.1 -> FairyCard.SKELETON
                    r < 0.3 -> FairyCard.DIRT
                    else -> FairyCard.AIR
                }

                // 入手
                val itemEntity = user.dropItem(fairyCard().createItemStack(), false)
                if (itemEntity != null) {
                    itemEntity.resetPickupDelay()
                    itemEntity.owner = user.uuid
                }

                // 妖精召還履歴に追加
                val nbt = CustomDataHelper.getPersistentData(user as ServerPlayerEntity)
                var count by nbt.wrapper[MirageFairy2023.modId]["fairy_count"][fairyCard.identifier.toString()].int.orDefault { 0 }
                count += 1

            }

            // エフェクト
            world.playSound(null, user.x, user.y, user.z, SoundEvents.BLOCK_DEEPSLATE_BREAK, SoundCategory.NEUTRAL, 1.0f, 1.0f)

        }

        return TypedActionResult.success(itemStack, world.isClient)
    }
}

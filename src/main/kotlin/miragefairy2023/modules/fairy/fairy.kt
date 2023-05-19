@file:Suppress("SpellCheckingInspection")

package miragefairy2023.modules.fairy

import miragefairy2023.MirageFairy2023
import miragefairy2023.api.fairyRegistry
import miragefairy2023.getOrPut
import miragefairy2023.module
import miragefairy2023.slotOf
import miragefairy2023.util.EMPTY_ITEM_STACK
import miragefairy2023.util.createItemStack
import miragefairy2023.util.hasSameItemAndNbt
import miragefairy2023.util.init.FeatureSlot
import miragefairy2023.util.init.TagScope
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.enJaItemGroup
import miragefairy2023.util.init.item
import miragefairy2023.util.init.itemTag
import miragefairy2023.util.init.registerColorProvider
import miragefairy2023.util.init.registerToTag
import miragefairy2023.util.init.translation
import miragefairy2023.util.isNotEmpty
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.data.client.Model
import net.minecraft.data.client.TextureKey
import net.minecraft.data.client.TextureMap
import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.recipe.SpecialCraftingRecipe
import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import java.util.Optional

// 妖精アイテムグループ
private val randomFairyIcon by lazy { FairyCard.values().random()().createItemStack() }
val fairyItemGroup: ItemGroup = FabricItemGroupBuilder.build(Identifier(MirageFairy2023.modId, "fairy")) { randomFairyIcon }

// 妖精アイテムタグ
val fairiesItemTag = slotOf<TagScope<Item>>()
val fairiesOfRareItemTag = mutableMapOf<Int, TagScope<Item>>()

// 妖精アイテム
val MAX_FAIRY_RANK = 9
private lateinit var fairyItems: Map<FairyCard, Map<Int, FeatureSlot<DemonFairyItem>>>
operator fun FairyCard.invoke(rank: Int = 1) = fairyItems[this]!![rank]!!.feature

// 凝縮レシピ
lateinit var fairyCondensationRecipeSerializer: SpecialRecipeSerializer<FairyCondensationRecipe>

val fairyModule = module {

    // アイテムグループ
    enJaItemGroup({ fairyItemGroup }, "MirageFairy2023: Fairy", "MirageFairy2023: 妖精")

    // 妖精の共通アイテムモデル
    onGenerateItemModels {
        val layer0 = TextureKey.of("layer0")
        val layer1 = TextureKey.of("layer1")
        val layer2 = TextureKey.of("layer2")
        val layer3 = TextureKey.of("layer3")
        val layer4 = TextureKey.of("layer4")
        val model = Model(Optional.of(Identifier("minecraft", "item/generated")), Optional.empty(), layer0, layer1, layer2, layer3, layer4)
        model.upload(Identifier(modId, "item/fairy"), TextureMap().apply {
            put(layer0, Identifier(modId, "item/fairy_skin"))
            put(layer1, Identifier(modId, "item/fairy_back"))
            put(layer2, Identifier(modId, "item/fairy_front"))
            put(layer3, Identifier(modId, "item/fairy_hair"))
            put(layer4, Identifier(modId, "item/fairy_dress"))
        }, it.writer)
    }

    // 翻訳登録
    translation(DemonFairyItem.RARE_KEY)
    translation(DemonFairyItem.CONDENSATION_RECIPE_KEY)
    translation(DemonFairyItem.DECONDENSATION_RECIPE_KEY)
    translation(DemonFairyItem.BOTH_RECIPE_KEY)

    // 妖精アイテム
    run {

        // スロット初期化
        val mutableFairyItems = mutableMapOf<FairyCard, MutableMap<Int, FeatureSlot<DemonFairyItem>>>()
        fairyItems = mutableFairyItems

        // モチーフごと
        FairyCard.values().forEach { fairyCard ->

            // ラングごと
            (1..MAX_FAIRY_RANK).forEach { rank ->

                // 妖精アイテム登録
                mutableFairyItems.getOrPut(fairyCard) { mutableMapOf() }[rank] = item(
                    "${fairyCard.motifPath}_fairy${if (rank == 1) "" else "_$rank"}",
                    { DemonFairyItem(fairyCard, rank, FabricItemSettings().group(fairyItemGroup)) },
                ) {

                    // タグに登録
                    registerToTag { fairiesItemTag.getOrPut { itemTag("fairies") } }
                    registerToTag { fairiesOfRareItemTag.getOrPut(feature.fairyLevel) { itemTag("rare${feature.fairyLevel}_fairies") } }

                    // モデル系
                    onGenerateItemModels { it.register(feature, Model(Optional.of(Identifier(modId, "item/fairy")), Optional.empty())) }
                    registerColorProvider { _, tintIndex ->
                        when (tintIndex) {
                            0 -> fairyCard.skinColor
                            1 -> fairyCard.backColor
                            2 -> fairyCard.frontColor
                            3 -> fairyCard.hairColor
                            4 -> getRankRgb(rank)
                            else -> 0xFFFFFF
                        }
                    }

                }

            }

            // 翻訳登録
            enJa("item.${fairyCard.motif.toTranslationKey()}_fairy", fairyCard.enName, fairyCard.jaName)

            // モチーフを妖精レジストリに登録
            Registry.register(fairyRegistry, fairyCard.motif, fairyCard.fairy)

            // モチーフ固有の初期化処理
            fairyCard.recipeContainer.recipes.forEach {
                it.init(this, fairyCard)
            }

        }

    }

    // 凝縮・展開レシピ
    fairyCondensationRecipeSerializer = Registry.register(Registry.RECIPE_SERIALIZER, "crafting_special_fairy_condensation", SpecialRecipeSerializer { FairyCondensationRecipe(it) })
    onGenerateRecipes {
        ComplexRecipeJsonBuilder.create(fairyCondensationRecipeSerializer).offerTo(it, Identifier(modId, "fairy_condensation").toString())
    }

}

class FairyCondensationRecipe(identifier: Identifier) : SpecialCraftingRecipe(identifier) {
    private fun match(inventory: CraftingInventory): ItemStack? {

        // 空白でないアイテムリスト抜き出し
        val itemStacks = (0 until inventory.size()).map { inventory.getStack(it) }.filter { it.isNotEmpty }

        // モード選択
        val isCondensation = when (itemStacks.size) {
            8 -> true
            1 -> false
            else -> return null
        }

        // アイテムが妖精かどうか
        val fairyItemStack = itemStacks.first()
        val item = fairyItemStack.item as? DemonFairyItem ?: return null

        // 一致判定
        if (!itemStacks.all { it hasSameItemAndNbt fairyItemStack }) return null

        // クラフト
        return if (isCondensation) {
            if (item.rank >= MAX_FAIRY_RANK) return null
            item.fairyCard(item.rank + 1).createItemStack()
        } else {
            if (item.rank <= 1) return null
            item.fairyCard(item.rank - 1).createItemStack(8)
        }
    }

    override fun matches(inventory: CraftingInventory, world: World) = match(inventory) != null
    override fun craft(inventory: CraftingInventory) = match(inventory) ?: EMPTY_ITEM_STACK
    override fun fits(width: Int, height: Int) = width * height >= 8
    override fun getSerializer() = fairyCondensationRecipeSerializer
}

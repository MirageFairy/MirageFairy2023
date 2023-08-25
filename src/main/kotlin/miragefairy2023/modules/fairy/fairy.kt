@file:Suppress("SpellCheckingInspection")

package miragefairy2023.modules.fairy

import miragefairy2023.MirageFairy2023
import miragefairy2023.api.fairyRegistry
import miragefairy2023.module
import miragefairy2023.modules.TrinketsSlotCard
import miragefairy2023.util.EMPTY_ITEM_STACK
import miragefairy2023.util.createItemStack
import miragefairy2023.util.datagen.TextureMap
import miragefairy2023.util.hasSameItemAndNbt
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.generateItemTag
import miragefairy2023.util.init.register
import miragefairy2023.util.init.registerColorProvider
import miragefairy2023.util.isNotEmpty
import miragefairy2023.util.string
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.data.client.Model
import net.minecraft.data.client.TextureKey
import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.recipe.SpecialCraftingRecipe
import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import java.util.Optional

// 妖精アイテムグループ
private val randomFairyIcon by lazy { FairyCard.values().random()[1].item.createItemStack() }
val fairyItemGroup: ItemGroup = FabricItemGroupBuilder.build(Identifier(MirageFairy2023.modId, "fairy")) { randomFairyIcon }

// 妖精アイテムタグ
val fairiesItemTag: TagKey<Item> = TagKey.of(Registry.ITEM_KEY, Identifier(MirageFairy2023.modId, "fairies"))
val fairiesOfRareTags: (rare: Int) -> TagKey<Item> = run {
    val map = mutableMapOf<Int, TagKey<Item>>()
    ({ rare -> map.getOrPut(rare) { TagKey.of(Registry.ITEM_KEY, Identifier(MirageFairy2023.modId, "rare${rare}_fairies")) } })
}

// 凝縮レシピ
val fairyCondensationRecipeSerializer: SpecialRecipeSerializer<FairyCondensationRecipe> = SpecialRecipeSerializer { FairyCondensationRecipe(it) }

val fairyModule = module {

    // アイテムグループ
    enJa(fairyItemGroup, "MirageFairy2023: Fairy", "MirageFairy2023: 妖精")

    // 妖精の共通アイテムモデル
    onGenerateItemModels {
        val layer0 = TextureKey.of("layer0")
        val layer1 = TextureKey.of("layer1")
        val layer2 = TextureKey.of("layer2")
        val layer3 = TextureKey.of("layer3")
        val layer4 = TextureKey.of("layer4")
        val model = Model(Optional.of(Identifier("minecraft", "item/generated")), Optional.empty(), layer0, layer1, layer2, layer3, layer4)
        val textureMap = TextureMap(
            layer0 to Identifier(MirageFairy2023.modId, "item/fairy_skin"),
            layer1 to Identifier(MirageFairy2023.modId, "item/fairy_back"),
            layer2 to Identifier(MirageFairy2023.modId, "item/fairy_front"),
            layer3 to Identifier(MirageFairy2023.modId, "item/fairy_hair"),
            layer4 to Identifier(MirageFairy2023.modId, "item/fairy_dress"),
        )
        model.upload(Identifier(MirageFairy2023.modId, "item/fairy"), textureMap, it.writer)
    }

    // 翻訳登録
    enJa(DemonFairyItem.RARE_KEY)
    enJa(DemonFairyItem.CONDENSATION_RECIPE_KEY)
    enJa(DemonFairyItem.DECONDENSATION_RECIPE_KEY)
    enJa(DemonFairyItem.BOTH_RECIPE_KEY)
    enJa(ITEM_TRANSPORTATION_DESCRIPTION_KEY)

    // 妖精アイテム
    run {

        // モチーフごと
        FairyCard.values().forEach { fairyCard ->

            // ラングごと
            (1..MAX_FAIRY_RANK).forEach { rank ->

                // 登録
                register(Registry.ITEM, fairyCard[rank].identifier, fairyCard[rank].item)

                // タグに登録
                generateItemTag(fairiesItemTag, fairyCard[rank].item)
                generateItemTag(fairiesOfRareTags(fairyCard[rank].item.rare), fairyCard[rank].item)
                generateItemTag(TrinketsSlotCard.HEAD_FAIRY.tag, fairyCard[rank].item)

                // モデル系
                onGenerateItemModels { it.register(fairyCard[rank].item, Model(Optional.of(Identifier(MirageFairy2023.modId, "item/fairy")), Optional.empty())) }
                registerColorProvider(fairyCard[rank].item) { _, tintIndex ->
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

            // 翻訳登録
            enJa("item.${fairyCard.motif.toTranslationKey()}_fairy", fairyCard.enName, fairyCard.jaName)

            // モチーフを妖精レジストリに登録
            register(fairyRegistry, fairyCard.motif, fairyCard.fairy)

            // モチーフ固有の初期化処理
            fairyCard.fairyRecipes.recipes.forEach {
                it.init(this, fairyCard)
            }

        }

    }

    // 凝縮・展開レシピ
    register(Registry.RECIPE_SERIALIZER, Identifier(MirageFairy2023.modId, "crafting_special_fairy_condensation"), fairyCondensationRecipeSerializer)
    onGenerateRecipes {
        ComplexRecipeJsonBuilder.create(fairyCondensationRecipeSerializer).offerTo(it, Identifier(MirageFairy2023.modId, "fairy_condensation").string)
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
            item.fairyCard[item.rank + 1].item.createItemStack()
        } else {
            if (item.rank <= 1) return null
            item.fairyCard[item.rank - 1].item.createItemStack(8)
        }
    }

    override fun matches(inventory: CraftingInventory, world: World) = match(inventory) != null
    override fun craft(inventory: CraftingInventory) = match(inventory) ?: EMPTY_ITEM_STACK
    override fun fits(width: Int, height: Int) = width * height >= 8
    override fun getSerializer() = fairyCondensationRecipeSerializer
}

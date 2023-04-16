@file:Suppress("SpellCheckingInspection")

package miragefairy2023.modules.fairy

import miragefairy2023.MirageFairy2023
import miragefairy2023.api.fairyRegistry
import miragefairy2023.getOrPut
import miragefairy2023.module
import miragefairy2023.slotOf
import miragefairy2023.util.createItemStack
import miragefairy2023.util.init.FeatureSlot
import miragefairy2023.util.init.TagScope
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.enJaItemGroup
import miragefairy2023.util.init.item
import miragefairy2023.util.init.itemTag
import miragefairy2023.util.init.registerColorProvider
import miragefairy2023.util.init.registerToTag
import miragefairy2023.util.init.translation
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.data.client.Model
import net.minecraft.data.client.TextureKey
import net.minecraft.data.client.TextureMap
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.Optional

val MAX_FAIRY_RANK = 1 // TODO -> 9

// 妖精アイテム
private lateinit var fairyItems: Map<FairyCard, Map<Int, FeatureSlot<DemonFairyItem>>>
operator fun FairyCard.invoke(rank: Int = 1) = fairyItems[this]!![rank]!!.feature

// 妖精アイテムグループ
private val randomFairyIcon by lazy { FairyCard.values().random()().createItemStack() }
val fairyItemGroup: ItemGroup = FabricItemGroupBuilder.build(Identifier(MirageFairy2023.modId, "fairy")) { randomFairyIcon }

// 妖精アイテムタグ
val fairiesItemTag = slotOf<TagScope<Item>>()
val fairiesOfRareItemTag = mutableMapOf<Int, TagScope<Item>>()

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

    // 妖精アイテムスロット
    val mutableFairyItems = mutableMapOf<FairyCard, MutableMap<Int, FeatureSlot<DemonFairyItem>>>()
    fairyItems = mutableFairyItems

    // モチーフごと
    FairyCard.values().forEach { fairyCard ->

        // ラングごと
        (1..MAX_FAIRY_RANK).forEach { rank ->

            // 妖精アイテム登録
            mutableFairyItems.getOrPut(fairyCard) { mutableMapOf() }[rank] = item(
                "${fairyCard.motif}_fairy${if (rank == 1) "" else "_$rank"}",
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
        enJa("item.${fairyCard.identifier.toTranslationKey()}_fairy", fairyCard.enName, fairyCard.jaName)

        // モチーフを妖精レジストリに登録
        Registry.register(fairyRegistry, fairyCard.identifier, fairyCard.fairy)

        // モチーフ固有の初期化処理
        fairyCard.recipeContainer.recipes.forEach {
            it.init(this, fairyCard)
        }

    }

}

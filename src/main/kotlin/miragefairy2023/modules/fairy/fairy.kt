@file:Suppress("SpellCheckingInspection")

package miragefairy2023.modules.fairy

import miragefairy2023.MirageFairy2023
import miragefairy2023.SlotContainer
import miragefairy2023.api.fairyRegistry
import miragefairy2023.module
import miragefairy2023.util.createItemStack
import miragefairy2023.util.init.TagScope
import miragefairy2023.util.init.enJaItem
import miragefairy2023.util.init.enJaItemGroup
import miragefairy2023.util.init.item
import miragefairy2023.util.init.itemTag
import miragefairy2023.util.init.registerColorProvider
import miragefairy2023.util.init.registerToTag
import miragefairy2023.util.init.translation
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.data.client.Model
import net.minecraft.data.client.TextureKey
import net.minecraft.data.client.TextureMap
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.Optional

// 妖精アイテム
private val fairyItems = SlotContainer<FairyCard, Item>()
operator fun FairyCard.invoke() = fairyItems[this]

// 妖精アイテムグループ
private val randomFairyIcon by lazy { FairyCard.values().random()().createItemStack() }
val fairyItemGroup: ItemGroup = FabricItemGroupBuilder.build(Identifier(MirageFairy2023.modId, "fairy")) { randomFairyIcon }

// 妖精アイテムタグ
lateinit var fairiesItemTag: TagScope<Item>
val fairiesOfRareItemTag = mutableMapOf<Int, TagScope<Item>>()

val fairyModule = module {

    // 全体
    run {

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

        // 妖精タグ
        fairiesItemTag = itemTag("fairies")
        (0..FairyCard.values().maxOf { it.rare }).forEach { rare ->
            fairiesOfRareItemTag[rare] = itemTag("rare${rare}_fairies")
        }

        // 翻訳登録
        translation(DemonFairyItem.RARE_KEY)
        translation(DemonFairyItem.DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY)
        translation(DemonFairyItem.DUPLICATED_PASSIVE_SKILL_DESCRIPTION_KEY)
        translation(DemonFairyItem.ENABLED_PASSIVE_SKILL_DESCRIPTION_KEY)
        translation(DemonFairyItem.ALWAYS_CONDITION_KEY)

    }

    // 妖精共通
    FairyCard.values().forEach { fairyCard ->

        // 妖精アイテム登録
        item("${fairyCard.motif}_fairy", { DemonFairyItem(fairyCard, FabricItemSettings().group(fairyItemGroup)) }) {

            // アイテム代入
            onRegisterItems { fairyItems[fairyCard] = feature }

            // タグに登録
            registerToTag { fairiesItemTag }
            registerToTag { fairiesOfRareItemTag[fairyCard.rare]!! }

            // モデル系
            onGenerateItemModels { it.register(feature, Model(Optional.of(Identifier(modId, "item/fairy")), Optional.empty())) }
            registerColorProvider { _, tintIndex ->
                when (tintIndex) {
                    0 -> fairyCard.skinColor
                    1 -> fairyCard.backColor
                    2 -> fairyCard.frontColor
                    3 -> fairyCard.hairColor
                    4 -> 0xAA0000
                    else -> 0xFFFFFF
                }
            }

            // 翻訳登録
            enJaItem({ feature }, fairyCard.enName, fairyCard.jaName)

        }

        // 妖精レジストリ登録
        Registry.register(fairyRegistry, fairyCard.identifier, fairyCard.fairy)

        // 妖精固有の初期化処理
        fairyCard.initializer.initializers.forEach {
            it(this, fairyCard)
        }

    }


    // パッシブスキル
    run {
        val terminators = mutableListOf<() -> Unit>()
        ServerTickEvents.END_SERVER_TICK.register { server ->
            if ((server.ticks % (20L * 10L)).toInt() != 132) return@register // 10秒毎

            // 前回判定時の掃除
            terminators.forEach {
                it()
            }
            terminators.clear()

            server.worlds.forEach { world ->
                world.players.forEach nextPlayer@{ player ->

                    if (player.isSpectator) return@nextPlayer // スペクテイターモードでは無効

                    // 有効な妖精のリスト
                    val triples = (player.inventory.offHand + player.inventory.main.slice(9 * 3 until 9 * 4))
                        .mapNotNull { itemStack ->
                            itemStack!!
                            val item = itemStack.item
                            if (item !is DemonFairyItem) return@mapNotNull null
                            Triple(itemStack, item, item.getFairy().getIdentifier())
                        }
                        .distinctBy { it.third }

                    val initializers = mutableListOf<() -> Unit>()

                    // 効果の計算
                    val passiveSkillVariable = mutableMapOf<Identifier, Any>()
                    triples.forEach { triple ->
                        triple.second.fairyCard.passiveSkills.forEach passiveSkillIsFailed@{ passiveSkill ->
                            passiveSkill.conditions.forEach { condition ->
                                if (!condition.test(player)) return@passiveSkillIsFailed
                            }
                            passiveSkill.effect.update(world, player, passiveSkillVariable, initializers, terminators)
                            passiveSkill.effect.affect(world, player)
                        }
                    }

                    // 効果を発動
                    initializers.forEach {
                        it()
                    }

                }
            }
        }
        ServerLifecycleEvents.SERVER_STOPPING.register {
            terminators.clear()
        }
    }

}

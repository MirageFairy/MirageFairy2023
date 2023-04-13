@file:Suppress("SpellCheckingInspection")

package miragefairy2023.modules.fairy

import miragefairy2023.MirageFairy2023
import miragefairy2023.SlotContainer
import miragefairy2023.api.Fairy
import miragefairy2023.api.FairyItem
import miragefairy2023.api.fairyRegistry
import miragefairy2023.module
import miragefairy2023.util.aqua
import miragefairy2023.util.createItemStack
import miragefairy2023.util.formatted
import miragefairy2023.util.gold
import miragefairy2023.util.gray
import miragefairy2023.util.init.TagScope
import miragefairy2023.util.init.Translation
import miragefairy2023.util.init.enJaItem
import miragefairy2023.util.init.enJaItemGroup
import miragefairy2023.util.init.item
import miragefairy2023.util.init.itemTag
import miragefairy2023.util.init.registerColorProvider
import miragefairy2023.util.init.registerToTag
import miragefairy2023.util.init.translation
import miragefairy2023.util.join
import miragefairy2023.util.red
import miragefairy2023.util.text
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.client.Model
import net.minecraft.data.client.TextureKey
import net.minecraft.data.client.TextureMap
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import java.util.Optional


private val fairyItems = SlotContainer<FairyCard, Item>()
operator fun FairyCard.invoke() = fairyItems[this]


private val randomFairyIcon by lazy { FairyCard.values().random()().createItemStack() }
val fairyItemGroup: ItemGroup = FabricItemGroupBuilder.build(Identifier(MirageFairy2023.modId, "fairy")) { randomFairyIcon }


lateinit var fairiesItemTag: TagScope<Item>
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

    // 妖精登録
    FairyCard.values().forEach { fairyCard ->
        item("${fairyCard.motif}_fairy", { DemonFairyItem(fairyCard, FabricItemSettings().group(fairyItemGroup)) }) {
            onRegisterItems { fairyItems[fairyCard] = feature }

            registerToTag { fairiesItemTag }
            registerToTag { fairiesOfRareItemTag[fairyCard.rare]!! }

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

            enJaItem({ feature }, fairyCard.enName, fairyCard.jaName)
        }
        Registry.register(fairyRegistry, fairyCard.identifier, fairyCard)
        fairyCard.initializer.initializers.forEach {
            it(this, fairyCard)
        }
    }

    // 妖精タグ
    fairiesItemTag = itemTag("fairies")
    (0..FairyCard.values().maxOf { it.rare }).forEach { rare ->
        fairiesOfRareItemTag[rare] = itemTag("rare${rare}_fairies")
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

    translation(DemonFairyItem.RARE_KEY)
    translation(DemonFairyItem.DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY)
    translation(DemonFairyItem.DUPLICATED_PASSIVE_SKILL_DESCRIPTION_KEY)
    translation(DemonFairyItem.ENABLED_PASSIVE_SKILL_DESCRIPTION_KEY)
    translation(DemonFairyItem.ALWAYS_CONDITION_KEY)

}


private class PassiveFairy(val player: PlayerEntity, val index: Int, val itemStack: ItemStack, val fairy: Fairy, val fairyIdentifier: Identifier, val isDuplicated: Boolean)

private fun PlayerEntity.getPassiveFairies(): List<PassiveFairy> {
    val itemStacks = this.inventory.offHand + this.inventory.main.slice(9 * 3 until 9 * 4)
    val result = mutableListOf<PassiveFairy>()
    val collectedFairyIdentifiers = mutableSetOf<Identifier>()
    itemStacks.forEachIndexed { index, itemStack ->
        itemStack!!
        val item = itemStack.item
        if (item !is FairyItem) return@forEachIndexed
        val fairy = item.getFairy()
        val fairyIdentifier = fairy.getIdentifier()
        val isDuplicated = fairyIdentifier in collectedFairyIdentifiers
        collectedFairyIdentifiers += fairyIdentifier
        result += PassiveFairy(this, index, itemStack, fairy, fairyIdentifier, isDuplicated)
    }
    return result.toList()
}


class DemonFairyItem(val fairyCard: FairyCard, settings: Settings) : Item(settings), FairyItem {
    companion object {
        val RARE_KEY = Translation("item.${MirageFairy2023.modId}.fairy.rare", "Rare", "レア度")
        val DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY = Translation("item.${MirageFairy2023.modId}.fairy.passive_skill.disabled", "Use passive skills in 3rd row of inventory", "インベントリの3行目でパッシブスキルを発動")
        val DUPLICATED_PASSIVE_SKILL_DESCRIPTION_KEY = Translation("item.${MirageFairy2023.modId}.fairy.passive_skill.duplicated", "Same fairies exist", "妖精が重複しています")
        val ENABLED_PASSIVE_SKILL_DESCRIPTION_KEY = Translation("item.${MirageFairy2023.modId}.fairy.passive_skill.enabled", "Passive skills are enabled", "パッシブスキル有効")
        val ALWAYS_CONDITION_KEY = Translation("${MirageFairy2023.modId}.passive_skill.condition.always", "Always", "常時")
    }

    override fun getFairy() = object : Fairy {
        override fun getIdentifier() = fairyCard.identifier
        override fun getItem() = this@DemonFairyItem
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)


        tooltip += text { (RARE_KEY() + ": "() + "${fairyCard.rare}"().formatted(getRareColor(fairyCard.rare))).aqua }


        val passiveSkills = fairyCard.passiveSkills
        if (passiveSkills.isNotEmpty()) {

            val player = MirageFairy2023.proxy?.getClientPlayer() ?: return

            val passiveFairy = player.getPassiveFairies().find { it.itemStack === stack }

            val isEnabled = passiveFairy != null
            val isDuplicated = passiveFairy != null && passiveFairy.isDuplicated

            // パッシブスキルタイトル行
            tooltip += text {
                when {
                    !isEnabled -> DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY().gray
                    isDuplicated -> DUPLICATED_PASSIVE_SKILL_DESCRIPTION_KEY().red
                    else -> ENABLED_PASSIVE_SKILL_DESCRIPTION_KEY().gold
                }
            }

            // パッシブスキル行
            passiveSkills.forEach { passiveSkill ->

                // 条件判定
                val conditions = passiveSkill.conditions.map { condition ->
                    Pair(condition, condition.test(player))
                }

                tooltip += text {
                    val effectText = passiveSkill.effect.getText()
                    val conditionTexts = conditions.map {
                        if (it.second) {
                            it.first.getText()
                        } else {
                            it.first.getText().red
                        }
                    }
                    val text = if (conditionTexts.isNotEmpty()) {
                        effectText + " ["() + conditionTexts.join(","()) + "]"()
                    } else {
                        effectText + " ["() + ALWAYS_CONDITION_KEY() + "]"()
                    }
                    if (isEnabled && !isDuplicated && conditions.all { it.second }) text.gold else text.gray
                }

            }

        }


    }
}

fun getRareColor(rare: Int): Formatting = when (rare) {
    0 -> Formatting.AQUA
    1 -> Formatting.GRAY
    2 -> Formatting.WHITE
    3 -> Formatting.GREEN
    4 -> Formatting.DARK_GREEN
    5 -> Formatting.YELLOW
    6 -> Formatting.GOLD
    7 -> Formatting.RED
    8 -> Formatting.DARK_RED
    9 -> Formatting.BLUE
    10 -> Formatting.DARK_BLUE
    11 -> Formatting.LIGHT_PURPLE
    12 -> Formatting.DARK_PURPLE
    else -> Formatting.DARK_AQUA
}

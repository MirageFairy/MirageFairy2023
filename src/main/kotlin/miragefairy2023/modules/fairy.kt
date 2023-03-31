@file:Suppress("SpellCheckingInspection")

package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.SlotContainer
import miragefairy2023.module
import miragefairy2023.modules.passiveskill.AirPassiveSkillCondition
import miragefairy2023.modules.passiveskill.AttackDamagePassiveSkillEffect
import miragefairy2023.modules.passiveskill.BiomePassiveSkillCondition
import miragefairy2023.modules.passiveskill.DarknessPassiveSkillCondition
import miragefairy2023.modules.passiveskill.DiamondToolPassiveSkillCondition
import miragefairy2023.modules.passiveskill.ExperiencePassiveSkillEffect
import miragefairy2023.modules.passiveskill.HasHoePassiveSkillCondition
import miragefairy2023.modules.passiveskill.InRainPassiveSkillCondition
import miragefairy2023.modules.passiveskill.IronToolPassiveSkillCondition
import miragefairy2023.modules.passiveskill.MaxHealthPassiveSkillEffect
import miragefairy2023.modules.passiveskill.MaximumLevelPassiveSkillCondition
import miragefairy2023.modules.passiveskill.MoonlightPassiveSkillCondition
import miragefairy2023.modules.passiveskill.MovementSpeedPassiveSkillEffect
import miragefairy2023.modules.passiveskill.NightPassiveSkillCondition
import miragefairy2023.modules.passiveskill.OnFirePassiveSkillCondition
import miragefairy2023.modules.passiveskill.OverworldPassiveSkillCondition
import miragefairy2023.modules.passiveskill.PassiveSkill
import miragefairy2023.modules.passiveskill.ShadePassiveSkillCondition
import miragefairy2023.modules.passiveskill.StatusEffectPassiveSkillEffect
import miragefairy2023.modules.passiveskill.SunshinePassiveSkillCondition
import miragefairy2023.util.Translation
import miragefairy2023.util.aqua
import miragefairy2023.util.enJaItem
import miragefairy2023.util.formatted
import miragefairy2023.util.gold
import miragefairy2023.util.gray
import miragefairy2023.util.item
import miragefairy2023.util.join
import miragefairy2023.util.red
import miragefairy2023.util.text
import miragefairy2023.util.translation
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.client.Model
import net.minecraft.data.client.TextureKey
import net.minecraft.data.client.TextureMap
import net.minecraft.data.server.RecipeProvider
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.tag.BiomeTags
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.world.World
import java.util.Optional


enum class FairyCard(
    val motif: String,
    val rare: Int,
    val enName: String,
    val jaName: String,
    val skinColor: Int,
    val frontColor: Int,
    val backColor: Int,
    val hairColor: Int,
    val passiveSkills: List<PassiveSkill>,
) {
    AIR(
        "air", 0, "Airia", "空気精アイリャ", 0xFFBE80, 0xDEFFFF, 0xDEFFFF, 0xB0FFFF,
        listOf(PassiveSkill(listOf(OverworldPassiveSkillCondition(), AirPassiveSkillCondition()), MovementSpeedPassiveSkillEffect(0.05))),
    ),
    FIRE(
        "fire", 2, "Firia", "火精フィーリャ",
        0xFF6C01, 0xF9DFA4, 0xFF7324, 0xFF4000,
        listOf(PassiveSkill(listOf(OnFirePassiveSkillCondition()), AttackDamagePassiveSkillEffect(2.0))),
    ),
    LAVA(
        "lava", 4, "Lavia", "溶岩精ラーヴャ",
        0xCD4208, 0xEDB54A, 0xCC4108, 0x4C1500,
        listOf(
            PassiveSkill(listOf(OnFirePassiveSkillCondition()), AttackDamagePassiveSkillEffect(2.0)),
            PassiveSkill(listOf(OnFirePassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0)),
        ),
    ),
    MOON(
        "moon", 9, "Moonia", "月精モーニャ",
        0xD9E4FF, 0x747D93, 0x0C121F, 0x2D4272,
        listOf(
            PassiveSkill(listOf(MoonlightPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.NIGHT_VISION, 0, additionalSeconds = 10)),
            PassiveSkill(listOf(MoonlightPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0)),
        ),
    ),
    SUN(
        "sun", 10, "Sunia", "太陽精スーニャ", 0xff2f00, 0xff972b, 0xff7500, 0xffe7b2,
        listOf(
            PassiveSkill(listOf(SunshinePassiveSkillCondition()), AttackDamagePassiveSkillEffect(6.0)),
            PassiveSkill(listOf(SunshinePassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0)),
        ),
    ),
    RAIN(
        "rain", 2, "Rainia", "雨精ライニャ",
        0xB4FFFF, 0x4D5670, 0x4D5670, 0x2D40F4,
        listOf(
            PassiveSkill(listOf(InRainPassiveSkillCondition()), AttackDamagePassiveSkillEffect(2.0)),
        ),
    ),
    DIRT(
        "dirt", 1, "Dirtia", "土精ディルチャ", 0xB87440, 0xB9855C, 0x593D29, 0x914A18,
        listOf(PassiveSkill(listOf(OverworldPassiveSkillCondition()), MaxHealthPassiveSkillEffect(1.0))),
    ),
    IRON(
        "iron", 4, "Ironia", "鉄精イローニャ", 0xA0A0A0, 0xD8D8D8, 0x727272, 0xD8AF93,
        listOf(
            PassiveSkill(listOf(IronToolPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 0)),
            PassiveSkill(listOf(IronToolPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 0)),
        ),
    ),
    DIAMOND(
        "diamond", 7, "Diamondia", "金剛石精ディアモンジャ",
        0x97FFE3, 0xD1FAF3, 0x70FFD9, 0x30DBBD,
        listOf(
            PassiveSkill(listOf(DiamondToolPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 1)),
            PassiveSkill(listOf(DiamondToolPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 0)),
            PassiveSkill(listOf(DiamondToolPassiveSkillCondition()), AttackDamagePassiveSkillEffect(2.0)),
        ),
    ),
    PLAYER(
        "player", 5, "Playeria", "人精プライェーリャ", 0xB58D63, 0x00AAAA, 0x322976, 0x4B3422,
        listOf(PassiveSkill(listOf(MaximumLevelPassiveSkillCondition(29)), ExperiencePassiveSkillEffect(1))),
    ),
    WARDEN(
        "warden", 8, "Wardenia", "監守者精ワルデーニャ", 0x0A3135, 0xCFCFA4, 0xA0AA7A, 0x2CD0CA,
        listOf(PassiveSkill(listOf(DarknessPassiveSkillCondition()), AttackDamagePassiveSkillEffect(8.0))),
    ),
    ZOMBIE(
        "zombie", 2, "Zombia", "硬屍精ゾンビャ", 0x2B4219, 0x00AAAA, 0x322976, 0x2B4219,
        listOf(PassiveSkill(listOf(ShadePassiveSkillCondition()), AttackDamagePassiveSkillEffect(1.0))),
    ),
    SPRUCE(
        "spruce", 6, "Sprucia", "松精スプルーツァ",
        0x795C36, 0x583E1F, 0x23160A, 0x4C784C,
        listOf(
            PassiveSkill(listOf(BiomePassiveSkillCondition(BiomeTags.IS_FOREST)), AttackDamagePassiveSkillEffect(2.0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(BiomeTags.IS_TAIGA)), AttackDamagePassiveSkillEffect(2.0)),
        ),
    ),
    HOE(
        "hoe", 3, "Hia", "鍬精ヒャ",
        0xFFFFFF, 0xFFC48E, 0x47FF00, 0xFFFFFF,
        listOf(
            PassiveSkill(listOf(HasHoePassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 0)),
            PassiveSkill(listOf(HasHoePassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 0)),
        ),
    ),
    FOREST(
        "forest", 3, "Forestia", "森精フォレスチャ", 0x80FF00, 0x7B9C62, 0x89591D, 0x2E6E14,
        listOf(PassiveSkill(listOf(BiomePassiveSkillCondition(BiomeTags.IS_FOREST)), StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0))),
    ),
    DESERT(
        "desert", 3, "Desertia", "砂漠精デセルチャ",
        0x80FF00, 0xDDD6A5, 0xD6CE9D, 0x0F6C1C,
        listOf(
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.DESERT), SunshinePassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.FIRE_RESISTANCE, 0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.DESERT), MoonlightPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 0)),
        ),
    ),
    AVALON(
        "avalon", 8, "Avalonia", "阿瓦隆精アヴァローニャ",
        0xFFE4CA, 0xE1FFCE, 0xD0FFE6, 0xFFCAFF,
        listOf(
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.MUSHROOM)), StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 1)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.MUSHROOM)), StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 1)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.FLORAL)), StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.FLORAL)), StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0)),
        ),
    ),
    VOID(
        "void", 11, "Voidia", "奈落精ヴォイジャ", 0x000000, 0x000000, 0x000000, 0xB1B1B1,
        listOf(
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_THE_END)), StatusEffectPassiveSkillEffect(StatusEffects.SLOW_FALLING, 0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_THE_END)), StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_THE_END)), StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 2)),
        ),
    ),
    NIGHT(
        "night", 7, "Nightia", "夜精ニグチャ", 0xFFE260, 0x2C2C2E, 0x0E0E10, 0x2D4272,
        listOf(
            PassiveSkill(listOf(NightPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 0)),
            PassiveSkill(listOf(NightPassiveSkillCondition()), MovementSpeedPassiveSkillEffect(0.05)),
        ),
    ),
    TIME(
        "time", 12, "Timia", "時精ティーミャ", 0x89D585, 0xD5DEBC, 0xD8DEA7, 0x8DD586,
        listOf(
            PassiveSkill(listOf(), MovementSpeedPassiveSkillEffect(0.50)),
            PassiveSkill(listOf(), StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 2)),
        ),
    ),
}

val FairyCard.identifier get() = Identifier(MirageFairy2023.modId, this.motif)

private val fairyItems = SlotContainer<FairyCard, Item>()
operator fun FairyCard.invoke() = fairyItems[this]


val fairyModule = module {

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
        item("${fairyCard.motif}_fairy", { FairyItem(fairyCard, FabricItemSettings().group(commonItemGroup)) }) {
            onRegisterItems { fairyItems[fairyCard] = item }

            onGenerateItemModels { it.register(item, Model(Optional.of(Identifier(modId, "item/fairy")), Optional.empty())) }
            onRegisterColorProvider { it ->
                it(item) { _, tintIndex ->
                    when (tintIndex) {
                        0 -> fairyCard.skinColor
                        1 -> fairyCard.backColor
                        2 -> fairyCard.frontColor
                        3 -> fairyCard.hairColor
                        4 -> 0xAA0000
                        else -> 0xFFFFFF
                    }
                }
            }

            enJaItem({ item }, fairyCard.enName, fairyCard.jaName)
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
                world.players.forEach { player ->

                    // 有効な妖精のリスト
                    val triples = (player.inventory.offHand + player.inventory.main.slice(9 * 3 until 9 * 4))
                        .mapNotNull { itemStack ->
                            itemStack!!
                            val item = itemStack.item
                            if (item !is FairyProviderItem) return@mapNotNull null
                            val fairy = item.getFairy()
                            Triple(itemStack, fairy, fairy.getIdentifier())
                        }
                        .distinctBy { it.third }

                    val initializers = mutableListOf<() -> Unit>()

                    // 効果の計算
                    val passiveSkillVariable = mutableMapOf<Identifier, Any>()
                    triples.forEach { triple ->
                        triple.second.getPassiveSkills().forEach passiveSkillIsFailed@{ passiveSkill ->
                            passiveSkill.conditions.forEach { condition ->
                                if (!condition.test(player)) return@passiveSkillIsFailed
                            }
                            passiveSkill.effect.update(player, passiveSkillVariable, initializers, terminators)
                            passiveSkill.effect.affect(player)
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

    translation(FairyItem.RARE_KEY)
    translation(FairyItem.DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY)
    translation(FairyItem.DUPLICATED_PASSIVE_SKILL_DESCRIPTION_KEY)
    translation(FairyItem.ENABLED_PASSIVE_SKILL_DESCRIPTION_KEY)
    translation(FairyItem.ALWAYS_CONDITION_KEY)


    // 紅天石＋土→土精
    onGenerateRecipes {
        ShapelessRecipeJsonBuilder
            .create(FairyCard.DIRT())
            .input(DemonItemCard.XARPITE())
            .input(DemonItemCard.MIRAGE_FLOUR())
            .input(Items.DIRT)
            .criterion("has_xarpite", RecipeProvider.conditionsFromItem(DemonItemCard.XARPITE()))
            .criterion("has_mirage_flour", RecipeProvider.conditionsFromItem(DemonItemCard.MIRAGE_FLOUR()))
            .criterion("has_dirt", RecipeProvider.conditionsFromItem(Items.DIRT))
            .offerTo(it, Identifier.of(modId, "fairy/dirt"))
    }

    // 紅天石＋鉄インゴット→鉄精
    onGenerateRecipes {
        ShapelessRecipeJsonBuilder
            .create(FairyCard.IRON())
            .input(DemonItemCard.XARPITE())
            .input(DemonItemCard.RARE_MIRAGE_FLOUR())
            .input(Items.IRON_INGOT)
            .criterion("has_xarpite", RecipeProvider.conditionsFromItem(DemonItemCard.XARPITE()))
            .criterion("has_rare_mirage_flour", RecipeProvider.conditionsFromItem(DemonItemCard.RARE_MIRAGE_FLOUR()))
            .criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT))
            .offerTo(it, Identifier.of(modId, "fairy/iron"))
    }

}


class PassiveFairy(val player: PlayerEntity, val index: Int, val itemStack: ItemStack, val fairy: Fairy, val fairyIdentifier: Identifier, val isDuplicated: Boolean)

private fun PlayerEntity.getPassiveFairies(): List<PassiveFairy> {
    val itemStacks = this.inventory.offHand + this.inventory.main.slice(9 * 3 until 9 * 4)
    val result = mutableListOf<PassiveFairy>()
    val collectedFairyIdentifiers = mutableSetOf<Identifier>()
    itemStacks.forEachIndexed { index, itemStack ->
        itemStack!!
        val item = itemStack.item
        if (item !is FairyProviderItem) return@forEachIndexed
        val fairy = item.getFairy()
        val fairyIdentifier = fairy.getIdentifier()
        val isDuplicated = fairyIdentifier in collectedFairyIdentifiers
        collectedFairyIdentifiers += fairyIdentifier
        result += PassiveFairy(this, index, itemStack, fairy, fairyIdentifier, isDuplicated)
    }
    return result.toList()
}


interface FairyProviderItem {
    fun getFairy(): Fairy
}

interface Fairy {
    fun getIdentifier(): Identifier
    fun getPassiveSkills(): List<PassiveSkill>
}


class FairyItem(val fairyCard: FairyCard, settings: Settings) : Item(settings), FairyProviderItem {
    companion object {
        val RARE_KEY = Translation("item.${MirageFairy2023.modId}.fairy.rare", "Rare", "レア度")
        val DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY = Translation("item.${MirageFairy2023.modId}.fairy.passive_skill.disabled", "Use passive skills in 3rd row of inventory", "インベントリの3行目でパッシブスキルを発動")
        val DUPLICATED_PASSIVE_SKILL_DESCRIPTION_KEY = Translation("item.${MirageFairy2023.modId}.fairy.passive_skill.duplicated", "Same fairies exist", "妖精が重複しています")
        val ENABLED_PASSIVE_SKILL_DESCRIPTION_KEY = Translation("item.${MirageFairy2023.modId}.fairy.passive_skill.enabled", "Passive skills are enabled", "パッシブスキル有効")
        val ALWAYS_CONDITION_KEY = Translation("${MirageFairy2023.modId}.passive_skill.condition.always", "Always", "常時")
    }

    override fun getFairy() = object : Fairy {
        override fun getIdentifier() = fairyCard.identifier
        override fun getPassiveSkills() = fairyCard.passiveSkills
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)


        tooltip += text { (RARE_KEY() + ": "() + "${fairyCard.rare}"().formatted(getRareColor(fairyCard.rare))).aqua }


        val passiveSkills = getFairy().getPassiveSkills()
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

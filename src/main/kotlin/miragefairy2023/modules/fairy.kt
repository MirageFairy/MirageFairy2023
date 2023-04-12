@file:Suppress("SpellCheckingInspection")

package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.SlotContainer
import miragefairy2023.module
import miragefairy2023.modules.passiveskill.AirPassiveSkillCondition
import miragefairy2023.modules.passiveskill.AttackDamagePassiveSkillEffect
import miragefairy2023.modules.passiveskill.BiomePassiveSkillCondition
import miragefairy2023.modules.passiveskill.CollectionPassiveSkillEffect
import miragefairy2023.modules.passiveskill.ExperiencePassiveSkillEffect
import miragefairy2023.modules.passiveskill.HasHoePassiveSkillCondition
import miragefairy2023.modules.passiveskill.InRainPassiveSkillCondition
import miragefairy2023.modules.passiveskill.IndoorPassiveSkillCondition
import miragefairy2023.modules.passiveskill.LuckPassiveSkillEffect
import miragefairy2023.modules.passiveskill.MaxHealthPassiveSkillEffect
import miragefairy2023.modules.passiveskill.MaximumLevelPassiveSkillCondition
import miragefairy2023.modules.passiveskill.MaximumLightLevelPassiveSkillCondition
import miragefairy2023.modules.passiveskill.MinimumLightLevelPassiveSkillCondition
import miragefairy2023.modules.passiveskill.MoonlightPassiveSkillCondition
import miragefairy2023.modules.passiveskill.MovementSpeedPassiveSkillEffect
import miragefairy2023.modules.passiveskill.NightPassiveSkillCondition
import miragefairy2023.modules.passiveskill.OnFirePassiveSkillCondition
import miragefairy2023.modules.passiveskill.OverworldPassiveSkillCondition
import miragefairy2023.modules.passiveskill.PassiveSkill
import miragefairy2023.modules.passiveskill.ShadePassiveSkillCondition
import miragefairy2023.modules.passiveskill.StatusEffectPassiveSkillEffect
import miragefairy2023.modules.passiveskill.SunshinePassiveSkillCondition
import miragefairy2023.modules.passiveskill.ToolMaterialPassiveSkillCondition
import miragefairy2023.modules.passiveskill.UnderwaterPassiveSkillCondition
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
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.fabricmc.fabric.api.event.registry.RegistryAttribute
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
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.ToolMaterials
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.SimpleRegistry
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
    LIGHT(
        "light", 3, "Lightia", "光精リグチャ", 0xFFFFD8, 0xFFFFD8, 0xFFFFC5, 0xFFFF00,
        listOf(PassiveSkill(listOf(MinimumLightLevelPassiveSkillCondition(12)), MovementSpeedPassiveSkillEffect(0.15))),
    ),
    FIRE(
        "fire", 2, "Firia", "火精フィーリャ", 0xFF6C01, 0xF9DFA4, 0xFF7324, 0xFF4000,
        listOf(PassiveSkill(listOf(OnFirePassiveSkillCondition()), AttackDamagePassiveSkillEffect(2.0))),
    ),
    LAVA(
        "lava", 4, "Lavia", "溶岩精ラーヴャ", 0xCD4208, 0xEDB54A, 0xCC4108, 0x4C1500,
        listOf(
            PassiveSkill(listOf(OnFirePassiveSkillCondition()), AttackDamagePassiveSkillEffect(1.0)),
            PassiveSkill(listOf(OnFirePassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 0)),
            PassiveSkill(listOf(OnFirePassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0)),
        ),
    ),
    MOON(
        "moon", 9, "Moonia", "月精モーニャ", 0xD9E4FF, 0x747D93, 0x0C121F, 0x2D4272,
        listOf(
            PassiveSkill(listOf(MoonlightPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.NIGHT_VISION, 0, additionalSeconds = 10)),
            PassiveSkill(listOf(MoonlightPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0)),
        ),
    ),
    SUN(
        "sun", 10, "Sunia", "太陽精スーニャ", 0xff2f00, 0xff972b, 0xff7500, 0xffe7b2,
        listOf(
            PassiveSkill(listOf(SunshinePassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 1)),
            PassiveSkill(listOf(SunshinePassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0)),
        ),
    ),
    RAIN(
        "rain", 2, "Rainia", "雨精ライニャ", 0xB4FFFF, 0x4D5670, 0x4D5670, 0x2D40F4,
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
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.IRON)), AttackDamagePassiveSkillEffect(1.0)),
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.IRON)), StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 0)),
        ),
    ),
    GOLD(
        "gold", 6, "Goldia", "金精ゴルジャ", 0xD2CD9A, 0xFFFF0B, 0xDC7613, 0xDEDE00,
        listOf(
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.GOLD)), AttackDamagePassiveSkillEffect(1.0)),
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.GOLD)), StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 0)),
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.GOLD)), MovementSpeedPassiveSkillEffect(0.10)),
        ),
    ),
    DIAMOND(
        "diamond", 7, "Diamondia", "金剛石精ディアモンジャ", 0x97FFE3, 0xD1FAF3, 0x70FFD9, 0x30DBBD,
        listOf(
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.DIAMOND)), AttackDamagePassiveSkillEffect(2.0)),
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.DIAMOND)), StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 0)),
        ),
    ),
    FISH(
        "fish", 2, "Fishia", "魚精フィーシャ", 0x6B9F93, 0x5A867C, 0x43655D, 0xADBEDB,
        listOf(PassiveSkill(listOf(UnderwaterPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.NIGHT_VISION, 0, additionalSeconds = 10))),
    ),
    CLOWNFISH(
        "clownfish", 7, "Clownfishia", "隈之実精ツロウンフィーシャ", 0xE46A22, 0xF46F20, 0xA94B1D, 0xFFDBC5,
        listOf(PassiveSkill(listOf(UnderwaterPassiveSkillCondition(), MinimumLightLevelPassiveSkillCondition(4)), StatusEffectPassiveSkillEffect(StatusEffects.WATER_BREATHING, 0))),
    ),
    PLAYER(
        "player", 5, "Playeria", "人精プライェーリャ", 0xB58D63, 0x00AAAA, 0x322976, 0x4B3422,
        listOf(PassiveSkill(listOf(MaximumLevelPassiveSkillCondition(29)), ExperiencePassiveSkillEffect(0.5))),
    ),
    ENDERMAN(
        "enderman", 6, "Endermania", "終界人精エンデルマーニャ", 0x000000, 0x161616, 0x161616, 0xEF84FA,
        listOf(PassiveSkill(listOf(), CollectionPassiveSkillEffect(4.0))),
    ),
    WARDEN(
        "warden", 7, "Wardenia", "監守者精ワルデーニャ", 0x0A3135, 0xCFCFA4, 0xA0AA7A, 0x2CD0CA,
        listOf(
            PassiveSkill(listOf(MaximumLightLevelPassiveSkillCondition(0)), StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 1)),
            PassiveSkill(listOf(MaximumLightLevelPassiveSkillCondition(0)), AttackDamagePassiveSkillEffect(2.0)),
        ),
    ),
    ZOMBIE(
        "zombie", 2, "Zombia", "硬屍精ゾンビャ", 0x2B4219, 0x00AAAA, 0x322976, 0x2B4219,
        listOf(PassiveSkill(listOf(ShadePassiveSkillCondition()), AttackDamagePassiveSkillEffect(1.0))),
    ),
    SPRUCE(
        "spruce", 6, "Sprucia", "松精スプルーツァ", 0x795C36, 0x583E1F, 0x23160A, 0x4C784C,
        listOf(
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.FOREST)), AttackDamagePassiveSkillEffect(1.0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.TAIGA)), AttackDamagePassiveSkillEffect(1.0)),
        ),
    ),
    HOE(
        "hoe", 3, "Hia", "鍬精ヒャ", 0xFFFFFF, 0xFFC48E, 0x47FF00, 0xFFFFFF,
        listOf(
            PassiveSkill(listOf(HasHoePassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 0)),
            PassiveSkill(listOf(HasHoePassiveSkillCondition()), LuckPassiveSkillEffect(0.5)),
        ),
    ),
    CRAFTING_TABLE(
        "crafting_table", 4, "Craftinge Tablia", "作業台精ツラフティンゲターブリャ", 0xFFFFFF, 0xFFBB9A, 0xFFC980, 0x000000,
        listOf(PassiveSkill(listOf(IndoorPassiveSkillCondition()), LuckPassiveSkillEffect(0.5))),
    ),
    PLAINS(
        "plains", 2, "Plainsia", "平原精プラインシャ", 0xB0DF83, 0xD4FF82, 0x86C91C, 0x489F25,
        listOf(PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.PLAINS)), StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 0))),
    ),
    OCEAN(
        "ocean", 3, "Oceania", "海精オツェアーニャ", 0x7DAEF5, 0x1B6CE9, 0x191CF0, 0x004DA5,
        listOf(PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.OCEAN)), StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 0))),
    ),
    TAIGA(
        "taiga", 5, "Taigia", "針葉樹林精タイギャ", 0x5D985E, 0x476545, 0x223325, 0x5A3711,
        listOf(PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.TAIGA)), StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 0))),
    ),
    MOUNTAIN(
        "mountain", 6, "Mountainia", "山精モウンタイニャ", 0x84BF80, 0xB1B0B1, 0x717173, 0xF0F0F0,
        listOf(PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.MOUNTAIN)), StatusEffectPassiveSkillEffect(StatusEffects.JUMP_BOOST, 1))),
    ),
    FOREST(
        "forest", 3, "Forestia", "森精フォレスチャ", 0x8EBF7A, 0x7B9C62, 0x89591D, 0x2E6E14,
        listOf(PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.FOREST)), StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0))),
    ),
    DESERT(
        "desert", 4, "Desertia", "砂漠精デセルチャ", 0xF9F0C8, 0xDDD6A5, 0xD6CE9D, 0x656054,
        listOf(
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.DESERT), SunshinePassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.FIRE_RESISTANCE, 0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.DESERT), MoonlightPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 0)),
        ),
    ),
    AVALON(
        "avalon", 8, "Avalonia", "阿瓦隆精アヴァローニャ", 0xFFE4CA, 0xE1FFCE, 0xD0FFE6, 0xFFCAFF,
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
        ),
    ),
    TIME(
        "time", 12, "Timia", "時精ティーミャ", 0x89D585, 0xD5DEBC, 0xD8DEA7, 0x8DD586,
        listOf(
            PassiveSkill(listOf(), StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 1)),
            PassiveSkill(listOf(), MovementSpeedPassiveSkillEffect(0.10)),
            PassiveSkill(listOf(), StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 1)),
        ),
    ),
    GRAVITY(
        "gravity", 12, "Gravitia", "重力精グラヴィーチャ", 0xC2A7F2, 0x3600FF, 0x2A00B1, 0x110047,
        listOf(
            PassiveSkill(listOf(), StatusEffectPassiveSkillEffect(StatusEffects.SLOW_FALLING, 0)),
            PassiveSkill(listOf(), AttackDamagePassiveSkillEffect(2.0)),
        ),
    ),
}

val FairyCard.identifier get() = Identifier(MirageFairy2023.modId, this.motif)

private val fairyItems = SlotContainer<FairyCard, Item>()
operator fun FairyCard.invoke() = fairyItems[this]


val fairyRegistry: SimpleRegistry<FairyCard> = FabricRegistryBuilder.createSimple(FairyCard::class.java, Identifier(MirageFairy2023.modId, "fairy"))
    .attribute(RegistryAttribute.SYNCED)
    .buildAndRegister()


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
        item("${fairyCard.motif}_fairy", { FairyItem(fairyCard, FabricItemSettings().group(fairyItemGroup)) }) {
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

    translation(FairyItem.RARE_KEY)
    translation(FairyItem.DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY)
    translation(FairyItem.DUPLICATED_PASSIVE_SKILL_DESCRIPTION_KEY)
    translation(FairyItem.ENABLED_PASSIVE_SKILL_DESCRIPTION_KEY)
    translation(FairyItem.ALWAYS_CONDITION_KEY)


    // 確定召喚レシピ
    fun registerFairySummoningRecipe(fairyCard: FairyCard, inputItemSupplier: () -> Item) = onGenerateRecipes {
        val inputItem = inputItemSupplier()
        val mirageFlowerItem = when (fairyCard.rare) {
            0 -> MirageFlourCard.TINY_MIRAGE_FLOUR()
            1, 2 -> MirageFlourCard.MIRAGE_FLOUR()
            3, 4 -> MirageFlourCard.RARE_MIRAGE_FLOUR()
            5, 6 -> MirageFlourCard.VERY_RARE_MIRAGE_FLOUR()
            7, 8 -> MirageFlourCard.ULTRA_RARE_MIRAGE_FLOUR()
            9, 10 -> MirageFlourCard.SUPER_RARE_MIRAGE_FLOUR()
            11, 12 -> MirageFlourCard.EXTREMELY_RARE_MIRAGE_FLOUR()
            else -> throw AssertionError()
        }
        ShapelessRecipeJsonBuilder
            .create(fairyCard())
            .input(DemonItemCard.XARPITE())
            .input(mirageFlowerItem)
            .input(inputItem)
            .criterion("has_xarpite", RecipeProvider.conditionsFromItem(DemonItemCard.XARPITE()))
            .criterion("has_${Registry.ITEM.getId(mirageFlowerItem).path}", RecipeProvider.conditionsFromItem(mirageFlowerItem))
            .criterion("has_${Registry.ITEM.getId(inputItem).path}", RecipeProvider.conditionsFromItem(inputItem))
            .offerTo(it, Identifier.of(modId, "fairy/${fairyCard.motif}"))
    }
    registerFairySummoningRecipe(FairyCard.LAVA) { Items.LAVA_BUCKET } // 溶岩
    registerFairySummoningRecipe(FairyCard.DIRT) { Items.DIRT } // 土
    registerFairySummoningRecipe(FairyCard.IRON) { Items.IRON_INGOT } // 鉄
    registerFairySummoningRecipe(FairyCard.GOLD) { Items.GOLD_INGOT } // 金
    registerFairySummoningRecipe(FairyCard.DIAMOND) { Items.DIAMOND } // ダイヤモンド
    registerFairySummoningRecipe(FairyCard.FISH) { Items.COD } // 魚 // TODO 魚精は希釈で得る
    registerFairySummoningRecipe(FairyCard.CLOWNFISH) { Items.TROPICAL_FISH } // クマノミ
    registerFairySummoningRecipe(FairyCard.SPRUCE) { Items.SPRUCE_SAPLING } // 松
    registerFairySummoningRecipe(FairyCard.HOE) { Items.STONE_HOE } // クワ

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

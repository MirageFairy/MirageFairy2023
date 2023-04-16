package miragefairy2023.modules.fairy

import miragefairy2023.InitializationScope
import miragefairy2023.MirageFairy2023
import miragefairy2023.api.Fairy
import miragefairy2023.api.PassiveSkillCondition
import miragefairy2023.api.PassiveSkillEffect
import miragefairy2023.modules.BlockFairyRelation
import miragefairy2023.modules.DemonItemCard
import miragefairy2023.modules.DreamCatcherItem
import miragefairy2023.modules.MirageFlourCard
import miragefairy2023.modules.MirageFlourItem
import miragefairy2023.modules.invoke
import miragefairy2023.modules.passiveskill.AirPassiveSkillCondition
import miragefairy2023.modules.passiveskill.AttackDamagePassiveSkillEffect
import miragefairy2023.modules.passiveskill.BiomePassiveSkillCondition
import miragefairy2023.modules.passiveskill.CollectionPassiveSkillEffect
import miragefairy2023.modules.passiveskill.ExperiencePassiveSkillEffect
import miragefairy2023.modules.passiveskill.FairyLevelPassiveSkillCondition
import miragefairy2023.modules.passiveskill.HasHoePassiveSkillCondition
import miragefairy2023.modules.passiveskill.InRainPassiveSkillCondition
import miragefairy2023.modules.passiveskill.IndoorPassiveSkillCondition
import miragefairy2023.modules.passiveskill.LuckPassiveSkillEffect
import miragefairy2023.modules.passiveskill.MaxHealthPassiveSkillEffect
import miragefairy2023.modules.passiveskill.MaximumHealthPassiveSkillCondition
import miragefairy2023.modules.passiveskill.MaximumLevelPassiveSkillCondition
import miragefairy2023.modules.passiveskill.MaximumLightLevelPassiveSkillCondition
import miragefairy2023.modules.passiveskill.MinimumLightLevelPassiveSkillCondition
import miragefairy2023.modules.passiveskill.MoonlightPassiveSkillCondition
import miragefairy2023.modules.passiveskill.MovementSpeedPassiveSkillEffect
import miragefairy2023.modules.passiveskill.NightPassiveSkillCondition
import miragefairy2023.modules.passiveskill.OnFirePassiveSkillCondition
import miragefairy2023.modules.passiveskill.OutdoorPassiveSkillCondition
import miragefairy2023.modules.passiveskill.OverworldPassiveSkillCondition
import miragefairy2023.modules.passiveskill.ShadePassiveSkillCondition
import miragefairy2023.modules.passiveskill.StatusEffectPassiveSkillEffect
import miragefairy2023.modules.passiveskill.SunshinePassiveSkillCondition
import miragefairy2023.modules.passiveskill.ToolMaterialPassiveSkillCondition
import miragefairy2023.modules.passiveskill.UnderwaterPassiveSkillCondition
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.data.server.RecipeProvider
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.item.ToolMaterials
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

enum class FairyCard(
    val motif: String,
    val rare: Int,
    val enName: String,
    val jaName: String,
    val skinColor: Int,
    val frontColor: Int,
    val backColor: Int,
    val hairColor: Int,
    val passiveSkillProviders: List<PassiveSkillProvider>,
    val recipeContainer: RecipeContainer,
) {
    AIR(
        "air", 0, "Airia", "空気精アイリャ", 0xFFBE80, 0xDEFFFF, 0xDEFFFF, 0xB0FFFF,
        listOf(PassiveSkillProvider(listOf(OverworldPassiveSkillCondition(), AirPassiveSkillCondition())) { MovementSpeedPassiveSkillEffect(0.30 * it) }),
        RecipeContainer().common().block { Blocks.AIR },
    ),
    LIGHT(
        "light", 3, "Lightia", "光精リグチャ", 0xFFFFD8, 0xFFFFD8, 0xFFFFC5, 0xFFFF00,
        listOf(PassiveSkillProvider(listOf(MinimumLightLevelPassiveSkillCondition(12))) { MovementSpeedPassiveSkillEffect(0.30 * it) }),
        RecipeContainer().common(),
    ),
    FIRE(
        "fire", 2, "Firia", "火精フィーリャ", 0xFF6C01, 0xF9DFA4, 0xFF7324, 0xFF4000,
        listOf(PassiveSkillProvider(listOf(OnFirePassiveSkillCondition())) { AttackDamagePassiveSkillEffect(4.0 * it) }),
        RecipeContainer().common().block { Blocks.FIRE },
    ),
    WATER(
        "water", 1, "Wateria", "水精ワテーリャ", 0x5469F2, 0x5985FF, 0x172AD3, 0x2D40F4,
        listOf(PassiveSkillProvider(listOf(UnderwaterPassiveSkillCondition())) { MaxHealthPassiveSkillEffect(12.0 * it) }),
        RecipeContainer().common().block { Blocks.WATER }.recipe { Items.WATER_BUCKET },
    ),
    LAVA(
        "lava", 4, "Lavia", "溶岩精ラーヴャ", 0xCD4208, 0xEDB54A, 0xCC4108, 0x4C1500,
        listOf(
            PassiveSkillProvider(listOf(OnFirePassiveSkillCondition())) { AttackDamagePassiveSkillEffect(2.0 * it) },
            PassiveSkillProvider(listOf(OnFirePassiveSkillCondition())) { StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 0) },
            PassiveSkillProvider(listOf(OnFirePassiveSkillCondition())) { StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0) },
        ),
        RecipeContainer().common().block { Blocks.LAVA }.recipe { Items.LAVA_BUCKET },
    ),
    MOON(
        "moon", 9, "Moonia", "月精モーニャ", 0xD9E4FF, 0x747D93, 0x0C121F, 0x2D4272,
        listOf(
            PassiveSkillProvider(listOf(MoonlightPassiveSkillCondition())) { StatusEffectPassiveSkillEffect(StatusEffects.NIGHT_VISION, 0, additionalSeconds = 10) },
            PassiveSkillProvider(listOf(MoonlightPassiveSkillCondition())) { StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0) },
        ),
        RecipeContainer().common(),
    ),
    SUN(
        "sun", 10, "Sunia", "太陽精スーニャ", 0xff2f00, 0xff972b, 0xff7500, 0xffe7b2,
        listOf(
            PassiveSkillProvider(listOf(SunshinePassiveSkillCondition())) { StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 1) },
            PassiveSkillProvider(listOf(SunshinePassiveSkillCondition())) { StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0) },
        ),
        RecipeContainer().common(),
    ),
    RAIN(
        "rain", 2, "Rainia", "雨精ライニャ", 0xB4FFFF, 0x4D5670, 0x4D5670, 0x2D40F4,
        listOf(PassiveSkillProvider(listOf(InRainPassiveSkillCondition())) { AttackDamagePassiveSkillEffect(4.0 * it) }),
        RecipeContainer().common(),
    ),
    DIRT(
        "dirt", 1, "Dirtia", "土精ディルチャ", 0xB87440, 0xB9855C, 0x593D29, 0x914A18,
        listOf(PassiveSkillProvider(listOf(OverworldPassiveSkillCondition())) { MaxHealthPassiveSkillEffect(10.0 * it) }),
        RecipeContainer().common().block { Blocks.DIRT }.recipe { Items.DIRT },
    ),
    SCULK(
        "sculk", 6, "Sculkia", "幽匿塊精スツルキャ", 0x19222C, 0x023F3D, 0x023F3D, 0x19C0C0,
        listOf(
            PassiveSkillProvider(listOf(MaximumLightLevelPassiveSkillCondition(0))) { MaxHealthPassiveSkillEffect(8.0 * it) },
            PassiveSkillProvider(listOf(MaximumLightLevelPassiveSkillCondition(0))) { AttackDamagePassiveSkillEffect(3.0 * it) },
        ),
        RecipeContainer().common().block { Blocks.SCULK }.recipe { Items.SCULK },
    ),
    STONE(
        "stone", 1, "Stonia", "石精ストーニャ", 0x333333, 0x8F8F8F, 0x686868, 0x747474,
        listOf(
            PassiveSkillProvider(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.STONE))) { AttackDamagePassiveSkillEffect(2.0 * it) },
            PassiveSkillProvider(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.STONE))) { StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0) },
            PassiveSkillProvider(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.STONE), FairyLevelPassiveSkillCondition(7))) { StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 1) },
        ),
        RecipeContainer().common().block { Blocks.STONE }.recipe { Items.STONE },
    ),
    IRON(
        "iron", 4, "Ironia", "鉄精イローニャ", 0xA0A0A0, 0xD8D8D8, 0x727272, 0xD8AF93,
        listOf(
            PassiveSkillProvider(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.IRON))) { AttackDamagePassiveSkillEffect(2.0 * it) },
            PassiveSkillProvider(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.IRON))) { StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 0) },
            PassiveSkillProvider(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.IRON), FairyLevelPassiveSkillCondition(8))) { StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 1) },
        ),
        RecipeContainer().common().block { Blocks.IRON_BLOCK }.recipe { Items.IRON_INGOT },
    ),
    GOLD(
        "gold", 6, "Goldia", "金精ゴルジャ", 0xD2CD9A, 0xFFFF0B, 0xDC7613, 0xDEDE00,
        listOf(
            PassiveSkillProvider(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.GOLD))) { AttackDamagePassiveSkillEffect(2.0 * it) },
            PassiveSkillProvider(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.GOLD))) { StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 0) },
            PassiveSkillProvider(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.GOLD), FairyLevelPassiveSkillCondition(10))) { StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 1) },
        ),
        RecipeContainer().common().block { Blocks.GOLD_BLOCK }.recipe { Items.GOLD_INGOT },
    ),
    NETHERITE(
        "netherite", 8, "Netheritia", "地獄合金精", 0x8F788F, 0x74585B, 0x705558, 0x77302D,
        listOf(
            PassiveSkillProvider(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.NETHERITE))) { AttackDamagePassiveSkillEffect(2.0 * it) },
            PassiveSkillProvider(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.NETHERITE))) { StatusEffectPassiveSkillEffect(StatusEffects.FIRE_RESISTANCE, 0) },
            PassiveSkillProvider(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.NETHERITE), FairyLevelPassiveSkillCondition(12))) { StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 1) },
        ),
        RecipeContainer().block { Blocks.NETHERITE_BLOCK }.recipe { Items.NETHERITE_INGOT },
    ),
    DIAMOND(
        "diamond", 7, "Diamondia", "金剛石精ディアモンジャ", 0x97FFE3, 0xD1FAF3, 0x70FFD9, 0x30DBBD,
        listOf(
            PassiveSkillProvider(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.DIAMOND))) { AttackDamagePassiveSkillEffect(2.0 * it) },
            PassiveSkillProvider(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.DIAMOND))) { StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 0) },
            PassiveSkillProvider(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.DIAMOND), FairyLevelPassiveSkillCondition(12))) { StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 1) },
        ),
        RecipeContainer().common().block { Blocks.DIAMOND_BLOCK }.recipe { Items.DIAMOND },
    ),
    FISH(
        "fish", 2, "Fishia", "魚精フィーシャ", 0x6B9F93, 0x5A867C, 0x43655D, 0xADBEDB,
        listOf(
            PassiveSkillProvider(listOf(UnderwaterPassiveSkillCondition())) { StatusEffectPassiveSkillEffect(StatusEffects.NIGHT_VISION, 0, additionalSeconds = 10) },
            PassiveSkillProvider(listOf(UnderwaterPassiveSkillCondition(), FairyLevelPassiveSkillCondition(10))) { StatusEffectPassiveSkillEffect(StatusEffects.WATER_BREATHING, 0) },
        ),
        RecipeContainer().common().recipe { Items.COD }, // TODO 魚精は希釈で得る
    ),
    CLOWNFISH(
        "clownfish", 7, "Clownfishia", "隈之実精ツロウンフィーシャ", 0xE46A22, 0xF46F20, 0xA94B1D, 0xFFDBC5,
        listOf(
            PassiveSkillProvider(listOf(UnderwaterPassiveSkillCondition(), MinimumLightLevelPassiveSkillCondition(4))) { StatusEffectPassiveSkillEffect(StatusEffects.WATER_BREATHING, 0) },
            PassiveSkillProvider(listOf(UnderwaterPassiveSkillCondition(), FairyLevelPassiveSkillCondition(10))) { StatusEffectPassiveSkillEffect(StatusEffects.WATER_BREATHING, 0) },
        ),
        RecipeContainer().common().recipe { Items.TROPICAL_FISH },
    ),
    PLAYER(
        "player", 5, "Playeria", "人精プライェーリャ", 0xB58D63, 0x00AAAA, 0x322976, 0x4B3422,
        listOf(PassiveSkillProvider(listOf(MaximumLevelPassiveSkillCondition(29))) { ExperiencePassiveSkillEffect(1.0 * it) }),
        RecipeContainer().common(),
    ),
    ENDERMAN(
        "enderman", 6, "Endermania", "終界人精エンデルマーニャ", 0x000000, 0x161616, 0x161616, 0xEF84FA,
        listOf(PassiveSkillProvider(listOf()) { CollectionPassiveSkillEffect(8.0 * it) }),
        RecipeContainer().common(),
    ),
    WARDEN(
        "warden", 7, "Wardenia", "監守者精ワルデーニャ", 0x0A3135, 0xCFCFA4, 0xA0AA7A, 0x2CD0CA,
        listOf(
            PassiveSkillProvider(listOf(MaximumLightLevelPassiveSkillCondition(0))) { StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 1) },
            PassiveSkillProvider(listOf(MaximumLightLevelPassiveSkillCondition(0))) { AttackDamagePassiveSkillEffect(2.0 * it) },
        ),
        RecipeContainer().common(),
    ),
    ZOMBIE(
        "zombie", 2, "Zombia", "硬屍精ゾンビャ", 0x2B4219, 0x00AAAA, 0x322976, 0x2B4219,
        listOf(PassiveSkillProvider(listOf(ShadePassiveSkillCondition())) { AttackDamagePassiveSkillEffect(2.0 * it) }),
        RecipeContainer().common(),
    ),
    WOOD(
        "wood", 1, "Woodia", "木精ウォージャ", 0xE7C697, 0xAD8232, 0xAD8232, 0x8B591C,
        listOf(
            PassiveSkillProvider(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.WOOD))) { AttackDamagePassiveSkillEffect(2.0 * it) },
            PassiveSkillProvider(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.WOOD))) { StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 0) },
            PassiveSkillProvider(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.WOOD), FairyLevelPassiveSkillCondition(7))) { StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 1) },
        ),
        RecipeContainer().common().block { Blocks.OAK_PLANKS }.recipe { Items.OAK_PLANKS },
    ),
    SPRUCE(
        "spruce", 6, "Sprucia", "松精スプルーツァ", 0x795C36, 0x583E1F, 0x23160A, 0x4C784C,
        listOf(
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.FOREST))) { AttackDamagePassiveSkillEffect(1.0 * it) },
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.TAIGA))) { AttackDamagePassiveSkillEffect(1.0 * it) },
        ),
        RecipeContainer().common().block { Blocks.SPRUCE_SAPLING }.recipe { Items.SPRUCE_SAPLING },
    ),
    HOE(
        "hoe", 3, "Hia", "鍬精ヒャ", 0xFFFFFF, 0xFFC48E, 0x47FF00, 0xFFFFFF,
        listOf(
            PassiveSkillProvider(listOf(HasHoePassiveSkillCondition())) { StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 0) },
            PassiveSkillProvider(listOf(HasHoePassiveSkillCondition())) { LuckPassiveSkillEffect(3.0 * it) },
        ),
        RecipeContainer().recipe { Items.STONE_HOE },
    ),
    CRAFTING_TABLE(
        "crafting_table", 3, "Craftinge Tablia", "作業台精ツラフティンゲターブリャ", 0xFFFFFF, 0xFFBB9A, 0xFFC980, 0x000000,
        listOf(PassiveSkillProvider(listOf(IndoorPassiveSkillCondition())) { LuckPassiveSkillEffect(2.0 * it) }),
        RecipeContainer().block { Blocks.CRAFTING_TABLE }.recipe { Items.CRAFTING_TABLE },
    ),
    ANVIL(
        "anvil", 4, "Anvilia", "金床精アンヴィーリャ", 0xFFFFFF, 0xA9A9A9, 0x909090, 0xA86F18,
        listOf(PassiveSkillProvider(listOf(IndoorPassiveSkillCondition())) { AttackDamagePassiveSkillEffect(2.0 * it) }),
        RecipeContainer().block { Blocks.ANVIL }.recipe { Items.ANVIL },
    ),
    ENCHANTING_TABLE(
        "enchanting_table", 6, "Enchantinge Tablia", "付魔台精エンキャンティンゲターブリャ", 0x472F65, 0xCE2828, 0xCE2828, 0x7BFFDD,
        listOf(
            PassiveSkillProvider(listOf(IndoorPassiveSkillCondition(), MaximumLevelPassiveSkillCondition(19))) { ExperiencePassiveSkillEffect(0.4 * it) },
            PassiveSkillProvider(listOf(IndoorPassiveSkillCondition(), MaximumLevelPassiveSkillCondition(29))) { ExperiencePassiveSkillEffect(0.6 * it) },
        ),
        RecipeContainer().block { Blocks.ENCHANTING_TABLE }.recipe { Items.ENCHANTING_TABLE },
    ),
    HOPPER(
        "hopper", 3, "Hopperia", "漏斗精ホッペーリャ", 0xFFFFFF, 0x797979, 0x646464, 0x5A5A5A,
        listOf(PassiveSkillProvider(listOf(IndoorPassiveSkillCondition())) { CollectionPassiveSkillEffect(10.0 * it) }),
        RecipeContainer().block { Blocks.HOPPER }.recipe { Items.HOPPER },
    ),
    BEACON(
        "beacon", 11, "Beaconia", "信標精ベアツォーニャ", 0x97FFE3, 0x6029B3, 0x2E095E, 0xD4EAE6,
        listOf(
            PassiveSkillProvider(listOf(OutdoorPassiveSkillCondition())) { StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 0) },
            PassiveSkillProvider(listOf(OutdoorPassiveSkillCondition())) { StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 0) },
            PassiveSkillProvider(listOf(OutdoorPassiveSkillCondition())) { StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0) },
            PassiveSkillProvider(listOf(OutdoorPassiveSkillCondition())) { StatusEffectPassiveSkillEffect(StatusEffects.JUMP_BOOST, 1) },
            PassiveSkillProvider(listOf(OutdoorPassiveSkillCondition())) { StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 0) },
            PassiveSkillProvider(listOf(OutdoorPassiveSkillCondition())) { StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0) },
        ),
        RecipeContainer().block { Blocks.BEACON }.recipe { Items.BEACON },
    ),
    GLASS(
        "glass", 4, "Glassia", "硝子精グラッシャ", 0xFFFFFF, 0xEFF5FF, 0xE8EDF5, 0xADE0E9,
        listOf(
            PassiveSkillProvider(listOf(MaximumHealthPassiveSkillCondition(1))) { StatusEffectPassiveSkillEffect(StatusEffects.INVISIBILITY, 0) },
            PassiveSkillProvider(listOf(FairyLevelPassiveSkillCondition(11))) { StatusEffectPassiveSkillEffect(StatusEffects.INVISIBILITY, 0) },
            PassiveSkillProvider(listOf(MaximumHealthPassiveSkillCondition(1))) { StatusEffectPassiveSkillEffect(StatusEffects.GLOWING, 0) },
            PassiveSkillProvider(listOf(FairyLevelPassiveSkillCondition(11))) { StatusEffectPassiveSkillEffect(StatusEffects.GLOWING, 0) },
        ),
        RecipeContainer().block { Blocks.GLASS }.recipe { Items.GLASS },
    ),
    PRISMARINE(
        "prismarine", 5, "Prismarinia", "海晶石精プリスマリーニャ", 0xA3D3C7, 0x769A91, 0x769A91, 0x69C4C0,
        listOf(PassiveSkillProvider(listOf(UnderwaterPassiveSkillCondition())) { StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 1) }),
        RecipeContainer().block { Blocks.PRISMARINE }.recipe { Items.PRISMARINE },
    ),
    IRON_BARS(
        "iron_bars", 4, "Irone Barsia", "鉄格子精イローネバルシャ", 0xFFFFFF, 0xA1A1A3, 0x404040, 0x404040,
        listOf(PassiveSkillProvider(listOf()) { MaxHealthPassiveSkillEffect(5.0 * it) }),
        RecipeContainer().block { Blocks.IRON_BARS }.recipe { Items.IRON_BARS },
    ),
    PLAINS(
        "plains", 2, "Plainsia", "平原精プラインシャ", 0xB0DF83, 0xD4FF82, 0x86C91C, 0x489F25,
        listOf(
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.PLAINS))) { StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 0) },
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.PLAINS), FairyLevelPassiveSkillCondition(7))) { StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 1) },
        ),
        RecipeContainer().common(),
    ),
    OCEAN(
        "ocean", 3, "Oceania", "海精オツェアーニャ", 0x7DAEF5, 0x1B6CE9, 0x191CF0, 0x004DA5,
        listOf(
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.OCEAN))) { StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 0) },
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.OCEAN), FairyLevelPassiveSkillCondition(7))) { StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 1) },
        ),
        RecipeContainer().common(),
    ),
    TAIGA(
        "taiga", 4, "Taigia", "針葉樹林精タイギャ", 0x5D985E, 0x476545, 0x223325, 0x5A3711,
        listOf(
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.TAIGA))) { StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 0) },
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.TAIGA), FairyLevelPassiveSkillCondition(7))) { StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 1) },
        ),
        RecipeContainer().common(),
    ),
    MOUNTAIN(
        "mountain", 3, "Mountainia", "山精モウンタイニャ", 0x84BF80, 0xB1B0B1, 0x717173, 0xF0F0F0,
        listOf(
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.MOUNTAIN))) { StatusEffectPassiveSkillEffect(StatusEffects.JUMP_BOOST, 0) },
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.MOUNTAIN), FairyLevelPassiveSkillCondition(7))) { StatusEffectPassiveSkillEffect(StatusEffects.JUMP_BOOST, 1) },
        ),
        RecipeContainer().common(),
    ),
    FOREST(
        "forest", 3, "Forestia", "森精フォレスチャ", 0x8EBF7A, 0x7B9C62, 0x89591D, 0x2E6E14,
        listOf(
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.FOREST))) { StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0) },
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.FOREST), FairyLevelPassiveSkillCondition(7))) { StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 1) },
        ),
        RecipeContainer().common(),
    ),
    DESERT(
        "desert", 2, "Desertia", "砂漠精デセルチャ", 0xF9F0C8, 0xDDD6A5, 0xD6CE9D, 0x656054,
        listOf(
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.DESERT), SunshinePassiveSkillCondition())) { StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0) },
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.DESERT), SunshinePassiveSkillCondition(), FairyLevelPassiveSkillCondition(7))) { StatusEffectPassiveSkillEffect(StatusEffects.FIRE_RESISTANCE, 0) },
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.DESERT), MoonlightPassiveSkillCondition())) { StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 0) },
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.DESERT), MoonlightPassiveSkillCondition(), FairyLevelPassiveSkillCondition(7))) { StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 1) },
        ),
        RecipeContainer().common(),
    ),
    AVALON(
        "avalon", 8, "Avalonia", "阿瓦隆精アヴァローニャ", 0xFFE4CA, 0xE1FFCE, 0xD0FFE6, 0xFFCAFF,
        listOf(
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.MUSHROOM))) { StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 1) },
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.FLORAL))) { StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 1) },
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.MUSHROOM))) { StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 1) },
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.FLORAL))) { StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 1) },
        ),
        RecipeContainer().common(), // TODO イベント終了時コモン枠除去
    ),
    VOID(
        "void", 11, "Voidia", "奈落精ヴォイジャ", 0x000000, 0x000000, 0x000000, 0xB1B1B1,
        listOf(
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_THE_END))) { StatusEffectPassiveSkillEffect(StatusEffects.SLOW_FALLING, 0) },
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_THE_END))) { StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0) },
            PassiveSkillProvider(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_THE_END))) { StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 2) },
        ),
        RecipeContainer().common(), // TODO エンドのコモン
    ),
    NIGHT(
        "night", 7, "Nightia", "夜精ニグチャ", 0xFFE260, 0x2C2C2E, 0x0E0E10, 0x2D4272,
        listOf(
            PassiveSkillProvider(listOf(NightPassiveSkillCondition())) { StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 0) },
        ),
        RecipeContainer().common(),
    ),
    TIME(
        "time", 12, "Timia", "時精ティーミャ", 0x89D585, 0xD5DEBC, 0xD8DEA7, 0x8DD586,
        listOf(
            PassiveSkillProvider(listOf()) { StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 1) },
            PassiveSkillProvider(listOf()) { MovementSpeedPassiveSkillEffect(0.20 * it) },
            PassiveSkillProvider(listOf()) { StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 1) },
        ),
        RecipeContainer().common(),
    ),
    GRAVITY(
        "gravity", 12, "Gravitia", "重力精グラヴィーチャ", 0xC2A7F2, 0x3600FF, 0x2A00B1, 0x110047,
        listOf(
            PassiveSkillProvider(listOf()) { StatusEffectPassiveSkillEffect(StatusEffects.SLOW_FALLING, 0) },
            PassiveSkillProvider(listOf()) { AttackDamagePassiveSkillEffect(2.0 * it) },
        ),
        RecipeContainer().common(),
    ),
    ;

    val identifier = Identifier(MirageFairy2023.modId, motif)
    val fairy: Fairy = object : Fairy {
        override fun getIdentifier() = identifier
        override fun getItem() = this@FairyCard()
        override fun getRare() = rare
    }


    // 妖精レシピ

    class RecipeContainer {
        val recipes = mutableListOf<Recipe>()
    }

    interface Recipe {
        fun getWikiString(): String
        fun init(initializationScope: InitializationScope, fairyCard: FairyCard)
    }


    /**
     * multiplier: 妖精の★の数に比例し、★10のときに1.0です。
     * ただし、★0の場合は例外的に0.05です。
     * この変数には様々な補正が乗る可能性があります。
     */
    class PassiveSkillProvider(val conditions: List<PassiveSkillCondition>, val effectProvider: (multiplier: Double) -> PassiveSkillEffect)

}

private fun FairyCard.RecipeContainer.common() = this.also {
    this.recipes += object : FairyCard.Recipe {
        override fun getWikiString() = "コモン"
        override fun init(initializationScope: InitializationScope, fairyCard: FairyCard) {
            initializationScope.onRegisterRecipes {
                MirageFlourItem.COMMON_FAIRY_LIST += fairyCard.fairy
            }
        }
    }
}

private fun FairyCard.RecipeContainer.block(blockSupplier: () -> Block) = this.also {
    this.recipes += object : FairyCard.Recipe {
        override fun getWikiString() = "ブロック：${blockSupplier().name.string}"
        override fun init(initializationScope: InitializationScope, fairyCard: FairyCard) {
            initializationScope.onRegisterRecipes {
                DreamCatcherItem.BLOCK_FAIRY_RELATION_LIST += BlockFairyRelation(blockSupplier(), fairyCard.fairy)
            }
        }
    }
}

private fun FairyCard.RecipeContainer.recipe(inputItemSupplier: () -> Item) = this.also {
    this.recipes += object : FairyCard.Recipe {
        override fun getWikiString() = "クラフト：${inputItemSupplier().name.string}"
        override fun init(initializationScope: InitializationScope, fairyCard: FairyCard) {
            initializationScope.onGenerateRecipes {
                val inputItem = inputItemSupplier()
                val mirageFlourItem = when (fairyCard.rare) {
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
                    .input(mirageFlourItem)
                    .input(inputItem)
                    .criterion("has_xarpite", RecipeProvider.conditionsFromItem(DemonItemCard.XARPITE()))
                    .criterion("has_${Registry.ITEM.getId(mirageFlourItem).path}", RecipeProvider.conditionsFromItem(mirageFlourItem))
                    .criterion("has_${Registry.ITEM.getId(inputItem).path}", RecipeProvider.conditionsFromItem(inputItem))
                    .offerTo(it, Identifier.of(initializationScope.modId, "fairy/${fairyCard.motif}"))
            }
        }
    }
}

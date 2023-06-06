package miragefairy2023.modules.fairy

import miragefairy2023.MirageFairy2023
import miragefairy2023.api.Fairy
import miragefairy2023.api.PassiveSkill
import miragefairy2023.api.PassiveSkillCondition
import miragefairy2023.api.PassiveSkillEffect
import miragefairy2023.modules.DemonItemCard
import miragefairy2023.modules.ToolMaterialCard
import miragefairy2023.modules.invoke
import miragefairy2023.modules.passiveskill.AttackDamagePassiveSkillEffect
import miragefairy2023.modules.passiveskill.BiomePassiveSkillCondition
import miragefairy2023.modules.passiveskill.CollectionPassiveSkillEffect
import miragefairy2023.modules.passiveskill.CombustionPassiveSkillEffect
import miragefairy2023.modules.passiveskill.ExperiencePassiveSkillEffect
import miragefairy2023.modules.passiveskill.FoodPassiveSkillCondition
import miragefairy2023.modules.passiveskill.HasHoePassiveSkillCondition
import miragefairy2023.modules.passiveskill.InRainPassiveSkillCondition
import miragefairy2023.modules.passiveskill.InVillagePassiveSkillCondition
import miragefairy2023.modules.passiveskill.IndoorPassiveSkillCondition
import miragefairy2023.modules.passiveskill.LuckPassiveSkillEffect
import miragefairy2023.modules.passiveskill.ManaPassiveSkillEffect
import miragefairy2023.modules.passiveskill.MaxHealthPassiveSkillEffect
import miragefairy2023.modules.passiveskill.MaximumFoodLevelPassiveSkillCondition
import miragefairy2023.modules.passiveskill.MaximumHealthPassiveSkillCondition
import miragefairy2023.modules.passiveskill.MaximumLevelPassiveSkillCondition
import miragefairy2023.modules.passiveskill.MaximumLightLevelPassiveSkillCondition
import miragefairy2023.modules.passiveskill.MinimumFoodLevelPassiveSkillCondition
import miragefairy2023.modules.passiveskill.MinimumLightLevelPassiveSkillCondition
import miragefairy2023.modules.passiveskill.MinimumManaPassiveSkillCondition
import miragefairy2023.modules.passiveskill.MoonlightPassiveSkillCondition
import miragefairy2023.modules.passiveskill.MovementSpeedPassiveSkillEffect
import miragefairy2023.modules.passiveskill.NightPassiveSkillCondition
import miragefairy2023.modules.passiveskill.OnFirePassiveSkillCondition
import miragefairy2023.modules.passiveskill.OutdoorPassiveSkillCondition
import miragefairy2023.modules.passiveskill.OverworldPassiveSkillCondition
import miragefairy2023.modules.passiveskill.PassiveSkillConditions
import miragefairy2023.modules.passiveskill.PassiveSkills
import miragefairy2023.modules.passiveskill.PassiveSkillsBuilder
import miragefairy2023.modules.passiveskill.RegenerationPassiveSkillEffect
import miragefairy2023.modules.passiveskill.ShadePassiveSkillCondition
import miragefairy2023.modules.passiveskill.StatusEffectPassiveSkillCondition
import miragefairy2023.modules.passiveskill.StatusEffectPassiveSkillEffect
import miragefairy2023.modules.passiveskill.SunshinePassiveSkillCondition
import miragefairy2023.modules.passiveskill.TelescopeMissionPassiveSkillCondition
import miragefairy2023.modules.passiveskill.ThunderingPassiveSkillCondition
import miragefairy2023.modules.passiveskill.ToolMaterialPassiveSkillCondition
import miragefairy2023.modules.passiveskill.UnderwaterPassiveSkillCondition
import miragefairy2023.modules.passiveskill.air
import miragefairy2023.modules.passiveskill.movementSpeed
import miragefairy2023.modules.passiveskill.overworld
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags
import net.minecraft.block.Blocks
import net.minecraft.entity.EntityType
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Items
import net.minecraft.util.Identifier
import net.minecraft.world.biome.BiomeKeys


private fun listOf(block: PassiveSkillsBuilder.() -> Unit) = PassiveSkills(block)

private fun PassiveSkillsBuilder.on(effect: PassiveSkillEffect, conditions: List<PassiveSkillCondition>) {
    effect on PassiveSkillConditions(conditions)
}

enum class FairyCard(
    val motifPath: String,
    val rare: Int,
    val enName: String,
    val jaName: String,
    val skinColor: Int,
    val frontColor: Int,
    val backColor: Int,
    val hairColor: Int,
    val passiveSkills: List<PassiveSkill>,
    val fairyRecipes: FairyRecipes,
) {
    AIR(
        "air", 0, "Airia", "空気精アイリャ", 0xFFBE80, 0xDEFFFF, 0xDEFFFF, 0xB0FFFF,
        PassiveSkills {
            movementSpeed(0.30) on overworld() * air()
        },
        FairyRecipes().always().block { Blocks.AIR },
    ),
    LIGHT(
        "light", 3, "Lightia", "光精リグチャ", 0xFFFFD8, 0xFFFFD8, 0xFFFFC5, 0xFFFF00,
        listOf {
            on(MovementSpeedPassiveSkillEffect(0.30), listOf(MinimumLightLevelPassiveSkillCondition(12)))
        },
        FairyRecipes().always(),
    ),
    FIRE(
        "fire", 2, "Firia", "火精フィーリャ", 0xFF6C01, 0xF9DFA4, 0xFF7324, 0xFF4000,
        listOf {
            on(AttackDamagePassiveSkillEffect(4.0), listOf(OnFirePassiveSkillCondition()))
        },
        FairyRecipes().biome(ConventionalBiomeTags.IN_NETHER).block { Blocks.FIRE },
    ),
    WATER(
        "water", 1, "Wateria", "水精ワテーリャ", 0x5469F2, 0x5985FF, 0x172AD3, 0x2D40F4,
        listOf {
            on(MaxHealthPassiveSkillEffect(12.0), listOf(UnderwaterPassiveSkillCondition()))
        },
        FairyRecipes().overworld().block { Blocks.WATER }.recipe { Items.WATER_BUCKET },
    ),
    LAVA(
        "lava", 4, "Lavia", "溶岩精ラーヴャ", 0xCD4208, 0xEDB54A, 0xCC4108, 0x4C1500,
        listOf {
            on(AttackDamagePassiveSkillEffect(2.0), listOf(OnFirePassiveSkillCondition()))
            on(StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 0), listOf(OnFirePassiveSkillCondition()))
            on(StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0), listOf(OnFirePassiveSkillCondition()))
        },
        FairyRecipes().biome(ConventionalBiomeTags.IN_NETHER).block { Blocks.LAVA }.recipe { Items.LAVA_BUCKET },
    ),
    MOON(
        "moon", 9, "Moonia", "月精モーニャ", 0xD9E4FF, 0x747D93, 0x0C121F, 0x2D4272,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.NIGHT_VISION, 0, additionalSeconds = 10), listOf(MoonlightPassiveSkillCondition()))
            on(StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0), listOf(MoonlightPassiveSkillCondition()))
        },
        FairyRecipes().overworld(),
    ),
    SUN(
        "sun", 10, "Sunia", "太陽精スーニャ", 0xff2f00, 0xff972b, 0xff7500, 0xffe7b2,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 1), listOf(SunshinePassiveSkillCondition()))
            on(StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0), listOf(SunshinePassiveSkillCondition()))
        },
        FairyRecipes().overworld(),
    ),
    RAIN(
        "rain", 2, "Rainia", "雨精ライニャ", 0xB4FFFF, 0x4D5670, 0x4D5670, 0x2D40F4,
        listOf {
            on(AttackDamagePassiveSkillEffect(4.0), listOf(InRainPassiveSkillCondition()))
        },
        FairyRecipes().overworld(),
    ),
    DIRT(
        "dirt", 1, "Dirtia", "土精ディルチャ", 0xB87440, 0xB9855C, 0x593D29, 0x914A18,
        listOf {
            on(MaxHealthPassiveSkillEffect(10.0), listOf(OverworldPassiveSkillCondition()))
        },
        FairyRecipes().overworld().block { Blocks.DIRT }.recipe { Items.DIRT },
    ),
    MYCELIUM(
        "mycelium", 6, "Myceliumia", "菌糸精ミツェリウミャ", 0x8F7E86, 0x8B7071, 0x8B7071, 0x8B6264,
        listOf {
            on(AttackDamagePassiveSkillEffect(1.0), listOf())
            on(AttackDamagePassiveSkillEffect(2.0), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.MUSHROOM)))
            on(MaxHealthPassiveSkillEffect(12.0), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.MUSHROOM)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.MUSHROOM), MinimumManaPassiveSkillCondition(10.0)))
        },
        FairyRecipes().biome(ConventionalBiomeTags.MUSHROOM).block { Blocks.MYCELIUM }.recipe { Items.MYCELIUM },
    ),
    SCULK(
        "sculk", 6, "Sculkia", "幽匿塊精スツルキャ", 0x19222C, 0x023F3D, 0x023F3D, 0x19C0C0,
        listOf {
            on(MaxHealthPassiveSkillEffect(8.0), listOf(MaximumLightLevelPassiveSkillCondition(0)))
            on(AttackDamagePassiveSkillEffect(3.0), listOf(MaximumLightLevelPassiveSkillCondition(0)))
        },
        FairyRecipes().biome(BiomeKeys.DEEP_DARK).block { Blocks.SCULK }.recipe { Items.SCULK },
    ),
    STONE(
        "stone", 1, "Stonia", "石精ストーニャ", 0x333333, 0x8F8F8F, 0x686868, 0x747474,
        listOf {
            on(AttackDamagePassiveSkillEffect(2.0), listOf(ToolMaterialPassiveSkillCondition(ToolMaterialCard.STONE)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0), listOf(ToolMaterialPassiveSkillCondition(ToolMaterialCard.STONE)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 1), listOf(ToolMaterialPassiveSkillCondition(ToolMaterialCard.STONE), MinimumManaPassiveSkillCondition(7.0)))
        },
        FairyRecipes().overworld().block { Blocks.STONE }.recipe { Items.STONE },
    ),
    DRIPSTONE(
        "dripstone", 3, "Dripstonia", "鍾乳石精ドリプストーニャ", 0xB19C7E, 0xA97F6F, 0xA97F6F, 0xAD7069,
        listOf {
            on(AttackDamagePassiveSkillEffect(1.5), listOf(ShadePassiveSkillCondition()))
            on(MaxHealthPassiveSkillEffect(6.0), listOf(ShadePassiveSkillCondition()))
        },
        FairyRecipes().biome(BiomeKeys.DRIPSTONE_CAVES).block { Blocks.DRIPSTONE_BLOCK }.recipe { Items.DRIPSTONE_BLOCK },
    ),
    REINFORCED_DEEPSLATE(
        "reinforced_deepslate", 9, "Reinforcede Deepslatia", "強化深層岩精レインフォルツェーデデープスラーチャ", 0x6C7180, 0x5C606C, 0x5C606C, 0xACCF9D,
        listOf {
            on(MaxHealthPassiveSkillEffect(10.0), listOf(ShadePassiveSkillCondition()))
        },
        FairyRecipes().block { Blocks.REINFORCED_DEEPSLATE }.recipe { Items.REINFORCED_DEEPSLATE },
    ),
    ANCIENT_DEBRIS(
        "ancient_debris", 8, "Anciente Debrisia", "古代残骸精アンツィエンテデブリーシャ", 0x8F645A, 0x8F645A, 0x885040, 0xD8C2B7,
        listOf {
            on(AttackDamagePassiveSkillEffect(1.0), listOf())
            on(AttackDamagePassiveSkillEffect(1.0), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_NETHER)))
            on(MaxHealthPassiveSkillEffect(2.0), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_NETHER)))
            on(LuckPassiveSkillEffect(2.0), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_NETHER)))
        },
        FairyRecipes().biome(ConventionalBiomeTags.IN_NETHER).block { Blocks.ANCIENT_DEBRIS }.recipe { Items.ANCIENT_DEBRIS },
    ),
    PURPUR(
        "purpur", 7, "Purpuria", "紫珀精プルプーリャ", 0xCBA8CB, 0xC08AC0, 0xC08AC0, 0xBC68BB,
        listOf {
            on(CollectionPassiveSkillEffect(0.5), listOf())
            on(AttackDamagePassiveSkillEffect(2.0), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_THE_END)))
            on(MaxHealthPassiveSkillEffect(4.0), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_THE_END)))
        },
        FairyRecipes().biome(ConventionalBiomeTags.END_ISLANDS).block { Blocks.PURPUR_BLOCK }.recipe { Items.PURPUR_BLOCK },
    ),
    COPPER(
        "copper", 3, "Copperia", "銅精ツォッペーリャ", 0xF69D7F, 0xF77653, 0xF77653, 0x5DC09A,
        listOf {
            on(LuckPassiveSkillEffect(0.2), listOf())
            on(AttackDamagePassiveSkillEffect(2.5), listOf(ThunderingPassiveSkillCondition()))
        },
        FairyRecipes().overworld().block { Blocks.COPPER_BLOCK }.recipe { Items.COPPER_INGOT },
    ),
    IRON(
        "iron", 4, "Ironia", "鉄精イローニャ", 0xA0A0A0, 0xD8D8D8, 0x727272, 0xD8AF93,
        listOf {
            on(AttackDamagePassiveSkillEffect(2.0), listOf(ToolMaterialPassiveSkillCondition(ToolMaterialCard.IRON)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 0), listOf(ToolMaterialPassiveSkillCondition(ToolMaterialCard.IRON)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 1), listOf(ToolMaterialPassiveSkillCondition(ToolMaterialCard.IRON), MinimumManaPassiveSkillCondition(8.0)))
        },
        FairyRecipes().overworld().block { Blocks.IRON_BLOCK }.recipe { Items.IRON_INGOT },
    ),
    GOLD(
        "gold", 6, "Goldia", "金精ゴルジャ", 0xEFE642, 0xF4CC17, 0xF4CC17, 0xFDB61E,
        listOf {
            on(LuckPassiveSkillEffect(0.8), listOf())
            on(AttackDamagePassiveSkillEffect(2.0), listOf(ToolMaterialPassiveSkillCondition(ToolMaterialCard.GOLD)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 0), listOf(ToolMaterialPassiveSkillCondition(ToolMaterialCard.GOLD)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 1), listOf(ToolMaterialPassiveSkillCondition(ToolMaterialCard.GOLD), MinimumManaPassiveSkillCondition(10.0)))
        },
        FairyRecipes().overworld().biome(ConventionalBiomeTags.IN_NETHER).block { Blocks.GOLD_BLOCK }.recipe { Items.GOLD_INGOT },
    ),
    NETHERITE(
        "netherite", 8, "Netheritia", "地獄合金精ネテリーチャ", 0x8F788F, 0x74585B, 0x705558, 0x77302D,
        listOf {
            on(AttackDamagePassiveSkillEffect(2.0), listOf(ToolMaterialPassiveSkillCondition(ToolMaterialCard.NETHERITE)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.FIRE_RESISTANCE, 0), listOf(ToolMaterialPassiveSkillCondition(ToolMaterialCard.NETHERITE)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 1), listOf(ToolMaterialPassiveSkillCondition(ToolMaterialCard.NETHERITE), MinimumManaPassiveSkillCondition(12.0)))
        },
        FairyRecipes().biome(ConventionalBiomeTags.IN_NETHER).block { Blocks.NETHERITE_BLOCK }.recipe { Items.NETHERITE_INGOT },
    ),
    MIRANAGITE(
        "miranagite", 5, "Miranagitia", "蒼天石精ミラナギーチャ", 0x4EC5F4, 0x4394D3, 0x004477, 0x0C4CEF,
        listOf {
            on(LuckPassiveSkillEffect(0.5), listOf())
            on(StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 0), listOf(ToolMaterialPassiveSkillCondition(ToolMaterialCard.MIRANAGITE)))
            on(ManaPassiveSkillEffect(0.5), listOf())
        },
        FairyRecipes().overworld().recipe { DemonItemCard.MIRANAGITE() },
    ),
    AMETHYST(
        "amethyst", 5, "Amethystia", "紫水晶精アメティスチャ", 0xCAA9FF, 0xA974FF, 0x9D60FF, 0xBC92FF,
        listOf {
            on(LuckPassiveSkillEffect(0.5), listOf())
            on(AttackDamagePassiveSkillEffect(0.5), listOf(MaximumLightLevelPassiveSkillCondition(7)))
            on(LuckPassiveSkillEffect(2.0), listOf(MaximumLightLevelPassiveSkillCondition(7)))
        },
        FairyRecipes().overworld().block { Blocks.AMETHYST_BLOCK }.recipe { Items.AMETHYST_SHARD },
    ),
    EMERALD(
        "emerald", 6, "Emeraldia", "翠玉精エメラルジャ", 0x9FF9B5, 0x81F99E, 0x17DD62, 0x008A25,
        listOf {
            on(LuckPassiveSkillEffect(0.5), listOf())
            on(LuckPassiveSkillEffect(1.5), listOf(InVillagePassiveSkillCondition()))
        },
        FairyRecipes().overworld().block { Blocks.EMERALD_BLOCK }.recipe { Items.EMERALD },
    ),
    DIAMOND(
        "diamond", 7, "Diamondia", "金剛石精ディアモンジャ", 0x97FFE3, 0xD1FAF3, 0x70FFD9, 0x30DBBD,
        listOf {
            on(LuckPassiveSkillEffect(0.5), listOf())
            on(AttackDamagePassiveSkillEffect(2.0), listOf(ToolMaterialPassiveSkillCondition(ToolMaterialCard.DIAMOND)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 0), listOf(ToolMaterialPassiveSkillCondition(ToolMaterialCard.DIAMOND)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 1), listOf(ToolMaterialPassiveSkillCondition(ToolMaterialCard.DIAMOND), MinimumManaPassiveSkillCondition(12.0)))
        },
        FairyRecipes().overworld().block { Blocks.DIAMOND_BLOCK }.recipe { Items.DIAMOND },
    ),
    ADAMANTITE(
        "adamantite", 9, "Adamantitia", "精金精アダマンティーチャ", 0xE5B3CA, 0xDB7A9C, 0xDB7A9C, 0xFD2888,
        listOf {
            on(LuckPassiveSkillEffect(0.5), listOf())
            on(AttackDamagePassiveSkillEffect(2.0), listOf())
        },
        FairyRecipes().always(), // TODO イベ限解除
    ),
    COAL(
        "coal", 2, "Coalia", "石炭精ツォアーリャ", 0x4C2510, 0x52504C, 0x39352E, 0x150B00,
        listOf {
            on(MaxHealthPassiveSkillEffect(4.0), listOf())
            on(LuckPassiveSkillEffect(2.0), listOf(OnFirePassiveSkillCondition()))
        },
        FairyRecipes().overworld().biome(ConventionalBiomeTags.IN_NETHER).block { Blocks.COAL_BLOCK }.recipe { Items.COAL },
    ),
    REDSTONE(
        "redstone", 4, "Redstonia", "赤石精レドストーニャ", 0xFF5959, 0xFF0000, 0xCD0000, 0xBA0000,
        listOf {
            on(LuckPassiveSkillEffect(0.5), listOf(IndoorPassiveSkillCondition()))
            on(AttackDamagePassiveSkillEffect(1.0), listOf(IndoorPassiveSkillCondition()))
            on(MaxHealthPassiveSkillEffect(2.0), listOf(IndoorPassiveSkillCondition()))
        },
        FairyRecipes().overworld().block { Blocks.REDSTONE_BLOCK }.recipe { Items.REDSTONE },
    ),
    LAPIS_LAZULI(
        "lapis_lazuli", 5, "Lapise Lazulia", "瑠璃石精ラピセラズーリャ", 0x77A2FF, 0x3064D3, 0x3064D3, 0x3976F9,
        listOf {
            on(LuckPassiveSkillEffect(0.5), listOf())
            on(LuckPassiveSkillEffect(1.5), listOf(OverworldPassiveSkillCondition()))
        },
        FairyRecipes().overworld().block { Blocks.LAPIS_BLOCK }.recipe { Items.LAPIS_LAZULI },
    ),
    GLOWSTONE(
        "glowstone", 6, "Glowstonia", "蛍光石精グロウストーニャ", 0xEFC298, 0xEAA463, 0xEAA463, 0xEADD52,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.GLOWING, 0), listOf())
            on(AttackDamagePassiveSkillEffect(1.5), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_NETHER)))
        },
        FairyRecipes().biome(ConventionalBiomeTags.IN_NETHER).block { Blocks.GLOWSTONE }.recipe { Items.GLOWSTONE_DUST },
    ),
    OBSIDIAN(
        "obsidian", 5, "Obsidiania", "黒耀石精オブシディアーニャ", 0x775599, 0x6029B3, 0x2E095E, 0x0F0033,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0), listOf())
            on(StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 1), listOf(MinimumManaPassiveSkillCondition(9.0)))
        },
        FairyRecipes().overworld().biome(ConventionalBiomeTags.IN_THE_END).block { Blocks.OBSIDIAN }.recipe { Items.OBSIDIAN },
    ),
    FISH(
        "fish", 2, "Fishia", "魚精フィーシャ", 0x6B9F93, 0x5A867C, 0x43655D, 0xADBEDB,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.NIGHT_VISION, 0, additionalSeconds = 10), listOf(UnderwaterPassiveSkillCondition()))
            on(StatusEffectPassiveSkillEffect(StatusEffects.WATER_BREATHING, 0), listOf(UnderwaterPassiveSkillCondition(), MinimumManaPassiveSkillCondition(10.0)))
        },
        FairyRecipes().overworld().recipe { Items.COD }.entityType { EntityType.COD }, // TODO 魚精は希釈で得る
    ),
    CLOWNFISH(
        "clownfish", 7, "Clownfishia", "隈之実精ツロウンフィーシャ", 0xE46A22, 0xF46F20, 0xA94B1D, 0xFFDBC5,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.WATER_BREATHING, 0), listOf(UnderwaterPassiveSkillCondition(), MinimumLightLevelPassiveSkillCondition(4)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.WATER_BREATHING, 0), listOf(UnderwaterPassiveSkillCondition(), MinimumManaPassiveSkillCondition(10.0)))
        },
        FairyRecipes().overworld().recipe { Items.TROPICAL_FISH }.entityType { EntityType.TROPICAL_FISH },
    ),
    SPONGE(
        "sponge", 4, "Spongia", "海綿精スポンギャ", 0xEADF67, 0xB1A947, 0xB1A947, 0xDBCD5A,
        listOf {
            on(AttackDamagePassiveSkillEffect(2.0), listOf(UnderwaterPassiveSkillCondition()))
            on(StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0), listOf(UnderwaterPassiveSkillCondition(), MinimumManaPassiveSkillCondition(10.0)))
        },
        FairyRecipes().biome(ConventionalBiomeTags.OCEAN).block { Blocks.SPONGE }.recipe { Items.SPONGE },
    ),
    PLAYER(
        "player", 5, "Playeria", "人精プライェーリャ", 0xB58D63, 0x00AAAA, 0x322976, 0x4B3422,
        listOf {
            on(ExperiencePassiveSkillEffect(0.05), listOf(MaximumLevelPassiveSkillCondition(29)))
        },
        FairyRecipes().always().entityType { EntityType.PLAYER },
    ),
    ENDERMAN(
        "enderman", 6, "Endermania", "終界人精エンデルマーニャ", 0x000000, 0x161616, 0x161616, 0xEF84FA,
        listOf {
            on(CollectionPassiveSkillEffect(1.0), listOf())
        },
        FairyRecipes().overworld().biome(ConventionalBiomeTags.IN_NETHER).biome(ConventionalBiomeTags.IN_THE_END).entityType { EntityType.ENDERMAN },
    ),
    WARDEN(
        "warden", 7, "Wardenia", "監守者精ワルデーニャ", 0x0A3135, 0xCFCFA4, 0xA0AA7A, 0x2CD0CA,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 1), listOf(MaximumLightLevelPassiveSkillCondition(0)))
            on(AttackDamagePassiveSkillEffect(2.0), listOf(MaximumLightLevelPassiveSkillCondition(0)))
        },
        FairyRecipes().biome(BiomeKeys.DEEP_DARK).entityType { EntityType.WARDEN },
    ),
    ZOMBIE(
        "zombie", 2, "Zombia", "硬屍精ゾンビャ", 0x2B4219, 0x00AAAA, 0x322976, 0x2B4219,
        listOf {
            on(AttackDamagePassiveSkillEffect(2.0), listOf(MaximumFoodLevelPassiveSkillCondition(6)))
            on(AttackDamagePassiveSkillEffect(1.0), listOf(ShadePassiveSkillCondition()))
        },
        FairyRecipes().overworld().entityType { EntityType.ZOMBIE },
    ),
    SKELETON_HORSE(
        "skeleton_horse", 6, "Skeletone Horsia", "骸骨馬精スケレトーネホルシャ", 0xA1A1A1, 0xD4D4D4, 0x757575, 0xD5D5D5,
        listOf {
            on(MovementSpeedPassiveSkillEffect(0.30), listOf(MaximumFoodLevelPassiveSkillCondition(6)))
            on(AttackDamagePassiveSkillEffect(1.0), listOf(InRainPassiveSkillCondition()))
        },
        FairyRecipes().overworld().entityType { EntityType.SKELETON_HORSE },
    ),
    WITHER(
        "wither", 8, "Witheria", "枯精ウィテーリャ", 0x181818, 0x3C3C3C, 0x141414, 0x557272,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 1), listOf(MaximumFoodLevelPassiveSkillCondition(6)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.JUMP_BOOST, 1), listOf(MaximumFoodLevelPassiveSkillCondition(6)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 0), listOf(MaximumFoodLevelPassiveSkillCondition(6)))
        },
        FairyRecipes().entityType { EntityType.WITHER },
    ),
    BLAZE(
        "blaze", 7, "Blazia", "烈炎精ブラージャ", 0xE7DA21, 0xCB6E06, 0xB44500, 0xFF8025,
        listOf {
            on(AttackDamagePassiveSkillEffect(2.0), listOf(OnFirePassiveSkillCondition()))
            on(CombustionPassiveSkillEffect(), listOf(StatusEffectPassiveSkillCondition(StatusEffects.FIRE_RESISTANCE)))
        },
        FairyRecipes().biome(ConventionalBiomeTags.IN_NETHER).entityType { EntityType.BLAZE },
    ),
    WHEAT(
        "wheat", 3, "Wheatia", "麦精ウェアーチャ", 0xD8BF7F, 0xDBBB65, 0xDBBB65, 0x896D20,
        listOf {
            on(RegenerationPassiveSkillEffect(0.1), listOf(MinimumFoodLevelPassiveSkillCondition(12)))
            on(RegenerationPassiveSkillEffect(0.3), listOf(FoodPassiveSkillCondition { Items.BREAD }))
            on(AttackDamagePassiveSkillEffect(0.5), listOf(FoodPassiveSkillCondition { Items.BREAD }))
        },
        FairyRecipes().overworld().block { Blocks.HAY_BLOCK }.recipe { Items.WHEAT },
    ),
    CARROT(
        "carrot", 4, "Carrotia", "人参精ツァッローチャ", 0xF98D10, 0xFD7F11, 0xE3710F, 0x248420,
        listOf {
            on(RegenerationPassiveSkillEffect(0.1), listOf(MinimumFoodLevelPassiveSkillCondition(12)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.NIGHT_VISION, 0, additionalSeconds = 10), listOf(FoodPassiveSkillCondition { Items.CARROT }))
        },
        FairyRecipes().overworld().block { Blocks.CARROTS }.recipe { Items.CARROT },
    ),
    POTATO(
        "potato", 4, "Potatia", "芋精ポターチャ", 0xEAC278, 0xE7B456, 0xE7B456, 0x248420,
        listOf {
            on(RegenerationPassiveSkillEffect(0.1), listOf(MinimumFoodLevelPassiveSkillCondition(12)))
            on(RegenerationPassiveSkillEffect(0.2), listOf(FoodPassiveSkillCondition { Items.BAKED_POTATO }))
            on(AttackDamagePassiveSkillEffect(1.0), listOf(FoodPassiveSkillCondition { Items.BAKED_POTATO }))
        },
        FairyRecipes().overworld().block { Blocks.POTATOES }.recipe { Items.POTATO },
    ),
    POISONOUS_POTATO(
        "poisonous_potato", 5, "Poisonouse Potatia", "悪芋精ポイソノウセポターチャ", 0xCFE661, 0xE7B456, 0xE7B456, 0x61B835,
        listOf {
            on(AttackDamagePassiveSkillEffect(1.0), listOf(MinimumFoodLevelPassiveSkillCondition(12)))
            on(AttackDamagePassiveSkillEffect(2.0), listOf(FoodPassiveSkillCondition { Items.POISONOUS_POTATO }))
        },
        FairyRecipes().overworld().block { Blocks.POTATOES }.recipe { Items.POISONOUS_POTATO },
    ),
    BEETROOT(
        "beetroot", 4, "Beetrootia", "火焔菜精ベートローチャ", 0xC1727C, 0xA74D55, 0x96383D, 0x01A900,
        listOf {
            on(RegenerationPassiveSkillEffect(0.1), listOf(MinimumFoodLevelPassiveSkillCondition(12)))
            on(AttackDamagePassiveSkillEffect(2.0), listOf(FoodPassiveSkillCondition { Items.BEETROOT }))
        },
        FairyRecipes().overworld().block { Blocks.BEETROOTS }.recipe { Items.BEETROOT },
    ),
    MELON(
        "melon", 4, "Melonia", "西瓜精メローニャ", 0xFF5440, 0xA6EE63, 0x195612, 0x01A900,
        listOf {
            on(RegenerationPassiveSkillEffect(0.1), listOf(MinimumFoodLevelPassiveSkillCondition(12)))
            on(RegenerationPassiveSkillEffect(0.4), listOf(FoodPassiveSkillCondition { Items.MELON_SLICE }))
        },
        FairyRecipes().biome(ConventionalBiomeTags.JUNGLE).block { Blocks.MELON }.recipe { Items.MELON },
    ),
    APPLE(
        "apple", 4, "Applia", "林檎精アップーリャ", 0xFF755D, 0xFF564E, 0xFF0000, 0x01A900,
        listOf {
            on(RegenerationPassiveSkillEffect(0.1), listOf(MinimumFoodLevelPassiveSkillCondition(12)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0), listOf(FoodPassiveSkillCondition { Items.APPLE }))
        },
        FairyRecipes().overworld().recipe { Items.APPLE },
    ),
    WOOD(
        "wood", 1, "Woodia", "木精ウォージャ", 0xE7C697, 0xAD8232, 0xAD8232, 0x8B591C,
        listOf {
            on(AttackDamagePassiveSkillEffect(2.0), listOf(ToolMaterialPassiveSkillCondition(ToolMaterialCard.WOOD)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 0), listOf(ToolMaterialPassiveSkillCondition(ToolMaterialCard.WOOD)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 1), listOf(ToolMaterialPassiveSkillCondition(ToolMaterialCard.WOOD), MinimumManaPassiveSkillCondition(7.0)))
        },
        FairyRecipes().overworld().block { Blocks.OAK_PLANKS }.recipe { Items.OAK_PLANKS },
    ),
    SPRUCE(
        "spruce", 6, "Sprucia", "松精スプルーツァ", 0x795C36, 0x583E1F, 0x23160A, 0x4C784C,
        listOf {
            on(AttackDamagePassiveSkillEffect(1.0), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.FOREST)))
            on(AttackDamagePassiveSkillEffect(1.0), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.TAIGA)))
        },
        FairyRecipes().biome(ConventionalBiomeTags.TAIGA).block { Blocks.SPRUCE_SAPLING }.recipe { Items.SPRUCE_SAPLING },
    ),
    HOE(
        "hoe", 3, "Hia", "鍬精ヒャ", 0xFFFFFF, 0xFFC48E, 0x47FF00, 0xFFFFFF,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 0), listOf(HasHoePassiveSkillCondition()))
            on(LuckPassiveSkillEffect(3.0), listOf(HasHoePassiveSkillCondition()))
        },
        FairyRecipes().recipe { Items.STONE_HOE },
    ),
    CRAFTING_TABLE(
        "crafting_table", 3, "Craftinge Tablia", "作業台精ツラフティンゲターブリャ", 0xFFFFFF, 0xFFBB9A, 0xFFC980, 0x000000,
        listOf {
            on(LuckPassiveSkillEffect(2.0), listOf(IndoorPassiveSkillCondition()))
        },
        FairyRecipes().block { Blocks.CRAFTING_TABLE }.recipe { Items.CRAFTING_TABLE },
    ),
    ANVIL(
        "anvil", 4, "Anvilia", "金床精アンヴィーリャ", 0xFFFFFF, 0xA9A9A9, 0x909090, 0xA86F18,
        listOf {
            on(AttackDamagePassiveSkillEffect(2.0), listOf(IndoorPassiveSkillCondition()))
        },
        FairyRecipes().block { Blocks.ANVIL }.recipe { Items.ANVIL },
    ),
    ENCHANTING_TABLE(
        "enchanting_table", 6, "Enchantinge Tablia", "付魔台精エンキャンティンゲターブリャ", 0x472F65, 0xCE2828, 0xCE2828, 0x7BFFDD,
        listOf {
            on(ExperiencePassiveSkillEffect(0.03), listOf(IndoorPassiveSkillCondition(), MaximumLevelPassiveSkillCondition(19)))
            on(ExperiencePassiveSkillEffect(0.03), listOf(IndoorPassiveSkillCondition(), MaximumLevelPassiveSkillCondition(29)))
        },
        FairyRecipes().block { Blocks.ENCHANTING_TABLE }.recipe { Items.ENCHANTING_TABLE },
    ),
    HOPPER(
        "hopper", 3, "Hopperia", "漏斗精ホッペーリャ", 0xFFFFFF, 0x797979, 0x646464, 0x5A5A5A,
        listOf {
            on(CollectionPassiveSkillEffect(1.5), listOf(IndoorPassiveSkillCondition()))
        },
        FairyRecipes().block { Blocks.HOPPER }.recipe { Items.HOPPER },
    ),
    BEACON(
        "beacon", 11, "Beaconia", "信標精ベアツォーニャ", 0x97FFE3, 0x6029B3, 0x2E095E, 0xD4EAE6,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 0), listOf(OutdoorPassiveSkillCondition()))
            on(StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 0), listOf(OutdoorPassiveSkillCondition()))
            on(StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0), listOf(OutdoorPassiveSkillCondition()))
            on(StatusEffectPassiveSkillEffect(StatusEffects.JUMP_BOOST, 1), listOf(OutdoorPassiveSkillCondition()))
            on(StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 0), listOf(OutdoorPassiveSkillCondition()))
            on(StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0), listOf(OutdoorPassiveSkillCondition()))
        },
        FairyRecipes().block { Blocks.BEACON }.recipe { Items.BEACON },
    ),
    GLASS(
        "glass", 4, "Glassia", "硝子精グラッシャ", 0xFFFFFF, 0xEFF5FF, 0xE8EDF5, 0xADE0E9,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.INVISIBILITY, 0), listOf(MaximumHealthPassiveSkillCondition(1)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.INVISIBILITY, 0), listOf(MinimumManaPassiveSkillCondition(11.0)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.GLOWING, 0), listOf(MaximumHealthPassiveSkillCondition(1)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.GLOWING, 0), listOf(MinimumManaPassiveSkillCondition(11.0)))
        },
        FairyRecipes().block { Blocks.GLASS }.recipe { Items.GLASS },
    ),
    PRISMARINE(
        "prismarine", 5, "Prismarinia", "海晶石精プリスマリーニャ", 0xA3D3C7, 0x769A91, 0x769A91, 0x69C4C0,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 1), listOf(UnderwaterPassiveSkillCondition()))
        },
        FairyRecipes().biome(ConventionalBiomeTags.OCEAN).block { Blocks.PRISMARINE }.recipe { Items.PRISMARINE },
    ),
    IRON_BARS(
        "iron_bars", 4, "Irone Barsia", "鉄格子精イローネバルシャ", 0xFFFFFF, 0xA1A1A3, 0x404040, 0x404040,
        listOf {
            on(MaxHealthPassiveSkillEffect(5.0), listOf())
        },
        FairyRecipes().block { Blocks.IRON_BARS }.recipe { Items.IRON_BARS },
    ),
    PLAINS(
        "plains", 2, "Plainsia", "平原精プラインシャ", 0xB0DF83, 0xD4FF82, 0x86C91C, 0x489F25,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 0), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.PLAINS)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 1), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.PLAINS), MinimumManaPassiveSkillCondition(7.0)))
        },
        FairyRecipes().biome(ConventionalBiomeTags.PLAINS),
    ),
    OCEAN(
        "ocean", 3, "Oceania", "海精オツェアーニャ", 0x7DAEF5, 0x1B6CE9, 0x191CF0, 0x004DA5,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 0), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.OCEAN)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 1), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.OCEAN), MinimumManaPassiveSkillCondition(7.0)))
        },
        FairyRecipes().biome(ConventionalBiomeTags.OCEAN),
    ),
    TAIGA(
        "taiga", 4, "Taigia", "針葉樹林精タイギャ", 0x5D985E, 0x476545, 0x223325, 0x5A3711,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 0), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.TAIGA)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 1), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.TAIGA), MinimumManaPassiveSkillCondition(7.0)))
        },
        FairyRecipes().biome(ConventionalBiomeTags.TAIGA),
    ),
    MOUNTAIN(
        "mountain", 3, "Mountainia", "山精モウンタイニャ", 0x84BF80, 0xB1B0B1, 0x717173, 0xF0F0F0,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.JUMP_BOOST, 0), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.MOUNTAIN)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.JUMP_BOOST, 1), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.MOUNTAIN), MinimumManaPassiveSkillCondition(7.0)))
        },
        FairyRecipes().biome(ConventionalBiomeTags.MOUNTAIN),
    ),
    FOREST(
        "forest", 3, "Forestia", "森精フォレスチャ", 0x8EBF7A, 0x7B9C62, 0x89591D, 0x2E6E14,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.FOREST)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 1), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.FOREST), MinimumManaPassiveSkillCondition(7.0)))
        },
        FairyRecipes().biome(ConventionalBiomeTags.FOREST),
    ),
    DESERT(
        "desert", 2, "Desertia", "砂漠精デセルチャ", 0xF9F0C8, 0xDDD6A5, 0xD6CE9D, 0x656054,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.DESERT), SunshinePassiveSkillCondition()))
            on(StatusEffectPassiveSkillEffect(StatusEffects.FIRE_RESISTANCE, 0), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.DESERT), SunshinePassiveSkillCondition(), MinimumManaPassiveSkillCondition(7.0)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 0), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.DESERT), MoonlightPassiveSkillCondition()))
            on(StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 1), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.DESERT), MoonlightPassiveSkillCondition(), MinimumManaPassiveSkillCondition(7.0)))
        },
        FairyRecipes().biome(ConventionalBiomeTags.DESERT),
    ),
    AVALON(
        "avalon", 8, "Avalonia", "阿瓦隆精アヴァローニャ", 0xFFE4CA, 0xE1FFCE, 0xD0FFE6, 0xFFCAFF,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 1), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.MUSHROOM)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 1), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.FLORAL)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 1), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.MUSHROOM)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 1), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.FLORAL)))
        },
        FairyRecipes().always(), // TODO イベント終了時コモン枠除去
    ),
    VOID(
        "void", 11, "Voidia", "奈落精ヴォイジャ", 0x000000, 0x000000, 0x000000, 0xB1B1B1,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.SLOW_FALLING, 0), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_THE_END)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_THE_END)))
            on(StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 2), listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_THE_END)))
        },
        FairyRecipes().biome(ConventionalBiomeTags.IN_THE_END),
    ),
    NIGHT(
        "night", 7, "Nightia", "夜精ニグチャ", 0xFFE260, 0x2C2C2E, 0x0E0E10, 0x2D4272,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 0), listOf(NightPassiveSkillCondition()))
        },
        FairyRecipes().overworld(),
    ),
    TIME(
        "time", 12, "Timia", "時精ティーミャ", 0xCDFFBF, 0xD5DEBC, 0xD8DEA7, 0x8DD586,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 1), listOf())
            on(MovementSpeedPassiveSkillEffect(0.20), listOf())
            on(StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 1), listOf())
        },
        FairyRecipes().always(),
    ),
    GRAVITY(
        "gravity", 12, "Gravitia", "重力精グラヴィーチャ", 0xC2A7F2, 0x3600FF, 0x2A00B1, 0x110047,
        listOf {
            on(StatusEffectPassiveSkillEffect(StatusEffects.SLOW_FALLING, 0), listOf())
            on(AttackDamagePassiveSkillEffect(2.0), listOf())
        },
        FairyRecipes().always(),
    ),
    DREAM(
        "dream", 10, "Dreamia", "夢幻精ドレアミャ", 0xBFC3FF, 0xD1BAD8, 0xDBBCD4, 0x848ACC,
        listOf {
            on(LuckPassiveSkillEffect(1.0), listOf())
            on(RegenerationPassiveSkillEffect(0.1), listOf())
            on(ManaPassiveSkillEffect(0.5), listOf())
            // TODO 夢のエフェクトが見えるようになる
        },
        FairyRecipes().always(),
    ),
    MINA(
        "mina", 5, "Minia", "銀子精ミーニャ", 0xFFFF84, 0xFFFF00, 0xFFFF00, 0xFFC800,
        listOf {
            on(ManaPassiveSkillEffect(1.0), listOf(TelescopeMissionPassiveSkillCondition()))
        },
        FairyRecipes().always(),
    ),
    ;

    val motif = Identifier(MirageFairy2023.modId, motifPath)
    val fairy: Fairy = object : Fairy {
        override val motif get() = this@FairyCard.motif
        override val item get() = this@FairyCard()
        override val rare get() = this@FairyCard.rare
    }

}

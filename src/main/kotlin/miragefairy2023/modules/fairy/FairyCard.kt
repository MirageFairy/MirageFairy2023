package miragefairy2023.modules.fairy

import miragefairy2023.InitializationScope
import miragefairy2023.MirageFairy2023
import miragefairy2023.api.Fairy
import miragefairy2023.api.PassiveSkill
import miragefairy2023.modules.BlockFairyRelation
import miragefairy2023.modules.CommonFairyEntry
import miragefairy2023.modules.DemonItemCard
import miragefairy2023.modules.DemonToolMaterials
import miragefairy2023.modules.DreamCatcherItem
import miragefairy2023.modules.EntityTypeFairyRelation
import miragefairy2023.modules.MirageFlourCard
import miragefairy2023.modules.MirageFlourItem
import miragefairy2023.modules.invoke
import miragefairy2023.modules.passiveskill.AirPassiveSkillCondition
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
import miragefairy2023.modules.passiveskill.RegenerationPassiveSkillEffect
import miragefairy2023.modules.passiveskill.ShadePassiveSkillCondition
import miragefairy2023.modules.passiveskill.StatusEffectPassiveSkillCondition
import miragefairy2023.modules.passiveskill.StatusEffectPassiveSkillEffect
import miragefairy2023.modules.passiveskill.SunshinePassiveSkillCondition
import miragefairy2023.modules.passiveskill.ThunderingPassiveSkillCondition
import miragefairy2023.modules.passiveskill.ToolMaterialPassiveSkillCondition
import miragefairy2023.modules.passiveskill.UnderwaterPassiveSkillCondition
import miragefairy2023.util.concat
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.group
import miragefairy2023.util.text
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder
import net.minecraft.entity.EntityType
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.item.ToolMaterials
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeKeys

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
    val recipeContainer: RecipeContainer,
) {
    AIR(
        "air", 0, "Airia", "空気精アイリャ", 0xFFBE80, 0xDEFFFF, 0xDEFFFF, 0xB0FFFF,
        listOf(PassiveSkill(listOf(OverworldPassiveSkillCondition(), AirPassiveSkillCondition()), MovementSpeedPassiveSkillEffect(0.30))),
        RecipeContainer().always().block { Blocks.AIR },
    ),
    LIGHT(
        "light", 3, "Lightia", "光精リグチャ", 0xFFFFD8, 0xFFFFD8, 0xFFFFC5, 0xFFFF00,
        listOf(PassiveSkill(listOf(MinimumLightLevelPassiveSkillCondition(12)), MovementSpeedPassiveSkillEffect(0.30))),
        RecipeContainer().always(),
    ),
    FIRE(
        "fire", 2, "Firia", "火精フィーリャ", 0xFF6C01, 0xF9DFA4, 0xFF7324, 0xFF4000,
        listOf(PassiveSkill(listOf(OnFirePassiveSkillCondition()), AttackDamagePassiveSkillEffect(4.0))),
        RecipeContainer().biome(ConventionalBiomeTags.IN_NETHER).block { Blocks.FIRE },
    ),
    WATER(
        "water", 1, "Wateria", "水精ワテーリャ", 0x5469F2, 0x5985FF, 0x172AD3, 0x2D40F4,
        listOf(PassiveSkill(listOf(UnderwaterPassiveSkillCondition()), MaxHealthPassiveSkillEffect(12.0))),
        RecipeContainer().overworld().block { Blocks.WATER }.recipe { Items.WATER_BUCKET },
    ),
    LAVA(
        "lava", 4, "Lavia", "溶岩精ラーヴャ", 0xCD4208, 0xEDB54A, 0xCC4108, 0x4C1500,
        listOf(
            PassiveSkill(listOf(OnFirePassiveSkillCondition()), AttackDamagePassiveSkillEffect(2.0)),
            PassiveSkill(listOf(OnFirePassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 0)),
            PassiveSkill(listOf(OnFirePassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0)),
        ),
        RecipeContainer().biome(ConventionalBiomeTags.IN_NETHER).block { Blocks.LAVA }.recipe { Items.LAVA_BUCKET },
    ),
    MOON(
        "moon", 9, "Moonia", "月精モーニャ", 0xD9E4FF, 0x747D93, 0x0C121F, 0x2D4272,
        listOf(
            PassiveSkill(listOf(MoonlightPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.NIGHT_VISION, 0, additionalSeconds = 10)),
            PassiveSkill(listOf(MoonlightPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0)),
        ),
        RecipeContainer().overworld(),
    ),
    SUN(
        "sun", 10, "Sunia", "太陽精スーニャ", 0xff2f00, 0xff972b, 0xff7500, 0xffe7b2,
        listOf(
            PassiveSkill(listOf(SunshinePassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 1)),
            PassiveSkill(listOf(SunshinePassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0)),
        ),
        RecipeContainer().overworld(),
    ),
    RAIN(
        "rain", 2, "Rainia", "雨精ライニャ", 0xB4FFFF, 0x4D5670, 0x4D5670, 0x2D40F4,
        listOf(PassiveSkill(listOf(InRainPassiveSkillCondition()), AttackDamagePassiveSkillEffect(4.0))),
        RecipeContainer().overworld(),
    ),
    DIRT(
        "dirt", 1, "Dirtia", "土精ディルチャ", 0xB87440, 0xB9855C, 0x593D29, 0x914A18,
        listOf(PassiveSkill(listOf(OverworldPassiveSkillCondition()), MaxHealthPassiveSkillEffect(10.0))),
        RecipeContainer().overworld().block { Blocks.DIRT }.recipe { Items.DIRT },
    ),
    MYCELIUM(
        "mycelium", 6, "Myceliumia", "菌糸精ミツェリウミャ", 0x8F7E86, 0x8B7071, 0x8B7071, 0x8B6264,
        listOf(
            PassiveSkill(listOf(), AttackDamagePassiveSkillEffect(1.0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.MUSHROOM)), AttackDamagePassiveSkillEffect(2.0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.MUSHROOM)), MaxHealthPassiveSkillEffect(12.0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.MUSHROOM), MinimumManaPassiveSkillCondition(10.0)), StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0)),
        ),
        RecipeContainer().biome(ConventionalBiomeTags.MUSHROOM).block { Blocks.MYCELIUM }.recipe { Items.MYCELIUM },
    ),
    SCULK(
        "sculk", 6, "Sculkia", "幽匿塊精スツルキャ", 0x19222C, 0x023F3D, 0x023F3D, 0x19C0C0,
        listOf(
            PassiveSkill(listOf(MaximumLightLevelPassiveSkillCondition(0)), MaxHealthPassiveSkillEffect(8.0)),
            PassiveSkill(listOf(MaximumLightLevelPassiveSkillCondition(0)), AttackDamagePassiveSkillEffect(3.0)),
        ),
        RecipeContainer().biome(BiomeKeys.DEEP_DARK).block { Blocks.SCULK }.recipe { Items.SCULK },
    ),
    STONE(
        "stone", 1, "Stonia", "石精ストーニャ", 0x333333, 0x8F8F8F, 0x686868, 0x747474,
        listOf(
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.STONE)), AttackDamagePassiveSkillEffect(2.0)),
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.STONE)), StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0)),
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.STONE), MinimumManaPassiveSkillCondition(7.0)), StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 1)),
        ),
        RecipeContainer().overworld().block { Blocks.STONE }.recipe { Items.STONE },
    ),
    DRIPSTONE(
        "dripstone", 3, "Dripstonia", "鍾乳石精ドリプストーニャ", 0xB19C7E, 0xA97F6F, 0xA97F6F, 0xAD7069,
        listOf(
            PassiveSkill(listOf(ShadePassiveSkillCondition()), AttackDamagePassiveSkillEffect(1.5)),
            PassiveSkill(listOf(ShadePassiveSkillCondition()), MaxHealthPassiveSkillEffect(6.0)),
        ),
        RecipeContainer().biome(BiomeKeys.DRIPSTONE_CAVES).block { Blocks.DRIPSTONE_BLOCK }.recipe { Items.DRIPSTONE_BLOCK },
    ),
    REINFORCED_DEEPSLATE(
        "reinforced_deepslate", 9, "Reinforcede Deepslatia", "強化深層岩精レインフォルツェーデデープスラーチャ", 0x6C7180, 0x5C606C, 0x5C606C, 0xACCF9D,
        listOf(
            PassiveSkill(listOf(ShadePassiveSkillCondition()), MaxHealthPassiveSkillEffect(10.0)),
        ),
        RecipeContainer().block { Blocks.REINFORCED_DEEPSLATE }.recipe { Items.REINFORCED_DEEPSLATE },
    ),
    ANCIENT_DEBRIS(
        "ancient_debris", 8, "Anciente Debrisia", "古代残骸精アンツィエンテデブリーシャ", 0x8F645A, 0x8F645A, 0x885040, 0xD8C2B7,
        listOf(
            PassiveSkill(listOf(), AttackDamagePassiveSkillEffect(1.0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_NETHER)), AttackDamagePassiveSkillEffect(1.0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_NETHER)), MaxHealthPassiveSkillEffect(2.0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_NETHER)), LuckPassiveSkillEffect(2.0)),
        ),
        RecipeContainer().biome(ConventionalBiomeTags.IN_NETHER).block { Blocks.ANCIENT_DEBRIS }.recipe { Items.ANCIENT_DEBRIS },
    ),
    PURPUR(
        "purpur", 7, "Purpuria", "紫珀精プルプーリャ", 0xCBA8CB, 0xC08AC0, 0xC08AC0, 0xBC68BB,
        listOf(
            PassiveSkill(listOf(), CollectionPassiveSkillEffect(0.5)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_THE_END)), AttackDamagePassiveSkillEffect(2.0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_THE_END)), MaxHealthPassiveSkillEffect(4.0)),
        ),
        RecipeContainer().biome(ConventionalBiomeTags.END_ISLANDS).block { Blocks.PURPUR_BLOCK }.recipe { Items.PURPUR_BLOCK },
    ),
    COPPER(
        "copper", 3, "Copperia", "銅精ツォッペーリャ", 0xF69D7F, 0xF77653, 0xF77653, 0x5DC09A,
        listOf(
            PassiveSkill(listOf(), LuckPassiveSkillEffect(0.2)),
            PassiveSkill(listOf(ThunderingPassiveSkillCondition()), AttackDamagePassiveSkillEffect(2.5)),
        ),
        RecipeContainer().overworld().block { Blocks.COPPER_BLOCK }.recipe { Items.COPPER_INGOT },
    ),
    IRON(
        "iron", 4, "Ironia", "鉄精イローニャ", 0xA0A0A0, 0xD8D8D8, 0x727272, 0xD8AF93,
        listOf(
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.IRON)), AttackDamagePassiveSkillEffect(2.0)),
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.IRON)), StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 0)),
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.IRON), MinimumManaPassiveSkillCondition(8.0)), StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 1)),
        ),
        RecipeContainer().overworld().block { Blocks.IRON_BLOCK }.recipe { Items.IRON_INGOT },
    ),
    GOLD(
        "gold", 6, "Goldia", "金精ゴルジャ", 0xEFE642, 0xF4CC17, 0xF4CC17, 0xFDB61E,
        listOf(
            PassiveSkill(listOf(), LuckPassiveSkillEffect(0.8)),
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.GOLD)), AttackDamagePassiveSkillEffect(2.0)),
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.GOLD)), StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 0)),
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.GOLD), MinimumManaPassiveSkillCondition(10.0)), StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 1)),
        ),
        RecipeContainer().overworld().biome(ConventionalBiomeTags.IN_NETHER).block { Blocks.GOLD_BLOCK }.recipe { Items.GOLD_INGOT },
    ),
    NETHERITE(
        "netherite", 8, "Netheritia", "地獄合金精ネテリーチャ", 0x8F788F, 0x74585B, 0x705558, 0x77302D,
        listOf(
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.NETHERITE)), AttackDamagePassiveSkillEffect(2.0)),
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.NETHERITE)), StatusEffectPassiveSkillEffect(StatusEffects.FIRE_RESISTANCE, 0)),
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.NETHERITE), MinimumManaPassiveSkillCondition(12.0)), StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 1)),
        ),
        RecipeContainer().biome(ConventionalBiomeTags.IN_NETHER).block { Blocks.NETHERITE_BLOCK }.recipe { Items.NETHERITE_INGOT },
    ),
    MIRANAGITE(
        "miranagite", 5, "Miranagitia", "蒼天石精ミラナギーチャ", 0x4EC5F4, 0x4394D3, 0x004477, 0x0C4CEF,
        listOf(
            PassiveSkill(listOf(), LuckPassiveSkillEffect(0.5)),
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(DemonToolMaterials.MIRANAGITE)), StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 0)),
            PassiveSkill(listOf(), ManaPassiveSkillEffect(0.5)),
            PassiveSkill(listOf(MinimumManaPassiveSkillCondition(9.0)), ManaPassiveSkillEffect(0.5)),
        ),
        RecipeContainer().overworld().recipe { DemonItemCard.MIRANAGITE() },
    ),
    AMETHYST(
        "amethyst", 5, "Amethystia", "紫水晶精アメティスチャ", 0xCAA9FF, 0xA974FF, 0x9D60FF, 0xBC92FF,
        listOf(
            PassiveSkill(listOf(), LuckPassiveSkillEffect(0.5)),
            PassiveSkill(listOf(MaximumLightLevelPassiveSkillCondition(7)), AttackDamagePassiveSkillEffect(0.5)),
            PassiveSkill(listOf(MaximumLightLevelPassiveSkillCondition(7)), LuckPassiveSkillEffect(2.0)),
        ),
        RecipeContainer().overworld().block { Blocks.AMETHYST_BLOCK }.recipe { Items.AMETHYST_SHARD },
    ),
    EMERALD(
        "emerald", 6, "Emeraldia", "翠玉精エメラルジャ", 0x9FF9B5, 0x81F99E, 0x17DD62, 0x008A25,
        listOf(
            PassiveSkill(listOf(), LuckPassiveSkillEffect(0.5)),
            PassiveSkill(listOf(InVillagePassiveSkillCondition()), LuckPassiveSkillEffect(1.5)),
        ),
        RecipeContainer().overworld().block { Blocks.EMERALD_BLOCK }.recipe { Items.EMERALD },
    ),
    DIAMOND(
        "diamond", 7, "Diamondia", "金剛石精ディアモンジャ", 0x97FFE3, 0xD1FAF3, 0x70FFD9, 0x30DBBD,
        listOf(
            PassiveSkill(listOf(), LuckPassiveSkillEffect(0.5)),
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.DIAMOND)), AttackDamagePassiveSkillEffect(2.0)),
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.DIAMOND)), StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 0)),
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.DIAMOND), MinimumManaPassiveSkillCondition(12.0)), StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 1)),
        ),
        RecipeContainer().overworld().block { Blocks.DIAMOND_BLOCK }.recipe { Items.DIAMOND },
    ),
    ADAMANTITE(
        "adamantite", 9, "Adamantitia", "精金精アダマンティーチャ", 0xE5B3CA, 0xDB7A9C, 0xDB7A9C, 0xFD2888,
        listOf(
            PassiveSkill(listOf(), LuckPassiveSkillEffect(0.5)),
            PassiveSkill(listOf(), AttackDamagePassiveSkillEffect(2.0)),
        ),
        RecipeContainer().always(), // TODO イベ限解除
    ),
    COAL(
        "coal", 2, "Coalia", "石炭精ツォアーリャ", 0x4C2510, 0x52504C, 0x39352E, 0x150B00,
        listOf(
            PassiveSkill(listOf(), MaxHealthPassiveSkillEffect(4.0)),
            PassiveSkill(listOf(OnFirePassiveSkillCondition()), LuckPassiveSkillEffect(2.0)),
        ),
        RecipeContainer().overworld().biome(ConventionalBiomeTags.IN_NETHER).block { Blocks.COAL_BLOCK }.recipe { Items.COAL },
    ),
    REDSTONE(
        "redstone", 4, "Redstonia", "赤石精レドストーニャ", 0xFF5959, 0xFF0000, 0xCD0000, 0xBA0000,
        listOf(
            PassiveSkill(listOf(IndoorPassiveSkillCondition()), LuckPassiveSkillEffect(0.5)),
            PassiveSkill(listOf(IndoorPassiveSkillCondition()), AttackDamagePassiveSkillEffect(1.0)),
            PassiveSkill(listOf(IndoorPassiveSkillCondition()), MaxHealthPassiveSkillEffect(2.0)),
        ),
        RecipeContainer().overworld().block { Blocks.REDSTONE_BLOCK }.recipe { Items.REDSTONE },
    ),
    LAPIS_LAZULI(
        "lapis_lazuli", 5, "Lapise Lazulia", "瑠璃石精ラピセラズーリャ", 0x77A2FF, 0x3064D3, 0x3064D3, 0x3976F9,
        listOf(
            PassiveSkill(listOf(), LuckPassiveSkillEffect(0.5)),
            PassiveSkill(listOf(OverworldPassiveSkillCondition()), LuckPassiveSkillEffect(1.5)),
        ),
        RecipeContainer().overworld().block { Blocks.LAPIS_BLOCK }.recipe { Items.LAPIS_LAZULI },
    ),
    GLOWSTONE(
        "glowstone", 6, "Glowstonia", "蛍光石精グロウストーニャ", 0xEFC298, 0xEAA463, 0xEAA463, 0xEADD52,
        listOf(
            PassiveSkill(listOf(), StatusEffectPassiveSkillEffect(StatusEffects.GLOWING, 0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_NETHER)), AttackDamagePassiveSkillEffect(1.5)),
        ),
        RecipeContainer().biome(ConventionalBiomeTags.IN_NETHER).block { Blocks.GLOWSTONE }.recipe { Items.GLOWSTONE_DUST },
    ),
    OBSIDIAN(
        "obsidian", 5, "Obsidiania", "黒耀石精オブシディアーニャ", 0x775599, 0x6029B3, 0x2E095E, 0x0F0033,
        listOf(
            PassiveSkill(listOf(), StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0)),
            PassiveSkill(listOf(MinimumManaPassiveSkillCondition(9.0)), StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 1)),
        ),
        RecipeContainer().overworld().biome(ConventionalBiomeTags.IN_THE_END).block { Blocks.OBSIDIAN }.recipe { Items.OBSIDIAN },
    ),
    FISH(
        "fish", 2, "Fishia", "魚精フィーシャ", 0x6B9F93, 0x5A867C, 0x43655D, 0xADBEDB,
        listOf(
            PassiveSkill(listOf(UnderwaterPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.NIGHT_VISION, 0, additionalSeconds = 10)),
            PassiveSkill(listOf(UnderwaterPassiveSkillCondition(), MinimumManaPassiveSkillCondition(10.0)), StatusEffectPassiveSkillEffect(StatusEffects.WATER_BREATHING, 0)),
        ),
        RecipeContainer().overworld().recipe { Items.COD }.entityType { EntityType.COD }, // TODO 魚精は希釈で得る
    ),
    CLOWNFISH(
        "clownfish", 7, "Clownfishia", "隈之実精ツロウンフィーシャ", 0xE46A22, 0xF46F20, 0xA94B1D, 0xFFDBC5,
        listOf(
            PassiveSkill(listOf(UnderwaterPassiveSkillCondition(), MinimumLightLevelPassiveSkillCondition(4)), StatusEffectPassiveSkillEffect(StatusEffects.WATER_BREATHING, 0)),
            PassiveSkill(listOf(UnderwaterPassiveSkillCondition(), MinimumManaPassiveSkillCondition(10.0)), StatusEffectPassiveSkillEffect(StatusEffects.WATER_BREATHING, 0)),
        ),
        RecipeContainer().overworld().recipe { Items.TROPICAL_FISH }.entityType { EntityType.TROPICAL_FISH },
    ),
    SPONGE(
        "sponge", 4, "Spongia", "海綿精スポンギャ", 0xEADF67, 0xB1A947, 0xB1A947, 0xDBCD5A,
        listOf(
            PassiveSkill(listOf(UnderwaterPassiveSkillCondition()), AttackDamagePassiveSkillEffect(2.0)),
            PassiveSkill(listOf(UnderwaterPassiveSkillCondition(), MinimumManaPassiveSkillCondition(10.0)), StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0)),
        ),
        RecipeContainer().biome(ConventionalBiomeTags.OCEAN).block { Blocks.SPONGE }.recipe { Items.SPONGE },
    ),
    PLAYER(
        "player", 5, "Playeria", "人精プライェーリャ", 0xB58D63, 0x00AAAA, 0x322976, 0x4B3422,
        listOf(PassiveSkill(listOf(MaximumLevelPassiveSkillCondition(29)), ExperiencePassiveSkillEffect(0.1))),
        RecipeContainer().always().entityType { EntityType.PLAYER },
    ),
    ENDERMAN(
        "enderman", 6, "Endermania", "終界人精エンデルマーニャ", 0x000000, 0x161616, 0x161616, 0xEF84FA,
        listOf(PassiveSkill(listOf(), CollectionPassiveSkillEffect(1.0))),
        RecipeContainer().overworld().biome(ConventionalBiomeTags.IN_NETHER).biome(ConventionalBiomeTags.IN_THE_END).entityType { EntityType.ENDERMAN },
    ),
    WARDEN(
        "warden", 7, "Wardenia", "監守者精ワルデーニャ", 0x0A3135, 0xCFCFA4, 0xA0AA7A, 0x2CD0CA,
        listOf(
            PassiveSkill(listOf(MaximumLightLevelPassiveSkillCondition(0)), StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 1)),
            PassiveSkill(listOf(MaximumLightLevelPassiveSkillCondition(0)), AttackDamagePassiveSkillEffect(2.0)),
        ),
        RecipeContainer().biome(BiomeKeys.DEEP_DARK).entityType { EntityType.WARDEN },
    ),
    ZOMBIE(
        "zombie", 2, "Zombia", "硬屍精ゾンビャ", 0x2B4219, 0x00AAAA, 0x322976, 0x2B4219,
        listOf(
            PassiveSkill(listOf(MaximumFoodLevelPassiveSkillCondition(6)), AttackDamagePassiveSkillEffect(2.0)),
            PassiveSkill(listOf(ShadePassiveSkillCondition()), AttackDamagePassiveSkillEffect(1.0)),
        ),
        RecipeContainer().overworld().entityType { EntityType.ZOMBIE },
    ),
    SKELETON_HORSE(
        "skeleton_horse", 6, "Skeletone Horsia", "骸骨馬精スケレトーネホルシャ", 0xA1A1A1, 0xD4D4D4, 0x757575, 0xD5D5D5,
        listOf(
            PassiveSkill(listOf(MaximumFoodLevelPassiveSkillCondition(6)), MovementSpeedPassiveSkillEffect(0.30)),
            PassiveSkill(listOf(InRainPassiveSkillCondition()), AttackDamagePassiveSkillEffect(1.0)),
        ),
        RecipeContainer().overworld().entityType { EntityType.SKELETON_HORSE },
    ),
    WITHER(
        "wither", 8, "Witheria", "枯精ウィテーリャ", 0x181818, 0x3C3C3C, 0x141414, 0x557272,
        listOf(
            PassiveSkill(listOf(MaximumFoodLevelPassiveSkillCondition(6)), StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 1)),
            PassiveSkill(listOf(MaximumFoodLevelPassiveSkillCondition(6)), StatusEffectPassiveSkillEffect(StatusEffects.JUMP_BOOST, 1)),
            PassiveSkill(listOf(MaximumFoodLevelPassiveSkillCondition(6)), StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 0)),
        ),
        RecipeContainer().entityType { EntityType.WITHER },
    ),
    BLAZE(
        "blaze", 7, "Blazia", "烈炎精ブラージャ", 0xE7DA21, 0xCB6E06, 0xB44500, 0xFF8025,
        listOf(
            PassiveSkill(listOf(OnFirePassiveSkillCondition()), AttackDamagePassiveSkillEffect(2.0)),
            PassiveSkill(listOf(StatusEffectPassiveSkillCondition(StatusEffects.FIRE_RESISTANCE)), CombustionPassiveSkillEffect()),
        ),
        RecipeContainer().biome(ConventionalBiomeTags.IN_NETHER).entityType { EntityType.BLAZE },
    ),
    WHEAT(
        "wheat", 3, "Wheatia", "麦精ウェアーチャ", 0xD8BF7F, 0xDBBB65, 0xDBBB65, 0x896D20,
        listOf(
            PassiveSkill(listOf(MinimumFoodLevelPassiveSkillCondition(12)), RegenerationPassiveSkillEffect(0.1)),
            PassiveSkill(listOf(FoodPassiveSkillCondition { Items.BREAD }), RegenerationPassiveSkillEffect(0.3)),
            PassiveSkill(listOf(FoodPassiveSkillCondition { Items.BREAD }), AttackDamagePassiveSkillEffect(0.5)),
        ),
        RecipeContainer().overworld().block { Blocks.HAY_BLOCK }.recipe { Items.WHEAT },
    ),
    CARROT(
        "carrot", 4, "Carrotia", "人参精ツァッローチャ", 0xF98D10, 0xFD7F11, 0xE3710F, 0x248420,
        listOf(
            PassiveSkill(listOf(MinimumFoodLevelPassiveSkillCondition(12)), RegenerationPassiveSkillEffect(0.1)),
            PassiveSkill(listOf(FoodPassiveSkillCondition { Items.CARROT }), StatusEffectPassiveSkillEffect(StatusEffects.NIGHT_VISION, 0, additionalSeconds = 10)),
        ),
        RecipeContainer().overworld().block { Blocks.CARROTS }.recipe { Items.CARROT },
    ),
    POTATO(
        "potato", 4, "Potatia", "芋精ポターチャ", 0xEAC278, 0xE7B456, 0xE7B456, 0x248420,
        listOf(
            PassiveSkill(listOf(MinimumFoodLevelPassiveSkillCondition(12)), RegenerationPassiveSkillEffect(0.1)),
            PassiveSkill(listOf(FoodPassiveSkillCondition { Items.BAKED_POTATO }), RegenerationPassiveSkillEffect(0.2)),
            PassiveSkill(listOf(FoodPassiveSkillCondition { Items.BAKED_POTATO }), AttackDamagePassiveSkillEffect(1.0)),
        ),
        RecipeContainer().overworld().block { Blocks.POTATOES }.recipe { Items.POTATO },
    ),
    POISONOUS_POTATO(
        "poisonous_potato", 5, "Poisonouse Potatia", "悪芋精ポイソノウセポターチャ", 0xCFE661, 0xE7B456, 0xE7B456, 0x61B835,
        listOf(
            PassiveSkill(listOf(MinimumFoodLevelPassiveSkillCondition(12)), AttackDamagePassiveSkillEffect(1.0)),
            PassiveSkill(listOf(FoodPassiveSkillCondition { Items.POISONOUS_POTATO }), AttackDamagePassiveSkillEffect(2.0)),
        ),
        RecipeContainer().overworld().block { Blocks.POTATOES }.recipe { Items.POISONOUS_POTATO },
    ),
    BEETROOT(
        "beetroot", 4, "Beetrootia", "火焔菜精ベートローチャ", 0xC1727C, 0xA74D55, 0x96383D, 0x01A900,
        listOf(
            PassiveSkill(listOf(MinimumFoodLevelPassiveSkillCondition(12)), RegenerationPassiveSkillEffect(0.1)),
            PassiveSkill(listOf(FoodPassiveSkillCondition { Items.BEETROOT }), AttackDamagePassiveSkillEffect(2.0)),
        ),
        RecipeContainer().overworld().block { Blocks.BEETROOTS }.recipe { Items.BEETROOT },
    ),
    MELON(
        "melon", 4, "Melonia", "西瓜精メローニャ", 0xFF5440, 0xA6EE63, 0x195612, 0x01A900,
        listOf(
            PassiveSkill(listOf(MinimumFoodLevelPassiveSkillCondition(12)), RegenerationPassiveSkillEffect(0.1)),
            PassiveSkill(listOf(FoodPassiveSkillCondition { Items.MELON_SLICE }), RegenerationPassiveSkillEffect(0.4)),
        ),
        RecipeContainer().biome(ConventionalBiomeTags.JUNGLE).block { Blocks.MELON }.recipe { Items.MELON },
    ),
    APPLE(
        "apple", 4, "Applia", "林檎精アップーリャ", 0xFF755D, 0xFF564E, 0xFF0000, 0x01A900,
        listOf(
            PassiveSkill(listOf(MinimumFoodLevelPassiveSkillCondition(12)), RegenerationPassiveSkillEffect(0.1)),
            PassiveSkill(listOf(FoodPassiveSkillCondition { Items.APPLE }), StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0)),
        ),
        RecipeContainer().overworld().recipe { Items.APPLE },
    ),
    WOOD(
        "wood", 1, "Woodia", "木精ウォージャ", 0xE7C697, 0xAD8232, 0xAD8232, 0x8B591C,
        listOf(
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.WOOD)), AttackDamagePassiveSkillEffect(2.0)),
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.WOOD)), StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 0)),
            PassiveSkill(listOf(ToolMaterialPassiveSkillCondition(ToolMaterials.WOOD), MinimumManaPassiveSkillCondition(7.0)), StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 1)),
        ),
        RecipeContainer().overworld().block { Blocks.OAK_PLANKS }.recipe { Items.OAK_PLANKS },
    ),
    SPRUCE(
        "spruce", 6, "Sprucia", "松精スプルーツァ", 0x795C36, 0x583E1F, 0x23160A, 0x4C784C,
        listOf(
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.FOREST)), AttackDamagePassiveSkillEffect(1.0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.TAIGA)), AttackDamagePassiveSkillEffect(1.0)),
        ),
        RecipeContainer().biome(ConventionalBiomeTags.TAIGA).block { Blocks.SPRUCE_SAPLING }.recipe { Items.SPRUCE_SAPLING },
    ),
    HOE(
        "hoe", 3, "Hia", "鍬精ヒャ", 0xFFFFFF, 0xFFC48E, 0x47FF00, 0xFFFFFF,
        listOf(
            PassiveSkill(listOf(HasHoePassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 0)),
            PassiveSkill(listOf(HasHoePassiveSkillCondition()), LuckPassiveSkillEffect(3.0)),
        ),
        RecipeContainer().recipe { Items.STONE_HOE },
    ),
    CRAFTING_TABLE(
        "crafting_table", 3, "Craftinge Tablia", "作業台精ツラフティンゲターブリャ", 0xFFFFFF, 0xFFBB9A, 0xFFC980, 0x000000,
        listOf(PassiveSkill(listOf(IndoorPassiveSkillCondition()), LuckPassiveSkillEffect(2.0))),
        RecipeContainer().block { Blocks.CRAFTING_TABLE }.recipe { Items.CRAFTING_TABLE },
    ),
    ANVIL(
        "anvil", 4, "Anvilia", "金床精アンヴィーリャ", 0xFFFFFF, 0xA9A9A9, 0x909090, 0xA86F18,
        listOf(PassiveSkill(listOf(IndoorPassiveSkillCondition()), AttackDamagePassiveSkillEffect(2.0))),
        RecipeContainer().block { Blocks.ANVIL }.recipe { Items.ANVIL },
    ),
    ENCHANTING_TABLE(
        "enchanting_table", 6, "Enchantinge Tablia", "付魔台精エンキャンティンゲターブリャ", 0x472F65, 0xCE2828, 0xCE2828, 0x7BFFDD,
        listOf(
            PassiveSkill(listOf(IndoorPassiveSkillCondition(), MaximumLevelPassiveSkillCondition(19)), ExperiencePassiveSkillEffect(0.04)),
            PassiveSkill(listOf(IndoorPassiveSkillCondition(), MaximumLevelPassiveSkillCondition(29)), ExperiencePassiveSkillEffect(0.06)),
        ),
        RecipeContainer().block { Blocks.ENCHANTING_TABLE }.recipe { Items.ENCHANTING_TABLE },
    ),
    HOPPER(
        "hopper", 3, "Hopperia", "漏斗精ホッペーリャ", 0xFFFFFF, 0x797979, 0x646464, 0x5A5A5A,
        listOf(PassiveSkill(listOf(IndoorPassiveSkillCondition()), CollectionPassiveSkillEffect(1.5))),
        RecipeContainer().block { Blocks.HOPPER }.recipe { Items.HOPPER },
    ),
    BEACON(
        "beacon", 11, "Beaconia", "信標精ベアツォーニャ", 0x97FFE3, 0x6029B3, 0x2E095E, 0xD4EAE6,
        listOf(
            PassiveSkill(listOf(OutdoorPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 0)),
            PassiveSkill(listOf(OutdoorPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 0)),
            PassiveSkill(listOf(OutdoorPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0)),
            PassiveSkill(listOf(OutdoorPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.JUMP_BOOST, 1)),
            PassiveSkill(listOf(OutdoorPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 0)),
            PassiveSkill(listOf(OutdoorPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0)),
        ),
        RecipeContainer().block { Blocks.BEACON }.recipe { Items.BEACON },
    ),
    GLASS(
        "glass", 4, "Glassia", "硝子精グラッシャ", 0xFFFFFF, 0xEFF5FF, 0xE8EDF5, 0xADE0E9,
        listOf(
            PassiveSkill(listOf(MaximumHealthPassiveSkillCondition(1)), StatusEffectPassiveSkillEffect(StatusEffects.INVISIBILITY, 0)),
            PassiveSkill(listOf(MinimumManaPassiveSkillCondition(11.0)), StatusEffectPassiveSkillEffect(StatusEffects.INVISIBILITY, 0)),
            PassiveSkill(listOf(MaximumHealthPassiveSkillCondition(1)), StatusEffectPassiveSkillEffect(StatusEffects.GLOWING, 0)),
            PassiveSkill(listOf(MinimumManaPassiveSkillCondition(11.0)), StatusEffectPassiveSkillEffect(StatusEffects.GLOWING, 0)),
        ),
        RecipeContainer().block { Blocks.GLASS }.recipe { Items.GLASS },
    ),
    PRISMARINE(
        "prismarine", 5, "Prismarinia", "海晶石精プリスマリーニャ", 0xA3D3C7, 0x769A91, 0x769A91, 0x69C4C0,
        listOf(PassiveSkill(listOf(UnderwaterPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 1))),
        RecipeContainer().biome(ConventionalBiomeTags.OCEAN).block { Blocks.PRISMARINE }.recipe { Items.PRISMARINE },
    ),
    IRON_BARS(
        "iron_bars", 4, "Irone Barsia", "鉄格子精イローネバルシャ", 0xFFFFFF, 0xA1A1A3, 0x404040, 0x404040,
        listOf(PassiveSkill(listOf(), MaxHealthPassiveSkillEffect(5.0))),
        RecipeContainer().block { Blocks.IRON_BARS }.recipe { Items.IRON_BARS },
    ),
    PLAINS(
        "plains", 2, "Plainsia", "平原精プラインシャ", 0xB0DF83, 0xD4FF82, 0x86C91C, 0x489F25,
        listOf(
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.PLAINS)), StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.PLAINS), MinimumManaPassiveSkillCondition(7.0)), StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 1)),
        ),
        RecipeContainer().biome(ConventionalBiomeTags.PLAINS),
    ),
    OCEAN(
        "ocean", 3, "Oceania", "海精オツェアーニャ", 0x7DAEF5, 0x1B6CE9, 0x191CF0, 0x004DA5,
        listOf(
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.OCEAN)), StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.OCEAN), MinimumManaPassiveSkillCondition(7.0)), StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 1)),
        ),
        RecipeContainer().biome(ConventionalBiomeTags.OCEAN),
    ),
    TAIGA(
        "taiga", 4, "Taigia", "針葉樹林精タイギャ", 0x5D985E, 0x476545, 0x223325, 0x5A3711,
        listOf(
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.TAIGA)), StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.TAIGA), MinimumManaPassiveSkillCondition(7.0)), StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 1)),
        ),
        RecipeContainer().biome(ConventionalBiomeTags.TAIGA),
    ),
    MOUNTAIN(
        "mountain", 3, "Mountainia", "山精モウンタイニャ", 0x84BF80, 0xB1B0B1, 0x717173, 0xF0F0F0,
        listOf(
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.MOUNTAIN)), StatusEffectPassiveSkillEffect(StatusEffects.JUMP_BOOST, 0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.MOUNTAIN), MinimumManaPassiveSkillCondition(7.0)), StatusEffectPassiveSkillEffect(StatusEffects.JUMP_BOOST, 1)),
        ),
        RecipeContainer().biome(ConventionalBiomeTags.MOUNTAIN),
    ),
    FOREST(
        "forest", 3, "Forestia", "森精フォレスチャ", 0x8EBF7A, 0x7B9C62, 0x89591D, 0x2E6E14,
        listOf(
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.FOREST)), StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.FOREST), MinimumManaPassiveSkillCondition(7.0)), StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 1)),
        ),
        RecipeContainer().biome(ConventionalBiomeTags.FOREST),
    ),
    DESERT(
        "desert", 2, "Desertia", "砂漠精デセルチャ", 0xF9F0C8, 0xDDD6A5, 0xD6CE9D, 0x656054,
        listOf(
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.DESERT), SunshinePassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.RESISTANCE, 0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.DESERT), SunshinePassiveSkillCondition(), MinimumManaPassiveSkillCondition(7.0)), StatusEffectPassiveSkillEffect(StatusEffects.FIRE_RESISTANCE, 0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.DESERT), MoonlightPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.DESERT), MoonlightPassiveSkillCondition(), MinimumManaPassiveSkillCondition(7.0)), StatusEffectPassiveSkillEffect(StatusEffects.STRENGTH, 1)),
        ),
        RecipeContainer().biome(ConventionalBiomeTags.DESERT),
    ),
    AVALON(
        "avalon", 8, "Avalonia", "阿瓦隆精アヴァローニャ", 0xFFE4CA, 0xE1FFCE, 0xD0FFE6, 0xFFCAFF,
        listOf(
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.MUSHROOM)), StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 1)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.FLORAL)), StatusEffectPassiveSkillEffect(StatusEffects.LUCK, 1)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.MUSHROOM)), StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 1)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.FLORAL)), StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 1)),
        ),
        RecipeContainer().always(), // TODO イベント終了時コモン枠除去
    ),
    VOID(
        "void", 11, "Voidia", "奈落精ヴォイジャ", 0x000000, 0x000000, 0x000000, 0xB1B1B1,
        listOf(
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_THE_END)), StatusEffectPassiveSkillEffect(StatusEffects.SLOW_FALLING, 0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_THE_END)), StatusEffectPassiveSkillEffect(StatusEffects.REGENERATION, 0)),
            PassiveSkill(listOf(BiomePassiveSkillCondition(ConventionalBiomeTags.IN_THE_END)), StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 2)),
        ),
        RecipeContainer().biome(ConventionalBiomeTags.IN_THE_END),
    ),
    NIGHT(
        "night", 7, "Nightia", "夜精ニグチャ", 0xFFE260, 0x2C2C2E, 0x0E0E10, 0x2D4272,
        listOf(
            PassiveSkill(listOf(NightPassiveSkillCondition()), StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 0)),
        ),
        RecipeContainer().overworld(),
    ),
    TIME(
        "time", 12, "Timia", "時精ティーミャ", 0xCDFFBF, 0xD5DEBC, 0xD8DEA7, 0x8DD586,
        listOf(
            PassiveSkill(listOf(), StatusEffectPassiveSkillEffect(StatusEffects.SPEED, 1)),
            PassiveSkill(listOf(), MovementSpeedPassiveSkillEffect(0.20)),
            PassiveSkill(listOf(), StatusEffectPassiveSkillEffect(StatusEffects.HASTE, 1)),
        ),
        RecipeContainer().always(),
    ),
    GRAVITY(
        "gravity", 12, "Gravitia", "重力精グラヴィーチャ", 0xC2A7F2, 0x3600FF, 0x2A00B1, 0x110047,
        listOf(
            PassiveSkill(listOf(), StatusEffectPassiveSkillEffect(StatusEffects.SLOW_FALLING, 0)),
            PassiveSkill(listOf(), AttackDamagePassiveSkillEffect(2.0)),
        ),
        RecipeContainer().always(),
    ),
    DREAM(
        "dream", 10, "Dreamia", "夢幻精ドレアミャ", 0xBFC3FF, 0xD1BAD8, 0xDBBCD4, 0x848ACC,
        listOf(
            PassiveSkill(listOf(), LuckPassiveSkillEffect(1.0)),
            PassiveSkill(listOf(), RegenerationPassiveSkillEffect(0.1)),
            PassiveSkill(listOf(), ManaPassiveSkillEffect(0.5)),
            // TODO 夢のエフェクトが見えるようになる
        ),
        RecipeContainer().always(),
    ),
    ;

    val motif = Identifier(MirageFairy2023.modId, motifPath)
    val fairy: Fairy = object : Fairy {
        override val motif get() = this@FairyCard.motif
        override val item get() = this@FairyCard()
        override val rare get() = this@FairyCard.rare
    }


    // 妖精レシピ

    class RecipeContainer {
        val recipes = mutableListOf<Recipe>()
    }

    interface Recipe {
        fun getWikiString(): String
        fun init(initializationScope: InitializationScope, fairyCard: FairyCard)
    }

}

private fun FairyCard.RecipeContainer.always() = this.also {
    this.recipes += object : FairyCard.Recipe {
        override fun getWikiString() = "コモン：全世界"
        override fun init(initializationScope: InitializationScope, fairyCard: FairyCard) {
            initializationScope.onRegisterRecipes {
                MirageFlourItem.COMMON_FAIRY_LIST += CommonFairyEntry(fairyCard.fairy) { true }
            }
        }
    }
}

private fun FairyCard.RecipeContainer.overworld() = this.also {
    this.recipes += object : FairyCard.Recipe {
        override fun getWikiString() = "コモン：地上世界"
        override fun init(initializationScope: InitializationScope, fairyCard: FairyCard) {
            initializationScope.onRegisterRecipes {
                MirageFlourItem.COMMON_FAIRY_LIST += CommonFairyEntry(fairyCard.fairy) { it.world.dimension.natural }
            }
        }
    }
}

private fun FairyCard.RecipeContainer.biome(biomeTag: TagKey<Biome>) = this.also {
    this.recipes += object : FairyCard.Recipe {
        override fun getWikiString() = "コモン：${text { translate(biomeTag.id.toTranslationKey(BiomePassiveSkillCondition.keyPrefix)) }.string}"
        override fun init(initializationScope: InitializationScope, fairyCard: FairyCard) {
            initializationScope.onRegisterRecipes {
                MirageFlourItem.COMMON_FAIRY_LIST += CommonFairyEntry(fairyCard.fairy) { it.world.getBiome(it.blockPos).isIn(biomeTag) }
            }
        }
    }
}

private fun FairyCard.RecipeContainer.biome(biome: RegistryKey<Biome>) = this.also {
    this.recipes += object : FairyCard.Recipe {
        override fun getWikiString() = "コモン：${text { translate(biome.value.toTranslationKey("biome")) }.string}"
        override fun init(initializationScope: InitializationScope, fairyCard: FairyCard) {
            initializationScope.onRegisterRecipes {
                MirageFlourItem.COMMON_FAIRY_LIST += CommonFairyEntry(fairyCard.fairy) { it.world.getBiome(it.blockPos) === biome }
            }
        }
    }
}

private fun FairyCard.RecipeContainer.block(blockSupplier: () -> Block) = this.also {
    this.recipes += object : FairyCard.Recipe {
        override fun getWikiString() = "夢：ブロック：${blockSupplier().name.string}"
        override fun init(initializationScope: InitializationScope, fairyCard: FairyCard) {
            initializationScope.onRegisterRecipes {
                DreamCatcherItem.BLOCK_FAIRY_RELATION_LIST += BlockFairyRelation(blockSupplier(), fairyCard.fairy)
            }
        }
    }
}

private fun FairyCard.RecipeContainer.entityType(entityTypeSupplier: () -> EntityType<*>) = this.also {
    this.recipes += object : FairyCard.Recipe {
        override fun getWikiString() = "夢：エンティティ：${entityTypeSupplier().name.string}"
        override fun init(initializationScope: InitializationScope, fairyCard: FairyCard) {
            initializationScope.onRegisterRecipes {
                DreamCatcherItem.ENTITY_TYPE_FAIRY_RELATION_LIST += EntityTypeFairyRelation(entityTypeSupplier(), fairyCard.fairy)
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
                    .criterion(inputItem)
                    .group(fairyCard())
                    .offerTo(it, "fairy/" concat fairyCard.motif)
            }
        }
    }
}

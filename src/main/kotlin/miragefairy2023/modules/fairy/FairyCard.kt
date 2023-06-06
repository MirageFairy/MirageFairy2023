package miragefairy2023.modules.fairy

import miragefairy2023.MirageFairy2023
import miragefairy2023.api.Fairy
import miragefairy2023.api.PassiveSkill
import miragefairy2023.modules.DemonItemCard
import miragefairy2023.modules.ToolMaterialCard
import miragefairy2023.modules.invoke
import miragefairy2023.modules.passiveskill.air
import miragefairy2023.modules.passiveskill.always
import miragefairy2023.modules.passiveskill.attackDamage
import miragefairy2023.modules.passiveskill.biome
import miragefairy2023.modules.passiveskill.collection
import miragefairy2023.modules.passiveskill.combustion
import miragefairy2023.modules.passiveskill.experience
import miragefairy2023.modules.passiveskill.food
import miragefairy2023.modules.passiveskill.hasHoe
import miragefairy2023.modules.passiveskill.inRain
import miragefairy2023.modules.passiveskill.inVillage
import miragefairy2023.modules.passiveskill.indoor
import miragefairy2023.modules.passiveskill.luck
import miragefairy2023.modules.passiveskill.mana
import miragefairy2023.modules.passiveskill.maxHealth
import miragefairy2023.modules.passiveskill.maximumFoodLevel
import miragefairy2023.modules.passiveskill.maximumHealth
import miragefairy2023.modules.passiveskill.maximumLevel
import miragefairy2023.modules.passiveskill.maximumLightLevel
import miragefairy2023.modules.passiveskill.minimumFoodLevel
import miragefairy2023.modules.passiveskill.minimumLightLevel
import miragefairy2023.modules.passiveskill.minimumMana
import miragefairy2023.modules.passiveskill.moonlight
import miragefairy2023.modules.passiveskill.movementSpeed
import miragefairy2023.modules.passiveskill.night
import miragefairy2023.modules.passiveskill.onFire
import miragefairy2023.modules.passiveskill.outdoor
import miragefairy2023.modules.passiveskill.overworld
import miragefairy2023.modules.passiveskill.passiveSkills
import miragefairy2023.modules.passiveskill.regeneration
import miragefairy2023.modules.passiveskill.shade
import miragefairy2023.modules.passiveskill.statusEffect
import miragefairy2023.modules.passiveskill.sunshine
import miragefairy2023.modules.passiveskill.telescopeMission
import miragefairy2023.modules.passiveskill.thundering
import miragefairy2023.modules.passiveskill.toolMaterial
import miragefairy2023.modules.passiveskill.underwater
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags
import net.minecraft.block.Blocks
import net.minecraft.entity.EntityType
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Items
import net.minecraft.util.Identifier
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
    val fairyRecipes: FairyRecipes,
) {
    AIR(
        "air", 0, "Airia", "空気精アイリャ", 0xFFBE80, 0xDEFFFF, 0xDEFFFF, 0xB0FFFF,
        passiveSkills {
            movementSpeed(0.30) on overworld() * air()
        },
        FairyRecipes().always().block { Blocks.AIR },
    ),
    LIGHT(
        "light", 3, "Lightia", "光精リグチャ", 0xFFFFD8, 0xFFFFD8, 0xFFFFC5, 0xFFFF00,
        passiveSkills {
            movementSpeed(0.30) on minimumLightLevel(12)
        },
        FairyRecipes().always(),
    ),
    FIRE(
        "fire", 2, "Firia", "火精フィーリャ", 0xFF6C01, 0xF9DFA4, 0xFF7324, 0xFF4000,
        passiveSkills {
            attackDamage(4.0) on onFire()
        },
        FairyRecipes().biome(ConventionalBiomeTags.IN_NETHER).block { Blocks.FIRE },
    ),
    WATER(
        "water", 1, "Wateria", "水精ワテーリャ", 0x5469F2, 0x5985FF, 0x172AD3, 0x2D40F4,
        passiveSkills {
            maxHealth(12.0) on underwater()
        },
        FairyRecipes().overworld().block { Blocks.WATER }.recipe { Items.WATER_BUCKET },
    ),
    LAVA(
        "lava", 4, "Lavia", "溶岩精ラーヴャ", 0xCD4208, 0xEDB54A, 0xCC4108, 0x4C1500,
        passiveSkills {
            attackDamage(2.0) on onFire()
            statusEffect(StatusEffects.STRENGTH, 0) on onFire()
            statusEffect(StatusEffects.RESISTANCE, 0) on onFire()
        },
        FairyRecipes().biome(ConventionalBiomeTags.IN_NETHER).block { Blocks.LAVA }.recipe { Items.LAVA_BUCKET },
    ),
    MOON(
        "moon", 9, "Moonia", "月精モーニャ", 0xD9E4FF, 0x747D93, 0x0C121F, 0x2D4272,
        passiveSkills {
            statusEffect(StatusEffects.NIGHT_VISION, 0, additionalSeconds = 10) on moonlight()
            statusEffect(StatusEffects.REGENERATION, 0) on moonlight()
        },
        FairyRecipes().overworld(),
    ),
    SUN(
        "sun", 10, "Sunia", "太陽精スーニャ", 0xff2f00, 0xff972b, 0xff7500, 0xffe7b2,
        passiveSkills {
            statusEffect(StatusEffects.STRENGTH, 1) on sunshine()
            statusEffect(StatusEffects.REGENERATION, 0) on sunshine()
        },
        FairyRecipes().overworld(),
    ),
    RAIN(
        "rain", 2, "Rainia", "雨精ライニャ", 0xB4FFFF, 0x4D5670, 0x4D5670, 0x2D40F4,
        passiveSkills {
            attackDamage(4.0) on inRain()
        },
        FairyRecipes().overworld(),
    ),
    DIRT(
        "dirt", 1, "Dirtia", "土精ディルチャ", 0xB87440, 0xB9855C, 0x593D29, 0x914A18,
        passiveSkills {
            maxHealth(10.0) on overworld()
        },
        FairyRecipes().overworld().block { Blocks.DIRT }.recipe { Items.DIRT },
    ),
    MYCELIUM(
        "mycelium", 6, "Myceliumia", "菌糸精ミツェリウミャ", 0x8F7E86, 0x8B7071, 0x8B7071, 0x8B6264,
        passiveSkills {
            attackDamage(1.0) on always()
            attackDamage(2.0) on biome(ConventionalBiomeTags.MUSHROOM)
            maxHealth(12.0) on biome(ConventionalBiomeTags.MUSHROOM)
            statusEffect(StatusEffects.REGENERATION, 0) on biome(ConventionalBiomeTags.MUSHROOM) * minimumMana(10.0)
        },
        FairyRecipes().biome(ConventionalBiomeTags.MUSHROOM).block { Blocks.MYCELIUM }.recipe { Items.MYCELIUM },
    ),
    SCULK(
        "sculk", 6, "Sculkia", "幽匿塊精スツルキャ", 0x19222C, 0x023F3D, 0x023F3D, 0x19C0C0,
        passiveSkills {
            maxHealth(8.0) on maximumLightLevel(0)
            attackDamage(3.0) on maximumLightLevel(0)
        },
        FairyRecipes().biome(BiomeKeys.DEEP_DARK).block { Blocks.SCULK }.recipe { Items.SCULK },
    ),
    STONE(
        "stone", 1, "Stonia", "石精ストーニャ", 0x333333, 0x8F8F8F, 0x686868, 0x747474,
        passiveSkills {
            attackDamage(2.0) on toolMaterial(ToolMaterialCard.STONE)
            statusEffect(StatusEffects.RESISTANCE, 0) on toolMaterial(ToolMaterialCard.STONE)
            statusEffect(StatusEffects.RESISTANCE, 1) on toolMaterial(ToolMaterialCard.STONE) * minimumMana(7.0)
        },
        FairyRecipes().overworld().block { Blocks.STONE }.recipe { Items.STONE },
    ),
    DRIPSTONE(
        "dripstone", 3, "Dripstonia", "鍾乳石精ドリプストーニャ", 0xB19C7E, 0xA97F6F, 0xA97F6F, 0xAD7069,
        passiveSkills {
            attackDamage(1.5) on shade()
            maxHealth(6.0) on shade()
        },
        FairyRecipes().biome(BiomeKeys.DRIPSTONE_CAVES).block { Blocks.DRIPSTONE_BLOCK }.recipe { Items.DRIPSTONE_BLOCK },
    ),
    REINFORCED_DEEPSLATE(
        "reinforced_deepslate", 9, "Reinforcede Deepslatia", "強化深層岩精レインフォルツェーデデープスラーチャ", 0x6C7180, 0x5C606C, 0x5C606C, 0xACCF9D,
        passiveSkills {
            maxHealth(10.0) on shade()
        },
        FairyRecipes().block { Blocks.REINFORCED_DEEPSLATE }.recipe { Items.REINFORCED_DEEPSLATE },
    ),
    ANCIENT_DEBRIS(
        "ancient_debris", 8, "Anciente Debrisia", "古代残骸精アンツィエンテデブリーシャ", 0x8F645A, 0x8F645A, 0x885040, 0xD8C2B7,
        passiveSkills {
            attackDamage(1.0) on always()
            attackDamage(1.0) on biome(ConventionalBiomeTags.IN_NETHER)
            maxHealth(2.0) on biome(ConventionalBiomeTags.IN_NETHER)
            luck(2.0) on biome(ConventionalBiomeTags.IN_NETHER)
        },
        FairyRecipes().biome(ConventionalBiomeTags.IN_NETHER).block { Blocks.ANCIENT_DEBRIS }.recipe { Items.ANCIENT_DEBRIS },
    ),
    PURPUR(
        "purpur", 7, "Purpuria", "紫珀精プルプーリャ", 0xCBA8CB, 0xC08AC0, 0xC08AC0, 0xBC68BB,
        passiveSkills {
            collection(0.5) on always()
            attackDamage(2.0) on biome(ConventionalBiomeTags.IN_THE_END)
            maxHealth(4.0) on biome(ConventionalBiomeTags.IN_THE_END)
        },
        FairyRecipes().biome(ConventionalBiomeTags.END_ISLANDS).block { Blocks.PURPUR_BLOCK }.recipe { Items.PURPUR_BLOCK },
    ),
    COPPER(
        "copper", 3, "Copperia", "銅精ツォッペーリャ", 0xF69D7F, 0xF77653, 0xF77653, 0x5DC09A,
        passiveSkills {
            luck(0.2) on always()
            attackDamage(2.5) on thundering()
        },
        FairyRecipes().overworld().block { Blocks.COPPER_BLOCK }.recipe { Items.COPPER_INGOT },
    ),
    IRON(
        "iron", 4, "Ironia", "鉄精イローニャ", 0xA0A0A0, 0xD8D8D8, 0x727272, 0xD8AF93,
        passiveSkills {
            attackDamage(2.0) on toolMaterial(ToolMaterialCard.IRON)
            statusEffect(StatusEffects.STRENGTH, 0) on toolMaterial(ToolMaterialCard.IRON)
            statusEffect(StatusEffects.STRENGTH, 1) on toolMaterial(ToolMaterialCard.IRON) * minimumMana(8.0)
        },
        FairyRecipes().overworld().block { Blocks.IRON_BLOCK }.recipe { Items.IRON_INGOT },
    ),
    GOLD(
        "gold", 6, "Goldia", "金精ゴルジャ", 0xEFE642, 0xF4CC17, 0xF4CC17, 0xFDB61E,
        passiveSkills {
            luck(0.8) on always()
            attackDamage(2.0) on toolMaterial(ToolMaterialCard.GOLD)
            statusEffect(StatusEffects.LUCK, 0) on toolMaterial(ToolMaterialCard.GOLD)
            statusEffect(StatusEffects.LUCK, 1) on toolMaterial(ToolMaterialCard.GOLD) * minimumMana(10.0)
        },
        FairyRecipes().overworld().biome(ConventionalBiomeTags.IN_NETHER).block { Blocks.GOLD_BLOCK }.recipe { Items.GOLD_INGOT },
    ),
    NETHERITE(
        "netherite", 8, "Netheritia", "地獄合金精ネテリーチャ", 0x8F788F, 0x74585B, 0x705558, 0x77302D,
        passiveSkills {
            attackDamage(2.0) on toolMaterial(ToolMaterialCard.NETHERITE)
            statusEffect(StatusEffects.FIRE_RESISTANCE, 0) on toolMaterial(ToolMaterialCard.NETHERITE)
            statusEffect(StatusEffects.HASTE, 1) on toolMaterial(ToolMaterialCard.NETHERITE) * minimumMana(12.0)
        },
        FairyRecipes().biome(ConventionalBiomeTags.IN_NETHER).block { Blocks.NETHERITE_BLOCK }.recipe { Items.NETHERITE_INGOT },
    ),
    MIRANAGITE(
        "miranagite", 5, "Miranagitia", "蒼天石精ミラナギーチャ", 0x4EC5F4, 0x4394D3, 0x004477, 0x0C4CEF,
        passiveSkills {
            luck(0.5) on always()
            statusEffect(StatusEffects.LUCK, 0) on toolMaterial(ToolMaterialCard.MIRANAGITE)
            mana(0.5) on always()
        },
        FairyRecipes().overworld().recipe { DemonItemCard.MIRANAGITE() },
    ),
    AMETHYST(
        "amethyst", 5, "Amethystia", "紫水晶精アメティスチャ", 0xCAA9FF, 0xA974FF, 0x9D60FF, 0xBC92FF,
        passiveSkills {
            luck(0.5) on always()
            attackDamage(0.5) on maximumLightLevel(7)
            luck(2.0) on maximumLightLevel(7)
        },
        FairyRecipes().overworld().block { Blocks.AMETHYST_BLOCK }.recipe { Items.AMETHYST_SHARD },
    ),
    EMERALD(
        "emerald", 6, "Emeraldia", "翠玉精エメラルジャ", 0x9FF9B5, 0x81F99E, 0x17DD62, 0x008A25,
        passiveSkills {
            luck(0.5) on always()
            luck(1.5) on inVillage()
        },
        FairyRecipes().overworld().block { Blocks.EMERALD_BLOCK }.recipe { Items.EMERALD },
    ),
    DIAMOND(
        "diamond", 7, "Diamondia", "金剛石精ディアモンジャ", 0x97FFE3, 0xD1FAF3, 0x70FFD9, 0x30DBBD,
        passiveSkills {
            luck(0.5) on always()
            attackDamage(2.0) on toolMaterial(ToolMaterialCard.DIAMOND)
            statusEffect(StatusEffects.HASTE, 0) on toolMaterial(ToolMaterialCard.DIAMOND)
            statusEffect(StatusEffects.HASTE, 1) on toolMaterial(ToolMaterialCard.DIAMOND) * minimumMana(12.0)
        },
        FairyRecipes().overworld().block { Blocks.DIAMOND_BLOCK }.recipe { Items.DIAMOND },
    ),
    ADAMANTITE(
        "adamantite", 9, "Adamantitia", "精金精アダマンティーチャ", 0xE5B3CA, 0xDB7A9C, 0xDB7A9C, 0xFD2888,
        passiveSkills {
            luck(0.5) on always()
            attackDamage(2.0) on always()
        },
        FairyRecipes().always(), // TODO イベ限解除
    ),
    COAL(
        "coal", 2, "Coalia", "石炭精ツォアーリャ", 0x4C2510, 0x52504C, 0x39352E, 0x150B00,
        passiveSkills {
            maxHealth(4.0) on always()
            luck(2.0) on onFire()
        },
        FairyRecipes().overworld().biome(ConventionalBiomeTags.IN_NETHER).block { Blocks.COAL_BLOCK }.recipe { Items.COAL },
    ),
    REDSTONE(
        "redstone", 4, "Redstonia", "赤石精レドストーニャ", 0xFF5959, 0xFF0000, 0xCD0000, 0xBA0000,
        passiveSkills {
            luck(0.5) on indoor()
            attackDamage(1.0) on indoor()
            maxHealth(2.0) on indoor()
        },
        FairyRecipes().overworld().block { Blocks.REDSTONE_BLOCK }.recipe { Items.REDSTONE },
    ),
    LAPIS_LAZULI(
        "lapis_lazuli", 5, "Lapise Lazulia", "瑠璃石精ラピセラズーリャ", 0x77A2FF, 0x3064D3, 0x3064D3, 0x3976F9,
        passiveSkills {
            luck(0.5) on always()
            luck(1.5) on overworld()
        },
        FairyRecipes().overworld().block { Blocks.LAPIS_BLOCK }.recipe { Items.LAPIS_LAZULI },
    ),
    GLOWSTONE(
        "glowstone", 6, "Glowstonia", "蛍光石精グロウストーニャ", 0xEFC298, 0xEAA463, 0xEAA463, 0xEADD52,
        passiveSkills {
            statusEffect(StatusEffects.GLOWING, 0) on always()
            attackDamage(1.5) on biome(ConventionalBiomeTags.IN_NETHER)
        },
        FairyRecipes().biome(ConventionalBiomeTags.IN_NETHER).block { Blocks.GLOWSTONE }.recipe { Items.GLOWSTONE_DUST },
    ),
    OBSIDIAN(
        "obsidian", 5, "Obsidiania", "黒耀石精オブシディアーニャ", 0x775599, 0x6029B3, 0x2E095E, 0x0F0033,
        passiveSkills {
            statusEffect(StatusEffects.RESISTANCE, 0) on always()
            statusEffect(StatusEffects.RESISTANCE, 1) on minimumMana(9.0)
        },
        FairyRecipes().overworld().biome(ConventionalBiomeTags.IN_THE_END).block { Blocks.OBSIDIAN }.recipe { Items.OBSIDIAN },
    ),
    FISH(
        "fish", 2, "Fishia", "魚精フィーシャ", 0x6B9F93, 0x5A867C, 0x43655D, 0xADBEDB,
        passiveSkills {
            statusEffect(StatusEffects.NIGHT_VISION, 0, additionalSeconds = 10) on underwater()
            statusEffect(StatusEffects.WATER_BREATHING, 0) on underwater() * minimumMana(10.0)
        },
        FairyRecipes().overworld().recipe { Items.COD }.entityType { EntityType.COD }, // TODO 魚精は希釈で得る
    ),
    CLOWNFISH(
        "clownfish", 7, "Clownfishia", "隈之実精ツロウンフィーシャ", 0xE46A22, 0xF46F20, 0xA94B1D, 0xFFDBC5,
        passiveSkills {
            statusEffect(StatusEffects.WATER_BREATHING, 0) on underwater() * minimumLightLevel(4)
            statusEffect(StatusEffects.WATER_BREATHING, 0) on underwater() * minimumMana(10.0)
        },
        FairyRecipes().overworld().recipe { Items.TROPICAL_FISH }.entityType { EntityType.TROPICAL_FISH },
    ),
    SPONGE(
        "sponge", 4, "Spongia", "海綿精スポンギャ", 0xEADF67, 0xB1A947, 0xB1A947, 0xDBCD5A,
        passiveSkills {
            attackDamage(2.0) on underwater()
            statusEffect(StatusEffects.REGENERATION, 0) on underwater() * minimumMana(10.0)
        },
        FairyRecipes().biome(ConventionalBiomeTags.OCEAN).block { Blocks.SPONGE }.recipe { Items.SPONGE },
    ),
    PLAYER(
        "player", 5, "Playeria", "人精プライェーリャ", 0xB58D63, 0x00AAAA, 0x322976, 0x4B3422,
        passiveSkills {
            experience(0.05) on maximumLevel(29)
        },
        FairyRecipes().always().entityType { EntityType.PLAYER },
    ),
    ENDERMAN(
        "enderman", 6, "Endermania", "終界人精エンデルマーニャ", 0x000000, 0x161616, 0x161616, 0xEF84FA,
        passiveSkills {
            collection(1.0) on always()
        },
        FairyRecipes().overworld().biome(ConventionalBiomeTags.IN_NETHER).biome(ConventionalBiomeTags.IN_THE_END).entityType { EntityType.ENDERMAN },
    ),
    WARDEN(
        "warden", 7, "Wardenia", "監守者精ワルデーニャ", 0x0A3135, 0xCFCFA4, 0xA0AA7A, 0x2CD0CA,
        passiveSkills {
            statusEffect(StatusEffects.STRENGTH, 1) on maximumLightLevel(0)
            attackDamage(2.0) on maximumLightLevel(0)
        },
        FairyRecipes().biome(BiomeKeys.DEEP_DARK).entityType { EntityType.WARDEN },
    ),
    ZOMBIE(
        "zombie", 2, "Zombia", "硬屍精ゾンビャ", 0x2B4219, 0x00AAAA, 0x322976, 0x2B4219,
        passiveSkills {
            attackDamage(2.0) on maximumFoodLevel(6)
            attackDamage(1.0) on shade()
        },
        FairyRecipes().overworld().entityType { EntityType.ZOMBIE },
    ),
    SKELETON_HORSE(
        "skeleton_horse", 6, "Skeletone Horsia", "骸骨馬精スケレトーネホルシャ", 0xA1A1A1, 0xD4D4D4, 0x757575, 0xD5D5D5,
        passiveSkills {
            movementSpeed(0.30) on maximumFoodLevel(6)
            attackDamage(1.0) on inRain()
        },
        FairyRecipes().overworld().entityType { EntityType.SKELETON_HORSE },
    ),
    WITHER(
        "wither", 8, "Witheria", "枯精ウィテーリャ", 0x181818, 0x3C3C3C, 0x141414, 0x557272,
        passiveSkills {
            statusEffect(StatusEffects.STRENGTH, 1) on maximumFoodLevel(6)
            statusEffect(StatusEffects.JUMP_BOOST, 1) on maximumFoodLevel(6)
            statusEffect(StatusEffects.HASTE, 0) on maximumFoodLevel(6)
        },
        FairyRecipes().entityType { EntityType.WITHER },
    ),
    BLAZE(
        "blaze", 7, "Blazia", "烈炎精ブラージャ", 0xE7DA21, 0xCB6E06, 0xB44500, 0xFF8025,
        passiveSkills {
            attackDamage(2.0) on onFire()
            combustion() on statusEffect(StatusEffects.FIRE_RESISTANCE)
        },
        FairyRecipes().biome(ConventionalBiomeTags.IN_NETHER).entityType { EntityType.BLAZE },
    ),
    WHEAT(
        "wheat", 3, "Wheatia", "麦精ウェアーチャ", 0xD8BF7F, 0xDBBB65, 0xDBBB65, 0x896D20,
        passiveSkills {
            regeneration(0.1) on minimumFoodLevel(12)
            regeneration(0.3) on food { Items.BREAD }
            attackDamage(0.5) on food { Items.BREAD }
        },
        FairyRecipes().overworld().block { Blocks.HAY_BLOCK }.recipe { Items.WHEAT },
    ),
    CARROT(
        "carrot", 4, "Carrotia", "人参精ツァッローチャ", 0xF98D10, 0xFD7F11, 0xE3710F, 0x248420,
        passiveSkills {
            regeneration(0.1) on minimumFoodLevel(12)
            statusEffect(StatusEffects.NIGHT_VISION, 0, additionalSeconds = 10) on food { Items.CARROT }
        },
        FairyRecipes().overworld().block { Blocks.CARROTS }.recipe { Items.CARROT },
    ),
    POTATO(
        "potato", 4, "Potatia", "芋精ポターチャ", 0xEAC278, 0xE7B456, 0xE7B456, 0x248420,
        passiveSkills {
            regeneration(0.1) on minimumFoodLevel(12)
            regeneration(0.2) on food { Items.BAKED_POTATO }
            attackDamage(1.0) on food { Items.BAKED_POTATO }
        },
        FairyRecipes().overworld().block { Blocks.POTATOES }.recipe { Items.POTATO },
    ),
    POISONOUS_POTATO(
        "poisonous_potato", 5, "Poisonouse Potatia", "悪芋精ポイソノウセポターチャ", 0xCFE661, 0xE7B456, 0xE7B456, 0x61B835,
        passiveSkills {
            attackDamage(1.0) on minimumFoodLevel(12)
            attackDamage(2.0) on food { Items.POISONOUS_POTATO }
        },
        FairyRecipes().overworld().block { Blocks.POTATOES }.recipe { Items.POISONOUS_POTATO },
    ),
    BEETROOT(
        "beetroot", 4, "Beetrootia", "火焔菜精ベートローチャ", 0xC1727C, 0xA74D55, 0x96383D, 0x01A900,
        passiveSkills {
            regeneration(0.1) on minimumFoodLevel(12)
            attackDamage(2.0) on food { Items.BEETROOT }
        },
        FairyRecipes().overworld().block { Blocks.BEETROOTS }.recipe { Items.BEETROOT },
    ),
    MELON(
        "melon", 4, "Melonia", "西瓜精メローニャ", 0xFF5440, 0xA6EE63, 0x195612, 0x01A900,
        passiveSkills {
            regeneration(0.1) on minimumFoodLevel(12)
            regeneration(0.4) on food { Items.MELON_SLICE }
        },
        FairyRecipes().biome(ConventionalBiomeTags.JUNGLE).block { Blocks.MELON }.recipe { Items.MELON },
    ),
    APPLE(
        "apple", 4, "Applia", "林檎精アップーリャ", 0xFF755D, 0xFF564E, 0xFF0000, 0x01A900,
        passiveSkills {
            regeneration(0.1) on minimumFoodLevel(12)
            statusEffect(StatusEffects.RESISTANCE, 0) on food { Items.APPLE }
        },
        FairyRecipes().overworld().recipe { Items.APPLE },
    ),
    WOOD(
        "wood", 1, "Woodia", "木精ウォージャ", 0xE7C697, 0xAD8232, 0xAD8232, 0x8B591C,
        passiveSkills {
            attackDamage(2.0) on toolMaterial(ToolMaterialCard.WOOD)
            statusEffect(StatusEffects.SPEED, 0) on toolMaterial(ToolMaterialCard.WOOD)
            statusEffect(StatusEffects.SPEED, 1) on toolMaterial(ToolMaterialCard.WOOD) * minimumMana(7.0)
        },
        FairyRecipes().overworld().block { Blocks.OAK_PLANKS }.recipe { Items.OAK_PLANKS },
    ),
    SPRUCE(
        "spruce", 6, "Sprucia", "松精スプルーツァ", 0x795C36, 0x583E1F, 0x23160A, 0x4C784C,
        passiveSkills {
            attackDamage(1.0) on biome(ConventionalBiomeTags.FOREST)
            attackDamage(1.0) on biome(ConventionalBiomeTags.TAIGA)
        },
        FairyRecipes().biome(ConventionalBiomeTags.TAIGA).block { Blocks.SPRUCE_SAPLING }.recipe { Items.SPRUCE_SAPLING },
    ),
    HOE(
        "hoe", 3, "Hia", "鍬精ヒャ", 0xFFFFFF, 0xFFC48E, 0x47FF00, 0xFFFFFF,
        passiveSkills {
            statusEffect(StatusEffects.HASTE, 0) on hasHoe()
            luck(3.0) on hasHoe()
        },
        FairyRecipes().recipe { Items.STONE_HOE },
    ),
    CRAFTING_TABLE(
        "crafting_table", 3, "Craftinge Tablia", "作業台精ツラフティンゲターブリャ", 0xFFFFFF, 0xFFBB9A, 0xFFC980, 0x000000,
        passiveSkills {
            luck(2.0) on indoor()
        },
        FairyRecipes().block { Blocks.CRAFTING_TABLE }.recipe { Items.CRAFTING_TABLE },
    ),
    ANVIL(
        "anvil", 4, "Anvilia", "金床精アンヴィーリャ", 0xFFFFFF, 0xA9A9A9, 0x909090, 0xA86F18,
        passiveSkills {
            attackDamage(2.0) on indoor()
        },
        FairyRecipes().block { Blocks.ANVIL }.recipe { Items.ANVIL },
    ),
    ENCHANTING_TABLE(
        "enchanting_table", 6, "Enchantinge Tablia", "付魔台精エンキャンティンゲターブリャ", 0x472F65, 0xCE2828, 0xCE2828, 0x7BFFDD,
        passiveSkills {
            experience(0.03) on indoor() * maximumLevel(19)
            experience(0.03) on indoor() * maximumLevel(29)
        },
        FairyRecipes().block { Blocks.ENCHANTING_TABLE }.recipe { Items.ENCHANTING_TABLE },
    ),
    HOPPER(
        "hopper", 3, "Hopperia", "漏斗精ホッペーリャ", 0xFFFFFF, 0x797979, 0x646464, 0x5A5A5A,
        passiveSkills {
            collection(1.5) on indoor()
        },
        FairyRecipes().block { Blocks.HOPPER }.recipe { Items.HOPPER },
    ),
    BEACON(
        "beacon", 11, "Beaconia", "信標精ベアツォーニャ", 0x97FFE3, 0x6029B3, 0x2E095E, 0xD4EAE6,
        passiveSkills {
            statusEffect(StatusEffects.SPEED, 0) on outdoor()
            statusEffect(StatusEffects.HASTE, 0) on outdoor()
            statusEffect(StatusEffects.RESISTANCE, 0) on outdoor()
            statusEffect(StatusEffects.JUMP_BOOST, 1) on outdoor()
            statusEffect(StatusEffects.STRENGTH, 0) on outdoor()
            statusEffect(StatusEffects.REGENERATION, 0) on outdoor()
        },
        FairyRecipes().block { Blocks.BEACON }.recipe { Items.BEACON },
    ),
    GLASS(
        "glass", 4, "Glassia", "硝子精グラッシャ", 0xFFFFFF, 0xEFF5FF, 0xE8EDF5, 0xADE0E9,
        passiveSkills {
            statusEffect(StatusEffects.INVISIBILITY, 0) on maximumHealth(1)
            statusEffect(StatusEffects.INVISIBILITY, 0) on minimumMana(11.0)
            statusEffect(StatusEffects.GLOWING, 0) on maximumHealth(1)
            statusEffect(StatusEffects.GLOWING, 0) on minimumMana(11.0)
        },
        FairyRecipes().block { Blocks.GLASS }.recipe { Items.GLASS },
    ),
    PRISMARINE(
        "prismarine", 5, "Prismarinia", "海晶石精プリスマリーニャ", 0xA3D3C7, 0x769A91, 0x769A91, 0x69C4C0,
        passiveSkills {
            statusEffect(StatusEffects.RESISTANCE, 1) on underwater()
        },
        FairyRecipes().biome(ConventionalBiomeTags.OCEAN).block { Blocks.PRISMARINE }.recipe { Items.PRISMARINE },
    ),
    IRON_BARS(
        "iron_bars", 4, "Irone Barsia", "鉄格子精イローネバルシャ", 0xFFFFFF, 0xA1A1A3, 0x404040, 0x404040,
        passiveSkills {
            maxHealth(5.0) on always()
        },
        FairyRecipes().block { Blocks.IRON_BARS }.recipe { Items.IRON_BARS },
    ),
    PLAINS(
        "plains", 2, "Plainsia", "平原精プラインシャ", 0xB0DF83, 0xD4FF82, 0x86C91C, 0x489F25,
        passiveSkills {
            statusEffect(StatusEffects.SPEED, 0) on biome(ConventionalBiomeTags.PLAINS)
            statusEffect(StatusEffects.SPEED, 1) on biome(ConventionalBiomeTags.PLAINS) * minimumMana(7.0)
        },
        FairyRecipes().biome(ConventionalBiomeTags.PLAINS),
    ),
    OCEAN(
        "ocean", 3, "Oceania", "海精オツェアーニャ", 0x7DAEF5, 0x1B6CE9, 0x191CF0, 0x004DA5,
        passiveSkills {
            statusEffect(StatusEffects.LUCK, 0) on biome(ConventionalBiomeTags.OCEAN)
            statusEffect(StatusEffects.LUCK, 1) on biome(ConventionalBiomeTags.OCEAN) * minimumMana(7.0)
        },
        FairyRecipes().biome(ConventionalBiomeTags.OCEAN),
    ),
    TAIGA(
        "taiga", 4, "Taigia", "針葉樹林精タイギャ", 0x5D985E, 0x476545, 0x223325, 0x5A3711,
        passiveSkills {
            statusEffect(StatusEffects.STRENGTH, 0) on biome(ConventionalBiomeTags.TAIGA)
            statusEffect(StatusEffects.STRENGTH, 1) on biome(ConventionalBiomeTags.TAIGA) * minimumMana(7.0)
        },
        FairyRecipes().biome(ConventionalBiomeTags.TAIGA),
    ),
    MOUNTAIN(
        "mountain", 3, "Mountainia", "山精モウンタイニャ", 0x84BF80, 0xB1B0B1, 0x717173, 0xF0F0F0,
        passiveSkills {
            statusEffect(StatusEffects.JUMP_BOOST, 0) on biome(ConventionalBiomeTags.MOUNTAIN)
            statusEffect(StatusEffects.JUMP_BOOST, 1) on biome(ConventionalBiomeTags.MOUNTAIN) * minimumMana(7.0)
        },
        FairyRecipes().biome(ConventionalBiomeTags.MOUNTAIN),
    ),
    FOREST(
        "forest", 3, "Forestia", "森精フォレスチャ", 0x8EBF7A, 0x7B9C62, 0x89591D, 0x2E6E14,
        passiveSkills {
            statusEffect(StatusEffects.RESISTANCE, 0) on biome(ConventionalBiomeTags.FOREST)
            statusEffect(StatusEffects.RESISTANCE, 1) on biome(ConventionalBiomeTags.FOREST) * minimumMana(7.0)
        },
        FairyRecipes().biome(ConventionalBiomeTags.FOREST),
    ),
    DESERT(
        "desert", 2, "Desertia", "砂漠精デセルチャ", 0xF9F0C8, 0xDDD6A5, 0xD6CE9D, 0x656054,
        passiveSkills {
            statusEffect(StatusEffects.RESISTANCE, 0) on biome(ConventionalBiomeTags.DESERT) * sunshine()
            statusEffect(StatusEffects.FIRE_RESISTANCE, 0) on biome(ConventionalBiomeTags.DESERT) * sunshine() * minimumMana(7.0)
            statusEffect(StatusEffects.STRENGTH, 0) on biome(ConventionalBiomeTags.DESERT) * moonlight()
            statusEffect(StatusEffects.STRENGTH, 1) on biome(ConventionalBiomeTags.DESERT) * moonlight() * minimumMana(7.0)
        },
        FairyRecipes().biome(ConventionalBiomeTags.DESERT),
    ),
    AVALON(
        "avalon", 8, "Avalonia", "阿瓦隆精アヴァローニャ", 0xFFE4CA, 0xE1FFCE, 0xD0FFE6, 0xFFCAFF,
        passiveSkills {
            statusEffect(StatusEffects.LUCK, 1) on biome(ConventionalBiomeTags.MUSHROOM)
            statusEffect(StatusEffects.LUCK, 1) on biome(ConventionalBiomeTags.FLORAL)
            statusEffect(StatusEffects.REGENERATION, 1) on biome(ConventionalBiomeTags.MUSHROOM)
            statusEffect(StatusEffects.REGENERATION, 1) on biome(ConventionalBiomeTags.FLORAL)
        },
        FairyRecipes().always(), // TODO イベント終了時コモン枠除去
    ),
    VOID(
        "void", 11, "Voidia", "奈落精ヴォイジャ", 0x000000, 0x000000, 0x000000, 0xB1B1B1,
        passiveSkills {
            statusEffect(StatusEffects.SLOW_FALLING, 0) on biome(ConventionalBiomeTags.IN_THE_END)
            statusEffect(StatusEffects.REGENERATION, 0) on biome(ConventionalBiomeTags.IN_THE_END)
            statusEffect(StatusEffects.SPEED, 2) on biome(ConventionalBiomeTags.IN_THE_END)
        },
        FairyRecipes().biome(ConventionalBiomeTags.IN_THE_END),
    ),
    NIGHT(
        "night", 7, "Nightia", "夜精ニグチャ", 0xFFE260, 0x2C2C2E, 0x0E0E10, 0x2D4272,
        passiveSkills {
            statusEffect(StatusEffects.SPEED, 0) on night()
        },
        FairyRecipes().overworld(),
    ),
    TIME(
        "time", 12, "Timia", "時精ティーミャ", 0xCDFFBF, 0xD5DEBC, 0xD8DEA7, 0x8DD586,
        passiveSkills {
            statusEffect(StatusEffects.SPEED, 1) on always()
            movementSpeed(0.20) on always()
            statusEffect(StatusEffects.HASTE, 1) on always()
        },
        FairyRecipes().always(),
    ),
    GRAVITY(
        "gravity", 12, "Gravitia", "重力精グラヴィーチャ", 0xC2A7F2, 0x3600FF, 0x2A00B1, 0x110047,
        passiveSkills {
            statusEffect(StatusEffects.SLOW_FALLING, 0) on always()
            attackDamage(2.0) on always()
        },
        FairyRecipes().always(),
    ),
    DREAM(
        "dream", 10, "Dreamia", "夢幻精ドレアミャ", 0xBFC3FF, 0xD1BAD8, 0xDBBCD4, 0x848ACC,
        passiveSkills {
            luck(1.0) on always()
            regeneration(0.1) on always()
            mana(0.5) on always()
            // TODO 夢のエフェクトが見えるようになる
        },
        FairyRecipes().always(),
    ),
    MINA(
        "mina", 5, "Minia", "銀子精ミーニャ", 0xFFFF84, 0xFFFF00, 0xFFFF00, 0xFFC800,
        passiveSkills {
            mana(1.0) on telescopeMission()
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

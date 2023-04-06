package miragefairy2023.modules.passiveskill

import miragefairy2023.MirageFairy2023
import miragefairy2023.util.Translation
import miragefairy2023.util.text
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterial
import net.minecraft.item.ToolMaterials
import net.minecraft.tag.FluidTags
import net.minecraft.tag.TagKey
import net.minecraft.util.math.BlockPos
import net.minecraft.world.biome.Biome

class OverworldPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.overworld", "Overworld", "地上世界")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity) = player.world.dimension.natural
}

class AirPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.air", "Air", "空気")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity): Boolean {
        val blockState = player.world.getBlockState(BlockPos(player.eyePos))
        return !blockState.isOpaque && blockState.fluidState.isEmpty
    }
}

class UnderwaterPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.underwater", "Underwater", "水中")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity): Boolean {
        val blockState = player.world.getBlockState(BlockPos(player.eyePos))
        return blockState.fluidState.isIn(FluidTags.WATER)
    }
}

class DaytimePassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.daytime", "Daytime", "昼")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity): Boolean {
        player.world.calculateAmbientDarkness()
        return player.world.isDay
    }
}

class NightPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.night", "Night", "夜")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity): Boolean {
        player.world.calculateAmbientDarkness()
        return player.world.isNight
    }
}

class SunshinePassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.sunshine", "Sunshine", "日光")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity): Boolean {
        player.world.calculateAmbientDarkness()
        return player.world.isDay && !player.world.hasRain(player.blockPos) && player.world.isSkyVisible(BlockPos(player.eyePos))
    }
}

class MoonlightPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.moonlight", "Moonlight", "月光")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity): Boolean {
        player.world.calculateAmbientDarkness()
        return player.world.isNight && !player.world.hasRain(player.blockPos) && player.world.isSkyVisible(BlockPos(player.eyePos))
    }
}

class ShadePassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.shade", "Shade", "日陰")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity): Boolean {
        player.world.calculateAmbientDarkness()
        return !(player.world.isDay && !player.world.hasRain(player.blockPos) && player.world.isSkyVisible(BlockPos(player.eyePos)))
    }
}

class MinimumLightLevelPassiveSkillCondition(private val lightLevel: Int) : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.minimum_light_level", "Light>=%s", "明るさ%s以上")
    }

    override fun getText() = text { key(lightLevel) }
    override fun test(player: PlayerEntity) = player.world.getLightLevel(BlockPos(player.eyePos)) >= lightLevel
}

class MaximumLightLevelPassiveSkillCondition(private val lightLevel: Int) : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.maximum_light_level", "Light<=%s", "明るさ%s以下")
    }

    override fun getText() = text { key(lightLevel) }
    override fun test(player: PlayerEntity) = player.world.getLightLevel(BlockPos(player.eyePos)) <= lightLevel
}

class InRainPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.in_rain", "Rain", "雨")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity) = player.world.hasRain(player.blockPos) && player.world.isSkyVisible(BlockPos(player.eyePos))
}

class BiomePassiveSkillCondition(private val biomeTag: TagKey<Biome>) : PassiveSkillCondition {
    companion object {
        val keyPrefix = "${MirageFairy2023.modId}.passive_skill.condition.biome"
    }

    enum class Key(val translation: Translation) {
        FOREST(Translation(ConventionalBiomeTags.FOREST.id.toTranslationKey(keyPrefix), "Forest", "森林")),
        TAIGA(Translation(ConventionalBiomeTags.TAIGA.id.toTranslationKey(keyPrefix), "Taiga", "タイガ")),
        DESERT(Translation(ConventionalBiomeTags.DESERT.id.toTranslationKey(keyPrefix), "Desert", "砂漠")),
        MUSHROOM(Translation(ConventionalBiomeTags.MUSHROOM.id.toTranslationKey(keyPrefix), "Mushroom Island", "キノコ島")),
        FLORAL(Translation(ConventionalBiomeTags.FLORAL.id.toTranslationKey(keyPrefix), "Floral", "花畑")),
        IN_THE_END(Translation(ConventionalBiomeTags.IN_THE_END.id.toTranslationKey(keyPrefix), "The End", "エンド")),
        PLAINS(Translation(ConventionalBiomeTags.PLAINS.id.toTranslationKey(keyPrefix), "Plains", "平原")),
        OCEAN(Translation(ConventionalBiomeTags.OCEAN.id.toTranslationKey(keyPrefix), "Ocean", "海洋")),
        MOUNTAIN(Translation(ConventionalBiomeTags.MOUNTAIN.id.toTranslationKey(keyPrefix), "Mountain", "山岳")),
    }

    override fun getText() = text { translate("$keyPrefix.${biomeTag.id.toTranslationKey()}") }
    override fun test(player: PlayerEntity) = player.world.getBiome(player.blockPos).isIn(biomeTag)
}

class HasHoePassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.has_hoe", "Hoe", "クワ")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity) = player.mainHandStack.isIn(ConventionalItemTags.HOES)
}

class ToolMaterialPassiveSkillCondition(private val toolMaterial: ToolMaterial, private val toolMaterialName: String) : PassiveSkillCondition {
    companion object {
        val keyPrefix = "${MirageFairy2023.modId}.passive_skill.condition.tool_material"
    }

    enum class Key(val translation: Translation) {
        IRON(Translation("$keyPrefix.iron", "Iron Tool", "鉄ツール")),
        GOLD(Translation("$keyPrefix.gold", "Golden Tool", "金ツール")),
        DIAMOND(Translation("$keyPrefix.diamond", "Diamond Tool", "ダイヤモンドツール")),
    }

    constructor(toolMaterial: ToolMaterials) : this(toolMaterial, toolMaterial.name.lowercase())

    override fun getText() = text { translate("$keyPrefix.$toolMaterialName") }
    override fun test(player: PlayerEntity): Boolean {
        val item = player.mainHandStack.item as? ToolItem ?: return false
        return item.material === toolMaterial
    }
}

class MaximumLevelPassiveSkillCondition(private val level: Int) : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.maximum_level", "Level<=%s", "レベル%s以下")
    }

    override fun getText() = text { key(level) }
    override fun test(player: PlayerEntity) = player.experienceLevel <= level
}

class OnFirePassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.on_fire", "Fire", "炎上")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity) = player.isOnFire
}

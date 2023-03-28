package miragefairy2023.modules.passiveskill

import miragefairy2023.MirageFairy2023
import miragefairy2023.util.Translation
import miragefairy2023.util.text
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterials
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
        return player.world.isDay && !player.world.isRaining && player.world.isSkyVisible(BlockPos(player.eyePos))
    }
}

class ShadePassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.shade", "Shade", "日陰")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity): Boolean {
        player.world.calculateAmbientDarkness()
        return !(player.world.isDay && !player.world.isRaining && player.world.isSkyVisible(BlockPos(player.eyePos)))
    }
}

class DarknessPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.darkness", "Darkness", "暗闇")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity) = player.world.getLightLevel(BlockPos(player.eyePos)) <= 7
}

class BiomePassiveSkillCondition(private val biomeTag: TagKey<Biome>) : PassiveSkillCondition {
    companion object {
        val keyPrefix = "${MirageFairy2023.modId}.passive_skill.condition.biome"
        val isForestKey = Translation("$keyPrefix.minecraft.is_forest", "Forest", "森林")
    }

    override fun getText() = text { translate("$keyPrefix.${biomeTag.id.toTranslationKey()}") }
    override fun test(player: PlayerEntity) = player.world.getBiome(player.blockPos).isIn(biomeTag)
}

class IronToolPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.iron_tool", "Iron Tool", "鉄ツール")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity): Boolean {
        val item = player.mainHandStack.item as? ToolItem ?: return false
        return item.material === ToolMaterials.IRON
    }
}

class MaximumLevelPassiveSkillCondition(private val level: Int) : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.maximum_level", "Lv.<=%s", "レベル%s以下")
    }

    override fun getText() = text { key(level) }
    override fun test(player: PlayerEntity) = player.experienceLevel <= level
}
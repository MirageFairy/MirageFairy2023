package miragefairy2023.modules.passiveskill

import miragefairy2023.MirageFairy2023
import miragefairy2023.api.PassiveSkillCondition
import miragefairy2023.modules.fairy.DemonFairyItem
import miragefairy2023.util.Symbol
import miragefairy2023.util.castOr
import miragefairy2023.util.eyeBlockPos
import miragefairy2023.util.init.Translation
import miragefairy2023.util.text
import mirrg.kotlin.hydrogen.formatAs
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterial
import net.minecraft.item.ToolMaterials
import net.minecraft.tag.FluidTags
import net.minecraft.tag.TagKey
import net.minecraft.util.math.Box
import net.minecraft.world.Heightmap
import net.minecraft.world.biome.Biome

private fun isInNaturalDimension(player: PlayerEntity) = player.world.dimension.natural

private fun isSkyVisible(player: PlayerEntity) = player.world.isSkyVisible(player.eyeBlockPos)

private fun isSpaceVisible(player: PlayerEntity) = isWorldFine(player) && isSkyVisible(player)

private fun isOutdoor(player: PlayerEntity) = player.eyeBlockPos.y >= player.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, player.eyeBlockPos).y

private fun isIndoor(player: PlayerEntity) = !isOutdoor(player)

private fun isWorldFine(player: PlayerEntity) = !isWorldRain(player)

private fun isWorldRain(player: PlayerEntity) = player.world.isRaining

private fun isWorldThunder(player: PlayerEntity) = player.world.isThundering

private fun isInFine(player: PlayerEntity) = isWorldFine(player) || !isSkyVisible(player) || isIndoor(player) || player.world.getBiome(player.eyeBlockPos).value().precipitation == Biome.Precipitation.NONE

private fun isInRain(player: PlayerEntity) = player.world.hasRain(player.eyeBlockPos)

private fun isWorldDaytime(player: PlayerEntity): Boolean {
    player.world.calculateAmbientDarkness()
    return player.world.ambientDarkness < 4
}

private fun isWorldNight(player: PlayerEntity) = !isWorldDaytime(player)

private fun format(value: Double) = (value formatAs "%.4f").replace("""\.?0*$""".toRegex(), "")


class OverworldPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.overworld", "Overworld", "地上世界")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, itemStack: ItemStack) = isInNaturalDimension(player)
}

class DaytimePassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.daytime", "Daytime", "昼")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, itemStack: ItemStack) = isInNaturalDimension(player) && isWorldDaytime(player)
}

class NightPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.night", "Night", "夜")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, itemStack: ItemStack) = isInNaturalDimension(player) && isWorldNight(player)
}

class SunshinePassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.sunshine", "Sunshine", "日光")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, itemStack: ItemStack) = isInNaturalDimension(player) && isWorldDaytime(player) && isSpaceVisible(player)
}

class MoonlightPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.moonlight", "Moonlight", "月光")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, itemStack: ItemStack) = isInNaturalDimension(player) && isWorldNight(player) && isSpaceVisible(player)
}

class ShadePassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.shade", "Shade", "日陰")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, itemStack: ItemStack) = !(isInNaturalDimension(player) && isWorldDaytime(player) && isSpaceVisible(player))
}

class OutdoorPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.outdoor", "Outdoor", "屋外")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, itemStack: ItemStack) = isOutdoor(player)
}

class IndoorPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.indoor", "Indoor", "屋内")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, itemStack: ItemStack) = isIndoor(player)
}

class AirPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.air", "Air", "空気")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, itemStack: ItemStack): Boolean {
        val blockState = player.world.getBlockState(player.eyeBlockPos)
        return !blockState.isOpaque && blockState.fluidState.isEmpty
    }
}

class UnderwaterPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.underwater", "Underwater", "水中")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, itemStack: ItemStack): Boolean {
        val blockState = player.world.getBlockState(player.eyeBlockPos)
        return blockState.fluidState.isIn(FluidTags.WATER)
    }
}

class InRainPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.in_rain", "Rain", "雨")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, itemStack: ItemStack) = isInRain(player)
}

class ThunderingPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.thundering", "Thundering", "雷雨")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, itemStack: ItemStack) = isWorldThunder(player)
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
        IN_NETHER(Translation(ConventionalBiomeTags.IN_NETHER.id.toTranslationKey(keyPrefix), "Nether", "ネザー")),
        IN_THE_END(Translation(ConventionalBiomeTags.IN_THE_END.id.toTranslationKey(keyPrefix), "The End", "エンド")),
        PLAINS(Translation(ConventionalBiomeTags.PLAINS.id.toTranslationKey(keyPrefix), "Plains", "平原")),
        OCEAN(Translation(ConventionalBiomeTags.OCEAN.id.toTranslationKey(keyPrefix), "Ocean", "海洋")),
        MOUNTAIN(Translation(ConventionalBiomeTags.MOUNTAIN.id.toTranslationKey(keyPrefix), "Mountain", "山岳")),
        JUNGLE(Translation(ConventionalBiomeTags.JUNGLE.id.toTranslationKey(keyPrefix), "Jungle", "ジャングル")),
        END_ISLANDS(Translation(ConventionalBiomeTags.END_ISLANDS.id.toTranslationKey(keyPrefix), "End Islands", "エンドの島々")),
    }

    override fun getText() = text { translate(biomeTag.id.toTranslationKey(keyPrefix)) }
    override fun test(player: PlayerEntity, itemStack: ItemStack) = player.world.getBiome(player.blockPos).isIn(biomeTag)
}

class InVillagePassiveSkillCondition() : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.in_village", "Village", "村")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, itemStack: ItemStack): Boolean {
        return player.world.getNonSpectatingEntities(VillagerEntity::class.java, Box(player.pos, player.pos).expand(32.0)).isNotEmpty()
    }
}

class MinimumLightLevelPassiveSkillCondition(private val lightLevel: Int) : PassiveSkillCondition {
    override fun getText() = text { "${Symbol.LIGHT}$lightLevel${Symbol.UP}"() }
    override fun test(player: PlayerEntity, itemStack: ItemStack) = player.world.getLightLevel(player.eyeBlockPos) >= lightLevel
}

class MaximumLightLevelPassiveSkillCondition(private val lightLevel: Int) : PassiveSkillCondition {
    override fun getText() = text { "${Symbol.LIGHT}$lightLevel${Symbol.DOWN}"() }
    override fun test(player: PlayerEntity, itemStack: ItemStack) = player.world.getLightLevel(player.eyeBlockPos) <= lightLevel
}

class HasHoePassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.has_hoe", "Hoe", "クワ")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, itemStack: ItemStack) = player.mainHandStack.isIn(ConventionalItemTags.HOES)
}

class ToolMaterialPassiveSkillCondition(private val toolMaterial: ToolMaterial, private val toolMaterialName: String) : PassiveSkillCondition {
    companion object {
        val keyPrefix = "${MirageFairy2023.modId}.passive_skill.condition.tool_material"
    }

    enum class Key(val translation: Translation) {
        WOOD(Translation("$keyPrefix.wood", "Wooden Tool", "木ツール")),
        STONE(Translation("$keyPrefix.stone", "Stone Tool", "石ツール")),
        IRON(Translation("$keyPrefix.iron", "Iron Tool", "鉄ツール")),
        GOLD(Translation("$keyPrefix.gold", "Golden Tool", "金ツール")),
        DIAMOND(Translation("$keyPrefix.diamond", "Diamond Tool", "ダイヤモンドツール")),
        NETHERITE(Translation("$keyPrefix.netherite", "Netherite Tool", "ネザライトツール")),
    }

    constructor(toolMaterial: ToolMaterials) : this(toolMaterial, toolMaterial.name.lowercase())

    override fun getText() = text { translate("$keyPrefix.$toolMaterialName") }
    override fun test(player: PlayerEntity, itemStack: ItemStack): Boolean {
        val item = player.mainHandStack.item as? ToolItem ?: return false
        return item.material === toolMaterial
    }
}

class FoodPassiveSkillCondition(private val foodItem: () -> Item) : PassiveSkillCondition {
    override fun getText() = text { foodItem().name }
    override fun test(player: PlayerEntity, itemStack: ItemStack): Boolean {
        val itemStacks = listOf(player.inventory.mainHandStack) + player.inventory.offHand + player.inventory.main
        val primaryFoodItemStack = itemStacks.firstOrNull { it.isFood }
        return primaryFoodItemStack?.isOf(foodItem()) ?: false
    }
}

class MaximumLevelPassiveSkillCondition(private val level: Int) : PassiveSkillCondition {
    override fun getText() = text { "${Symbol.LEVEL}$level${Symbol.DOWN}"() }
    override fun test(player: PlayerEntity, itemStack: ItemStack) = player.experienceLevel <= level
}

class MaximumHealthPassiveSkillCondition(private val health: Int) : PassiveSkillCondition {
    override fun getText() = text { "${Symbol.HEART}${format(health * 0.5)}${Symbol.DOWN}"() }
    override fun test(player: PlayerEntity, itemStack: ItemStack) = player.health <= health
}

class MinimumFoodLevelPassiveSkillCondition(private val foodLevel: Int) : PassiveSkillCondition {
    override fun getText() = text { "${Symbol.FOOD}${format(foodLevel * 0.5)}${Symbol.UP}"() }
    override fun test(player: PlayerEntity, itemStack: ItemStack) = player.hungerManager.foodLevel >= foodLevel
}

class MaximumFoodLevelPassiveSkillCondition(private val foodLevel: Int) : PassiveSkillCondition {
    override fun getText() = text { "${Symbol.FOOD}${format(foodLevel * 0.5)}${Symbol.DOWN}"() }
    override fun test(player: PlayerEntity, itemStack: ItemStack) = player.hungerManager.foodLevel <= foodLevel
}

class OnFirePassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.on_fire", "Fire", "炎上")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, itemStack: ItemStack) = player.isOnFire
}

class StatusEffectPassiveSkillCondition(private val statusEffect: StatusEffect) : PassiveSkillCondition {
    override fun getText() = text { translate(statusEffect.translationKey) }
    override fun test(player: PlayerEntity, itemStack: ItemStack) = player.hasStatusEffect(statusEffect)
}

class FairyLevelPassiveSkillCondition(private val fairyLevel: Int) : PassiveSkillCondition {
    override fun getText() = text { "${Symbol.STAR}$fairyLevel${Symbol.UP}"() }
    override fun test(player: PlayerEntity, itemStack: ItemStack) = itemStack.item.castOr<DemonFairyItem> { return false }.fairyLevel >= fairyLevel
}

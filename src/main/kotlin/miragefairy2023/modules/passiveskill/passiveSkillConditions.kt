package miragefairy2023.modules.passiveskill

import miragefairy2023.MirageFairy2023
import miragefairy2023.api.PassiveSkillCondition
import miragefairy2023.modules.ToolMaterialCard
import miragefairy2023.modules.getTelescopeActions
import miragefairy2023.modules.lastFoodProperty
import miragefairy2023.util.Symbol
import miragefairy2023.util.Translation
import miragefairy2023.util.eyeBlockPos
import miragefairy2023.util.removeTrailingZeros
import miragefairy2023.util.text
import mirrg.kotlin.hydrogen.formatAs
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.tag.FluidTags
import net.minecraft.tag.TagKey
import net.minecraft.util.math.Box
import net.minecraft.world.Heightmap
import net.minecraft.world.biome.Biome
import java.time.Instant

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

private fun format(value: Double) = (value formatAs "%.4f").removeTrailingZeros()


@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.overworld() = PassiveSkillConditions(OverworldPassiveSkillCondition())

class OverworldPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.overworld", "Overworld", "地上世界")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, mana: Double) = isInNaturalDimension(player)
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.daytime() = PassiveSkillConditions(DaytimePassiveSkillCondition())

class DaytimePassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.daytime", "Daytime", "昼")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, mana: Double) = isInNaturalDimension(player) && isWorldDaytime(player)
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.night() = PassiveSkillConditions(NightPassiveSkillCondition())

class NightPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.night", "Night", "夜")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, mana: Double) = isInNaturalDimension(player) && isWorldNight(player)
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.sunshine() = PassiveSkillConditions(SunshinePassiveSkillCondition())

class SunshinePassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.sunshine", "Sunshine", "日光")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, mana: Double) = isInNaturalDimension(player) && isWorldDaytime(player) && isSpaceVisible(player)
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.moonlight() = PassiveSkillConditions(MoonlightPassiveSkillCondition())

class MoonlightPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.moonlight", "Moonlight", "月光")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, mana: Double) = isInNaturalDimension(player) && isWorldNight(player) && isSpaceVisible(player)
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.shade() = PassiveSkillConditions(ShadePassiveSkillCondition())

class ShadePassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.shade", "Shade", "日陰")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, mana: Double) = !(isInNaturalDimension(player) && isWorldDaytime(player) && isSpaceVisible(player))
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.outdoor() = PassiveSkillConditions(OutdoorPassiveSkillCondition())

class OutdoorPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.outdoor", "Outdoor", "屋外")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, mana: Double) = isOutdoor(player)
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.indoor() = PassiveSkillConditions(IndoorPassiveSkillCondition())

class IndoorPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.indoor", "Indoor", "屋内")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, mana: Double) = isIndoor(player)
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.air() = PassiveSkillConditions(AirPassiveSkillCondition())

class AirPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.air", "Air", "空気")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, mana: Double): Boolean {
        val blockState = player.world.getBlockState(player.eyeBlockPos)
        return !blockState.isOpaque && blockState.fluidState.isEmpty
    }
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.underwater() = PassiveSkillConditions(UnderwaterPassiveSkillCondition())

class UnderwaterPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.underwater", "Underwater", "水中")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, mana: Double): Boolean {
        val blockState = player.world.getBlockState(player.eyeBlockPos)
        return blockState.fluidState.isIn(FluidTags.WATER)
    }
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.inRain() = PassiveSkillConditions(InRainPassiveSkillCondition())

class InRainPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.in_rain", "In Rain", "雨中")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, mana: Double) = isInRain(player)
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.thundering() = PassiveSkillConditions(ThunderingPassiveSkillCondition())

class ThunderingPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.thundering", "Thundering", "雷雨")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, mana: Double) = isWorldThunder(player)
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.biome(biomeTag: TagKey<Biome>) = PassiveSkillConditions(BiomePassiveSkillCondition(biomeTag))

class BiomePassiveSkillCondition(private val biomeTag: TagKey<Biome>) : PassiveSkillCondition {
    companion object {
        val keyPrefix = "${MirageFairy2023.modId}.passive_skill.condition.biome"
    }

    enum class Key(val translation: Translation) {
        FOREST(Translation(ConventionalBiomeTags.FOREST.id.toTranslationKey(keyPrefix), "Forest", "森林")),
        TAIGA(Translation(ConventionalBiomeTags.TAIGA.id.toTranslationKey(keyPrefix), "Taiga", "タイガ")),
        DESERT(Translation(ConventionalBiomeTags.DESERT.id.toTranslationKey(keyPrefix), "Desert", "砂漠")),
        ICY(Translation(ConventionalBiomeTags.ICY.id.toTranslationKey(keyPrefix), "Icy", "氷の地域")),
        MUSHROOM(Translation(ConventionalBiomeTags.MUSHROOM.id.toTranslationKey(keyPrefix), "Mushroom Island", "キノコ島")),
        FLORAL(Translation(ConventionalBiomeTags.FLORAL.id.toTranslationKey(keyPrefix), "Floral", "花畑")),
        IN_NETHER(Translation(ConventionalBiomeTags.IN_NETHER.id.toTranslationKey(keyPrefix), "Nether", "ネザー")),
        IN_THE_END(Translation(ConventionalBiomeTags.IN_THE_END.id.toTranslationKey(keyPrefix), "The End", "エンド")),
        PLAINS(Translation(ConventionalBiomeTags.PLAINS.id.toTranslationKey(keyPrefix), "Plains", "平原")),
        OCEAN(Translation(ConventionalBiomeTags.OCEAN.id.toTranslationKey(keyPrefix), "Ocean", "海洋")),
        MOUNTAIN(Translation(ConventionalBiomeTags.MOUNTAIN.id.toTranslationKey(keyPrefix), "Mountain", "山岳")),
        JUNGLE(Translation(ConventionalBiomeTags.JUNGLE.id.toTranslationKey(keyPrefix), "Jungle", "ジャングル")),
        SWAMP(Translation(ConventionalBiomeTags.SWAMP.id.toTranslationKey(keyPrefix), "Swamp", "沼地")),
        SNOWY(Translation(ConventionalBiomeTags.SNOWY.id.toTranslationKey(keyPrefix), "Snowy", "雪の地域")),
        BADLANDS(Translation(ConventionalBiomeTags.BADLANDS.id.toTranslationKey(keyPrefix), "Badlands", "荒地")),
        END_ISLANDS(Translation(ConventionalBiomeTags.END_ISLANDS.id.toTranslationKey(keyPrefix), "End Islands", "エンドの島々")),
        CLIMATE_HOT(Translation(ConventionalBiomeTags.CLIMATE_HOT.id.toTranslationKey(keyPrefix), "Hot", "高温")),
        CLIMATE_COLD(Translation(ConventionalBiomeTags.CLIMATE_COLD.id.toTranslationKey(keyPrefix), "Cold", "寒冷")),
        CLIMATE_WET(Translation(ConventionalBiomeTags.CLIMATE_WET.id.toTranslationKey(keyPrefix), "Wet", "多湿")),
        CLIMATE_DRY(Translation(ConventionalBiomeTags.CLIMATE_DRY.id.toTranslationKey(keyPrefix), "Dry", "乾燥")),
    }

    override fun getText() = text { translate(biomeTag.id.toTranslationKey(keyPrefix)) }
    override fun test(player: PlayerEntity, mana: Double) = player.world.getBiome(player.blockPos).isIn(biomeTag)
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.inVillage() = PassiveSkillConditions(InVillagePassiveSkillCondition())

class InVillagePassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.in_village", "Village", "村")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, mana: Double): Boolean {
        return player.world.getNonSpectatingEntities(VillagerEntity::class.java, Box(player.eyePos, player.eyePos).expand(32.0)).isNotEmpty()
    }
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.minimumLightLevel(lightLevel: Int) = PassiveSkillConditions(MinimumLightLevelPassiveSkillCondition(lightLevel))

class MinimumLightLevelPassiveSkillCondition(private val lightLevel: Int) : PassiveSkillCondition {
    override fun getText() = text { "${Symbol.LIGHT}$lightLevel${Symbol.UP}"() }
    override fun test(player: PlayerEntity, mana: Double) = player.world.getLightLevel(player.eyeBlockPos) >= lightLevel
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.maximumLightLevel(lightLevel: Int) = PassiveSkillConditions(MaximumLightLevelPassiveSkillCondition(lightLevel))

class MaximumLightLevelPassiveSkillCondition(private val lightLevel: Int) : PassiveSkillCondition {
    override fun getText() = text { "${Symbol.LIGHT}$lightLevel${Symbol.DOWN}"() }
    override fun test(player: PlayerEntity, mana: Double) = player.world.getLightLevel(player.eyeBlockPos) <= lightLevel
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.hasHoe() = PassiveSkillConditions(HasHoePassiveSkillCondition())

class HasHoePassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.has_hoe", "Hoe", "クワ")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, mana: Double) = player.mainHandStack.isIn(ConventionalItemTags.HOES)
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.toolMaterial(toolMaterialCard: ToolMaterialCard) = PassiveSkillConditions(ToolMaterialPassiveSkillCondition(toolMaterialCard))

class ToolMaterialPassiveSkillCondition(private val toolMaterialCard: ToolMaterialCard) : PassiveSkillCondition {
    override fun getText() = text { toolMaterialCard.translation() }
    override fun test(player: PlayerEntity, mana: Double) = player.mainHandStack.isIn(toolMaterialCard.tag)
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.food(foodItem: () -> Item) = PassiveSkillConditions(FoodPassiveSkillCondition(foodItem))

class FoodPassiveSkillCondition(private val foodItem: () -> Item) : PassiveSkillCondition {
    override fun getText() = text { foodItem().name }
    override fun test(player: PlayerEntity, mana: Double) = player.lastFoodProperty.get()?.isOf(foodItem()) ?: false
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.maximumLevel(level: Int) = PassiveSkillConditions(MaximumLevelPassiveSkillCondition(level))

class MaximumLevelPassiveSkillCondition(private val level: Int) : PassiveSkillCondition {
    override fun getText() = text { "${Symbol.LEVEL}$level${Symbol.DOWN}"() }
    override fun test(player: PlayerEntity, mana: Double) = player.experienceLevel <= level
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.maximumHealth(health: Int) = PassiveSkillConditions(MaximumHealthPassiveSkillCondition(health))

class MaximumHealthPassiveSkillCondition(private val health: Int) : PassiveSkillCondition {
    override fun getText() = text { "${Symbol.HEART}${format(health * 0.5)}${Symbol.DOWN}"() }
    override fun test(player: PlayerEntity, mana: Double) = player.health <= health
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.minimumFoodLevel(foodLevel: Int) = PassiveSkillConditions(MinimumFoodLevelPassiveSkillCondition(foodLevel))

class MinimumFoodLevelPassiveSkillCondition(private val foodLevel: Int) : PassiveSkillCondition {
    override fun getText() = text { "${Symbol.FOOD}${format(foodLevel * 0.5)}${Symbol.UP}"() }
    override fun test(player: PlayerEntity, mana: Double) = player.hungerManager.foodLevel >= foodLevel
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.maximumFoodLevel(foodLevel: Int) = PassiveSkillConditions(MaximumFoodLevelPassiveSkillCondition(foodLevel))

class MaximumFoodLevelPassiveSkillCondition(private val foodLevel: Int) : PassiveSkillCondition {
    override fun getText() = text { "${Symbol.FOOD}${format(foodLevel * 0.5)}${Symbol.DOWN}"() }
    override fun test(player: PlayerEntity, mana: Double) = player.hungerManager.foodLevel <= foodLevel
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.onFire() = PassiveSkillConditions(OnFirePassiveSkillCondition())

class OnFirePassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.on_fire", "Fire", "炎上")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, mana: Double) = player.isOnFire
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.statusEffect(statusEffect: StatusEffect) = PassiveSkillConditions(StatusEffectPassiveSkillCondition(statusEffect))

class StatusEffectPassiveSkillCondition(private val statusEffect: StatusEffect) : PassiveSkillCondition {
    override fun getText() = text { translate(statusEffect.translationKey) }
    override fun test(player: PlayerEntity, mana: Double) = player.hasStatusEffect(statusEffect)
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.minimumMana(mana: Double) = PassiveSkillConditions(MinimumManaPassiveSkillCondition(mana))

class MinimumManaPassiveSkillCondition(private val mana: Double) : PassiveSkillCondition {
    override fun getText() = text { "${Symbol.STAR}$mana${Symbol.UP}"() }
    override fun test(player: PlayerEntity, mana: Double) = mana >= this.mana
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.telescopeMission() = PassiveSkillConditions(TelescopeMissionPassiveSkillCondition())

class TelescopeMissionPassiveSkillCondition : PassiveSkillCondition {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.condition.telescope_mission", "Telescope Mission", "望遠鏡ミッション")
    }

    override fun getText() = text { key() }
    override fun test(player: PlayerEntity, mana: Double) = getTelescopeActions(Instant.now(), player).isEmpty()
}

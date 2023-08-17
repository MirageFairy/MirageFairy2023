package miragefairy2023.modules.passiveskill

import miragefairy2023.MirageFairy2023
import miragefairy2023.api.PassiveSkillEffect
import miragefairy2023.modules.DemonPlayerAttributeCard
import miragefairy2023.modules.DemonSoundEventCard
import miragefairy2023.util.Symbol
import miragefairy2023.util.Translation
import miragefairy2023.util.blockVisitor
import miragefairy2023.util.eyeBlockPos
import miragefairy2023.util.randomInt
import miragefairy2023.util.removeTrailingZeros
import miragefairy2023.util.text
import miragefairy2023.util.toRoman
import mirrg.kotlin.hydrogen.atLeast
import mirrg.kotlin.hydrogen.formatAs
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box
import java.util.UUID

abstract class AttributePassiveSkillEffect : PassiveSkillEffect {
    protected abstract val uuid: UUID
    protected abstract val identifier: Identifier
    protected abstract val entityAttribute: EntityAttribute
    protected abstract val operation: EntityAttributeModifier.Operation
    protected abstract val power: Double
    override fun update(world: ServerWorld, player: PlayerEntity, efficiency: Double, passiveSkillVariable: MutableMap<Identifier, Any>, initializers: MutableList<() -> Unit>, terminators: MutableList<() -> Unit>) {

        if (passiveSkillVariable[identifier] == null) {
            passiveSkillVariable[identifier] = 0.0

            val entityAttributeInstance = player.attributes.getCustomInstance(entityAttribute)
            if (entityAttributeInstance != null) {
                initializers += {
                    val totalPower = passiveSkillVariable[identifier] as Double
                    if (totalPower != 0.0) {
                        val entityAttributeModifier = EntityAttributeModifier(uuid, attributeKey.key, totalPower, operation)
                        entityAttributeInstance.addTemporaryModifier(entityAttributeModifier)
                    }
                }
                terminators += {
                    entityAttributeInstance.removeModifier(uuid)
                }
            }
        }

        passiveSkillVariable[identifier] = passiveSkillVariable[identifier] as Double + power * efficiency

    }
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.movementSpeed(power: Double) = MovementSpeedPassiveSkillEffect(power)

class MovementSpeedPassiveSkillEffect(override val power: Double) : AttributePassiveSkillEffect() {
    override fun getText(baseEfficiency: Double, efficiency: Double) = text { translate("attribute.name.generic.movement_speed") + ": "() + (power * efficiency * 100 formatAs "%+.1f%%")() }
    override val uuid: UUID = UUID.fromString("378C9369-6CC3-4B45-AADD-5B221DF26ED0")
    override val identifier = Identifier(MirageFairy2023.modId, "movement_speed")
    override val entityAttribute: EntityAttribute get() = EntityAttributes.GENERIC_MOVEMENT_SPEED
    override val operation = EntityAttributeModifier.Operation.MULTIPLY_BASE
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.attackDamage(power: Double) = AttackDamagePassiveSkillEffect(power)

class AttackDamagePassiveSkillEffect(override val power: Double) : AttributePassiveSkillEffect() {
    override fun getText(baseEfficiency: Double, efficiency: Double) = text { translate("attribute.name.generic.attack_damage") + ": "() + (power * efficiency formatAs "%+.2f")() }
    override val uuid: UUID = UUID.fromString("19306783-21EE-4A02-AC1F-46FFECE309A2")
    override val identifier = Identifier(MirageFairy2023.modId, "attack_damage")
    override val entityAttribute: EntityAttribute = EntityAttributes.GENERIC_ATTACK_DAMAGE
    override val operation = EntityAttributeModifier.Operation.ADDITION
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.maxHealth(power: Double) = MaxHealthPassiveSkillEffect(power)

class MaxHealthPassiveSkillEffect(override val power: Double) : AttributePassiveSkillEffect() {
    override fun getText(baseEfficiency: Double, efficiency: Double) = text { translate("attribute.name.generic.max_health") + ": "() + (power * efficiency formatAs "%+.2f")() }
    override val uuid: UUID = UUID.fromString("A3610FD7-694C-443C-B9D3-7F2815526EA7")
    override val identifier = Identifier(MirageFairy2023.modId, "max_health")
    override val entityAttribute: EntityAttribute = EntityAttributes.GENERIC_MAX_HEALTH
    override val operation = EntityAttributeModifier.Operation.ADDITION
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.luck(power: Double) = LuckPassiveSkillEffect(power)

class LuckPassiveSkillEffect(override val power: Double) : AttributePassiveSkillEffect() {
    override fun getText(baseEfficiency: Double, efficiency: Double) = text { translate("attribute.name.generic.luck") + ": "() + (power * efficiency formatAs "%+.2f")() }
    override val uuid: UUID = UUID.fromString("A69D69CB-1658-4D58-BB45-B18445DD8757")
    override val identifier = Identifier(MirageFairy2023.modId, "luck")
    override val entityAttribute: EntityAttribute = EntityAttributes.GENERIC_LUCK
    override val operation = EntityAttributeModifier.Operation.ADDITION
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.shootingDamage(power: Double) = ShootingDamageSkillEffect(power)

class ShootingDamageSkillEffect(override val power: Double) : AttributePassiveSkillEffect() {
    override fun getText(baseEfficiency: Double, efficiency: Double) = text { translate(DemonPlayerAttributeCard.SHOOTING_DAMAGE.translationKey) + ": "() + (power * efficiency formatAs "%+.2f")() }
    override val uuid: UUID = UUID.fromString("B9E36634-FB66-49E6-849F-BAC417AE5064")
    override val identifier = Identifier(MirageFairy2023.modId, DemonPlayerAttributeCard.SHOOTING_DAMAGE.path)
    override val entityAttribute: EntityAttribute = DemonPlayerAttributeCard.SHOOTING_DAMAGE.entityAttribute
    override val operation = EntityAttributeModifier.Operation.ADDITION
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.magicDamage(power: Double) = MagicDamageSkillEffect(power)

class MagicDamageSkillEffect(override val power: Double) : AttributePassiveSkillEffect() {
    override fun getText(baseEfficiency: Double, efficiency: Double) = text { translate(DemonPlayerAttributeCard.MAGIC_DAMAGE.translationKey) + ": "() + (power * efficiency formatAs "%+.2f")() }
    override val uuid: UUID = UUID.fromString("92B4465E-18B2-4438-8FCD-2375B91AF9FC")
    override val identifier = Identifier(MirageFairy2023.modId, DemonPlayerAttributeCard.MAGIC_DAMAGE.path)
    override val entityAttribute: EntityAttribute = DemonPlayerAttributeCard.MAGIC_DAMAGE.entityAttribute
    override val operation = EntityAttributeModifier.Operation.ADDITION
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.statusEffect(
    statusEffect: StatusEffect,
    amplifier: Int,
    additionalSeconds: Int = 0,
    showParticles: Boolean = false,
) = StatusEffectPassiveSkillEffect(statusEffect, amplifier, additionalSeconds, showParticles)

class StatusEffectPassiveSkillEffect(
    private val statusEffect: StatusEffect,
    private val amplifier: Int,
    private val additionalSeconds: Int,
    private val showParticles: Boolean,
) : PassiveSkillEffect {
    override fun getText(baseEfficiency: Double, efficiency: Double) = text { translate(statusEffect.translationKey) + (if (amplifier > 0) " ${(amplifier + 1).toRoman()}" else "")() }
    override fun affect(world: ServerWorld, player: PlayerEntity, efficiency: Double, passiveSkillVariable: MutableMap<Identifier, Any>, initializers: MutableList<() -> Unit>) {
        player.addStatusEffect(StatusEffectInstance(statusEffect, 20 * (1 + 1 + additionalSeconds), amplifier, true, showParticles, true))
    }
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.combustion() = CombustionPassiveSkillEffect()

class CombustionPassiveSkillEffect : PassiveSkillEffect {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.effect.combustion", "Combustion", "発火")
    }

    override fun getText(baseEfficiency: Double, efficiency: Double) = text { key() }
    override fun affect(world: ServerWorld, player: PlayerEntity, efficiency: Double, passiveSkillVariable: MutableMap<Identifier, Any>, initializers: MutableList<() -> Unit>) {
        if (player.isWet || player.inPowderSnow || player.wasInPowderSnow) return
        player.fireTicks = 30 atLeast player.fireTicks
    }
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.experience(amount: Double) = ExperiencePassiveSkillEffect(amount)

class ExperiencePassiveSkillEffect(private val amount: Double) : PassiveSkillEffect {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.effect.experience", "Experience: %s/s", "経験値: %s/秒")
    }

    override fun getText(baseEfficiency: Double, efficiency: Double) = text { key(amount * efficiency formatAs "%+.2f") }
    override fun affect(world: ServerWorld, player: PlayerEntity, efficiency: Double, passiveSkillVariable: MutableMap<Identifier, Any>, initializers: MutableList<() -> Unit>) {
        val actualAmount = world.random.randomInt(amount * efficiency)
        if (actualAmount > 0) player.addExperience(actualAmount)
    }
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.regeneration(amount: Double) = RegenerationPassiveSkillEffect(amount)

class RegenerationPassiveSkillEffect(private val amount: Double) : PassiveSkillEffect {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.effect.regeneration", "Regeneration: %s/s", "継続回復: %s/秒")
    }

    override fun getText(baseEfficiency: Double, efficiency: Double) = text { key(amount * efficiency formatAs "%+.2f") }
    override fun affect(world: ServerWorld, player: PlayerEntity, efficiency: Double, passiveSkillVariable: MutableMap<Identifier, Any>, initializers: MutableList<() -> Unit>) {
        if (player.health < player.maxHealth) player.heal((amount * efficiency).toFloat())
    }
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.collection(amount: Double) = CollectionPassiveSkillEffect(amount)

class CollectionPassiveSkillEffect(private val amount: Double) : PassiveSkillEffect {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.effect.collection", "Collection: %s Stacks/s", "収集: %sスタック/秒")
        private val identifier = Identifier(MirageFairy2023.modId, "collection")
    }

    override fun getText(baseEfficiency: Double, efficiency: Double) = text { key(amount * efficiency formatAs "%.2f") }
    override fun affect(world: ServerWorld, player: PlayerEntity, efficiency: Double, passiveSkillVariable: MutableMap<Identifier, Any>, initializers: MutableList<() -> Unit>) {

        if (passiveSkillVariable[identifier] == null) {
            passiveSkillVariable[identifier] = 0.0

            initializers += {
                val actualAmount = world.random.randomInt(passiveSkillVariable[identifier] as Double)
                if (actualAmount > 0) {
                    val originalBlockPos = player.eyeBlockPos
                    val reach = 15
                    val itemEntities = world.getEntitiesByClass(ItemEntity::class.java, Box(originalBlockPos).expand(reach.toDouble())) {
                        when {
                            it.isSpectator -> false
                            it.boundingBox.intersects(player.boundingBox) -> false
                            else -> true
                        }
                    }

                    var remainingAmount = actualAmount
                    var processedCount = 0
                    run finish@{
                        blockVisitor(originalBlockPos, maxDistance = reach) { fromBlockPos, direction, toBlockPos ->
                            !world.getBlockState(fromBlockPos).isSideSolidFullSquare(world, fromBlockPos, direction) && !world.getBlockState(toBlockPos).isSideSolidFullSquare(world, toBlockPos, direction.opposite)
                        }.forEach { (_, blockPos) ->
                            val currentBox = Box(blockPos).expand(0.98, 0.0, 0.98)
                            itemEntities
                                .filter { it.boundingBox.intersects(currentBox) }
                                .forEach {

                                    it.teleport(player.x, player.y, player.z)
                                    it.resetPickupDelay()

                                    processedCount++

                                    remainingAmount--
                                    if (remainingAmount <= 0) return@finish

                                }
                        }
                    }

                    if (processedCount > 0) {

                        // Effect
                        world.playSound(null, player.x, player.y, player.z, DemonSoundEventCard.COLLECT.soundEvent, SoundCategory.PLAYERS, 0.15F, 0.8F + (world.random.nextFloat() - 0.5F) * 0.5F)

                    }

                }
            }
        }

        passiveSkillVariable[identifier] = passiveSkillVariable[identifier] as Double + amount * efficiency

    }
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.mana(mana: Double) = ManaPassiveSkillEffect(mana)

class ManaPassiveSkillEffect(private val mana: Double) : PassiveSkillEffect {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.effect.mana", "Mana: %s", "マナ: %s")
    }

    override fun getText(baseEfficiency: Double, efficiency: Double) = text { key((mana * baseEfficiency formatAs "${Symbol.STAR}%+.3f").removeTrailingZeros()) }
    override fun getMana(baseEfficiency: Double) = mana * baseEfficiency
}

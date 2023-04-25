package miragefairy2023.modules.passiveskill

import miragefairy2023.MirageFairy2023
import miragefairy2023.api.PassiveSkillEffect
import miragefairy2023.util.eyeBlockPos
import miragefairy2023.util.init.Translation
import miragefairy2023.util.randomInt
import miragefairy2023.util.text
import miragefairy2023.util.toRoman
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
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import java.util.UUID

abstract class AttributePassiveSkillEffect : PassiveSkillEffect {
    protected abstract val uuid: UUID
    protected abstract val identifier: Identifier
    protected abstract val entityAttribute: EntityAttribute
    protected abstract val operation: EntityAttributeModifier.Operation
    protected abstract val power: Double
    override fun update(world: ServerWorld, player: PlayerEntity, passiveSkillVariable: MutableMap<Identifier, Any>, initializers: MutableList<() -> Unit>, terminators: MutableList<() -> Unit>) {

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

        passiveSkillVariable[identifier] = passiveSkillVariable[identifier] as Double + power

    }
}

class MovementSpeedPassiveSkillEffect(override val power: Double) : AttributePassiveSkillEffect() {
    override fun getText() = text { translate("attribute.name.generic.movement_speed") + ": "() + (power * 100 formatAs "%+.1f%%")() }
    override val uuid: UUID = UUID.fromString("378C9369-6CC3-4B45-AADD-5B221DF26ED0")
    override val identifier = Identifier(MirageFairy2023.modId, "movement_speed")
    override val entityAttribute: EntityAttribute get() = EntityAttributes.GENERIC_MOVEMENT_SPEED
    override val operation = EntityAttributeModifier.Operation.MULTIPLY_BASE
}

class AttackDamagePassiveSkillEffect(override val power: Double) : AttributePassiveSkillEffect() {
    override fun getText() = text { translate("attribute.name.generic.attack_damage") + ": "() + (power formatAs "%+.2f")() }
    override val uuid: UUID = UUID.fromString("19306783-21EE-4A02-AC1F-46FFECE309A2")
    override val identifier = Identifier(MirageFairy2023.modId, "attack_damage")
    override val entityAttribute: EntityAttribute = EntityAttributes.GENERIC_ATTACK_DAMAGE
    override val operation = EntityAttributeModifier.Operation.ADDITION
}

class MaxHealthPassiveSkillEffect(override val power: Double) : AttributePassiveSkillEffect() {
    override fun getText() = text { translate("attribute.name.generic.max_health") + ": "() + (power formatAs "%+.2f")() }
    override val uuid: UUID = UUID.fromString("A3610FD7-694C-443C-B9D3-7F2815526EA7")
    override val identifier = Identifier(MirageFairy2023.modId, "max_health")
    override val entityAttribute: EntityAttribute = EntityAttributes.GENERIC_MAX_HEALTH
    override val operation = EntityAttributeModifier.Operation.ADDITION
}

class LuckPassiveSkillEffect(override val power: Double) : AttributePassiveSkillEffect() {
    override fun getText() = text { translate("attribute.name.generic.luck") + ": "() + (power formatAs "%+.2f")() }
    override val uuid: UUID = UUID.fromString("A69D69CB-1658-4D58-BB45-B18445DD8757")
    override val identifier = Identifier(MirageFairy2023.modId, "luck")
    override val entityAttribute: EntityAttribute = EntityAttributes.GENERIC_LUCK
    override val operation = EntityAttributeModifier.Operation.ADDITION
}

class StatusEffectPassiveSkillEffect(
    private val statusEffect: StatusEffect,
    private val amplifier: Int,
    private val additionalSeconds: Int = 0,
    private val showParticles: Boolean = false,
) : PassiveSkillEffect {
    override fun getText() = text { translate(statusEffect.translationKey) + (if (amplifier > 0) " ${(amplifier + 1).toRoman()}" else "")() }
    override fun affect(world: ServerWorld, player: PlayerEntity, passiveSkillVariable: MutableMap<Identifier, Any>, initializers: MutableList<() -> Unit>) {
        player.addStatusEffect(StatusEffectInstance(statusEffect, 20 * (1 + 1 + additionalSeconds), amplifier, true, showParticles, true))
    }
}

class ExperiencePassiveSkillEffect(private val amount: Double) : PassiveSkillEffect {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.effect.experience", "Experience: %s/s", "経験値: %s/秒")
    }

    override fun getText() = text { key(amount formatAs "%+.2f") }
    override fun affect(world: ServerWorld, player: PlayerEntity, passiveSkillVariable: MutableMap<Identifier, Any>, initializers: MutableList<() -> Unit>) {
        val actualAmount = world.random.randomInt(amount)
        if (actualAmount > 0) player.addExperience(actualAmount)
    }
}

class CollectionPassiveSkillEffect(private val amount: Double) : PassiveSkillEffect {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.effect.collection", "Collection: %s Stacks/s", "収集: %sスタック/秒")
        private val identifier = Identifier(MirageFairy2023.modId, "collection")
    }

    override fun getText() = text { key(amount formatAs "%.2f") }
    override fun affect(world: ServerWorld, player: PlayerEntity, passiveSkillVariable: MutableMap<Identifier, Any>, initializers: MutableList<() -> Unit>) {

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
                        blockVisitor(reach, originalBlockPos) { fromBlockPos, direction, toBlockPos ->
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
                        world.playSound(null, player.x, player.y, player.z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 0.25F, 1.0F)

                    }

                }
            }
        }

        passiveSkillVariable[identifier] = passiveSkillVariable[identifier] as Double + amount

    }
}

fun blockVisitor(maxDistance: Int, originalBlockPos: BlockPos, predicate: (fromBlockPos: BlockPos, direction: Direction, toBlockPos: BlockPos) -> Boolean) = sequence {
    val checkedBlockPosList = mutableSetOf<BlockPos>()
    var nextBlockPosList = mutableSetOf(originalBlockPos)

    (0..maxDistance).forEach { distance ->

        val currentBlockPosList: Set<BlockPos> = nextBlockPosList
        nextBlockPosList = mutableSetOf()

        currentBlockPosList.forEach nextCurrentBlockPos@{ fromBlockPos ->

            yield(Pair(distance, fromBlockPos))

            fun check(direction: Direction) {
                val toBlockPos = fromBlockPos.offset(direction)
                if (toBlockPos !in checkedBlockPosList && predicate(fromBlockPos, direction, toBlockPos)) {
                    checkedBlockPosList += toBlockPos
                    nextBlockPosList += toBlockPos
                }
            }

            check(Direction.DOWN)
            check(Direction.UP)
            check(Direction.NORTH)
            check(Direction.SOUTH)
            check(Direction.WEST)
            check(Direction.EAST)

        }

    }

}

package miragefairy2023.modules.passiveskill

import miragefairy2023.MirageFairy2023
import miragefairy2023.util.Translation
import miragefairy2023.util.text
import mirrg.kotlin.hydrogen.formatAs
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import java.util.UUID

abstract class AttributePassiveSkillEffect : PassiveSkillEffect {
    protected abstract val uuid: UUID
    protected abstract val identifier: Identifier
    protected abstract val entityAttribute: EntityAttribute
    protected abstract val operation: EntityAttributeModifier.Operation
    protected abstract val power: Double
    override fun update(player: PlayerEntity, passiveSkillVariable: MutableMap<Identifier, Any>, initializers: MutableList<() -> Unit>, terminators: MutableList<() -> Unit>) {

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
    override fun getText() = text { translate("attribute.name.generic.movement_speed") + " "() + (power * 100 formatAs "%+.0f%%")() }
    override val uuid: UUID = UUID.fromString("378C9369-6CC3-4B45-AADD-5B221DF26ED0")
    override val identifier = Identifier(MirageFairy2023.modId, "movement_speed")
    override val entityAttribute: EntityAttribute get() = EntityAttributes.GENERIC_MOVEMENT_SPEED
    override val operation = EntityAttributeModifier.Operation.MULTIPLY_BASE
}

class AttackDamagePassiveSkillEffect(override val power: Double) : AttributePassiveSkillEffect() {
    override fun getText() = text { translate("attribute.name.generic.attack_damage") + " "() + (power formatAs "%+.0f")() }
    override val uuid: UUID = UUID.fromString("19306783-21EE-4A02-AC1F-46FFECE309A2")
    override val identifier = Identifier(MirageFairy2023.modId, "attack_damage")
    override val entityAttribute: EntityAttribute = EntityAttributes.GENERIC_ATTACK_DAMAGE
    override val operation = EntityAttributeModifier.Operation.ADDITION
}

class MaxHealthPassiveSkillEffect(override val power: Double) : AttributePassiveSkillEffect() {
    override fun getText() = text { translate("attribute.name.generic.max_health") + " "() + (power formatAs "%+.0f")() }
    override val uuid: UUID = UUID.fromString("A3610FD7-694C-443C-B9D3-7F2815526EA7")
    override val identifier = Identifier(MirageFairy2023.modId, "max_health")
    override val entityAttribute: EntityAttribute = EntityAttributes.GENERIC_MAX_HEALTH
    override val operation = EntityAttributeModifier.Operation.ADDITION
}

class StatusEffectPassiveSkillEffect(private val statusEffect: StatusEffect, private val amplifier: Int, private val additionalSeconds: Int = 0) : PassiveSkillEffect {
    override fun getText() = text { translate(statusEffect.translationKey) + " Lv.${amplifier + 1}"() }
    override fun affect(player: PlayerEntity) {
        player.addStatusEffect(StatusEffectInstance(statusEffect, 20 * (10 + 1 + additionalSeconds), amplifier, true, false, true))
    }
}

class ExperiencePassiveSkillEffect(private val amount: Int) : PassiveSkillEffect {
    companion object {
        val key = Translation("${MirageFairy2023.modId}.passive_skill.effect.experience", "Experience", "経験値")
    }

    override fun getText() = text { key() + " +$amount"() }
    override fun affect(player: PlayerEntity) = player.addExperience(amount)
}

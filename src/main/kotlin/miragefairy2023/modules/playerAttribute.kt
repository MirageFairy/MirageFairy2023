package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.mixins.api.DefaultAttributeRegistryHelper
import miragefairy2023.module
import miragefairy2023.util.init.enJa
import net.minecraft.entity.EntityType
import net.minecraft.entity.attribute.ClampedEntityAttribute
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

val playerAttributeModule = module {
    DemonPlayerAttributeCard.values().forEach { card ->
        enJa(card.translationKey, card.en, card.ja)
        DefaultAttributeRegistryHelper.addDefaultAttribute(EntityType.PLAYER, card.entityAttribute)
    }
}

enum class DemonPlayerAttributeCard(val path: String, val en: String, val ja: String, fallback: Double, min: Double, max: Double) {
    SHOOTING_DAMAGE("shooting_damage", "Shooting Damage", "射撃攻撃力", 0.0, 0.0, 2048.0),
    MAGIC_DAMAGE("magic_damage", "Magic Damage", "魔法攻撃力", 0.0, 0.0, 2048.0),
    ;

    val identifier = Identifier(MirageFairy2023.modId, path)
    val translationKey = "attribute.name.${MirageFairy2023.modId}.$path"
    val entityAttribute: EntityAttribute = Registry.register(Registry.ATTRIBUTE, identifier, ClampedEntityAttribute(translationKey, fallback, min, max))
}

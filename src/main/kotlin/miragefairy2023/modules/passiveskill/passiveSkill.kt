package miragefairy2023.modules.passiveskill

import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.Translation
import miragefairy2023.util.translation
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier

val attributeKey = Translation("${MirageFairy2023.modId}.passive_skill.attribute", "Fairy Bonus", "妖精ボーナス")

val passiveSkillModule = module {

    translation(attributeKey)

    translation(OverworldPassiveSkillCondition.key)
    translation(AirPassiveSkillCondition.key)
    translation(DaytimePassiveSkillCondition.key)
    translation(NightPassiveSkillCondition.key)
    translation(SunshinePassiveSkillCondition.key)
    translation(MoonlightPassiveSkillCondition.key)
    translation(ShadePassiveSkillCondition.key)
    translation(DarknessPassiveSkillCondition.key)
    translation(BiomePassiveSkillCondition.forestKey)
    translation(BiomePassiveSkillCondition.taigaKey)
    translation(BiomePassiveSkillCondition.desertKey)
    translation(BiomePassiveSkillCondition.mushroomKey)
    translation(BiomePassiveSkillCondition.floralKey)
    translation(BiomePassiveSkillCondition.inTheEndKey)
    translation(InRainPassiveSkillCondition.key)
    translation(HasHoePassiveSkillCondition.key)
    translation(IronToolPassiveSkillCondition.key)
    translation(DiamondToolPassiveSkillCondition.key)
    translation(MaximumLevelPassiveSkillCondition.key)
    translation(OnFirePassiveSkillCondition.key)

    translation(ExperiencePassiveSkillEffect.key)

}

class PassiveSkill(val conditions: List<PassiveSkillCondition>, val effect: PassiveSkillEffect)

interface PassiveSkillCondition {
    fun getText(): Text
    fun test(player: PlayerEntity): Boolean
}

interface PassiveSkillEffect {
    fun getText(): Text

    /**
     * 任意のタイミングで更新されうる持続的な効果を更新します。
     * プレイヤーやサーバーのリロードに伴って揮発するか、制限時間付きの効果を使用する必要があります。
     * @param terminators プレイヤーのアンロードおよびサーバーの終了時は呼び出されません。
     */
    fun update(player: PlayerEntity, passiveSkillVariable: MutableMap<Identifier, Any>, initializers: MutableList<() -> Unit>, terminators: MutableList<() -> Unit>) = Unit

    /**
     * 10秒おきに呼び出されるアクションを発揮します。
     */
    fun affect(player: PlayerEntity) = Unit
}

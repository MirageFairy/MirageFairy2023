package miragefairy2023.modules.passiveskill

import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.Translation
import miragefairy2023.util.translation
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Identifier

val attributeKey = Translation("${MirageFairy2023.modId}.passive_skill.attribute", "Fairy Bonus", "妖精ボーナス")

val passiveSkillModule = module {

    translation(attributeKey)

    translation(OverworldPassiveSkillCondition.key)
    translation(AirPassiveSkillCondition.key)
    translation(UnderwaterPassiveSkillCondition.key)
    translation(DaytimePassiveSkillCondition.key)
    translation(NightPassiveSkillCondition.key)
    translation(SunshinePassiveSkillCondition.key)
    translation(MoonlightPassiveSkillCondition.key)
    translation(ShadePassiveSkillCondition.key)
    translation(MinimumLightLevelPassiveSkillCondition.key)
    translation(MaximumLightLevelPassiveSkillCondition.key)
    BiomePassiveSkillCondition.Key.values().forEach {
        translation(it.translation)
    }
    translation(InRainPassiveSkillCondition.key)
    translation(HasHoePassiveSkillCondition.key)
    ToolMaterialPassiveSkillCondition.Key.values().forEach {
        translation(it.translation)
    }
    translation(MaximumLevelPassiveSkillCondition.key)
    translation(OnFirePassiveSkillCondition.key)

    translation(ExperiencePassiveSkillEffect.key)
    translation(CollectionPassiveSkillEffect.key)

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
     * このメソッドは必ず論理サーバーで呼び出されます。
     * @param terminators プレイヤーのアンロードおよびサーバーの終了時は呼び出されません。
     */
    fun update(world: ServerWorld, player: PlayerEntity, passiveSkillVariable: MutableMap<Identifier, Any>, initializers: MutableList<() -> Unit>, terminators: MutableList<() -> Unit>) = Unit

    /**
     * 10秒おきに呼び出されるアクションを発揮します。
     * このメソッドは必ず論理サーバーで呼び出されます。
     */
    fun affect(world: ServerWorld, player: PlayerEntity) = Unit
}

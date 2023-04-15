package miragefairy2023.modules.passiveskill

import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.init.Translation
import miragefairy2023.util.init.translation
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Identifier

val attributeKey = Translation("${MirageFairy2023.modId}.passive_skill.attribute", "Fairy Bonus", "妖精ボーナス")

val passiveSkillModule = module {

    // パッシブスキル
    run {
        val terminators = mutableListOf<() -> Unit>()
        ServerTickEvents.END_SERVER_TICK.register { server ->
            if ((server.ticks % (20L * 10L)).toInt() != 132) return@register // 10秒毎

            // 前回判定時の掃除
            terminators.forEach {
                it()
            }
            terminators.clear()

            server.worlds.forEach { world ->
                world.players.forEach nextPlayer@{ player ->

                    if (player.isSpectator) return@nextPlayer // スペクテイターモードでは無効

                    // 有効な妖精のリスト
                    val itemStacks: List<ItemStack> = player.inventory.offHand + player.inventory.main.slice(9 * 3 until 9 * 4)
                    val triples = itemStacks
                        .mapNotNull { itemStack ->
                            val item = itemStack.item as? PassiveSkillItem ?: return@mapNotNull null
                            Pair(itemStack, item)
                        }
                        .distinctBy { it.second.getPassiveSkillIdentifier() }

                    val initializers = mutableListOf<() -> Unit>()

                    // 効果の計算
                    val passiveSkillVariable = mutableMapOf<Identifier, Any>()
                    triples.forEach { pair ->
                        pair.second.getPassiveSkills(player, pair.first).forEach passiveSkillIsFailed@{ passiveSkill ->
                            passiveSkill.conditions.forEach { condition ->
                                if (!condition.test(player)) return@passiveSkillIsFailed
                            }
                            passiveSkill.effect.update(world, player, passiveSkillVariable, initializers, terminators)
                            passiveSkill.effect.affect(world, player)
                        }
                    }

                    // 効果を発動
                    initializers.forEach {
                        it()
                    }

                }
            }
        }
        ServerLifecycleEvents.SERVER_STOPPING.register {
            terminators.clear()
        }
    }


    translation(PassiveSkillKeys.DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY)
    translation(PassiveSkillKeys.DUPLICATED_PASSIVE_SKILL_DESCRIPTION_KEY)
    translation(PassiveSkillKeys.ENABLED_PASSIVE_SKILL_DESCRIPTION_KEY)

    translation(attributeKey)

    translation(OverworldPassiveSkillCondition.key)
    translation(AirPassiveSkillCondition.key)
    translation(UnderwaterPassiveSkillCondition.key)
    translation(DaytimePassiveSkillCondition.key)
    translation(NightPassiveSkillCondition.key)
    translation(SunshinePassiveSkillCondition.key)
    translation(MoonlightPassiveSkillCondition.key)
    translation(OutdoorPassiveSkillCondition.key)
    translation(IndoorPassiveSkillCondition.key)
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
    translation(MaximumHealthPassiveSkillCondition.key)
    translation(OnFirePassiveSkillCondition.key)

    translation(ExperiencePassiveSkillEffect.key)
    translation(CollectionPassiveSkillEffect.key)

}


interface PassiveSkillItem {
    fun getPassiveSkillIdentifier(): Identifier
    fun getPassiveSkills(player: PlayerEntity, itemStack: ItemStack): List<PassiveSkill>
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

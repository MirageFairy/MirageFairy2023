package miragefairy2023.modules.passiveskill

import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.init.Translation
import miragefairy2023.util.init.translation
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.util.Identifier

val attributeKey = Translation("${MirageFairy2023.modId}.passive_skill.attribute", "Fairy Bonus", "妖精ボーナス")

val passiveSkillModule = module {

    // パッシブスキル
    run {
        val terminators = mutableListOf<() -> Unit>()
        ServerTickEvents.END_SERVER_TICK.register { server ->
            if ((server.ticks % 20L).toInt() != 13) return@register // 1秒毎

            // 前回判定時の掃除
            terminators.forEach {
                it()
            }
            terminators.clear()

            server.worlds.forEach { world ->
                world.players.forEach nextPlayer@{ player ->

                    if (player.isSpectator) return@nextPlayer // スペクテイターモードでは無効

                    // パッシブスキル判定
                    val entries = player.getPassiveSkillEntries()

                    val initializers = mutableListOf<() -> Unit>()

                    // 効果の計算
                    val passiveSkillVariable = mutableMapOf<Identifier, Any>()
                    entries.forEach nextEntry@{ entry ->
                        if (entry.availability != PassiveSkillAvailability.ENABLED) return@nextEntry
                        entry.item.getPassiveSkills(player, entry.itemStack).forEach nextPassiveSkill@{ passiveSkill ->
                            passiveSkill.conditions.forEach { condition ->
                                if (!condition.test(player, entry.itemStack)) return@nextPassiveSkill
                            }
                            passiveSkill.effect.update(world, player, passiveSkillVariable, initializers, terminators)
                            passiveSkill.effect.affect(world, player, passiveSkillVariable, initializers)
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


    translation(PassiveSkillKeys.ENABLED_PASSIVE_SKILL_DESCRIPTION_KEY)
    translation(PassiveSkillKeys.OVERFLOWED_PASSIVE_SKILL_DESCRIPTION_KEY)
    translation(PassiveSkillKeys.HIDDEN_PASSIVE_SKILL_DESCRIPTION_KEY)
    translation(PassiveSkillKeys.DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY)

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
    BiomePassiveSkillCondition.Key.values().forEach {
        translation(it.translation)
    }
    translation(InVillagePassiveSkillCondition.key)
    translation(InRainPassiveSkillCondition.key)
    translation(ThunderingPassiveSkillCondition.key)
    translation(HasHoePassiveSkillCondition.key)
    ToolMaterialPassiveSkillCondition.Key.values().forEach {
        translation(it.translation)
    }
    translation(OnFirePassiveSkillCondition.key)

    translation(CombustionPassiveSkillEffect.key)
    translation(ExperiencePassiveSkillEffect.key)
    translation(RegenerationPassiveSkillEffect.key)
    translation(CollectionPassiveSkillEffect.key)

}

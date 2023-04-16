package miragefairy2023.modules.passiveskill

import miragefairy2023.MirageFairy2023
import miragefairy2023.api.PassiveSkillItem
import miragefairy2023.module
import miragefairy2023.util.init.Translation
import miragefairy2023.util.init.translation
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.item.ItemStack
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
                                if (!condition.test(player, pair.first)) return@passiveSkillIsFailed
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

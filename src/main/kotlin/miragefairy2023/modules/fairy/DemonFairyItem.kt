package miragefairy2023.modules.fairy

import miragefairy2023.MirageFairy2023
import miragefairy2023.util.aqua
import miragefairy2023.util.formatted
import miragefairy2023.util.gold
import miragefairy2023.util.gray
import miragefairy2023.util.init.Translation
import miragefairy2023.util.join
import miragefairy2023.util.red
import miragefairy2023.util.text
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.world.World

class DemonFairyItem(val fairyCard: FairyCard, val rank: Int, settings: Settings) : Item(settings) {
    companion object {
        val RARE_KEY = Translation("item.${MirageFairy2023.modId}.fairy.rare", "Rare", "レア度")
        val DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY = Translation("item.${MirageFairy2023.modId}.fairy.passive_skill.disabled", "Use passive skills in 3rd row of inventory", "インベントリの3行目でパッシブスキルを発動")
        val DUPLICATED_PASSIVE_SKILL_DESCRIPTION_KEY = Translation("item.${MirageFairy2023.modId}.fairy.passive_skill.duplicated", "Same fairies exist", "妖精が重複しています")
        val ENABLED_PASSIVE_SKILL_DESCRIPTION_KEY = Translation("item.${MirageFairy2023.modId}.fairy.passive_skill.enabled", "Passive skills are enabled", "パッシブスキル有効")
        val ALWAYS_CONDITION_KEY = Translation("${MirageFairy2023.modId}.passive_skill.condition.always", "Always", "常時")
    }

    val fairyLevel get() = fairyCard.rare + (rank - 1) * 2

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)


        val stars1 = listOf(
            (1..fairyCard.rare).map { "★" to { text: Text -> text.formatted(getRareColor(fairyCard.rare)) } },
            (1..(rank - 1) * 2).map { "★" to { text: Text -> text.aqua } },
        ).flatten()
        val stars2 = if (stars1.size > 15) {
            listOf(
                *stars1.take(15).toTypedArray(),
                "..." to stars1[15].second,
            )
        } else {
            stars1
        }
        val stars3 = stars2
            .asSequence()
            .map { text { it.second(it.first()) } }
            .chunked(5)
            .map { it.join() }
            .chunked(2)
            .map { it.join(text { " "() }) }
            .toList()
            .join(text { "  "() })
        tooltip += text { (RARE_KEY() + ": "() + stars3 + " $fairyLevel"()).aqua }


        val passiveSkills = fairyCard.passiveSkills
        if (passiveSkills.isNotEmpty()) {

            val player = MirageFairy2023.proxy?.getClientPlayer() ?: return

            val passiveFairy = player.getPassiveFairies().find { it.itemStack === stack }

            val isEnabled = passiveFairy != null
            val isDuplicated = passiveFairy != null && passiveFairy.isDuplicated

            // パッシブスキルタイトル行
            tooltip += text {
                when {
                    !isEnabled -> DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY().gray
                    isDuplicated -> DUPLICATED_PASSIVE_SKILL_DESCRIPTION_KEY().red
                    else -> ENABLED_PASSIVE_SKILL_DESCRIPTION_KEY().gold
                }
            }

            // パッシブスキル行
            passiveSkills.forEach { passiveSkill ->

                // 条件判定
                val conditions = passiveSkill.conditions.map { condition ->
                    Pair(condition, condition.test(player))
                }

                tooltip += text {
                    val effectText = passiveSkill.effect.getText()
                    val conditionTexts = conditions.map {
                        if (it.second) {
                            it.first.getText()
                        } else {
                            it.first.getText().red
                        }
                    }
                    val text = if (conditionTexts.isNotEmpty()) {
                        effectText + " ["() + conditionTexts.join(","()) + "]"()
                    } else {
                        effectText + " ["() + ALWAYS_CONDITION_KEY() + "]"()
                    }
                    if (isEnabled && !isDuplicated && conditions.all { it.second }) text.gold else text.gray
                }

            }

        }

    }
}

private class PassiveFairy(val itemStack: ItemStack, val isDuplicated: Boolean)

private fun PlayerEntity.getPassiveFairies(): List<PassiveFairy> {
    val itemStacks: List<ItemStack> = this.inventory.offHand + this.inventory.main.slice(9 * 3 until 9 * 4)
    val result = mutableListOf<PassiveFairy>()
    val collectedFairyIdentifiers = mutableSetOf<Identifier>()
    itemStacks.forEach { itemStack ->
        val item = itemStack.item
        if (item !is DemonFairyItem) return@forEach
        val isDuplicated = item.fairyCard.identifier in collectedFairyIdentifiers
        collectedFairyIdentifiers += item.fairyCard.identifier
        result += PassiveFairy(itemStack, isDuplicated)
    }
    return result.toList()
}

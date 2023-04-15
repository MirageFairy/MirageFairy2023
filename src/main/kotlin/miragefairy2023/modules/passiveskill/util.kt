package miragefairy2023.modules.passiveskill

import miragefairy2023.MirageFairy2023
import miragefairy2023.modules.fairy.DemonFairyItem
import miragefairy2023.util.gold
import miragefairy2023.util.gray
import miragefairy2023.util.join
import miragefairy2023.util.red
import miragefairy2023.util.text
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier

fun getPassiveSkillTooltip(itemStack: ItemStack, passiveSkills: List<PassiveSkill>): List<Text> {
    val player = MirageFairy2023.proxy?.getClientPlayer() ?: return listOf()
    if (passiveSkills.isEmpty()) return listOf() // パッシブスキルが無い場合は表示しない

    val tooltip = mutableListOf<Text>()

    val passiveFairy = player.getPassiveFairies().find { it.itemStack === itemStack }
    val isEnabled = passiveFairy != null
    val isDuplicated = passiveFairy != null && passiveFairy.isDuplicated

    // タイトルラベル
    tooltip += text {
        when {
            !isEnabled -> DemonFairyItem.DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY().gray
            isDuplicated -> DemonFairyItem.DUPLICATED_PASSIVE_SKILL_DESCRIPTION_KEY().red
            else -> DemonFairyItem.ENABLED_PASSIVE_SKILL_DESCRIPTION_KEY().gold
        }
    }

    // 各パッシブスキル
    passiveSkills.forEach { passiveSkill ->

        // 条件判定
        val conditions = passiveSkill.conditions.map { condition ->
            Pair(condition, condition.test(player))
        }

        // 追加
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
                effectText + " ["() + DemonFairyItem.ALWAYS_CONDITION_KEY() + "]"()
            }
            if (isEnabled && !isDuplicated && conditions.all { it.second }) text.gold else text.gray
        }

    }

    return tooltip
}

private class PassiveFairy(val itemStack: ItemStack, val isDuplicated: Boolean)

private fun PlayerEntity.getPassiveFairies(): List<PassiveFairy> {
    val itemStacks: List<ItemStack> = this.inventory.offHand + this.inventory.main.slice(9 * 3 until 9 * 4)
    val result = mutableListOf<PassiveFairy>()
    val collectedFairyIdentifiers = mutableSetOf<Identifier>()
    itemStacks.forEach { itemStack ->
        val item = itemStack.item as? PassiveSkillItem ?: return@forEach
        val isDuplicated = item.getPassiveSkillIdentifier() in collectedFairyIdentifiers
        collectedFairyIdentifiers += item.getPassiveSkillIdentifier()
        result += PassiveFairy(itemStack, isDuplicated)
    }
    return result.toList()
}

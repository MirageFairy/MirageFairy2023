package miragefairy2023.modules.passiveskill

import miragefairy2023.MirageFairy2023
import miragefairy2023.api.PassiveSkill
import miragefairy2023.api.PassiveSkillItem
import miragefairy2023.util.gold
import miragefairy2023.util.gray
import miragefairy2023.util.init.Translation
import miragefairy2023.util.join
import miragefairy2023.util.red
import miragefairy2023.util.text
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object PassiveSkillKeys {
    val ENABLED_PASSIVE_SKILL_DESCRIPTION_KEY = Translation("${MirageFairy2023.modId}.passive_skill.enabled", "Passive skills are enabled", "パッシブスキル有効")
    val HIDDEN_PASSIVE_SKILL_DESCRIPTION_KEY = Translation("${MirageFairy2023.modId}.passive_skill.hidden", "Duplicated passive skills!", "パッシブスキルが重複しています！")
    val DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY = Translation("${MirageFairy2023.modId}.passive_skill.disabled", "Passive skills are disabled", "パッシブスキル無効")
}

fun getPassiveSkillTooltip(itemStack: ItemStack, passiveSkills: List<PassiveSkill>): List<Text> {
    val player = MirageFairy2023.clientProxy?.getClientPlayer() ?: return listOf()
    if (passiveSkills.isEmpty()) return listOf() // パッシブスキルが無い場合は何も表示しない

    val tooltip = mutableListOf<Text>()

    val entry = player.getPassiveSkillEntries().find { it.itemStack === itemStack }
    val isEnabled = entry != null
    val isDuplicated = entry != null && entry.isDuplicated

    // タイトルラベル
    tooltip += text {
        when {
            !isEnabled -> PassiveSkillKeys.DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY().gray
            isDuplicated -> PassiveSkillKeys.HIDDEN_PASSIVE_SKILL_DESCRIPTION_KEY().red
            else -> PassiveSkillKeys.ENABLED_PASSIVE_SKILL_DESCRIPTION_KEY().gold
        }
    }

    // 各パッシブスキル
    passiveSkills.forEach { passiveSkill ->

        // 条件判定
        val conditions = passiveSkill.conditions.map { condition ->
            Pair(condition, condition.test(player, itemStack))
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
                effectText
            }
            if (isEnabled && !isDuplicated && conditions.all { it.second }) text.gold else text.gray
        }

    }

    return tooltip
}

class PassiveSkillEntry(val itemStack: ItemStack, val isDuplicated: Boolean)

fun PlayerEntity.getPassiveSkillEntries(): List<PassiveSkillEntry> {

    // パッシブスキル発動対象アイテム
    val itemStacks: List<ItemStack> = this.inventory.offHand + this.inventory.main.slice(9 * 3 until 9 * 4)

    // 有効化されたパッシブスキル発動アイテムのリスト
    val entries = mutableListOf<PassiveSkillEntry>()
    val collectedFairyIdentifiers = mutableSetOf<Identifier>()
    itemStacks.forEach { itemStack ->
        val item = itemStack.item as? PassiveSkillItem ?: return@forEach
        val isDuplicated = item.getPassiveSkillIdentifier() in collectedFairyIdentifiers
        collectedFairyIdentifiers += item.getPassiveSkillIdentifier()
        entries += PassiveSkillEntry(itemStack, isDuplicated)
    }

    return entries.toList()
}

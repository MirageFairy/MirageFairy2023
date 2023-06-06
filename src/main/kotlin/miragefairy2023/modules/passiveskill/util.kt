package miragefairy2023.modules.passiveskill

import dev.emi.trinkets.api.TrinketsApi
import miragefairy2023.MirageFairy2023
import miragefairy2023.api.PassiveSkill
import miragefairy2023.api.PassiveSkillCondition
import miragefairy2023.api.PassiveSkillEffect
import miragefairy2023.api.PassiveSkillItem
import miragefairy2023.util.gold
import miragefairy2023.util.gray
import miragefairy2023.util.init.Translation
import miragefairy2023.util.join
import miragefairy2023.util.red
import miragefairy2023.util.text
import mirrg.kotlin.hydrogen.or
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import kotlin.jvm.optionals.getOrNull


class PassiveSkillsBuilder {
    val passiveSkills = mutableListOf<PassiveSkill>()
    operator fun PassiveSkillConditions.times(other: PassiveSkillConditions) = PassiveSkillConditions(this.conditions + other.conditions)
    infix fun PassiveSkillEffect.on(conditions: PassiveSkillConditions) {
        passiveSkills += PassiveSkill(conditions.conditions, this)
    }
}

class PassiveSkillConditions(val conditions: List<PassiveSkillCondition>) {
    constructor (vararg conditions: PassiveSkillCondition) : this(conditions.toList())
}

fun passiveSkills(block: PassiveSkillsBuilder.() -> Unit): List<PassiveSkill> {
    val scope = PassiveSkillsBuilder()
    block(scope)
    return scope.passiveSkills
}

@Suppress("UnusedReceiverParameter")
fun PassiveSkillsBuilder.always() = PassiveSkillConditions()


object PassiveSkillKeys {
    val ENABLED_PASSIVE_SKILL_DESCRIPTION_KEY = Translation("${MirageFairy2023.modId}.passive_skill.enabled", "Passive skills are enabled", "パッシブスキル有効")
    val OVERFLOWED_PASSIVE_SKILL_DESCRIPTION_KEY = Translation("${MirageFairy2023.modId}.passive_skill.overflowed", "Too many passive skills!", "パッシブスキルが多すぎます！")
    val HIDDEN_PASSIVE_SKILL_DESCRIPTION_KEY = Translation("${MirageFairy2023.modId}.passive_skill.hidden", "Duplicated passive skills!", "パッシブスキルが重複しています！")
    val DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY = Translation("${MirageFairy2023.modId}.passive_skill.disabled", "Passive skills are disabled", "パッシブスキル無効")
}

fun getPassiveSkillTooltip(itemStack: ItemStack, additionalMana: Double, passiveSkills: List<PassiveSkill>): List<Text> {
    val player = MirageFairy2023.clientProxy?.getClientPlayer() ?: return listOf()
    val item = itemStack.item as? PassiveSkillItem ?: return listOf()
    if (passiveSkills.isEmpty()) return listOf() // パッシブスキルが無い場合は何も表示しない

    val tooltip = mutableListOf<Text>()

    // パッシブスキル判定
    val entries = player.getPassiveSkillEntries()

    val entry = entries.find { it.itemStack === itemStack }
    val availability = entry?.availability ?: PassiveSkillAvailability.DISABLED

    // タイトルラベル
    tooltip += text {
        when (availability) {
            PassiveSkillAvailability.ENABLED -> PassiveSkillKeys.ENABLED_PASSIVE_SKILL_DESCRIPTION_KEY().gold
            PassiveSkillAvailability.OVERFLOWED -> PassiveSkillKeys.OVERFLOWED_PASSIVE_SKILL_DESCRIPTION_KEY().red
            PassiveSkillAvailability.HIDDEN -> PassiveSkillKeys.HIDDEN_PASSIVE_SKILL_DESCRIPTION_KEY().red
            PassiveSkillAvailability.DISABLED -> PassiveSkillKeys.DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY().gray
        }
    }

    // 各パッシブスキル
    passiveSkills.forEach { passiveSkill ->

        // 条件判定
        val conditions = passiveSkill.conditions.map { condition ->
            Pair(condition, condition.test(player, item.passiveSkillProvider.mana))
        }

        // 追加
        tooltip += text {
            val effectText = passiveSkill.effect.getText(item.passiveSkillProvider.mana / 10.0, (item.passiveSkillProvider.mana + additionalMana) / 10.0)
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
            if (availability == PassiveSkillAvailability.ENABLED && conditions.all { it.second }) text.gold else text.gray
        }

    }

    return tooltip
}


class PassiveSkillEntry(val itemStack: ItemStack, val item: PassiveSkillItem, val availability: PassiveSkillAvailability)

enum class PassiveSkillAvailability {
    ENABLED,
    OVERFLOWED,
    HIDDEN,
    DISABLED,
}

fun PlayerEntity.getPassiveSkillEntries(): List<PassiveSkillEntry> {

    // パッシブスキル発動対象アイテム
    val itemStacks: List<ItemStack> = listOf(
        this.inventory.offHand, // オフハンド
        this.inventory.armor, // 装備
        TrinketsApi.getTrinketComponent(this).getOrNull()?.allEquipped.or { listOf() }.map { it.right }, // Trinkets
        this.inventory.main.slice(9 * 3 until 9 * 4), // インベントリ3行目
    ).flatten()

    // 有効化されたパッシブスキル発動アイテムのリスト
    val acceptedIdentifiers = mutableSetOf<Identifier>()
    var count = 0
    val entries = itemStacks
        .mapNotNull { itemStack ->
            val item = itemStack.item as? PassiveSkillItem ?: return@mapNotNull null
            Pair(itemStack, item)
        }
        .map { (itemStack, item) ->
            val identifier = item.passiveSkillProvider.identifier

            if (identifier in acceptedIdentifiers) return@map PassiveSkillEntry(itemStack, item, PassiveSkillAvailability.HIDDEN) // 同じアイテムは多重に発動しない
            if (count >= 10) return@map PassiveSkillEntry(itemStack, item, PassiveSkillAvailability.OVERFLOWED) // 既に10個発動している場合は発動しない

            acceptedIdentifiers += identifier
            count++

            PassiveSkillEntry(itemStack, item, PassiveSkillAvailability.ENABLED)
        }

    return entries
}

package miragefairy2023.modules.fairy

import miragefairy2023.MirageFairy2023
import miragefairy2023.api.FairyItem
import miragefairy2023.api.PassiveSkillItem
import miragefairy2023.api.PassiveSkillProvider
import miragefairy2023.modules.passiveskill.PassiveSkillAvailability
import miragefairy2023.modules.passiveskill.getPassiveSkillEntries
import miragefairy2023.modules.passiveskill.getPassiveSkillTooltip
import miragefairy2023.util.Symbol
import miragefairy2023.util.aqua
import miragefairy2023.util.darkGray
import miragefairy2023.util.formatted
import miragefairy2023.util.init.Translation
import miragefairy2023.util.join
import miragefairy2023.util.removeTrailingZeros
import miragefairy2023.util.text
import miragefairy2023.util.toRoman
import miragefairy2023.util.yellow
import mirrg.kotlin.hydrogen.formatAs
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World

/** @param rank 1から始まります。 */
class DemonFairyItem(val fairyCard: FairyCard, val rank: Int, settings: Settings) : Item(settings), FairyItem, PassiveSkillItem {
    companion object {
        val RARE_KEY = Translation("item.${MirageFairy2023.modId}.fairy.rare", "Rare", "レア度")
        val CONDENSATION_RECIPE_KEY = Translation("item.${MirageFairy2023.modId}.fairy.recipe.condensation", "Can condense 8 fairies", "作業台で8体で凝縮")
        val DECONDENSATION_RECIPE_KEY = Translation("item.${MirageFairy2023.modId}.fairy.recipe.decondensation", "Can decondense", "作業台で展開")
        val BOTH_RECIPE_KEY = Translation("item.${MirageFairy2023.modId}.fairy.recipe.both", "Can condense 8 fairies/decondense ", "作業台で8体で凝縮、1体で展開")
    }

    val rare get() = fairyCard.rare + (rank - 1) * 2

    override val fairy get() = fairyCard.fairy

    override val passiveSkillProvider: PassiveSkillProvider
        get() = object : PassiveSkillProvider {
            override val identifier get() = fairyCard.motif
            override val mana get() = if (rare != 0) rare.toDouble() else 0.5
            override fun getPassiveSkills(player: PlayerEntity, itemStack: ItemStack) = fairyCard.passiveSkills
        }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)
        val player = MirageFairy2023.clientProxy?.getClientPlayer() ?: return

        // パッシブスキル判定
        val entries = player.getPassiveSkillEntries()

        // スキルブースト効果計算
        var passiveSkillMana = 0.0
        entries.forEach nextEntry@{ entry ->
            if (entry.availability != PassiveSkillAvailability.ENABLED) return@nextEntry
            entry.item.passiveSkillProvider.getPassiveSkills(player, entry.itemStack).forEach nextPassiveSkill@{ passiveSkill ->
                val passiveSkillMana2 = passiveSkill.effect.getMana(entry.item.passiveSkillProvider.mana / 10.0)
                if (passiveSkillMana2 > 0.0) {
                    passiveSkill.conditions.forEach { condition ->
                        if (!condition.test(player, entry.item.passiveSkillProvider.mana)) return@nextPassiveSkill
                    }
                    passiveSkillMana += passiveSkillMana2
                }
            }
        }

        // レア度
        val stars1 = listOf(
            (1..fairyCard.rare).map { Symbol.STAR.uniformed to { text: Text -> text.formatted(getRareColor(fairyCard.rare)) } },
            (1..(rank - 1) * 2).map { Symbol.STAR.uniformed to { text: Text -> text.aqua } },
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
        tooltip += text { (RARE_KEY() + ": "() + stars3 + " ${(passiveSkillProvider.mana + passiveSkillMana formatAs "%.3f").removeTrailingZeros()}"()).aqua }

        // パッシブスキル
        tooltip += getPassiveSkillTooltip(stack, passiveSkillProvider.mana, passiveSkillProvider.mana + passiveSkillMana, fairyCard.passiveSkills)

        // 凝縮レシピ
        when (rank) {
            1 -> tooltip += text { CONDENSATION_RECIPE_KEY().yellow }
            MAX_FAIRY_RANK -> tooltip += text { DECONDENSATION_RECIPE_KEY().yellow }
            else -> tooltip += text { BOTH_RECIPE_KEY().yellow }
        }

        // motif
        if (context.isAdvanced) tooltip += text { ("Motif: ${fairyCard.motif}"()).darkGray }

    }

    override fun getTranslationKey(): String = if (rank == 1) super.getTranslationKey() else fairyCard.fairy.item.translationKey
    override fun getName() = text { super.getName() + (if (rank == 1) "" else " ${rank.toRoman()}")() }
    override fun getName(stack: ItemStack) = text { super.getName(stack) + (if (rank == 1) "" else " ${rank.toRoman()}")() }
}

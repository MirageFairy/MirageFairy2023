package miragefairy2023.modules.fairy

import miragefairy2023.MirageFairy2023
import miragefairy2023.api.PassiveSkillItem
import miragefairy2023.modules.passiveskill.getPassiveSkillTooltip
import miragefairy2023.util.aqua
import miragefairy2023.util.formatted
import miragefairy2023.util.init.Translation
import miragefairy2023.util.join
import miragefairy2023.util.text
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World

class DemonFairyItem(val fairyCard: FairyCard, val rank: Int, settings: Settings) : Item(settings), PassiveSkillItem {
    companion object {
        val RARE_KEY = Translation("item.${MirageFairy2023.modId}.fairy.rare", "Rare", "レア度")
    }

    val fairyLevel get() = fairyCard.rare + (rank - 1) * 2

    override fun getPassiveSkillIdentifier() = fairyCard.identifier
    override fun getPassiveSkills(player: PlayerEntity, itemStack: ItemStack) = fairyCard.passiveSkills

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)

        // レア度
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

        // パッシブスキル
        tooltip += getPassiveSkillTooltip(stack, fairyCard.passiveSkills)

    }
}

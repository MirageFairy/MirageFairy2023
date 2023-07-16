package miragefairy2023.modules.fairy

import net.minecraft.text.Text

interface ActiveSkill {
    fun getTooltip(): List<Text>
}

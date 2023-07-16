package miragefairy2023.modules.fairy

import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World

interface ActiveSkill {
    fun getTooltip(): List<Text>

    /** 論理クライアントと論理サーバーの両方で呼び出されます。 */
    fun action(itemStack: ItemStack, world: World, user: LivingEntity, useTicks: Int)
}

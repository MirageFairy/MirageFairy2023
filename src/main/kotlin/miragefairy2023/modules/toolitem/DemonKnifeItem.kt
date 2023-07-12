package miragefairy2023.modules.toolitem

import net.minecraft.block.BlockState
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.PickaxeItem
import net.minecraft.item.ToolMaterial
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class DemonKnifeItem(
    toolMaterial: ToolMaterial,
    private val silkTouch: Boolean,
    settings: Settings,
) : PickaxeItem(toolMaterial, 1, -1.5F, settings) { // TODO -> ToolItem
    override fun getMiningSpeedMultiplier(stack: ItemStack, state: BlockState) = 1.0F
    override fun isSuitableFor(state: BlockState) = false

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        if (world.isClient) return super.use(world, user, hand)
        if (silkTouch) {
            val itemStack = user.getStackInHand(hand)
            if (EnchantmentHelper.get(itemStack).isEmpty()) {
                if (user.isCreative || user.experienceLevel >= 5) {
                    if (!user.isCreative) user.addExperienceLevels(-5)
                    EnchantmentHelper.set(mapOf(Enchantments.SILK_TOUCH to 1), itemStack)
                    world.playSound(null, user.x, user.y, user.z, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F)
                }
            }
            return TypedActionResult.consume(itemStack)
        }
        return super.use(world, user, hand)
    }
}

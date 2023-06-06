package miragefairy2023.modules

import net.fabricmc.yarn.constants.MiningLevels
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.PickaxeItem
import net.minecraft.item.ToolMaterial
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.tag.BlockTags
import net.minecraft.tag.TagKey
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class DemonPickaxeItem(toolMaterial: ToolMaterial, attackDamage: Int, attackSpeed: Float, private val effectiveBlockTags: List<TagKey<Block>>, private val silkTouch: Boolean, settings: Settings) : PickaxeItem(toolMaterial, attackDamage, attackSpeed, settings) {
    override fun getMiningSpeedMultiplier(stack: ItemStack, state: BlockState) = if (effectiveBlockTags.any { state.isIn(it) }) miningSpeed else 1.0F

    override fun isSuitableFor(state: BlockState): Boolean {
        val itemMiningLevel = material.miningLevel
        return when {
            itemMiningLevel < MiningLevels.DIAMOND && state.isIn(BlockTags.NEEDS_DIAMOND_TOOL) -> false
            itemMiningLevel < MiningLevels.IRON && state.isIn(BlockTags.NEEDS_IRON_TOOL) -> false
            itemMiningLevel < MiningLevels.STONE && state.isIn(BlockTags.NEEDS_STONE_TOOL) -> false
            else -> effectiveBlockTags.any { state.isIn(it) }
        }
    }

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

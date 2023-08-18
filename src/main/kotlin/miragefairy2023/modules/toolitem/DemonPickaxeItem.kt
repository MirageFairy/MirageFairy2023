package miragefairy2023.modules.toolitem

import miragefairy2023.util.NeighborType
import miragefairy2023.util.blockVisitor
import miragefairy2023.util.breakBlockByMagic
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags
import net.fabricmc.yarn.constants.MiningLevels
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.PickaxeItem
import net.minecraft.item.ToolMaterial
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.tag.BlockTags
import net.minecraft.tag.TagKey
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class DemonPickaxeItem(
    toolMaterial: ToolMaterial,
    attackDamage: Int,
    attackSpeed: Float,
    private val effectiveBlockTags: List<TagKey<Block>>,
    private val silkTouch: Boolean,
    private val mineAll: Boolean,
    private val cutAll: Boolean,
    settings: Settings,
) : PickaxeItem(toolMaterial, attackDamage, attackSpeed, settings) {
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

    override fun postMine(stack: ItemStack, world: World, state: BlockState, pos: BlockPos, miner: LivingEntity): Boolean {
        super.postMine(stack, world, state, pos, miner)
        if (mineAll && !miner.isSneaking) run fail@{
            if (world.isClient) return@fail

            if (miner !is ServerPlayerEntity) return@fail // 使用者がプレイヤーでない
            if (!isSuitableFor(state)) return@fail // 掘ったブロックに対して特効でない
            if (!state.isIn(ConventionalBlockTags.ORES)) return@fail // 掘ったブロックが鉱石ではない

            // 発動

            blockVisitor(listOf(pos), visitOrigins = false, maxDistance = 19, maxCount = 19) { _, toBlockPos ->
                world.getBlockState(toBlockPos).block === state.block
            }.forEach { (_, blockPos) ->
                if (stack.isEmpty) return@fail // ツールの耐久値が枯渇した
                if (stack.maxDamage - stack.damage <= 1) return@fail // ツールの耐久値が残り1
                if (breakBlockByMagic(stack, world, blockPos, miner)) {
                    stack.damage(1, miner) {
                        it.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND)
                    }
                }
            }
        }
        if (cutAll && !miner.isSneaking) run fail@{
            if (world.isClient) return@fail

            if (miner !is ServerPlayerEntity) return@fail // 使用者がプレイヤーでない
            if (!isSuitableFor(state)) return@fail // 掘ったブロックに対して特効でない
            if (!state.isIn(BlockTags.LOGS)) return@fail // 掘ったブロックが原木ではない

            // 発動

            val logBlockPosList = mutableListOf<BlockPos>()
            blockVisitor(listOf(pos), visitOrigins = false, maxDistance = 19, maxCount = 19, neighborType = NeighborType.VERTICES) { _, toBlockPos ->
                world.getBlockState(toBlockPos).block === state.block
            }.forEach { (_, blockPos) ->
                if (stack.isEmpty) return@fail // ツールの耐久値が枯渇した
                if (stack.maxDamage - stack.damage <= 1) return@fail // ツールの耐久値が残り1
                if (breakBlockByMagic(stack, world, blockPos, miner)) {
                    stack.damage(1, miner) {
                        it.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND)
                    }
                    logBlockPosList += blockPos
                }
            }
            blockVisitor(logBlockPosList, visitOrigins = false, maxDistance = 6) { _, toBlockPos ->
                world.getBlockState(toBlockPos).isIn(BlockTags.LEAVES)
            }.forEach { (_, blockPos) ->
                if (stack.isEmpty) return@fail // ツールの耐久値が枯渇した
                if (stack.maxDamage - stack.damage <= 1) return@fail // ツールの耐久値が残り1
                if (breakBlockByMagic(stack, world, blockPos, miner)) {
                    if (miner.random.nextFloat() < 0.1F) {
                        stack.damage(1, miner) {
                            it.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND)
                        }
                    }
                }
            }
        }
        return true
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

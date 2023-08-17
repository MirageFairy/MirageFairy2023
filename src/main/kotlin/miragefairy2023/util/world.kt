package miragefairy2023.util

import net.minecraft.block.Block
import net.minecraft.block.OperatorBlock
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

fun blockVisitor(originalBlockPos: BlockPos, visitOrigins: Boolean = true, maxDistance: Int, maxCount: Int? = null, predicate: (fromBlockPos: BlockPos, direction: Direction, toBlockPos: BlockPos) -> Boolean) = sequence {
    val checkedBlockPosList = mutableSetOf<BlockPos>()
    var nextBlockPosList = mutableSetOf(originalBlockPos)
    var count = 0

    if (maxCount == 0) return@sequence

    (0..maxDistance).forEach { distance ->

        val currentBlockPosList: Set<BlockPos> = nextBlockPosList
        nextBlockPosList = mutableSetOf()

        currentBlockPosList.forEach nextCurrentBlockPos@{ fromBlockPos ->

            if (distance > 0 || visitOrigins) {
                yield(Pair(distance, fromBlockPos))
                count++
                if (maxCount != null && count >= maxCount) return@sequence
            }

            fun check(direction: Direction) {
                val toBlockPos = fromBlockPos.offset(direction)
                if (toBlockPos !in checkedBlockPosList && predicate(fromBlockPos, direction, toBlockPos)) {
                    checkedBlockPosList += toBlockPos
                    nextBlockPosList += toBlockPos
                }
            }

            check(Direction.DOWN)
            check(Direction.UP)
            check(Direction.NORTH)
            check(Direction.SOUTH)
            check(Direction.WEST)
            check(Direction.EAST)

        }

    }

}

fun breakBlock(itemStack: ItemStack, world: World, blockPos: BlockPos, player: ServerPlayerEntity): Boolean {
    // see ServerPlayerInteractionManager#tryBreakBlock
    val blockState = world.getBlockState(blockPos)
    if (!itemStack.item.canMine(blockState, world, blockPos, player)) return false // このツールでは掘れない
    val blockEntity = world.getBlockEntity(blockPos)
    val block = blockState.block
    if (block is OperatorBlock && !player.isCreativeLevelTwoOp) {
        world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL)
        return false // コマンドブロックを破壊しようとした
    }
    if (player.isBlockBreakingRestricted(world, blockPos, player.interactionManager.gameMode)) return false // 破壊する権限がない
    block.onBreak(world, blockPos, blockState, player)
    val success = world.removeBlock(blockPos, false)
    if (success) block.onBroken(world, blockPos, blockState)
    if (player.isCreative) return true // クリエイティブの場合、ドロップを省略
    val newItemStack = itemStack.copy()
    val canHarvest = player.canHarvest(blockState)
    itemStack.postMine(world, blockState, blockPos, player)
    if (success && canHarvest) block.afterBreak(world, player, blockPos, blockState, blockEntity, newItemStack)
    return true
}

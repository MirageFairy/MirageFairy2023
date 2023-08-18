package miragefairy2023.util

import net.minecraft.block.Block
import net.minecraft.block.OperatorBlock
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.network.ServerPlayerInteractionManager
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

fun blockVisitor(originalBlockPosList: Iterable<BlockPos>, visitOrigins: Boolean = true, maxDistance: Int, maxCount: Int? = null, predicate: (fromBlockPos: BlockPos, direction: Direction, toBlockPos: BlockPos) -> Boolean) = sequence {
    val checkedBlockPosList = mutableSetOf<BlockPos>()
    var nextBlockPosList = originalBlockPosList.toMutableSet()
    var count = 0

    if (maxCount == 0) return@sequence

    (0..maxDistance).forEach { distance ->
        if (nextBlockPosList.isEmpty()) return@sequence

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

/**
 * プレイヤーの動作としてブロックを壊します。
 *
 * [ServerPlayerInteractionManager.tryBreakBlock]とは以下の点で異なります。
 * - ブロックの硬度が無限の場合、無効になる。
 */
fun breakBlock(itemStack: ItemStack, world: World, blockPos: BlockPos, player: ServerPlayerEntity): Boolean {
    val blockState = world.getBlockState(blockPos)
    if (!itemStack.item.canMine(blockState, world, blockPos, player)) return false // このツールは採掘そのものができない
    val blockEntity = world.getBlockEntity(blockPos)
    val block = blockState.block

    if (blockState.getHardness(world, blockPos) == -1F) return false // このブロックは破壊不能
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

/**
 * 魔法効果としてブロックを壊します。
 *
 * [breakBlock]とは以下の点で異なります。
 * - 近接武器の採掘不能特性を無視します。
 * - 専用のツールが必要なブロックを、ツールの種類にかかわらず回収可能です。
 * - [Item.postMine]を起動せず、アイテムの耐久値の減少などが発生しません。
 */
fun breakBlockByMagic(itemStack: ItemStack, world: World, blockPos: BlockPos, player: ServerPlayerEntity): Boolean {
    val blockState = world.getBlockState(blockPos)
    val blockEntity = world.getBlockEntity(blockPos)
    val block = blockState.block

    if (blockState.getHardness(world, blockPos) == -1F) return false // このブロックは破壊不能
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
    if (success) block.afterBreak(world, player, blockPos, blockState, blockEntity, newItemStack)
    return true
}

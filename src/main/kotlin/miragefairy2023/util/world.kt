package miragefairy2023.util

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

fun blockVisitor(maxDistance: Int, originalBlockPos: BlockPos, maxCount: Int? = null, predicate: (fromBlockPos: BlockPos, direction: Direction, toBlockPos: BlockPos) -> Boolean) = sequence {
    val checkedBlockPosList = mutableSetOf<BlockPos>()
    var nextBlockPosList = mutableSetOf(originalBlockPos)
    var count = 0

    if (maxCount == 0) return@sequence

    (0..maxDistance).forEach { distance ->

        val currentBlockPosList: Set<BlockPos> = nextBlockPosList
        nextBlockPosList = mutableSetOf()

        currentBlockPosList.forEach nextCurrentBlockPos@{ fromBlockPos ->

            yield(Pair(distance, fromBlockPos))
            count++
            if (maxCount != null && count >= maxCount) return@sequence

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

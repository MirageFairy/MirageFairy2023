package miragefairy2023.util.lib

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.DirectionProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView

open class InstrumentBlock(settings: Settings) : Block(settings) {
    companion object {
        val FACING: DirectionProperty = Properties.HORIZONTAL_FACING
    }


    // プロパティ―

    init {
        defaultState = defaultState.with(FACING, Direction.NORTH)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    fun getFacing(blockState: BlockState): Direction = blockState[FACING]

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState = defaultState.with(FACING, getPlacementDirection(ctx.playerFacing))

    open fun getPlacementDirection(playerDirection: Direction): Direction = playerDirection.opposite


    // 変形

    @Suppress("OVERRIDE_DEPRECATION")
    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState = state.with(FACING, rotation.rotate(state[FACING]))

    @Suppress("OVERRIDE_DEPRECATION")
    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState = state.rotate(mirror.getRotation(state[FACING]))


    // 形状

    @Suppress("OVERRIDE_DEPRECATION")
    override fun canPathfindThrough(state: BlockState, world: BlockView, pos: BlockPos, type: NavigationType?) = false

}

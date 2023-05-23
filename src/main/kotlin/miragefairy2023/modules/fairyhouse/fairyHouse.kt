package miragefairy2023.modules.fairyhouse

import miragefairy2023.RenderingProxyBlockEntity
import miragefairy2023.util.InstrumentBlock
import miragefairy2023.util.get
import miragefairy2023.util.isNotEmpty
import miragefairy2023.util.list
import miragefairy2023.util.plus
import miragefairy2023.util.set
import miragefairy2023.util.wrapper
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.network.Packet
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.util.ActionResult
import net.minecraft.util.Clearable
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

abstract class FairyHouseBlock(settings: Settings) : InstrumentBlock(settings), BlockEntityProvider {

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onSyncedBlockEvent(state: BlockState, world: World, pos: BlockPos, type: Int, data: Int): Boolean {
        @Suppress("DEPRECATION")
        super.onSyncedBlockEvent(state, world, pos, type, data)
        val blockEntity = world.getBlockEntity(pos) ?: return false
        return blockEntity.onSyncedBlockEvent(type, data)
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
        if (!state.isOf(newState.block)) {
            val blockEntity = world.getBlockEntity(pos)
            if (blockEntity is FairyHouseBlockEntity) blockEntity.dropItems()
        }
        @Suppress("DEPRECATION")
        super.onStateReplaced(state, world, pos, newState, moved)
    }

    override fun hasRandomTicks(state: BlockState) = true

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        val blockEntity = world.getBlockEntity(pos) as? FairyHouseBlockEntity ?: return ActionResult.PASS
        val itemStack = player.getStackInHand(hand)

        // 配置
        (0 until blockEntity.size()).forEach nextSlot@{ slot ->
            if (blockEntity[slot].isNotEmpty) return@nextSlot // 埋まっている場合は次へ
            if (!blockEntity.isValid(slot, itemStack)) return@nextSlot // 入れられない場合は次へ
            // この判定はホッパーと異なりcanInsert/Extract判定を貫通する

            // 成立

            if (world.isClient) return ActionResult.CONSUME

            blockEntity[slot] = (if (player.abilities.creativeMode) itemStack.copy() else itemStack).split(1)

            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, blockEntity.cachedState))
            blockEntity.markDirty()

            return ActionResult.SUCCESS
        }

        // 回収
        (0 until blockEntity.size()).reversed().forEach nextSlot@{ slot ->
            if (blockEntity[slot].isEmpty) return@nextSlot // 空である場合は次へ
            // この判定はホッパーと異なりcanInsert/Extract判定を貫通する

            // 成立

            if (world.isClient) return ActionResult.CONSUME

            val itemEntity = ItemEntity(world, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, blockEntity.removeStack(slot).copy())
            itemEntity.setVelocity(0.0, 0.0, 0.0)
            itemEntity.resetPickupDelay()
            world.spawnEntity(itemEntity)

            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, blockEntity.cachedState))
            blockEntity.markDirty()

            return ActionResult.SUCCESS
        }

        return ActionResult.PASS
    }

}

abstract class FairyHouseBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) : BlockEntity(type, pos, state), Clearable, SidedInventory, RenderingProxyBlockEntity {

    private val inventories = mutableListOf<Pair<String, SimpleInventory>>()
    private var combinedInventory: Inventory = SimpleInventory(0)

    protected fun addInventory(name: String, inventory: SimpleInventory) {
        inventories += Pair(name, inventory)
        if (combinedInventory.size() == 0) {
            combinedInventory = inventory
        } else {
            combinedInventory += inventory
        }
    }

    override fun clear() {
        inventories.forEach {
            it.second.clear()
        }
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        val nbtWrapper = nbt.wrapper
        inventories.forEach {
            it.second.clear()
            it.second.readNbtList(nbtWrapper[it.first].list.get() ?: NbtList())
        }
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        val nbtWrapper = nbt.wrapper
        inventories.forEach {
            nbtWrapper[it.first].list.set(it.second.toNbtList())
        }
    }

    override fun toInitialChunkDataNbt(): NbtCompound {
        val nbt = NbtCompound()
        val nbtWrapper = nbt.wrapper
        inventories.forEach {
            nbtWrapper[it.first].list.set(it.second.toNbtList())
        }
        return nbt
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> = BlockEntityUpdateS2CPacket.create(this)

    fun dropItems() {
        inventories.forEach {
            ItemScatterer.spawn(world, pos, it.second)
        }
    }

    override fun markDirty() {
        super.markDirty()
        world?.updateListeners(pos, cachedState, cachedState, Block.NOTIFY_ALL)
    }

    override fun size() = combinedInventory.size()
    override fun isEmpty() = combinedInventory.isEmpty
    override fun getStack(slot: Int): ItemStack = combinedInventory.getStack(slot)
    override fun removeStack(slot: Int, amount: Int): ItemStack = combinedInventory.removeStack(slot, amount)
    override fun removeStack(slot: Int): ItemStack = combinedInventory.removeStack(slot)
    override fun setStack(slot: Int, stack: ItemStack) = combinedInventory.setStack(slot, stack)
    override fun getMaxCountPerStack() = combinedInventory.maxCountPerStack // TODO ここが64固定になっている問題
    override fun canPlayerUse(player: PlayerEntity) = combinedInventory.canPlayerUse(player)
    override fun isValid(slot: Int, stack: ItemStack?) = combinedInventory.isValid(slot, stack)
    override fun getAvailableSlots(side: Direction) = (0 until combinedInventory.size()).toList().toIntArray()
    override fun canInsert(slot: Int, stack: ItemStack, dir: Direction?) = this[slot].count < maxCountPerStack
    override fun canExtract(slot: Int, stack: ItemStack, dir: Direction) = this[slot].isNotEmpty

}

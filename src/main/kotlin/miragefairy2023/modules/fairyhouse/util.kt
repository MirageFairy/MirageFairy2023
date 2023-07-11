package miragefairy2023.modules.fairyhouse

import miragefairy2023.InitializationScope
import miragefairy2023.MirageFairy2023
import miragefairy2023.RenderingProxy
import miragefairy2023.RenderingProxyBlockEntity
import miragefairy2023.api.FairyItem
import miragefairy2023.modules.DemonParticleTypeCard
import miragefairy2023.modules.Poem
import miragefairy2023.modules.commonItemGroup
import miragefairy2023.modules.generatePoemList
import miragefairy2023.modules.registerPoemList
import miragefairy2023.util.castOr
import miragefairy2023.util.datagen.enJaBlock
import miragefairy2023.util.datagen.generateDefaultBlockLootTable
import miragefairy2023.util.datagen.generateHorizontalFacingBlockState
import miragefairy2023.util.get
import miragefairy2023.util.init.FeatureSlot
import miragefairy2023.util.init.block
import miragefairy2023.util.init.blockEntity
import miragefairy2023.util.init.item
import miragefairy2023.util.isNotEmpty
import miragefairy2023.util.lib.InstrumentBlock
import miragefairy2023.util.list
import miragefairy2023.util.plus
import miragefairy2023.util.set
import miragefairy2023.util.toList
import miragefairy2023.util.wrapper
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.network.Packet
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.tag.BlockTags
import net.minecraft.tag.TagKey
import net.minecraft.util.ActionResult
import net.minecraft.util.Clearable
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

class FairyHouseCard<BE>(
    val path: String,
    val blockEntityCreator: (BlockPos, BlockState) -> BE,
    val enName: String,
    val jaName: String,
    val poemList: List<Poem>,
    val material: Material,
    val soundGroup: BlockSoundGroup,
    val needsToolTag: TagKey<Block>?,
    val voxelShape: VoxelShape,
) where BE : BlockEntity, BE : RenderingProxyBlockEntity {
    lateinit var block: FeatureSlot<FairyHouseBlock>
    lateinit var blockEntityType: FeatureSlot<BlockEntityType<BE>>
    lateinit var blockItem: FeatureSlot<BlockItem>
}

fun <BE> InitializationScope.registerFairyHouse(card: FairyHouseCard<BE>) where BE : BlockEntity, BE : RenderingProxyBlockEntity {
    card.block = block(card.path, { FairyHouseBlock(card, FabricBlockSettings.of(card.material).sounds(card.soundGroup).requiresTool().strength(2.0F).nonOpaque()) }) {

        // レンダリング
        generateHorizontalFacingBlockState({ feature }, id)
        onInitializeClient { MirageFairy2023.clientProxy!!.registerCutoutBlockRenderLayer(feature) }

        // 翻訳
        enJaBlock({ feature }, card.enName, card.jaName)

        // レシピ
        onGenerateBlockTags { it(BlockTags.PICKAXE_MINEABLE).add(feature) }
        if (card.needsToolTag != null) onGenerateBlockTags { it(card.needsToolTag).add(feature) }
        generateDefaultBlockLootTable { feature }

    }
    card.blockEntityType = blockEntity(card.path, card.blockEntityCreator, { card.block.feature }) {
        onInitializeClient { MirageFairy2023.clientProxy!!.registerRenderingProxyBlockEntityRendererFactory(feature) }
    }
    card.blockItem = item(card.path, { BlockItem(card.block.feature, FabricItemSettings().group(commonItemGroup)) }) {
        generatePoemList({ feature }, card.poemList)
        onRegisterItems { registerPoemList(feature, card.poemList) }
    }
}

class FairyHouseBlock(val card: FairyHouseCard<*>, settings: Settings) : InstrumentBlock(settings), BlockEntityProvider {

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext) = card.voxelShape

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = card.blockEntityCreator(pos, state)

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
    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        world.getBlockEntity(pos)?.castOr<FairyHouseBlockEntity> { return }?.randomTick(world, this, pos, state, random)
    }

    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        world.getBlockEntity(pos)?.castOr<FairyHouseBlockEntity> { return }?.randomDisplayTick(world, this, pos, state, random)
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (world.isClient) return ActionResult.SUCCESS
        val blockEntity = world.getBlockEntity(pos) as? FairyHouseBlockEntity ?: return ActionResult.CONSUME
        val itemStack = player.getStackInHand(hand)

        if (player.isSneaking) {
            blockEntity.onShiftUse(world, pos, state, player)
            return ActionResult.CONSUME
        }

        // 配置
        if (itemStack.isNotEmpty) {
            (0 until blockEntity.size()).forEach nextSlot@{ slot ->
                if (blockEntity[slot].isNotEmpty) return@nextSlot // 埋まっている場合は次へ
                if (!blockEntity.isValid(slot, itemStack)) return@nextSlot // 入れられない場合は次へ
                // この判定はホッパーと異なりcanInsert/Extract判定を貫通する

                // 成立

                blockEntity[slot] = (if (player.abilities.creativeMode) itemStack.copy() else itemStack).split(1)

                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, blockEntity.cachedState))
                blockEntity.markDirty()

                return ActionResult.CONSUME
            }
        }

        // 回収
        run {
            (0 until blockEntity.size()).reversed().forEach nextSlot@{ slot ->
                if (blockEntity[slot].isEmpty) return@nextSlot // 空である場合は次へ
                // この判定はホッパーと異なりcanInsert/Extract判定を貫通する

                // 成立

                val itemEntity = ItemEntity(world, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, blockEntity.removeStack(slot).copy())
                itemEntity.setVelocity(0.0, 0.0, 0.0)
                itemEntity.resetPickupDelay()
                world.spawnEntity(itemEntity)

                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, blockEntity.cachedState))
                blockEntity.markDirty()

                return ActionResult.CONSUME
            }
        }

        return ActionResult.CONSUME
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
    override fun canInsert(slot: Int, stack: ItemStack, dir: Direction?) = isValid(slot, stack) && this[slot].count < maxCountPerStack
    override fun canExtract(slot: Int, stack: ItemStack, dir: Direction) = this[slot].isNotEmpty

    open fun randomTick(world: ServerWorld, block: FairyHouseBlock, blockPos: BlockPos, blockState: BlockState, random: Random) = Unit

    open fun randomDisplayTick(world: World, block: FairyHouseBlock, blockPos: BlockPos, blockState: BlockState, random: Random) = Unit

    open fun onShiftUse(world: World, blockPos: BlockPos, blockState: BlockState, player: PlayerEntity) = Unit

}

fun SimpleInventory.filterFairySlot(itemStack: ItemStack): Boolean {
    val item = itemStack.item as? FairyItem ?: return false
    return this.toList().none nextSlot@{ inventoryItemStack ->
        val inventoryItem = inventoryItemStack.item as? FairyItem ?: return@nextSlot false
        item.fairy.motif == inventoryItem.fairy.motif
    }
}

fun ServerWorld.spawnCraftingCompletionParticles(pos: Vec3d) {
    this.spawnParticles(DemonParticleTypeCard.DESCENDING_MAGIC.particleType, pos.x, pos.y, pos.z, 5, 0.0, 0.0, 0.0, 0.02)
}

fun RenderingProxy.renderItemStack(itemStack: ItemStack, dotX: Double, dotY: Double, dotZ: Double, scale: Float = 1.0F, rotate: Float = 0.0F) {
    this.stack {
        this.translate(dotX / 16.0, dotY / 16.0, dotZ / 16.0)
        this.scale(scale, scale, scale)
        this.rotateY(rotate)
        this.renderItem(itemStack)
    }
}

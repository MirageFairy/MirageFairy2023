package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.RenderingProxy
import miragefairy2023.RenderingProxyBlockEntity
import miragefairy2023.api.FairyItem
import miragefairy2023.module
import miragefairy2023.modules.fairy.isLiquidFairy
import miragefairy2023.util.InstrumentBlock
import miragefairy2023.util.Inventory
import miragefairy2023.util.castOr
import miragefairy2023.util.createItemStack
import miragefairy2023.util.get
import miragefairy2023.util.gray
import miragefairy2023.util.identifier
import miragefairy2023.util.init.FeatureSlot
import miragefairy2023.util.init.block
import miragefairy2023.util.init.blockEntity
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.enJaBlock
import miragefairy2023.util.init.generateDefaultBlockLootTable
import miragefairy2023.util.init.generateHorizontalFacingBlockState
import miragefairy2023.util.init.group
import miragefairy2023.util.init.item
import miragefairy2023.util.isNotEmpty
import miragefairy2023.util.list
import miragefairy2023.util.notEmptyOrNull
import miragefairy2023.util.plus
import miragefairy2023.util.set
import miragefairy2023.util.text
import miragefairy2023.util.wrapper
import miragefairy2023.util.yellow
import mirrg.kotlin.hydrogen.or
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.FluidDrainable
import net.minecraft.block.LeveledCauldronBlock
import net.minecraft.block.Material
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.network.Packet
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.tag.BlockTags
import net.minecraft.tag.TagKey
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Clearable
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import kotlin.jvm.optionals.getOrNull

class FairyHouseCard<B, BE>(
    val path: String,
    val blockCreator: (AbstractBlock.Settings) -> B,
    val blockEntityCreator: (BlockPos, BlockState) -> BE,
    val enName: String,
    val jaName: String,
    val enPoem: String,
    val jaPoem: String,
    val enDescription: String,
    val jaDescription: String,
    val material: Material,
    val soundGroup: BlockSoundGroup,
    val needsToolTag: TagKey<Block>?,
) where B : Block, BE : BlockEntity, BE : RenderingProxyBlockEntity {
    companion object {
        val FAIRY_FLUID_DRAINER = FairyHouseCard(
            "fairy_fluid_drainer", ::FairyFluidDrainerBlock, ::FairyFluidDrainerBlockEntity,
            "Fairy Fluid Drainer", "妖精の水汲み所",
            "Causes anti-Brownian motion", "覆水、盆に返る。",
            "Place a liquid fairy and a bucket", "液体系妖精と空バケツを配置",
            Material.METAL, BlockSoundGroup.METAL, BlockTags.NEEDS_STONE_TOOL,
        )
    }

    lateinit var block: FeatureSlot<B>
    lateinit var blockEntityType: FeatureSlot<BlockEntityType<BE>>
    lateinit var blockItem: FeatureSlot<BlockItem>
}

object FairyHouseModule {
    val init = module {

        fun <B, BE> registerFairyHouse(card: FairyHouseCard<B, BE>) where B : Block, BE : BlockEntity, BE : RenderingProxyBlockEntity {
            card.block = block(card.path, { card.blockCreator(FabricBlockSettings.of(card.material).sounds(card.soundGroup).requiresTool().strength(2.0F).nonOpaque()) }) {

                // レンダリング
                generateHorizontalFacingBlockState()
                onInitializeClient { MirageFairy2023.clientProxy!!.registerCutoutBlockRenderLayer(feature) }

                // 翻訳
                enJaBlock({ feature }, card.enName, card.jaName)
                enJa({ "${feature.translationKey}.poem" }, card.enPoem, card.jaPoem)
                enJa({ "${feature.translationKey}.description" }, card.enDescription, card.jaDescription)

                // レシピ
                onGenerateBlockTags { it(BlockTags.PICKAXE_MINEABLE).add(feature) }
                if (card.needsToolTag != null) onGenerateBlockTags { it(card.needsToolTag).add(feature) }
                generateDefaultBlockLootTable()

            }
            card.blockEntityType = blockEntity(card.path, card.blockEntityCreator, { card.block.feature }) {
                onInitializeClient { MirageFairy2023.clientProxy!!.registerRenderingProxyBlockEntityRendererFactory(feature) }
            }
            card.blockItem = item(card.path, {
                object : BlockItem(card.block.feature, FabricItemSettings().group(commonItemGroup)) {
                    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
                        super.appendTooltip(stack, world, tooltip, context)
                        tooltip += text { translate("$translationKey.poem").gray }
                        tooltip += text { translate("$translationKey.description").yellow }
                    }
                }
            })
        }

        // 妖精の水汲み所
        registerFairyHouse(FairyHouseCard.FAIRY_FLUID_DRAINER)
        onGenerateRecipes {
            ShapedRecipeJsonBuilder
                .create(FairyHouseCard.FAIRY_FLUID_DRAINER.blockItem.feature)
                .pattern("FMB")
                .pattern("III")
                .input('I', ConventionalItemTags.IRON_INGOTS)
                .input('F', Items.IRON_BARS)
                .input('M', DemonItemCard.MIRANAGITE())
                .input('B', Items.BUCKET)
                .criterion(DemonItemCard.MIRANAGITE())
                .group(FairyHouseCard.FAIRY_FLUID_DRAINER.blockItem.feature)
                .offerTo(it, FairyHouseCard.FAIRY_FLUID_DRAINER.blockItem.feature.identifier)
        }

    }
}


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

private fun ServerWorld.spawnCraftingCompletionParticles(pos: Vec3d) {
    this.spawnParticles(DemonParticleTypeCard.DESCENDING_MAGIC.particleType, pos.x, pos.y, pos.z, 5, 0.0, 0.0, 0.0, 0.02)
}

private fun RenderingProxy.renderItemStack(itemStack: ItemStack, dotX: Double, dotY: Double, dotZ: Double, scale: Float = 1.0F, rotate: Float = 0.0F) {
    this.stack {
        this.translate(dotX / 16.0, dotY / 16.0, dotZ / 16.0)
        this.scale(scale, scale, scale)
        this.rotateY(rotate)
        this.renderItem(itemStack)
    }
}


interface FairyFluidDrainerRecipe {
    companion object {
        val RECIPES = mutableListOf<FairyFluidDrainerRecipe>()

        init {

            // 液体ブロックとか
            RECIPES += object : FairyFluidDrainerRecipe {
                override fun match(world: World, fluidBlockPos: BlockPos, fluidBlockState: BlockState): Result? {
                    val block = fluidBlockState.block
                    if (block !is FluidDrainable) return null
                    return object : Result {
                        override fun tryDrain() = block.tryDrainFluid(world, fluidBlockPos, fluidBlockState).notEmptyOrNull
                        override fun getSoundEvent() = block.bucketFillSound.getOrNull() ?: SoundEvents.ITEM_BUCKET_FILL
                    }
                }
            }

            // 水入り大釜
            RECIPES += object : FairyFluidDrainerRecipe {
                override fun match(world: World, fluidBlockPos: BlockPos, fluidBlockState: BlockState): Result? {
                    val block = fluidBlockState.block
                    if (!fluidBlockState.isOf(Blocks.WATER_CAULDRON)) return null
                    if (block !is LeveledCauldronBlock) return null
                    if (!block.isFull(fluidBlockState)) return null
                    return object : Result {
                        override fun tryDrain(): ItemStack? {
                            if (!world.setBlockState(fluidBlockPos, Blocks.CAULDRON.defaultState)) return null
                            return Items.WATER_BUCKET.createItemStack()
                        }

                        override fun getSoundEvent() = SoundEvents.ITEM_BUCKET_FILL
                    }
                }
            }

            // 溶岩入り大釜
            RECIPES += object : FairyFluidDrainerRecipe {
                override fun match(world: World, fluidBlockPos: BlockPos, fluidBlockState: BlockState): Result? {
                    if (!fluidBlockState.isOf(Blocks.LAVA_CAULDRON)) return null
                    return object : Result {
                        override fun tryDrain(): ItemStack? {
                            if (!world.setBlockState(fluidBlockPos, Blocks.CAULDRON.defaultState)) return null
                            return Items.LAVA_BUCKET.createItemStack()
                        }

                        override fun getSoundEvent() = SoundEvents.ITEM_BUCKET_FILL_LAVA
                    }
                }
            }

        }
    }

    interface Result {
        fun tryDrain(): ItemStack?
        fun getSoundEvent(): SoundEvent
    }

    fun match(world: World, fluidBlockPos: BlockPos, fluidBlockState: BlockState): Result?
}

class FairyFluidDrainerBlock(settings: Settings) : FairyHouseBlock(settings) {
    companion object {
        private val SHAPE = createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0)!!
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext) = SHAPE

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = FairyFluidDrainerBlockEntity(pos, state)

    @Suppress("OVERRIDE_DEPRECATION")
    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        match(state, world, pos)?.craft?.invoke(world)
    }

    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        val result = match(state, world, pos)
        if (result != null) {
            world.addParticle(
                DemonParticleTypeCard.COLLECTING_MAGIC.particleType,
                pos.x.toDouble() + 0.5,
                pos.y.toDouble() + 0.8 + 1.0,
                pos.z.toDouble() + 0.5,
                result.fluidBlockPos.x.toDouble() + 0.1 + random.nextDouble() * 0.8 - (pos.x.toDouble() + 0.5),
                result.fluidBlockPos.y.toDouble() + 0.8 - (pos.y.toDouble() + 0.8 + 1.0),
                result.fluidBlockPos.z.toDouble() + 0.1 + random.nextDouble() * 0.8 - (pos.z.toDouble() + 0.5),
            )
        }
    }

    class Result(val fluidBlockPos: BlockPos, val craft: (ServerWorld) -> Unit)

    fun match(blockState: BlockState, world: World, blockPos: BlockPos): Result? {
        val facing = getFacing(blockState)
        val blockEntity = world.getBlockEntity(blockPos) as? FairyFluidDrainerBlockEntity ?: return null

        if (blockEntity.fairyInventory[0].isEmpty) return null // 妖精が居ない
        if (!blockEntity.bucketInventory[0].isOf(Items.BUCKET)) return null // 空のバケツが無い

        val frontBlockPos = blockPos.offset(facing)
        if (world.getBlockState(frontBlockPos).isSolidBlock(world, frontBlockPos)) return null // 正面が埋まっている

        val fluidBlockPos = frontBlockPos.down()
        val fluidBlockState = world.getBlockState(fluidBlockPos)
        val recipeResult = FairyFluidDrainerRecipe.RECIPES
            .asSequence()
            .mapNotNull { it.match(world, fluidBlockPos, fluidBlockState) }
            .firstOrNull() ?: return null // 該当するレシピがない

        // 成立

        return Result(fluidBlockPos) { serverWorld ->

            // 消費
            val filledBucketItemStack = recipeResult.tryDrain() ?: return@Result // 吸えなかった

            // 生産
            blockEntity.bucketInventory[0] = filledBucketItemStack
            blockEntity.markDirty()

            // エフェクト
            serverWorld.spawnCraftingCompletionParticles(Vec3d.of(blockPos).add(0.5, 0.6, 0.5))
            world.playSound(null, blockPos, recipeResult.getSoundEvent(), SoundCategory.BLOCKS, (soundGroup.volume + 1.0F) / 2.0F * 0.5F, soundGroup.pitch * 0.8F)

        }
    }

}

class FairyFluidDrainerBlockEntity(pos: BlockPos, state: BlockState) : FairyHouseBlockEntity(FairyHouseCard.FAIRY_FLUID_DRAINER.blockEntityType.feature, pos, state) {

    val fairyInventory = Inventory(1, maxCountPerStack = 1) { it.item.castOr<FairyItem> { return@Inventory false }.fairy.isLiquidFairy }.also { addInventory("FairyInventory", it) }
    val bucketInventory = Inventory(1, maxCountPerStack = 1) { it.isOf(Items.BUCKET) }.also { addInventory("BucketInventory", it) }

    override fun canInsert(slot: Int, stack: ItemStack, dir: Direction?) = super.canInsert(slot, stack, dir) && slot == 1
    override fun canExtract(slot: Int, stack: ItemStack, dir: Direction) = super.canExtract(slot, stack, dir) && slot == 1 && !bucketInventory[0].isOf(Items.BUCKET)

    override fun render(renderingProxy: RenderingProxy, tickDelta: Float, light: Int, overlay: Int) {
        val blockState = world.or { return }.getBlockState(pos)
        val block = blockState.block as? FairyHouseBlock ?: return

        renderingProxy.stack {
            renderingProxy.translate(0.5, 0.5, 0.5)
            renderingProxy.rotateY(-90F * block.getFacing(blockState).horizontal.toFloat())

            renderingProxy.renderItemStack(bucketInventory[0], 0.0, -4.0, 0.0)
            renderingProxy.renderItemStack(fairyInventory[0], 0.0, -5.0, 6.0, scale = 0.5F)
        }
    }

}

package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.RenderingProxy
import miragefairy2023.RenderingProxyBlockEntity
import miragefairy2023.api.FairyItem
import miragefairy2023.module
import miragefairy2023.util.InstrumentBlock
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
import miragefairy2023.util.text
import miragefairy2023.util.wrapper
import miragefairy2023.util.yellow
import mirrg.kotlin.hydrogen.or
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.FluidDrainable
import net.minecraft.block.LeveledCauldronBlock
import net.minecraft.block.MapColor
import net.minecraft.block.Material
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.DoubleInventory
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
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Clearable
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import kotlin.jvm.optionals.getOrNull

object FairyHouseModule {

    lateinit var fairyFluidDrainerBlock: FeatureSlot<FairyFluidDrainerBlock>
    lateinit var fairyFluidDrainerBlockEntityType: FeatureSlot<BlockEntityType<FairyFluidDrainerBlockEntity>>
    lateinit var fairyFluidDrainerBlockItem: FeatureSlot<BlockItem>

    val init = module {

        // 妖精の水汲み所
        fairyFluidDrainerBlock = block("fairy_fluid_drainer", { FairyFluidDrainerBlock(FabricBlockSettings.of(Material.METAL, MapColor.STONE_GRAY).sounds(BlockSoundGroup.METAL).requiresTool().strength(2.0F).nonOpaque()) }) {

            // レンダリング
            generateHorizontalFacingBlockState()
            onInitializeClient { MirageFairy2023.clientProxy!!.registerBlockRenderLayer(feature) }

            // 翻訳
            enJaBlock({ feature }, "Fairy Fluid Drainer", "妖精の水汲み所")
            enJa({ "${feature.translationKey}.poem" }, "Causes anti-Brownian motion", "覆水、盆に返る。")
            enJa({ "${feature.translationKey}.description" }, "Place a liquid fairy and a bucket", "液体系妖精と空バケツを配置")

            // レシピ
            onGenerateBlockTags { it(BlockTags.PICKAXE_MINEABLE).add(feature) }
            onGenerateBlockTags { it(BlockTags.NEEDS_STONE_TOOL).add(feature) }
            generateDefaultBlockLootTable()

        }
        fairyFluidDrainerBlockEntityType = blockEntity("fairy_fluid_drainer", ::FairyFluidDrainerBlockEntity, { fairyFluidDrainerBlock.feature }) {
            onInitializeClient { MirageFairy2023.clientProxy!!.registerRenderingProxyBlockEntityRendererFactory(feature) }
        }
        fairyFluidDrainerBlockItem = item("fairy_fluid_drainer", {
            object : BlockItem(fairyFluidDrainerBlock.feature, FabricItemSettings().group(commonItemGroup)) {
                override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
                    super.appendTooltip(stack, world, tooltip, context)
                    tooltip += text { translate("$translationKey.poem").gray }
                    tooltip += text { translate("$translationKey.description").yellow }
                }
            }
        })
        onGenerateRecipes {
            ShapedRecipeJsonBuilder
                .create(fairyFluidDrainerBlockItem.feature)
                .pattern("FMB")
                .pattern("III")
                .input('I', ConventionalItemTags.IRON_INGOTS)
                .input('F', Items.IRON_BARS)
                .input('M', DemonItemCard.MIRANAGITE())
                .input('B', Items.BUCKET)
                .criterion(DemonItemCard.MIRANAGITE())
                .group(fairyFluidDrainerBlockItem.feature)
                .offerTo(it, fairyFluidDrainerBlockItem.feature.identifier)
        }

    }

}

interface FairyFluidDrainerRecipe {
    fun match(world: World, blockPos: BlockPos, blockState: BlockState): FairyFluidDrainerRecipeResult?
}

interface FairyFluidDrainerRecipeResult {
    fun tryDrain(): ItemStack?
    fun getSoundEvent(): SoundEvent
}

class FairyFluidDrainerBlock(settings: Settings) : InstrumentBlock(settings), BlockEntityProvider {
    companion object {
        private val SHAPE = createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0)!!

        val RECIPES = mutableListOf<FairyFluidDrainerRecipe>()

        init {

            // 液体ブロックとか
            RECIPES += object : FairyFluidDrainerRecipe {
                override fun match(world: World, blockPos: BlockPos, blockState: BlockState): FairyFluidDrainerRecipeResult? {
                    val block = blockState.block
                    if (block !is FluidDrainable) return null
                    return object : FairyFluidDrainerRecipeResult {
                        override fun tryDrain() = block.tryDrainFluid(world, blockPos, blockState).notEmptyOrNull
                        override fun getSoundEvent() = block.bucketFillSound.getOrNull() ?: SoundEvents.ITEM_BUCKET_FILL
                    }
                }
            }

            // 水入り大釜
            RECIPES += object : FairyFluidDrainerRecipe {
                override fun match(world: World, blockPos: BlockPos, blockState: BlockState): FairyFluidDrainerRecipeResult? {
                    val block = blockState.block
                    if (!blockState.isOf(Blocks.WATER_CAULDRON)) return null
                    if (block !is LeveledCauldronBlock) return null
                    if (!block.isFull(blockState)) return null
                    return object : FairyFluidDrainerRecipeResult {
                        override fun tryDrain(): ItemStack? {
                            if (!world.setBlockState(blockPos, Blocks.CAULDRON.defaultState)) return null
                            return Items.WATER_BUCKET.createItemStack()
                        }

                        override fun getSoundEvent() = SoundEvents.ITEM_BUCKET_FILL
                    }
                }
            }

            // 溶岩入り大釜
            RECIPES += object : FairyFluidDrainerRecipe {
                override fun match(world: World, blockPos: BlockPos, blockState: BlockState): FairyFluidDrainerRecipeResult? {
                    if (!blockState.isOf(Blocks.LAVA_CAULDRON)) return null
                    return object : FairyFluidDrainerRecipeResult {
                        override fun tryDrain(): ItemStack? {
                            if (!world.setBlockState(blockPos, Blocks.CAULDRON.defaultState)) return null
                            return Items.LAVA_BUCKET.createItemStack()
                        }

                        override fun getSoundEvent() = SoundEvents.ITEM_BUCKET_FILL_LAVA
                    }
                }
            }

        }

    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext) = SHAPE

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onSyncedBlockEvent(state: BlockState, world: World, pos: BlockPos, type: Int, data: Int): Boolean {
        @Suppress("DEPRECATION")
        super.onSyncedBlockEvent(state, world, pos, type, data)
        val blockEntity = world.getBlockEntity(pos) ?: return false
        return blockEntity.onSyncedBlockEvent(type, data)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = FairyFluidDrainerBlockEntity(pos, state)

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        val blockEntity = world.getBlockEntity(pos) as? FairyFluidDrainerBlockEntity ?: return ActionResult.PASS
        val itemStack = player.getStackInHand(hand)

        // 配置
        (0 until blockEntity.size()).forEach nextSlot@{ slot ->
            if (blockEntity.getStack(slot).isNotEmpty) return@nextSlot // 埋まっている場合は次へ
            if (!blockEntity.isValid(slot, itemStack)) return@nextSlot // 入れられない場合は次へ

            // 成立

            if (world.isClient) return ActionResult.CONSUME

            blockEntity.setStack(slot, (if (player.abilities.creativeMode) itemStack.copy() else itemStack).split(1))

            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, blockEntity.cachedState))
            blockEntity.markDirty()

            return ActionResult.SUCCESS
        }

        // 回収
        (0 until blockEntity.size()).reversed().forEach nextSlot@{ slot ->
            if (blockEntity.getStack(slot).isEmpty) return@nextSlot // 空である場合は次へ

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

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
        if (!state.isOf(newState.block)) {
            val blockEntity = world.getBlockEntity(pos)
            if (blockEntity is FairyFluidDrainerBlockEntity) blockEntity.dropItems()
        }
        @Suppress("DEPRECATION")
        super.onStateReplaced(state, world, pos, newState, moved)
    }

    override fun hasRandomTicks(state: BlockState) = true

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

        if (blockEntity.fairyItemStack.isEmpty) return null // 妖精が居ない
        if (!blockEntity.bucketItemStack.isOf(Items.BUCKET)) return null // 空のバケツが無い

        val frontBlockPos = blockPos.offset(facing)
        if (world.getBlockState(frontBlockPos).isSolidBlock(world, frontBlockPos)) return null // 正面が埋まっている

        val fluidBlockPos = frontBlockPos.down()
        val fluidBlockState = world.getBlockState(fluidBlockPos)
        val recipeResult = RECIPES
            .asSequence()
            .mapNotNull { it.match(world, fluidBlockPos, fluidBlockState) }
            .firstOrNull() ?: return null // 該当するレシピがない

        // 成立

        return Result(fluidBlockPos) { serverWorld ->

            // 消費
            val filledBucketItemStack = recipeResult.tryDrain() ?: return@Result // 吸えなかった

            // 生産
            blockEntity.bucketItemStack = filledBucketItemStack
            blockEntity.markDirty()

            // エフェクト
            serverWorld.spawnParticles(
                DemonParticleTypeCard.DESCENDING_MAGIC.particleType,
                blockPos.x.toDouble() + 0.5,
                blockPos.y.toDouble() + 0.6,
                blockPos.z.toDouble() + 0.5,
                5,
                0.0,
                0.0,
                0.0,
                0.02,
            )
            world.playSound(null, blockPos, recipeResult.getSoundEvent(), SoundCategory.BLOCKS, (soundGroup.volume + 1.0F) / 2.0F * 0.5F, soundGroup.pitch * 0.8F)

        }
    }

}

class FairyFluidDrainerBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(FairyHouseModule.fairyFluidDrainerBlockEntityType.feature, pos, state), Clearable, SidedInventory, RenderingProxyBlockEntity {

    val fairyInventory = object : SimpleInventory(1) {
        override fun isValid(slot: Int, stack: ItemStack): Boolean {
            val fairyItem = stack.item as? FairyItem ?: return false
            return when (fairyItem.fairy.motif) {
                Identifier(MirageFairy2023.modId, "water") -> true // TODO respect
                Identifier(MirageFairy2023.modId, "lava") -> true
                else -> false
            }
        }

        override fun getMaxCountPerStack() = 1
    }
    val bucketInventory = object : SimpleInventory(1) {
        override fun isValid(slot: Int, stack: ItemStack) = stack.isOf(Items.BUCKET)
        override fun getMaxCountPerStack() = 1
    }
    val combinedInventory = DoubleInventory(fairyInventory, bucketInventory)

    var fairyItemStack: ItemStack
        get() = fairyInventory.getStack(0)
        set(it) {
            fairyInventory.setStack(0, it)
        }

    var bucketItemStack: ItemStack
        get() = bucketInventory.getStack(0)
        set(it) {
            bucketInventory.setStack(0, it)
        }

    override fun clear() {
        fairyInventory.clear()
        bucketInventory.clear()
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        val nbtWrapper = nbt.wrapper
        fairyInventory.clear()
        fairyInventory.readNbtList(nbtWrapper["FairyInventory"].list.get() ?: NbtList())
        bucketInventory.clear()
        bucketInventory.readNbtList(nbtWrapper["BucketInventory"].list.get() ?: NbtList())
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        val nbtWrapper = nbt.wrapper
        nbtWrapper["FairyInventory"].list.set(fairyInventory.toNbtList())
        nbtWrapper["BucketInventory"].list.set(bucketInventory.toNbtList())
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> = BlockEntityUpdateS2CPacket.create(this)

    override fun toInitialChunkDataNbt(): NbtCompound {
        val nbt = NbtCompound()
        val nbtWrapper = nbt.wrapper
        nbtWrapper["FairyInventory"].list.set(fairyInventory.toNbtList())
        nbtWrapper["BucketInventory"].list.set(bucketInventory.toNbtList())
        return nbt
    }

    fun dropItems() {
        ItemScatterer.spawn(world, pos, fairyInventory)
        ItemScatterer.spawn(world, pos, bucketInventory)
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
    override fun getMaxCountPerStack() = combinedInventory.maxCountPerStack
    override fun canPlayerUse(player: PlayerEntity) = combinedInventory.canPlayerUse(player)
    override fun isValid(slot: Int, stack: ItemStack?) = combinedInventory.isValid(slot, stack)
    override fun getAvailableSlots(side: Direction) = (0 until combinedInventory.size()).toList().toIntArray()
    override fun canInsert(slot: Int, stack: ItemStack, dir: Direction?) = getStack(slot).count < maxCountPerStack && slot == 1
    override fun canExtract(slot: Int, stack: ItemStack, dir: Direction) = getStack(slot).isNotEmpty && slot == 1 && !bucketItemStack.isOf(Items.BUCKET)

    override fun render(renderingProxy: RenderingProxy, tickDelta: Float, light: Int, overlay: Int) {
        val blockEntity = this
        val blockState = blockEntity.world.or { return }.getBlockState(blockEntity.pos)
        val block = blockState.block as? FairyFluidDrainerBlock ?: return

        renderingProxy.stack {
            renderingProxy.translate(0.5, 0.5, 0.5)
            renderingProxy.rotateY(-90F * block.getFacing(blockState).horizontal.toFloat())

            renderingProxy.stack {
                renderingProxy.translate(0.0 / 16.0, -4.0 / 16.0, -0.0 / 16.0)
                renderingProxy.renderItem(blockEntity.bucketItemStack)
            }

            renderingProxy.stack {
                renderingProxy.translate(0.0 / 16.0, -5.0 / 16.0, 6.0 / 16.0)
                renderingProxy.scale(0.5F, 0.5F, 0.5F)
                renderingProxy.renderItem(blockEntity.fairyItemStack)
            }

        }
    }

}

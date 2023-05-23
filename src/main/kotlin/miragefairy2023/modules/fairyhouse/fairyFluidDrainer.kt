package miragefairy2023.modules.fairyhouse

import miragefairy2023.RenderingProxy
import miragefairy2023.api.FairyItem
import miragefairy2023.module
import miragefairy2023.modules.DemonItemCard
import miragefairy2023.modules.DemonParticleTypeCard
import miragefairy2023.modules.fairy.isLiquidFairy
import miragefairy2023.modules.invoke
import miragefairy2023.util.EMPTY_ITEM_STACK
import miragefairy2023.util.Inventory
import miragefairy2023.util.castOr
import miragefairy2023.util.createItemStack
import miragefairy2023.util.get
import miragefairy2023.util.identifier
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.group
import miragefairy2023.util.isNotEmpty
import miragefairy2023.util.notEmptyOrNull
import miragefairy2023.util.set
import mirrg.kotlin.hydrogen.or
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.FluidDrainable
import net.minecraft.block.LeveledCauldronBlock
import net.minecraft.block.Material
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.tag.BlockTags
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import kotlin.jvm.optionals.getOrNull

val fairyFluidDrainer = FairyHouseCard(
    "fairy_fluid_drainer", ::FairyFluidDrainerBlockEntity,
    "Fairy Fluid Drainer", "妖精の水汲み所",
    "Causes anti-Brownian motion", "覆水、盆に返る。",
    "Place a liquid fairy and a bucket", "液体系妖精と空バケツを配置",
    Material.METAL, BlockSoundGroup.METAL, BlockTags.NEEDS_STONE_TOOL,
    Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
)

val fairyFluidDrainerModule = module {
    registerFairyHouse(fairyFluidDrainer)
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(fairyFluidDrainer.blockItem.feature)
            .pattern("FMB")
            .pattern("III")
            .input('I', ConventionalItemTags.IRON_INGOTS)
            .input('F', Items.IRON_BARS)
            .input('M', DemonItemCard.MIRANAGITE())
            .input('B', Items.BUCKET)
            .criterion(DemonItemCard.MIRANAGITE())
            .group(fairyFluidDrainer.blockItem.feature)
            .offerTo(it, fairyFluidDrainer.blockItem.feature.identifier)
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

class FairyFluidDrainerBlockEntity(pos: BlockPos, state: BlockState) : FairyHouseBlockEntity(fairyFluidDrainer.blockEntityType.feature, pos, state) {

    val fairyInventory = Inventory(1, maxCountPerStack = 1) { it.item.castOr<FairyItem> { return@Inventory false }.fairy.isLiquidFairy }.also { addInventory("FairyInventory", it) }
    val craftingInventory = Inventory(1, maxCountPerStack = 1) { it.isOf(Items.BUCKET) && resultInventory[0].isEmpty }.also { addInventory("BucketInventory", it) }
    val resultInventory = Inventory(1) { false }.also { addInventory("ResultInventory", it) }

    override fun canInsert(slot: Int, stack: ItemStack, dir: Direction?) = super.canInsert(slot, stack, dir) && slot == 1
    override fun canExtract(slot: Int, stack: ItemStack, dir: Direction) = super.canExtract(slot, stack, dir) && slot == 2

    override fun render(renderingProxy: RenderingProxy, tickDelta: Float, light: Int, overlay: Int) {
        val blockState = world.or { return }.getBlockState(pos)
        val block = blockState.block as? FairyHouseBlock ?: return

        renderingProxy.stack {
            renderingProxy.translate(0.5, 0.5, 0.5)
            renderingProxy.rotateY(-90F * block.getFacing(blockState).horizontal.toFloat())

            if (craftingInventory[0].isNotEmpty && resultInventory[0].isNotEmpty) {
                renderingProxy.renderItemStack(craftingInventory[0], 0.0, -4.0, 0.0)
                renderingProxy.renderItemStack(resultInventory[0], 5.0, -5.0, 5.0, scale = 0.5F)
            } else {
                renderingProxy.renderItemStack(craftingInventory[0], 0.0, -4.0, 0.0)
                renderingProxy.renderItemStack(resultInventory[0], 0.0, -4.0, 0.0)
            }
            renderingProxy.renderItemStack(fairyInventory[0], 0.0, -5.0, 6.0, scale = 0.5F)
        }
    }

    override fun randomTick(world: ServerWorld, block: FairyHouseBlock, blockPos: BlockPos, blockState: BlockState, random: Random) {
        match(world, pos, block, blockState)?.craft()
    }

    override fun randomDisplayTick(world: World, block: FairyHouseBlock, blockPos: BlockPos, blockState: BlockState, random: Random) {
        val result = match(world, pos, block, blockState)
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

    class Result<out W : World>(
        val recipeResult: FairyFluidDrainerRecipe.Result,
        val world: W,
        val blockPos: BlockPos,
        val blockEntity: FairyFluidDrainerBlockEntity,
        val fluidBlockPos: BlockPos,
    )

    fun <W : World> match(world: W, blockPos: BlockPos, block: FairyHouseBlock, blockState: BlockState): Result<W>? {
        val facing = block.getFacing(blockState)
        val blockEntity = world.getBlockEntity(blockPos) as? FairyFluidDrainerBlockEntity ?: return null

        if (blockEntity.fairyInventory[0].isEmpty) return null // 妖精が居ない
        if (!blockEntity.craftingInventory[0].isOf(Items.BUCKET)) return null // 空のバケツが無い
        if (blockEntity.resultInventory[0].isNotEmpty) return null // 出力先が埋まっている

        val frontBlockPos = blockPos.offset(facing)
        if (world.getBlockState(frontBlockPos).isSolidBlock(world, frontBlockPos)) return null // 正面が埋まっている

        val fluidBlockPos = frontBlockPos.down()
        val fluidBlockState = world.getBlockState(fluidBlockPos)
        val recipeResult = FairyFluidDrainerRecipe.RECIPES
            .asSequence()
            .mapNotNull { it.match(world, fluidBlockPos, fluidBlockState) }
            .firstOrNull() ?: return null // 該当するレシピがない

        // 成立

        return Result(recipeResult, world, blockPos, blockEntity, fluidBlockPos)
    }

    fun Result<ServerWorld>.craft() {

        // 消費
        val filledBucketItemStack = recipeResult.tryDrain() ?: return // 吸えなかった

        // 生産
        blockEntity.craftingInventory[0] = EMPTY_ITEM_STACK
        blockEntity.resultInventory[0] = filledBucketItemStack
        blockEntity.markDirty()

        // エフェクト
        world.spawnCraftingCompletionParticles(Vec3d.of(blockPos).add(0.5, 0.6, 0.5))
        world.playSound(null, blockPos, recipeResult.getSoundEvent(), SoundCategory.BLOCKS, (1.0F + 1.0F) / 2.0F * 0.5F, 1.0F * 0.8F)

    }

}

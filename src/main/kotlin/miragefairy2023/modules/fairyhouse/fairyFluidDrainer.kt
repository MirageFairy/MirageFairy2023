package miragefairy2023.modules.fairyhouse

import miragefairy2023.RenderingProxy
import miragefairy2023.api.FairyItem
import miragefairy2023.module
import miragefairy2023.modules.DemonItemCard
import miragefairy2023.modules.DemonParticleTypeCard
import miragefairy2023.modules.Description
import miragefairy2023.modules.Poem
import miragefairy2023.modules.fairy.isLiquidFairy
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
    listOf(
        Poem("Causes anti-Brownian motion", "覆水、盆に返る。"),
        Description("Place a liquid fairy and a bucket", "液体系妖精と空バケツを配置"),
    ),
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
            .input('M', DemonItemCard.MIRANAGITE.item.feature)
            .input('B', Items.BUCKET)
            .criterion(DemonItemCard.MIRANAGITE.item.feature)
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
                        override fun tryCreateDrainedItemStack() = block.tryDrainFluid(world, fluidBlockPos, fluidBlockState).notEmptyOrNull
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
                        override fun tryCreateDrainedItemStack(): ItemStack? {
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
                        override fun tryCreateDrainedItemStack(): ItemStack? {
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
        fun tryCreateDrainedItemStack(): ItemStack?
        fun getSoundEvent(): SoundEvent
    }

    fun match(world: World, fluidBlockPos: BlockPos, fluidBlockState: BlockState): Result?
}

class FairyFluidDrainerBlockEntity(pos: BlockPos, state: BlockState) : FairyHouseBlockEntity(fairyFluidDrainer.blockEntityType.feature, pos, state) {

    private val fairyInventory = Inventory(1, maxCountPerStack = 1) { it.item.castOr<FairyItem> { return@Inventory false }.fairy.isLiquidFairy }.also { addInventory("FairyInventory", it) }
    private val craftingInventory = Inventory(1, maxCountPerStack = 1) { it.isOf(Items.BUCKET) && resultInventory[0].isEmpty }.also { addInventory("BucketInventory", it) }
    private val resultInventory = Inventory(1) { false }.also { addInventory("ResultInventory", it) }

    override fun canInsert(slot: Int, stack: ItemStack, dir: Direction?) = super.canInsert(slot, stack, dir) && slot == 1
    override fun canExtract(slot: Int, stack: ItemStack, dir: Direction) = super.canExtract(slot, stack, dir) && slot == 2

    override fun render(renderingProxy: RenderingProxy, tickDelta: Float, light: Int, overlay: Int) {
        val blockState = world.or { return }.getBlockState(pos)
        val block = blockState.block as? FairyHouseBlock ?: return

        renderingProxy.stack {
            renderingProxy.translate(0.5, 0.5, 0.5)
            renderingProxy.rotateY(-90F * block.getFacing(blockState).horizontal.toFloat())

            renderingProxy.renderItemStack(fairyInventory[0], 0.0, -5.0, 6.0, scale = 0.5F)
            renderingProxy.renderItemStack(craftingInventory[0], 0.0, -4.0, 0.0)
            renderingProxy.renderItemStack(resultInventory[0], 0.0, -4.0, 0.0)
        }
    }

    override fun randomTick(world: ServerWorld, block: FairyHouseBlock, blockPos: BlockPos, blockState: BlockState, random: Random) {
        match()?.invoke(world)
    }

    override fun randomDisplayTick(world: World, block: FairyHouseBlock, blockPos: BlockPos, blockState: BlockState, random: Random) {
        match() ?: return
        val fluidBlockPos = pos.offset(block.getFacing(blockState)).down()
        world.addParticle(
            DemonParticleTypeCard.COLLECTING_MAGIC.particleType,
            pos.x.toDouble() + 0.5,
            pos.y.toDouble() + 0.8 + 1.0,
            pos.z.toDouble() + 0.5,
            fluidBlockPos.x.toDouble() + 0.1 + random.nextDouble() * 0.8 - (pos.x.toDouble() + 0.5),
            fluidBlockPos.y.toDouble() + 0.8 - (pos.y.toDouble() + 0.8 + 1.0),
            fluidBlockPos.z.toDouble() + 0.1 + random.nextDouble() * 0.8 - (pos.z.toDouble() + 0.5),
        )
    }

    private fun match(): ((ServerWorld) -> Unit)? {
        val world = world ?: return null
        val blockState = world.getBlockState(pos)
        val block = blockState.block as? FairyHouseBlock ?: return null
        val facing = block.getFacing(blockState)
        val frontBlockPos = pos.offset(facing)
        val fluidBlockPos = frontBlockPos.down()
        val fluidBlockState = world.getBlockState(fluidBlockPos)

        if (fairyInventory[0].isEmpty) return null // 妖精が居ない
        if (craftingInventory[0].count != 1) return null // 入力スロットが空かスタックされている
        if (!craftingInventory[0].isOf(Items.BUCKET)) return null // 空のバケツが無い
        if (resultInventory[0].isNotEmpty) return null // 出力スロットが埋まっている
        if (world.getBlockState(frontBlockPos).isSolidBlock(world, frontBlockPos)) return null // 正面が塞がっている

        // レシピ判定
        val result = FairyFluidDrainerRecipe.RECIPES
            .asSequence()
            .mapNotNull { it.match(world, fluidBlockPos, fluidBlockState) }
            .firstOrNull() ?: return null

        // 成立

        return craft@{ serverWorld ->

            // 消費
            val filledBucketItemStack = result.tryCreateDrainedItemStack() ?: return@craft // 吸えなかった

            // 生産
            craftingInventory[0] = EMPTY_ITEM_STACK
            resultInventory[0] = filledBucketItemStack
            markDirty()

            // エフェクト
            serverWorld.spawnCraftingCompletionParticles(Vec3d.of(pos).add(0.5, 0.6, 0.5))
            serverWorld.playSound(null, pos, result.getSoundEvent(), SoundCategory.BLOCKS, 0.5F, 0.8F)

        }
    }

}

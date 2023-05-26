package miragefairy2023.modules.fairyhouse

import miragefairy2023.MirageFairy2023
import miragefairy2023.RenderingProxy
import miragefairy2023.api.FairyItem
import miragefairy2023.module
import miragefairy2023.modules.DemonItemCard
import miragefairy2023.modules.DemonParticleTypeCard
import miragefairy2023.modules.MirageFlourCard
import miragefairy2023.modules.invoke
import miragefairy2023.modules.miranagiteBlockBlockItem
import miragefairy2023.util.Chance
import miragefairy2023.util.EMPTY_ITEM_STACK
import miragefairy2023.util.Inventory
import miragefairy2023.util.castOr
import miragefairy2023.util.createItemStack
import miragefairy2023.util.draw
import miragefairy2023.util.get
import miragefairy2023.util.identifier
import miragefairy2023.util.init.Translation
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.group
import miragefairy2023.util.init.translation
import miragefairy2023.util.isNotEmpty
import miragefairy2023.util.set
import miragefairy2023.util.text
import miragefairy2023.util.totalWeight
import mirrg.kotlin.hydrogen.formatAs
import mirrg.kotlin.hydrogen.or
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.Material
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.world.World

val fairyMetamorphosisAltar = FairyHouseCard(
    "fairy_metamorphosis_altar", ::FairyMetamorphosisAltarBlockEntity,
    "Fairy Metamorphosis Altar", "妖精の魔法の祭壇",
    listOf(
        TooltipText("poem", "Weaken the nuclear force to resonate", "妖精と無機物が心を通わすとき。", Formatting.GRAY),
        TooltipText("description", "Place 4 fairies and 1 material", "4体の妖精と素材を配置", Formatting.YELLOW),
        TooltipText("description2", "Use while sneaking to show table", "スニーク中に使用で提供割合を表示", Formatting.YELLOW),
    ),
    Material.STONE, BlockSoundGroup.STONE, null,
    Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 5.0, 15.0),
)

val fairyMetamorphosisAltarModule = module {
    registerFairyHouse(fairyMetamorphosisAltar)
    translation(FairyMetamorphosisAltarBlockEntity.INVALID_KEY)
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(fairyMetamorphosisAltar.blockItem.feature)
            .pattern(" B ")
            .pattern("GDG")
            .pattern("SSS")
            .input('B', miranagiteBlockBlockItem.feature)
            .input('G', DemonItemCard.MIRANAGITE())
            .input('D', MirageFlourCard.MIRAGE_FLOUR())
            .input('S', Blocks.STONE)
            .criterion(DemonItemCard.MIRANAGITE())
            .group(fairyMetamorphosisAltar.blockItem.feature)
            .offerTo(it, fairyMetamorphosisAltar.blockItem.feature.identifier)
    }
}

object FairyMetamorphosisAltarRecipe {

    enum class Category(val rate: Double) {
        N(1.0),
        R(0.1),
        SR(0.01),
        SSR(0.001),
    }

    val RECIPES = mutableMapOf<Item, MutableMap<Category, MutableList<ItemStack>>>()

    val DEFAULT_OUTPUT = Items.FLINT.createItemStack()

    fun register(input: Item, category: Category, output: ItemStack) {
        RECIPES.getOrPut(input) { mutableMapOf() }.getOrPut(category) { mutableListOf() } += output
    }

    /**
     * @return nullでないとき、必ず1個以上の要素が含まれ、確率の合計は100%になります。
     */
    fun getChanceTable(input: Item): List<Chance<ItemStack>>? {
        if (input !in RECIPES) return null

        val outputTable = RECIPES.getOrElse(input) { mapOf() }

        var chanceTable = listOf<Chance<ItemStack>>()
        var remainingRate = 1.0

        Category.values().sortedBy { it.rate }.forEach { category ->
            val nextRemainingRate = 1.0 - category.rate
            val outputs = outputTable.getOrElse(category) { listOf() }
            if (outputs.isNotEmpty()) {
                chanceTable = outputs.map { Chance((remainingRate - nextRemainingRate) / outputs.size.toDouble(), it) } + chanceTable
                remainingRate = nextRemainingRate
            }
        }

        if (chanceTable.isEmpty()) return null

        return chanceTable
    }

    init {
        register(Items.FLINT, Category.N, Items.STONE.createItemStack())
        register(Items.FLINT, Category.N, Items.GRANITE.createItemStack())
        register(Items.FLINT, Category.N, Items.DIORITE.createItemStack())
        register(Items.FLINT, Category.N, Items.ANDESITE.createItemStack())
        register(Items.FLINT, Category.N, Items.DEEPSLATE.createItemStack())
        register(Items.FLINT, Category.N, Items.TUFF.createItemStack())
        register(Items.FLINT, Category.N, Items.CALCITE.createItemStack())
        register(Items.FLINT, Category.N, Items.BASALT.createItemStack())
        register(Items.FLINT, Category.N, Items.BLACKSTONE.createItemStack())
        register(Items.FLINT, Category.N, Items.GLASS.createItemStack())
        register(Items.FLINT, Category.N, Items.OBSIDIAN.createItemStack())
        register(Items.FLINT, Category.N, Items.SANDSTONE.createItemStack())
        register(Items.FLINT, Category.N, Items.CLAY_BALL.createItemStack())
        register(Items.FLINT, Category.N, MirageFlourCard.VERY_RARE_MIRAGE_FLOUR().createItemStack())

        register(Items.FLINT, Category.R, Items.RAW_COPPER.createItemStack())
        register(Items.FLINT, Category.R, Items.RAW_IRON.createItemStack())
        register(Items.FLINT, Category.R, Items.RAW_GOLD.createItemStack())
        register(Items.FLINT, Category.R, Items.REDSTONE.createItemStack())
        register(Items.FLINT, Category.R, Items.LAPIS_LAZULI.createItemStack())
        register(Items.FLINT, Category.R, Items.GLOWSTONE.createItemStack())
        register(Items.FLINT, Category.R, Items.AMETHYST_SHARD.createItemStack())
        register(Items.FLINT, Category.R, DemonItemCard.MIRANAGITE().createItemStack())
        register(Items.FLINT, Category.R, DemonItemCard.XARPITE().createItemStack())
        register(Items.FLINT, Category.R, MirageFlourCard.ULTRA_RARE_MIRAGE_FLOUR().createItemStack())

        register(Items.FLINT, Category.SR, Items.DIAMOND.createItemStack())
        register(Items.FLINT, Category.SR, Items.EMERALD.createItemStack())
        register(Items.FLINT, Category.SR, DemonItemCard.CHAOS_STONE().createItemStack())
        register(Items.FLINT, Category.SR, MirageFlourCard.SUPER_RARE_MIRAGE_FLOUR().createItemStack())

        register(Items.FLINT, Category.SSR, Items.SHULKER_SHELL.createItemStack())
        register(Items.FLINT, Category.SSR, Items.TOTEM_OF_UNDYING.createItemStack())
        //register(Items.FLINT, Category.SSR, Items.NETHER_STAR.createItemStack())
        //register(Items.FLINT, Category.SSR, Items.ENCHANTED_GOLDEN_APPLE.createItemStack())
        //register(Items.FLINT, Category.SSR, Items.HEART_OF_THE_SEA.createItemStack())
        register(Items.FLINT, Category.SSR, MirageFlourCard.EXTREMELY_RARE_MIRAGE_FLOUR().createItemStack())

        register(DemonItemCard.CHAOS_STONE(), Category.N, Items.DIAMOND.createItemStack())
    }

}

class FairyMetamorphosisAltarBlockEntity(pos: BlockPos, state: BlockState) : FairyHouseBlockEntity(fairyMetamorphosisAltar.blockEntityType.feature, pos, state) {
    companion object {
        val INVALID_KEY = Translation("block.${MirageFairy2023.modId}.fairy_metamorphosis_altar.message.invalid", "Cannot be processed!", "加工ができません！")
    }

    private val fairyInventory = Inventory(4, maxCountPerStack = 1) { filterFairySlot(it) }.also { addInventory("FairyInventory", it) }
    private val craftingInventory = Inventory(1, maxCountPerStack = 1) { it.item !is FairyItem && resultInventory[0].isEmpty }.also { addInventory("CraftingInventory", it) }
    private val resultInventory = Inventory(1) { false }.also { addInventory("ResultInventory", it) }

    override fun canInsert(slot: Int, stack: ItemStack, dir: Direction?) = super.canInsert(slot, stack, dir) && slot == 4
    override fun canExtract(slot: Int, stack: ItemStack, dir: Direction) = super.canExtract(slot, stack, dir) && slot == 5

    override fun render(renderingProxy: RenderingProxy, tickDelta: Float, light: Int, overlay: Int) {
        val world = world ?: return
        val blockState = world.getBlockState(pos)
        val block = blockState.block as? FairyHouseBlock ?: return

        renderingProxy.stack {
            renderingProxy.translate(0.5, 0.5, 0.5)
            renderingProxy.rotateY(-90F * block.getFacing(blockState).horizontal.toFloat())

            renderingProxy.renderItemStack(fairyInventory[0], -5.0, -1.0, -5.0, scale = 0.5F, rotate = 180F - 45F)
            renderingProxy.renderItemStack(fairyInventory[1], 5.0, -1.0, -5.0, scale = 0.5F, rotate = 90F - 45F)
            renderingProxy.renderItemStack(fairyInventory[2], 5.0, -1.0, 5.0, scale = 0.5F, rotate = 0F - 45F)
            renderingProxy.renderItemStack(fairyInventory[3], -5.0, -1.0, 5.0, scale = 0.5F, rotate = 270F - 45F)
            renderingProxy.renderItemStack(craftingInventory[0], 0.0, 0.0, 0.0, rotate = world.time.toFloat() + tickDelta)
            renderingProxy.renderItemStack(resultInventory[0], 0.0, 0.0, 0.0, rotate = world.time.toFloat() + tickDelta)
        }
    }

    override fun randomTick(world: ServerWorld, block: FairyHouseBlock, blockPos: BlockPos, blockState: BlockState, random: Random) {
        match().or { return }.second(world)
    }

    override fun randomDisplayTick(world: World, block: FairyHouseBlock, blockPos: BlockPos, blockState: BlockState, random: Random) {
        match() ?: return
        val slot = random.nextInt(4)
        fun addParticle(xi: Float, zi: Float) {
            if (fairyInventory[slot].isNotEmpty) {
                world.addParticle(
                    DemonParticleTypeCard.ATTRACTING_MAGIC.particleType,
                    pos.x.toDouble() + (8F + xi) / 16F,
                    pos.y.toDouble() + (8F + 0F) / 16F,
                    pos.z.toDouble() + (8F + zi) / 16F,
                    pos.x.toDouble() + (8F - 0F) / 16F,
                    pos.y.toDouble() + (8F + 7F) / 16F,
                    pos.z.toDouble() + (8F - 0F) / 16F,
                )
            }
        }
        when ((block.getFacing(blockState).horizontal + slot) % 4) {
            0 -> addParticle(-4F, -4F)
            1 -> addParticle(4F, -4F)
            2 -> addParticle(4F, 4F)
            3 -> addParticle(-4F, 4F)
            else -> throw AssertionError()
        }
    }

    override fun onShiftUse(world: World, blockPos: BlockPos, blockState: BlockState, player: PlayerEntity) {
        val result = match()
        if (result != null) {
            player.sendMessage(text { "["() + craftingInventory[0].item.name + "]"() }, false)
            val totalWeight = result.first.totalWeight
            result.first.sortedBy { it.weight }.forEach { chance ->
                player.sendMessage(text { "${(chance.weight / totalWeight * 100 formatAs "%8.4f%%").replace(' ', '_')}: "() + chance.item.name }, false)
            }
        } else {
            player.sendMessage(text { INVALID_KEY() }, true)
        }
    }

    private fun match(): Pair<List<Chance<ItemStack>>, ((ServerWorld) -> Unit)>? {
        val world = world ?: return null

        val fairyLevel = (0..3).sumOf { fairyInventory[it].item.castOr<FairyItem> { return@sumOf 0 }.fairyLevel + 1 }
        if (fairyLevel <= 0) return null // 妖精が居ない
        if (craftingInventory[0].count != 1) return null // 入力スロットが空かスタックされている
        if (resultInventory[0].isNotEmpty) return null // 出力スロットが埋まっている

        val chanceTable = FairyMetamorphosisAltarRecipe.getChanceTable(craftingInventory[0].item) ?: return null // 加工できないアイテム

        // 成立

        return Pair(chanceTable) { serverWorld ->

            val output = chanceTable.draw(world.random)!!

            craftingInventory[0] = EMPTY_ITEM_STACK
            resultInventory[0] = output.copy()
            markDirty()

            serverWorld.spawnCraftingCompletionParticles(Vec3d.of(pos).add(0.5, 0.5, 0.5))
            serverWorld.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, SoundCategory.BLOCKS, 0.5F, 0.5F + world.random.nextFloat() * 1.2F)
            //serverWorld.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.BLOCKS, 0.5F, 1.0F * 1.2F) // 風鈴の音

        }
    }

}

package miragefairy2023.modules.fairyhouse

import miragefairy2023.MirageFairy2023
import miragefairy2023.RenderingProxy
import miragefairy2023.api.FairyItem
import miragefairy2023.api.PassiveSkillItem
import miragefairy2023.module
import miragefairy2023.modules.DemonItemCard
import miragefairy2023.modules.DemonParticleTypeCard
import miragefairy2023.modules.MirageFlourCard
import miragefairy2023.modules.invoke
import miragefairy2023.modules.miranagiteBlockBlockItem
import miragefairy2023.util.Chance
import miragefairy2023.util.EMPTY_ITEM_STACK
import miragefairy2023.util.Inventory
import miragefairy2023.util.TooltipText
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
import miragefairy2023.util.toList
import miragefairy2023.util.totalWeight
import mirrg.kotlin.hydrogen.atLeast
import mirrg.kotlin.hydrogen.atMost
import mirrg.kotlin.hydrogen.castOrNull
import mirrg.kotlin.hydrogen.formatAs
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
    translation(FairyMetamorphosisAltarBlockEntity.PROCESSING_SPEED_KEY)
    translation(FairyMetamorphosisAltarBlockEntity.FORTUNE_FACTOR_KEY)
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
    fun getChanceTable(input: Item, fortuneFactor: Double): List<Chance<ItemStack>>? {
        if (input !in RECIPES) return null

        val outputTable = RECIPES.getOrElse(input) { mapOf() }

        var chanceTable = listOf<Chance<ItemStack>>()
        var remainingRate = 1.0

        Category.values().sortedBy { it.rate }.forEach { category ->
            val nextRemainingRate = 1.0 - category.rate * fortuneFactor atLeast 0.0
            val availableRate = remainingRate - nextRemainingRate
            if (availableRate > 0) {
                val outputs = outputTable.getOrElse(category) { listOf() }
                if (outputs.isNotEmpty()) {
                    chanceTable = outputs.map { Chance(availableRate / outputs.size.toDouble(), it) } + chanceTable
                    remainingRate = nextRemainingRate
                }
            }
        }

        if (remainingRate > 0) chanceTable = listOf(Chance(remainingRate, DEFAULT_OUTPUT)) + chanceTable

        if (chanceTable.isEmpty()) return null

        return chanceTable
    }

    init {
        fun registerMirageFlour(item: Item) {
            register(item, Category.N, MirageFlourCard.VERY_RARE_MIRAGE_FLOUR().createItemStack())
            register(item, Category.R, MirageFlourCard.ULTRA_RARE_MIRAGE_FLOUR().createItemStack())
            register(item, Category.SR, MirageFlourCard.SUPER_RARE_MIRAGE_FLOUR().createItemStack())
            register(item, Category.SSR, MirageFlourCard.EXTREMELY_RARE_MIRAGE_FLOUR().createItemStack())
        }

        fun registerInteractive(item1: Item, category1: Category, item2: Item) {
            register(item1, category1, item2.createItemStack())
            register(item2, Category.N, item1.createItemStack())
        }


        registerMirageFlour(Items.GRAVEL)

        registerInteractive(Items.GRAVEL, Category.N, Items.STONE)
        registerInteractive(Items.GRAVEL, Category.N, Items.DIRT)
        registerInteractive(Items.GRAVEL, Category.N, Items.GRASS_BLOCK)
        registerInteractive(Items.GRAVEL, Category.N, Items.COARSE_DIRT)
        registerInteractive(Items.GRAVEL, Category.N, Items.PODZOL)
        registerInteractive(Items.GRAVEL, Category.N, Items.SAND)
        registerInteractive(Items.GRAVEL, Category.N, Items.DEEPSLATE)

        registerInteractive(Items.GRAVEL, Category.R, Items.CLAY)
        registerInteractive(Items.GRAVEL, Category.R, Items.SANDSTONE)
        registerInteractive(Items.GRAVEL, Category.R, Items.GRANITE)
        registerInteractive(Items.GRAVEL, Category.R, Items.DIORITE)
        registerInteractive(Items.GRAVEL, Category.R, Items.ANDESITE)
        registerInteractive(Items.GRAVEL, Category.R, Items.TUFF)
        registerInteractive(Items.GRAVEL, Category.R, Items.CALCITE)
        registerInteractive(Items.GRAVEL, Category.R, Items.BASALT)
        registerInteractive(Items.GRAVEL, Category.R, Items.BLACKSTONE)

        registerInteractive(Items.GRAVEL, Category.SR, Items.OBSIDIAN)
        registerInteractive(Items.GRAVEL, Category.SR, Items.MAGMA_BLOCK)


        registerMirageFlour(Items.FLINT)

        registerInteractive(Items.FLINT, Category.N, Items.GLASS)
        registerInteractive(Items.FLINT, Category.N, Items.CLAY_BALL)

        registerInteractive(Items.FLINT, Category.R, Items.RAW_COPPER)
        registerInteractive(Items.FLINT, Category.R, Items.RAW_IRON)
        registerInteractive(Items.FLINT, Category.R, Items.RAW_GOLD)
        registerInteractive(Items.FLINT, Category.R, Items.REDSTONE)
        registerInteractive(Items.FLINT, Category.R, Items.LAPIS_LAZULI)

        registerInteractive(Items.FLINT, Category.SR, Items.AMETHYST_SHARD)
        registerInteractive(Items.FLINT, Category.SR, Items.GLOWSTONE_DUST)
        registerInteractive(Items.FLINT, Category.SR, DemonItemCard.MIRANAGITE())
        registerInteractive(Items.FLINT, Category.SR, DemonItemCard.XARPITE())
        registerInteractive(Items.FLINT, Category.SR, DemonItemCard.CHAOS_STONE())

        registerInteractive(Items.FLINT, Category.SSR, Items.DIAMOND)
        registerInteractive(Items.FLINT, Category.SSR, Items.EMERALD)


        registerMirageFlour(Items.ROTTEN_FLESH)

        registerInteractive(Items.ROTTEN_FLESH, Category.N, Items.FEATHER)
        registerInteractive(Items.ROTTEN_FLESH, Category.N, Items.STRING)
        registerInteractive(Items.ROTTEN_FLESH, Category.N, Items.BONE)

        registerInteractive(Items.ROTTEN_FLESH, Category.R, Items.LEATHER)
        registerInteractive(Items.ROTTEN_FLESH, Category.R, Items.GUNPOWDER)
        registerInteractive(Items.ROTTEN_FLESH, Category.R, Items.BEEF)
        registerInteractive(Items.ROTTEN_FLESH, Category.R, Items.PORKCHOP)
        registerInteractive(Items.ROTTEN_FLESH, Category.R, Items.CHICKEN)
        registerInteractive(Items.ROTTEN_FLESH, Category.R, Items.MUTTON)

        registerInteractive(Items.ROTTEN_FLESH, Category.SR, Items.SPIDER_EYE)

        registerInteractive(Items.ROTTEN_FLESH, Category.SSR, Items.SHULKER_SHELL)


        register(DemonItemCard.CHAOS_STONE(), Category.N, Items.DIAMOND.createItemStack()) // TODO remove


        //registerInteractive(Items.FLINT, Category.SSR, Items.TOTEM_OF_UNDYING)
        //registerInteractive(Items.FLINT, Category.SSR, Items.NETHER_STAR)
        //registerInteractive(Items.FLINT, Category.SSR, Items.ENCHANTED_GOLDEN_APPLE)
        //registerInteractive(Items.FLINT, Category.SSR, Items.HEART_OF_THE_SEA)
    }

}

class FairyMetamorphosisAltarBlockEntity(pos: BlockPos, state: BlockState) : FairyHouseBlockEntity(fairyMetamorphosisAltar.blockEntityType.feature, pos, state) {
    companion object {
        val INVALID_KEY = Translation("block.${MirageFairy2023.modId}.fairy_metamorphosis_altar.message.invalid", "Cannot be processed!", "加工ができません！")
        val PROCESSING_SPEED_KEY = Translation("block.${MirageFairy2023.modId}.fairy_metamorphosis_altar.message.processing_speed", "Processing Speed", "加工速度")
        val FORTUNE_FACTOR_KEY = Translation("block.${MirageFairy2023.modId}.fairy_metamorphosis_altar.message.fortune_factor", "Fortune Factor", "幸運係数")
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
        val result = match() ?: return
        if (random.nextDouble() < result.processingSpeed) {
            result.craft(world)
        }
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
            val totalWeight = result.chanceTable.totalWeight
            result.chanceTable.sortedBy { it.weight }.forEach { chance ->
                player.sendMessage(text { "${(chance.weight / totalWeight * 100 formatAs "%8.4f%%").replace(' ', '_')}: "() + chance.item.name }, false)
            }

            player.sendMessage(text { ""() }, false)

            player.sendMessage(text { PROCESSING_SPEED_KEY() + ": "() + (result.processingSpeed * 100.0 formatAs "%.0f%%")() }, false)
            player.sendMessage(text { FORTUNE_FACTOR_KEY() + ": ×"() + (result.fortune formatAs "%.2f")() }, false)

        } else {
            player.sendMessage(text { INVALID_KEY() }, true)
        }
    }

    class Result(
        val processingSpeed: Double,
        val fortune: Double,
        val chanceTable: List<Chance<ItemStack>>,
        val craft: ((ServerWorld) -> Unit),
    )

    private fun match(): Result? {
        val world = world ?: return null

        val mana = getMana()
        if (mana <= 0.0) return null // 妖精が居ない

        val processingSpeed = getProcessingSpeed(mana)
        val fortuneFactor = getFortuneFactor(mana)

        if (craftingInventory[0].count != 1) return null // 入力スロットが空かスタックされている
        if (resultInventory[0].isNotEmpty) return null // 出力スロットが埋まっている

        val chanceTable = FairyMetamorphosisAltarRecipe.getChanceTable(craftingInventory[0].item, fortuneFactor) ?: return null // 加工できないアイテム

        // 成立

        return Result(processingSpeed, fortuneFactor, chanceTable) { serverWorld ->

            val output = chanceTable.draw(world.random)!!

            craftingInventory[0] = EMPTY_ITEM_STACK
            resultInventory[0] = output.copy()
            markDirty()

            serverWorld.spawnCraftingCompletionParticles(Vec3d.of(pos).add(0.5, 0.5, 0.5))
            serverWorld.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, SoundCategory.BLOCKS, 0.5F, 0.5F + world.random.nextFloat() * 1.2F)
            //serverWorld.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.BLOCKS, 0.5F, 1.0F * 1.2F) // 風鈴の音

        }
    }

    // TODO パッシブスキルの適用
    private fun getMana() = fairyInventory.toList().sumOf { it.item.castOrNull<PassiveSkillItem>()?.passiveSkillProvider?.mana ?: 0.0 }

    private fun getProcessingSpeed(mana: Double) = mana / 40.0 atMost 1.0

    private fun getFortuneFactor(mana: Double) = 1.0 + mana / 40.0

}

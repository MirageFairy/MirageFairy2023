package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.addAvailableParticle
import miragefairy2023.util.createItemStack
import miragefairy2023.util.get
import miragefairy2023.util.identifier
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.generateDefaultBlockLootTable
import miragefairy2023.util.init.generateHorizontalFacingBlockState
import miragefairy2023.util.init.group
import miragefairy2023.util.lib.InstrumentBlock
import miragefairy2023.util.long
import miragefairy2023.util.obtain
import miragefairy2023.util.toLocalDateTime
import miragefairy2023.util.wrapper
import mirrg.kotlin.hydrogen.floorMod
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.ShapeContext
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.tag.BlockTags
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.registry.Registry
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

object Telescope {
    val ZONE_OFFSET: ZoneOffset = ZoneOffset.ofHours(0)
    val DAY_OF_WEEK_ORIGIN = DayOfWeek.SUNDAY

    val identifier = Identifier(MirageFairy2023.modId, "telescope")
    val block = TelescopeBlock(
        FabricBlockSettings.of(Material.METAL)
            .sounds(BlockSoundGroup.COPPER)
            .strength(0.5F)
            .nonOpaque()
    )
    val item = BlockItem(block, FabricItemSettings().group(commonItemGroup))
}

val telescopeModule = module {

    // 登録
    Registry.register(Registry.BLOCK, Telescope.identifier, Telescope.block)
    Registry.register(Registry.ITEM, Telescope.identifier, Telescope.item)


    // モデル
    generateHorizontalFacingBlockState(Telescope.block, Telescope.identifier)
    onInitializeClient { MirageFairy2023.clientProxy!!.registerCutoutBlockRenderLayer(Telescope.block) }


    // 翻訳
    enJa(Telescope.block, "Minia's Telescope", "ミーニャの望遠鏡")
    val poemList = listOf(
        Poem("Tell me more about the human world!", "きみは妖精には見えないものが見えるんだね。"),
        Description("Use once a day to obtain Minia Crystals", "1日1回使用時にミーニャクリスタルを獲得"),
    )
    generatePoemList(Telescope.item, poemList)
    onRegisterItems { registerPoemList(Telescope.item, poemList) }


    // 性質
    onGenerateBlockTags { it(BlockTags.PICKAXE_MINEABLE).add(Telescope.block) }


    // レシピ

    // ドロップ
    generateDefaultBlockLootTable(Telescope.block)

    // クラフト
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(Telescope.item)
            .pattern("IIG")
            .pattern(" S ")
            .pattern("S S")
            .input('I', ConventionalItemTags.COPPER_INGOTS)
            .input('G', DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL.item)
            .input('S', Items.STICK)
            .criterion(DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL.item)
            .group(Telescope.item)
            .offerTo(it, Telescope.item.identifier)
    }

}

class TelescopeBlock(settings: Settings) : InstrumentBlock(settings) {
    companion object {
        private val FACING_TO_SHAPE: Map<Direction, VoxelShape> = mapOf(
            Direction.NORTH to createCuboidShape(4.0, 0.0, 1.0, 12.0, 16.0, 15.0),
            Direction.SOUTH to createCuboidShape(4.0, 0.0, 1.0, 12.0, 16.0, 15.0),
            Direction.WEST to createCuboidShape(1.0, 0.0, 4.0, 15.0, 16.0, 12.0),
            Direction.EAST to createCuboidShape(1.0, 0.0, 4.0, 15.0, 16.0, 12.0),
        )
    }

    override fun getPlacementDirection(playerDirection: Direction) = playerDirection

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext) = FACING_TO_SHAPE[getFacing(state)]


    @Suppress("OVERRIDE_DEPRECATION")
    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (world.isClient) return ActionResult.SUCCESS
        player as ServerPlayerEntity

        val now = Instant.now()
        val actions = getTelescopeActions(now, player)
        if (actions.isEmpty()) return ActionResult.CONSUME

        actions.forEach {
            it()
        }

        world.playSound(null, player.x, player.y, player.z, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.5F, 1.0F)

        player.lastTelescopeUseTimeProperty.set(now.toEpochMilli())
        player.syncCustomData()

        return ActionResult.CONSUME
    }

    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        val player = MirageFairy2023.clientProxy?.getClientPlayer() ?: return

        val now = Instant.now()
        val actions = getTelescopeActions(now, player)
        if (actions.isEmpty()) return

        addAvailableParticle(world, pos)
    }

}

fun getTelescopeActions(now: Instant, player: PlayerEntity): List<() -> Unit> {
    val actions = mutableListOf<() -> Unit>()

    val lastTelescopeUseTime = player.lastTelescopeUseTimeProperty.get()
    if (lastTelescopeUseTime != null) {

        val time = Instant.ofEpochMilli(lastTelescopeUseTime).toLocalDateTime(Telescope.ZONE_OFFSET)
        val lastMonthlyLimit: LocalDateTime = time.toLocalDate().withDayOfMonth(1).atStartOfDay()

        val lastWeeklyLimit: LocalDateTime = time.toLocalDate().minusDays((time.dayOfWeek.value - Telescope.DAY_OF_WEEK_ORIGIN.value floorMod 7).toLong()).atStartOfDay()
        val lastDailyLimit: LocalDateTime = time.toLocalDate().atStartOfDay()
        val nextMonthlyLimit = lastMonthlyLimit.plusMonths(1)
        val nextWeeklyLimit = lastWeeklyLimit.plusDays(7)
        val nextDailyLimit = lastDailyLimit.plusDays(1)

        val now2 = now.toLocalDateTime(Telescope.ZONE_OFFSET)
        if (now2 >= nextMonthlyLimit) {
            actions += { player.obtain(DemonItemCard.FAIRY_CRYSTAL_500.item.createItemStack(5)) }
        }
        if (now2 >= nextWeeklyLimit) {
            actions += { player.obtain(DemonItemCard.FAIRY_CRYSTAL_500.item.createItemStack(1)) }
            actions += { player.obtain(DemonItemCard.FAIRY_CRYSTAL_50.item.createItemStack(5)) }
        }
        if (now2 >= nextDailyLimit) {
            actions += { player.obtain(DemonItemCard.FAIRY_CRYSTAL_50.item.createItemStack(3)) }
        }

    } else {

        actions += { player.obtain(DemonItemCard.FAIRY_CRYSTAL_500.item.createItemStack(1)) }

    }

    return actions
}

val PlayerEntity.lastTelescopeUseTimeProperty get() = this.customData.wrapper[MirageFairy2023.modId]["mission"]["last_telescope_use_time"].long

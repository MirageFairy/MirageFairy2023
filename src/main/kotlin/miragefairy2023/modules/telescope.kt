package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.InstrumentBlock
import miragefairy2023.util.createItemStack
import miragefairy2023.util.get
import miragefairy2023.util.identifier
import miragefairy2023.util.init.FeatureSlot
import miragefairy2023.util.init.block
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.enJaBlock
import miragefairy2023.util.init.generateDefaultBlockLootTable
import miragefairy2023.util.init.generateHorizontalFacingBlockState
import miragefairy2023.util.init.group
import miragefairy2023.util.init.item
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
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

object TelescopeModule {
    val ZONE_OFFSET = ZoneOffset.ofHours(0)
    val DAY_OF_WEEK_ORIGIN = DayOfWeek.SUNDAY
}

lateinit var telescopeBlock: FeatureSlot<TelescopeBlock>
lateinit var telescopeBlockItem: FeatureSlot<BlockItem>

val telescopeModule = module {

    telescopeBlock = block("telescope", { TelescopeBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).strength(0.5F).nonOpaque()) }) {

        // レンダリング
        generateHorizontalFacingBlockState()
        onInitializeClient { MirageFairy2023.clientProxy!!.registerCutoutBlockRenderLayer(feature) }

        // 翻訳
        enJaBlock({ feature }, "Minia's Telescope", "ミーニャの望遠鏡")

        // レシピ
        onGenerateBlockTags { it(BlockTags.PICKAXE_MINEABLE).add(feature) }
        generateDefaultBlockLootTable()

    }
    telescopeBlockItem = item("telescope", { BlockItem(telescopeBlock.feature, FabricItemSettings().group(commonItemGroup)) }) {
        val poemList = listOf(
            Poem("Tell me more about the human world!", "きみは妖精には見えないものが見えるんだね。"),
            Description("Use once a day to obtain Minia Crystals", "1日1回使用時にミーニャクリスタルを獲得"),
        )
        generatePoemList(poemList)
        onRegisterItems { registerPoemList(feature, poemList) }
    }

    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(telescopeBlockItem.feature)
            .pattern("IIG")
            .pattern(" S ")
            .pattern("S S")
            .input('I', ConventionalItemTags.COPPER_INGOTS)
            .input('G', DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL())
            .input('S', Items.STICK)
            .criterion(DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL())
            .group(telescopeBlockItem.feature)
            .offerTo(it, telescopeBlockItem.feature.identifier)
    }

}

class TelescopeBlock(settings: Settings) : InstrumentBlock(settings) {
    companion object {
        private val FACING_TO_SHAPE = mapOf(
            Direction.NORTH to createCuboidShape(4.0, 0.0, 1.0, 12.0, 16.0, 15.0)!!,
            Direction.SOUTH to createCuboidShape(4.0, 0.0, 1.0, 12.0, 16.0, 15.0)!!,
            Direction.WEST to createCuboidShape(1.0, 0.0, 4.0, 15.0, 16.0, 12.0)!!,
            Direction.EAST to createCuboidShape(1.0, 0.0, 4.0, 15.0, 16.0, 12.0)!!,
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
        val actions = getTelescopeActions(now.toLocalDateTime(TelescopeModule.ZONE_OFFSET), player)


        if (actions.isNotEmpty()) {

            actions.forEach {
                it()
            }

            world.playSound(null, player.x, player.y, player.z, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.5F, 1.0F)

            player.lastTelescopeUseTimeProperty.set(now.toEpochMilli())
            syncCustomData(player)

        }

        return ActionResult.CONSUME
    }

    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        val player = MirageFairy2023.clientProxy?.getClientPlayer() ?: return

        val now = Instant.now()
        val actions = getTelescopeActions(now.toLocalDateTime(TelescopeModule.ZONE_OFFSET), player)

        if (actions.isNotEmpty()) {
            if (random.nextInt(1) == 0) {
                val x = pos.x.toDouble() + 0.0 + random.nextDouble() * 1.0
                val y = pos.y.toDouble() + 0.0 + random.nextDouble() * 0.5
                val z = pos.z.toDouble() + 0.0 + random.nextDouble() * 1.0
                world.addParticle(
                    DemonParticleTypeCard.MISSION.particleType,
                    x, y, z,
                    random.nextGaussian() * 0.00,
                    random.nextGaussian() * 0.00 + 0.4,
                    random.nextGaussian() * 0.00,
                )
            }
        }
    }

}

fun getTelescopeActions(now: LocalDateTime, player: PlayerEntity): List<() -> Unit> {
    val actions = mutableListOf<() -> Unit>()

    val lastTelescopeUseTime = player.lastTelescopeUseTimeProperty.get()
    if (lastTelescopeUseTime != null) {

        val time = Instant.ofEpochMilli(lastTelescopeUseTime).toLocalDateTime(TelescopeModule.ZONE_OFFSET)
        val lastMonthlyLimit: LocalDateTime = time.toLocalDate().withDayOfMonth(1).atStartOfDay()

        val lastWeeklyLimit: LocalDateTime = time.toLocalDate().minusDays((time.dayOfWeek.value - TelescopeModule.DAY_OF_WEEK_ORIGIN.value floorMod 7).toLong()).atStartOfDay()
        val lastDailyLimit: LocalDateTime = time.toLocalDate().atStartOfDay()
        val nextMonthlyLimit = lastMonthlyLimit.plusMonths(1)
        val nextWeeklyLimit = lastWeeklyLimit.plusDays(7)
        val nextDailyLimit = lastDailyLimit.plusDays(1)

        if (now >= nextMonthlyLimit) {
            actions += { player.obtain(DemonItemCard.FAIRY_CRYSTAL_500().createItemStack(5)) }
        }
        if (now >= nextWeeklyLimit) {
            actions += { player.obtain(DemonItemCard.FAIRY_CRYSTAL_500().createItemStack(1)) }
            actions += { player.obtain(DemonItemCard.FAIRY_CRYSTAL_50().createItemStack(5)) }
        }
        if (now >= nextDailyLimit) {
            actions += { player.obtain(DemonItemCard.FAIRY_CRYSTAL_50().createItemStack(3)) }
        }

    } else {

        actions += { player.obtain(DemonItemCard.FAIRY_CRYSTAL_500().createItemStack(1)) }

    }

    return actions
}

val PlayerEntity.lastTelescopeUseTimeProperty get() = this.customData.wrapper[MirageFairy2023.modId]["mission"]["last_telescope_use_time"].long

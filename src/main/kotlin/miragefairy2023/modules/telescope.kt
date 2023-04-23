package miragefairy2023.modules

import com.faux.customentitydata.api.CustomDataHelper
import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.concat
import miragefairy2023.util.createItemStack
import miragefairy2023.util.get
import miragefairy2023.util.gray
import miragefairy2023.util.identifier
import miragefairy2023.util.init.FeatureSlot
import miragefairy2023.util.init.Translation
import miragefairy2023.util.init.block
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.enJaBlock
import miragefairy2023.util.init.generateBlockState
import miragefairy2023.util.init.generateDefaultBlockLootTable
import miragefairy2023.util.init.group
import miragefairy2023.util.init.item
import miragefairy2023.util.init.translation
import miragefairy2023.util.jsonObjectOf
import miragefairy2023.util.jsonPrimitive
import miragefairy2023.util.long
import miragefairy2023.util.obtain
import miragefairy2023.util.text
import miragefairy2023.util.wrapper
import mirrg.kotlin.hydrogen.floorMod
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.block.AbstractFurnaceBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.ShapeContext
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.state.StateManager
import net.minecraft.state.property.DirectionProperty
import net.minecraft.state.property.Properties
import net.minecraft.tag.BlockTags
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

lateinit var telescopeBlock: FeatureSlot<TelescopeBlock>
lateinit var telescopeBlockItem: FeatureSlot<BlockItem>

val telescopeModule = module {

    telescopeBlock = block("telescope", { TelescopeBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).strength(0.5F).nonOpaque()) }) {

        // レンダリング
        generateBlockState {
            jsonObjectOf(
                "variants" to jsonObjectOf(listOf(
                    "north" to 0,
                    "south" to 180,
                    "west" to 270,
                    "east" to 90,
                ).map { (facing, y) ->
                    "facing=$facing" to jsonObjectOf(
                        "model" to "${"block/" concat id}".jsonPrimitive,
                        "y" to y.jsonPrimitive,
                    )
                }),
            )
        }
        onRegisterRenderLayers { it(feature, Unit) }

        // 翻訳
        enJaBlock({ feature }, "Minia's Telescope", "ミーニャの望遠鏡")
        enJa({ "${feature.translationKey}.poem" }, "Tell me more about the human world!", "きみは妖精には見えないものが見えるんだね。")

        // レシピ
        onGenerateBlockTags { it(BlockTags.PICKAXE_MINEABLE).add(feature) }
        generateDefaultBlockLootTable()

    }
    telescopeBlockItem = item("telescope", {
        object : BlockItem(telescopeBlock.feature, FabricItemSettings().group(commonItemGroup)) {
            override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
                super.appendTooltip(stack, world, tooltip, context)
                tooltip += text { translate("$translationKey.poem").gray }
                // TODO ミッション達成状況表示
            }
        }
    })

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

    translation(TelescopeBlock.REWARD_KEY)
    translation(TelescopeBlock.FAILURE_KEY)

}

class TelescopeBlock(settings: Settings) : Block(settings) {
    companion object {
        val FACING: DirectionProperty = Properties.HORIZONTAL_FACING
        private val FACING_TO_SHAPE = mapOf(
            Direction.NORTH to createCuboidShape(4.0, 0.0, 1.0, 12.0, 16.0, 15.0)!!,
            Direction.SOUTH to createCuboidShape(4.0, 0.0, 1.0, 12.0, 16.0, 15.0)!!,
            Direction.WEST to createCuboidShape(1.0, 0.0, 4.0, 15.0, 16.0, 12.0)!!,
            Direction.EAST to createCuboidShape(1.0, 0.0, 4.0, 15.0, 16.0, 12.0)!!,
        )

        private val ZONE_OFFSET = ZoneOffset.ofHours(0)
        private val DAY_OF_WEEK_ORIGIN = DayOfWeek.SUNDAY

        val REWARD_KEY = Translation("block.${MirageFairy2023.modId}.telescope.reward", "Acquired %s Minia Crystals!", "%sミーニャの報酬を手に入れた！")
        val FAILURE_KEY = Translation("block.${MirageFairy2023.modId}.telescope.failure", "Today's observations are recorded", "明日また観測してみよう")
    }


    // プロパティ―

    init {
        defaultState = defaultState.with(FACING, Direction.NORTH)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState = defaultState.with(FACING, ctx.playerFacing)


    // 変形

    @Suppress("OVERRIDE_DEPRECATION")
    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState = state.with(FACING, rotation.rotate(state[AbstractFurnaceBlock.FACING]))

    @Suppress("OVERRIDE_DEPRECATION")
    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState = state.rotate(mirror.getRotation(state[FACING]))


    // 形状

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext) = FACING_TO_SHAPE[state[FACING]]

    @Suppress("OVERRIDE_DEPRECATION")
    override fun canPathfindThrough(state: BlockState, world: BlockView, pos: BlockPos, type: NavigationType?) = false


    @Suppress("OVERRIDE_DEPRECATION")
    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (world.isClient) return ActionResult.SUCCESS
        player as ServerPlayerEntity


        fun Instant.toUtcLocalDateTime(): LocalDateTime = this.atOffset(ZONE_OFFSET).toLocalDateTime()
        fun LocalDateTime.toUtcInstant(): Instant = this.toInstant(ZONE_OFFSET)

        var lastTelescopeUseTime by player::lastTelescopeUseTime
        if (lastTelescopeUseTime != null) {

            val time = Instant.ofEpochMilli(lastTelescopeUseTime!!).toUtcLocalDateTime()
            val lastMonthlyLimit: LocalDateTime = time.toLocalDate().withDayOfMonth(1).atStartOfDay()

            val lastWeeklyLimit: LocalDateTime = time.toLocalDate().minusDays((time.dayOfWeek.value - DAY_OF_WEEK_ORIGIN.value floorMod 7).toLong()).atStartOfDay()
            val lastDailyLimit: LocalDateTime = time.toLocalDate().atStartOfDay()
            val nextMonthlyLimit = lastMonthlyLimit.plusMonths(1)
            val nextWeeklyLimit = lastWeeklyLimit.plusDays(7)
            val nextDailyLimit = lastDailyLimit.plusDays(1)

            val now: LocalDateTime = Instant.now().toUtcLocalDateTime()
            var success = false
            if (now >= nextMonthlyLimit) {
                player.sendMessage(text { REWARD_KEY(2500) }, false)
                player.obtain(DemonItemCard.FAIRY_CRYSTAL_500().createItemStack(5))
                success = true
            }
            if (now >= nextWeeklyLimit) {
                player.sendMessage(text { REWARD_KEY(750) }, false)
                player.obtain(DemonItemCard.FAIRY_CRYSTAL_500().createItemStack(1))
                player.obtain(DemonItemCard.FAIRY_CRYSTAL_50().createItemStack(5))
                success = true
            }
            if (now >= nextDailyLimit) {
                player.sendMessage(text { REWARD_KEY(150) }, false)
                player.obtain(DemonItemCard.FAIRY_CRYSTAL_50().createItemStack(3))
                success = true
            }
            if (success) {
                world.playSound(null, player.x, player.y, player.z, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.5F, 1.0F)
            } else {
                player.sendMessage(text { FAILURE_KEY() }, true)
            }

            lastTelescopeUseTime = now.toUtcInstant().toEpochMilli()

        } else {

            player.sendMessage(text { REWARD_KEY(500) }, false)
            player.obtain(DemonItemCard.FAIRY_CRYSTAL_500().createItemStack(1))

            world.playSound(null, player.x, player.y, player.z, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.5F, 1.0F)

            lastTelescopeUseTime = Instant.now().toEpochMilli()

        }

        return ActionResult.CONSUME
    }
}

var ServerPlayerEntity.lastTelescopeUseTime
    get() = CustomDataHelper.getPersistentData(this).wrapper[MirageFairy2023.modId]["mission"]["last_telescope_use_time"].long.get()
    set(it) {
        if (it != null) {
            CustomDataHelper.getPersistentData(this).wrapper[MirageFairy2023.modId]["mission"]["last_telescope_use_time"].long.set(it)
        } else {
            CustomDataHelper.getPersistentData(this).wrapper[MirageFairy2023.modId]["mission"]["last_telescope_use_time"].set(null)
        }
    }

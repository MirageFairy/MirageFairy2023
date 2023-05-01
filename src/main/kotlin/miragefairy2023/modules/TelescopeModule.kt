package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.InstrumentBlock
import miragefairy2023.util.createItemStack
import miragefairy2023.util.get
import miragefairy2023.util.getValue
import miragefairy2023.util.gray
import miragefairy2023.util.identifier
import miragefairy2023.util.init.FeatureSlot
import miragefairy2023.util.init.block
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.enJaBlock
import miragefairy2023.util.init.generateDefaultBlockLootTable
import miragefairy2023.util.init.generateHorizontalFacingBlockState
import miragefairy2023.util.init.group
import miragefairy2023.util.init.item
import miragefairy2023.util.jsonArrayOf
import miragefairy2023.util.jsonObjectOf
import miragefairy2023.util.jsonPrimitive
import miragefairy2023.util.long
import miragefairy2023.util.obtain
import miragefairy2023.util.setValue
import miragefairy2023.util.text
import miragefairy2023.util.toLocalDateTime
import miragefairy2023.util.wrapper
import mirrg.kotlin.hydrogen.floorMod
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.ShapeContext
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.particle.DefaultParticleType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.tag.BlockTags
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.registry.Registry
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

object TelescopeModule {

    val ZONE_OFFSET = ZoneOffset.ofHours(0)
    val DAY_OF_WEEK_ORIGIN = DayOfWeek.SUNDAY

    lateinit var telescopeBlock: FeatureSlot<TelescopeBlock>
    lateinit var telescopeBlockItem: FeatureSlot<BlockItem>

    val missionParticleType: DefaultParticleType = FabricParticleTypes.simple(true)

    val init = module {

        telescopeBlock = block("telescope", { TelescopeBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.COPPER).strength(0.5F).nonOpaque()) }) {

            // レンダリング
            generateHorizontalFacingBlockState()
            onInitializeClient { MirageFairy2023.clientProxy!!.registerBlockRenderLayer(feature) }

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

        onGenerateParticles {
            it[Identifier(modId, "mission")] = jsonObjectOf(
                "textures" to jsonArrayOf(
                    "miragefairy2023:mission".jsonPrimitive,
                ),
            )
        }

        Registry.register(Registry.PARTICLE_TYPE, Identifier(MirageFairy2023.modId, "mission"), missionParticleType)

        onInitializeClient {
            MirageFairy2023.clientProxy!!.registerParticleFactory(missionParticleType)
        }

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


        var lastTelescopeUseTime by player.lastTelescopeUseTimeProperty
        if (lastTelescopeUseTime != null) {

            val time = Instant.ofEpochMilli(lastTelescopeUseTime!!).toLocalDateTime(TelescopeModule.ZONE_OFFSET)
            val lastMonthlyLimit: LocalDateTime = time.toLocalDate().withDayOfMonth(1).atStartOfDay()

            val lastWeeklyLimit: LocalDateTime = time.toLocalDate().minusDays((time.dayOfWeek.value - TelescopeModule.DAY_OF_WEEK_ORIGIN.value floorMod 7).toLong()).atStartOfDay()
            val lastDailyLimit: LocalDateTime = time.toLocalDate().atStartOfDay()
            val nextMonthlyLimit = lastMonthlyLimit.plusMonths(1)
            val nextWeeklyLimit = lastWeeklyLimit.plusDays(7)
            val nextDailyLimit = lastDailyLimit.plusDays(1)

            val now: LocalDateTime = Instant.now().toLocalDateTime(TelescopeModule.ZONE_OFFSET)
            var success = false
            if (now >= nextMonthlyLimit) {
                player.obtain(DemonItemCard.FAIRY_CRYSTAL_500().createItemStack(5))
                success = true
            }
            if (now >= nextWeeklyLimit) {
                player.obtain(DemonItemCard.FAIRY_CRYSTAL_500().createItemStack(1))
                player.obtain(DemonItemCard.FAIRY_CRYSTAL_50().createItemStack(5))
                success = true
            }
            if (now >= nextDailyLimit) {
                player.obtain(DemonItemCard.FAIRY_CRYSTAL_50().createItemStack(3))
                success = true
            }
            if (success) {
                world.playSound(null, player.x, player.y, player.z, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.5F, 1.0F)
            }

            lastTelescopeUseTime = now.toInstant(TelescopeModule.ZONE_OFFSET).toEpochMilli()
            syncCustomData(player)

        } else {

            player.obtain(DemonItemCard.FAIRY_CRYSTAL_500().createItemStack(1))

            world.playSound(null, player.x, player.y, player.z, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.5F, 1.0F)

            lastTelescopeUseTime = Instant.now().toEpochMilli()
            syncCustomData(player)

        }

        return ActionResult.CONSUME
    }

    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {

        val lastTelescopeUseTime by MirageFairy2023.clientProxy?.getClientPlayer()?.lastTelescopeUseTimeProperty ?: return
        if (lastTelescopeUseTime != null) {

            val time = Instant.ofEpochMilli(lastTelescopeUseTime!!).toLocalDateTime(TelescopeModule.ZONE_OFFSET)
            val lastMonthlyLimit: LocalDateTime = time.toLocalDate().withDayOfMonth(1).atStartOfDay()

            val lastWeeklyLimit: LocalDateTime = time.toLocalDate().minusDays((time.dayOfWeek.value - TelescopeModule.DAY_OF_WEEK_ORIGIN.value floorMod 7).toLong()).atStartOfDay()
            val lastDailyLimit: LocalDateTime = time.toLocalDate().atStartOfDay()
            val nextMonthlyLimit = lastMonthlyLimit.plusMonths(1)
            val nextWeeklyLimit = lastWeeklyLimit.plusDays(7)
            val nextDailyLimit = lastDailyLimit.plusDays(1)

            val now: LocalDateTime = Instant.now().toLocalDateTime(TelescopeModule.ZONE_OFFSET)
            var success = false
            if (now >= nextMonthlyLimit) {
                success = true
            }
            if (now >= nextWeeklyLimit) {
                success = true
            }
            if (now >= nextDailyLimit) {
                success = true
            }
            if (!success) {
                return
            }

        }

        if (random.nextInt(1) == 0) {
            val x = pos.x.toDouble() + 0.0 + random.nextDouble() * 1.0
            val y = pos.y.toDouble() + 0.0 + random.nextDouble() * 0.5
            val z = pos.z.toDouble() + 0.0 + random.nextDouble() * 1.0
            world.addParticle(
                TelescopeModule.missionParticleType,
                x, y, z,
                random.nextGaussian() * 0.00,
                random.nextGaussian() * 0.00 + 0.4,
                random.nextGaussian() * 0.00,
            )
        }
    }

}

val PlayerEntity.lastTelescopeUseTimeProperty get() = this.customData.wrapper[MirageFairy2023.modId]["mission"]["last_telescope_use_time"].long

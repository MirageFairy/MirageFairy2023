package miragefairy2023.modules

import miragefairy2023.module
import miragefairy2023.util.applyExplosionDecay
import miragefairy2023.util.block
import miragefairy2023.util.blockStatePropertyLootCondition
import miragefairy2023.util.enJa
import miragefairy2023.util.enJaItem
import miragefairy2023.util.generateBlockLootTable
import miragefairy2023.util.generateBlockState
import miragefairy2023.util.gray
import miragefairy2023.util.item
import miragefairy2023.util.itemEntry
import miragefairy2023.util.jsonObjectOf
import miragefairy2023.util.jsonPrimitive
import miragefairy2023.util.lootPool
import miragefairy2023.util.lootTable
import miragefairy2023.util.randomInt
import miragefairy2023.util.registerGrassDrop
import miragefairy2023.util.statePredicate
import miragefairy2023.util.text
import mirrg.kotlin.hydrogen.atLeast
import mirrg.kotlin.hydrogen.atMost
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.ComposterBlock
import net.minecraft.block.FarmlandBlock
import net.minecraft.block.Fertilizable
import net.minecraft.block.Material
import net.minecraft.block.PlantBlock
import net.minecraft.block.ShapeContext
import net.minecraft.block.SideShapeType
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.client.Models
import net.minecraft.data.client.TextureKey
import net.minecraft.data.client.TextureMap
import net.minecraft.enchantment.Enchantments
import net.minecraft.item.AliasedBlockItem
import net.minecraft.item.ItemStack
import net.minecraft.loot.function.ApplyBonusLootFunction
import net.minecraft.loot.function.SetCountLootFunction
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.BlockView
import net.minecraft.world.World


lateinit var mirageFlowerBlock: () -> MirageFlowerBlock
lateinit var mirageSeedItem: () -> MirageSeedItem


val mirageFlowerModule = module {

    mirageFlowerBlock = block("mirage_flower", { MirageFlowerBlock(FabricBlockSettings.of(Material.PLANT).nonOpaque().noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GLASS)) }) {
        generateBlockState {
            jsonObjectOf(
                "variants" to jsonObjectOf((0..MirageFlowerBlock.MAX_AGE).map { age ->
                    "age=$age" to jsonObjectOf(
                        "model" to "$modId:block/mirage_flower_age$age".jsonPrimitive,
                    )
                }),
            )
        }
        onGenerateBlockStateModels { blockStateModelGenerator ->
            (0..MirageFlowerBlock.MAX_AGE).forEach { age ->
                blockStateModelGenerator.createSubModel(item, "_age$age", Models.CROSS) { TextureMap.of(TextureKey.CROSS, it) }
            }
        }
        onRegisterRenderLayers { it(item, Unit) }
        generateBlockLootTable {
            val condition = blockStatePropertyLootCondition(item) {
                properties(statePredicate {
                    exactMatch(MirageFlowerBlock.AGE, MirageFlowerBlock.MAX_AGE)
                })
            }
            lootTable {

                // 爆発時割合ロスト
                applyExplosionDecay(mirageSeedItem())

                // ベース種ドロップ
                pool(lootPool {
                    with(itemEntry(mirageSeedItem()))
                })

                // 追加種ドロップ
                pool(lootPool {
                    conditionally(condition)
                    with(itemEntry(mirageSeedItem()) {
                        apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(0.0f)))
                        apply(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.2f, 1))
                    })
                })

                // 成果物ドロップ
                pool(lootPool {
                    conditionally(condition)
                    with(itemEntry(DemonItemCard.TINY_MIRAGE_FLOUR()) {
                        apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0f, 6.0f)))
                        apply(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 2.0f, 0))
                    })
                })

            }
        }
    }

    mirageSeedItem = item("mirage_seed", { MirageSeedItem(mirageFlowerBlock(), FabricItemSettings().group(commonItemGroup)) }) {
        onGenerateItemModels { it.register(item, Models.GENERATED) }
        enJaItem({ item }, "Mirage Seed", "ミラージュの球根")
        enJa({ "${item.translationKey}.poem" }, "Scientific name: miragiume haimekunofa", "学名：ミラギウメ・ハイメクノファ")
        registerGrassDrop({ item }, 0.1)
        onRegisterRecipes {
            ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(item, 0.3f)
        }
    }

}


@Suppress("OVERRIDE_DEPRECATION")
class MirageFlowerBlock(settings: Settings) : PlantBlock(settings), Fertilizable {
    companion object {
        val AGE = Properties.AGE_3!!
        val MAX_AGE = 3
        private val AGE_TO_SHAPE = arrayOf(
            createCuboidShape(5.0, 0.0, 5.0, 11.0, 5.0, 11.0)!!,
            createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0)!!,
            createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0)!!,
            createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0)!!,
        )
    }


    // Property

    init {
        defaultState = defaultState.with(AGE, 0)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(AGE)
    }

    fun getAge(state: BlockState) = state[AGE]!!
    fun isMaxAge(state: BlockState) = getAge(state) >= MAX_AGE
    fun withAge(age: Int) = defaultState.with(AGE, age atLeast 0 atMost MAX_AGE)!!


    // 挙動
    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext) = AGE_TO_SHAPE[getAge(state)]
    override fun canPlantOnTop(floor: BlockState, world: BlockView, pos: BlockPos) = world.getBlockState(pos).isSideSolid(world, pos, Direction.UP, SideShapeType.CENTER) || floor.isOf(Blocks.FARMLAND)
    override fun getPickStack(world: BlockView, pos: BlockPos, state: BlockState) = ItemStack(mirageSeedItem())


    // 行動

    fun move(world: ServerWorld, pos: BlockPos, state: BlockState, speed: Double = 1.0) {

        val baseGrowthAmount = 0.01
        var ambientBonus = 1.0
        var farmlandBonus = 1.0

        // 光量が強いほど環境ボーナス最大+100%
        ambientBonus += run {
            val lightLevel = world.getBaseLightLevel(pos, 0)
            1.0 * lightLevel / 15.0
        }

        // 周囲が開けているほど環境ボーナス最大+100%
        ambientBonus += run {
            val blankScore = (0 until 4).sumOf {
                val targetPos = pos.add(world.random.nextBetween(-4, 4), world.random.nextBetween(1, 4), world.random.nextBetween(-4, 4))
                val blockState = world.getBlockState(targetPos)
                val blankScore = when {
                    blockState.isAir -> 2 // 空気なら2
                    !blockState.isOpaque -> 1 // 不透明でないなら1
                    else -> 0 // 不透明なら0
                }
                blankScore
            }
            1.0 * blankScore / (2 * 4).toDouble()
        }

        // 真下が湿った農地であるほど環境ボーナス最大+200%
        farmlandBonus += run {
            val floorMoisture = getFloorMoisture(world, pos.down())
            2.0 * floorMoisture / 7.0
        }


        val actualGrowthAmount = world.random.randomInt(baseGrowthAmount * ambientBonus * farmlandBonus * speed)
        val oldAge = getAge(state)
        val newAge = oldAge + actualGrowthAmount atMost MAX_AGE
        if (newAge != oldAge) {
            world.setBlockState(pos, withAge(newAge), NOTIFY_LISTENERS)
        }

    }

    /** 0 ~ 7 */
    fun getFloorMoisture(world: BlockView, pos: BlockPos): Int {
        val blockState = world.getBlockState(pos)
        return if (blockState.isOf(Blocks.FARMLAND)) {
            blockState.get(FarmlandBlock.MOISTURE)
        } else {
            0
        }
    }

    override fun hasRandomTicks(state: BlockState) = !isMaxAge(state)
    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) = move(world, pos, state)

    override fun isFertilizable(world: BlockView, pos: BlockPos, state: BlockState, isClient: Boolean) = !isMaxAge(state)
    override fun canGrow(world: World, random: Random, pos: BlockPos, state: BlockState) = true
    override fun grow(world: ServerWorld, random: Random, pos: BlockPos, state: BlockState) = move(world, pos, state, speed = 10.0)

}

class MirageSeedItem(block: Block, settings: Settings) : AliasedBlockItem(block, settings) {
    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)
        tooltip += text { translate("$translationKey.poem").gray }
    }
}

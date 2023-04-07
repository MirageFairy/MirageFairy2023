package miragefairy2023.modules

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import miragefairy2023.module
import miragefairy2023.util.FeatureSlot
import miragefairy2023.util.applyExplosionDecay
import miragefairy2023.util.block
import miragefairy2023.util.createItemStack
import miragefairy2023.util.enJa
import miragefairy2023.util.enJaItem
import miragefairy2023.util.exactMatchBlockStatePropertyLootCondition
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
import miragefairy2023.util.rangedMatchBlockStatePropertyLootCondition
import miragefairy2023.util.registerGrassDrop
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
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.client.Models
import net.minecraft.data.client.TextureKey
import net.minecraft.data.client.TextureMap
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.AliasedBlockItem
import net.minecraft.item.ItemStack
import net.minecraft.loot.condition.InvertedLootCondition
import net.minecraft.loot.condition.LootCondition
import net.minecraft.loot.condition.LootConditionType
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.loot.function.ApplyBonusLootFunction
import net.minecraft.loot.function.LootFunction
import net.minecraft.loot.function.LootFunctionType
import net.minecraft.loot.function.SetCountLootFunction
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.sound.SoundCategory
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.JsonSerializer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.util.registry.Registry
import net.minecraft.world.BlockView
import net.minecraft.world.World
import kotlin.math.pow


lateinit var mirageFlowerBlock: FeatureSlot<MirageFlowerBlock>
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
                blockStateModelGenerator.createSubModel(feature, "_age$age", Models.CROSS) { TextureMap.of(TextureKey.CROSS, it) }
            }
        }
        onRegisterRenderLayers { it(feature, Unit) }
        generateBlockLootTable {
            val age2Condition = rangedMatchBlockStatePropertyLootCondition(feature, MirageFlowerBlock.AGE, 2, 3)
            val age3Condition = exactMatchBlockStatePropertyLootCondition(feature, MirageFlowerBlock.AGE, 3)
            lootTable {

                // 爆発時割合ロスト
                applyExplosionDecay(mirageSeedItem())

                // ベース種ドロップ
                pool(lootPool {
                    conditionally(InvertedLootCondition.builder { PickedUpLootCondition() })
                    with(itemEntry(mirageSeedItem()))
                })

                // 追加種ドロップ
                pool(lootPool {
                    conditionally(age3Condition)
                    conditionally(InvertedLootCondition.builder { PickedUpLootCondition() })
                    with(itemEntry(mirageSeedItem()) {
                        apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(0.0f)))
                        apply(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.2f, 1))
                    })
                })

                // 茎ドロップ
                pool(lootPool {
                    conditionally(age2Condition)
                    conditionally(InvertedLootCondition.builder { PickedUpLootCondition() })
                    with(itemEntry(DemonItemCard.MIRAGE_STEM()))
                })

                // 花粉ドロップ
                pool(lootPool {
                    conditionally(age3Condition)
                    with(itemEntry(MirageFlourCard.TINY_MIRAGE_FLOUR()) {
                        apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0f, 6.0f)))
                        apply(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 1.0f, 0))
                        apply { ApplyLuckBonusLootFunction() }
                    })
                })

            }
        }
    }

    mirageSeedItem = item("mirage_seed", { MirageSeedItem(mirageFlowerBlock.feature, FabricItemSettings().group(commonItemGroup)) }) {
        onGenerateItemModels { it.register(item, Models.GENERATED) }
        enJaItem({ item }, "Mirage Seed", "ミラージュの球根")
        enJa({ "${item.translationKey}.poem" }, "Scientific name: miragiume haimekunofa", "学名：ミラギウメ・ハイメクノファ")
        registerGrassDrop({ item }, 0.1)
        onRegisterRecipes {
            ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(item, 0.3f)
        }
    }

    onRegisterLootConditionType {
        val serializer = object : JsonSerializer<LootCondition> {
            override fun toJson(json: JsonObject, `object`: LootCondition, context: JsonSerializationContext) = Unit
            override fun fromJson(json: JsonObject, context: JsonDeserializationContext) = PickedUpLootCondition()
        }
        pickedUpLootConditionType = Registry.register(Registry.LOOT_CONDITION_TYPE, Identifier(modId, "picked_up"), LootConditionType(serializer))
    }

    onRegisterLootFunctionType {
        val serializer = object : JsonSerializer<LootFunction> {
            override fun toJson(json: JsonObject, `object`: LootFunction, context: JsonSerializationContext) = Unit
            override fun fromJson(json: JsonObject, context: JsonDeserializationContext) = ApplyLuckBonusLootFunction()
        }
        applyLuckBonusLootFunctionType = Registry.register(Registry.LOOT_FUNCTION_TYPE, Identifier(modId, "apply_luck_bonus"), LootFunctionType(serializer))
    }

}


private lateinit var pickedUpLootConditionType: LootConditionType

private class PickingUpDummyBlockEntity : BlockEntity(BlockEntityType.CHEST, BlockPos.ORIGIN, Blocks.AIR.defaultState)

class PickedUpLootCondition : LootCondition {
    override fun getType() = pickedUpLootConditionType
    override fun test(t: LootContext) = t[LootContextParameters.BLOCK_ENTITY] is PickingUpDummyBlockEntity
}


private lateinit var applyLuckBonusLootFunctionType: LootFunctionType

class ApplyLuckBonusLootFunction : LootFunction {
    override fun getType() = applyLuckBonusLootFunctionType
    override fun apply(t: ItemStack, u: LootContext): ItemStack {
        val player = u[LootContextParameters.THIS_ENTITY] as? PlayerEntity ?: return t
        val luck = player.getAttributeValue(EntityAttributes.GENERIC_LUCK)

        val itemStack = t.copy()
        val factor = when {
            luck > 0 -> 1.0 + 0.2 * luck // 正効果
            luck < 0 -> 0.8.pow(-luck) // 負効果
            else -> 1.0
        }
        itemStack.count = u.random.randomInt(t.count * factor) atLeast 0 atMost itemStack.maxCount

        return itemStack
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
    override fun getPickStack(world: BlockView, pos: BlockPos, state: BlockState) = mirageSeedItem().createItemStack()


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

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (getAge(state) != MAX_AGE) return ActionResult.PASS
        if (world.isClient) return ActionResult.SUCCESS

        // 前提条件を計算
        world as ServerWorld
        val blockEntity = world.getBlockEntity(pos)
        val tool = player.mainHandStack

        // ドロップアイテムを計算
        val builder = LootContext.Builder(world)
            .random(world.random)
            .parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
            .parameter(LootContextParameters.BLOCK_STATE, state)
            .optionalParameter(LootContextParameters.BLOCK_ENTITY, PickingUpDummyBlockEntity())
            .optionalParameter(LootContextParameters.THIS_ENTITY, player)
            .parameter(LootContextParameters.TOOL, tool)
        val lootContext = builder.build(LootContextTypes.BLOCK)
        val lootTable = world.server.lootManager.getTable(this.getLootTableId())
        val lootItemStacks = lootTable.generateLoot(lootContext)
        val experience = world.random.randomInt(0.2)

        // アイテムを生成
        lootItemStacks.forEach { itemStack ->
            dropStack(world, pos, itemStack)
        }
        if (experience > 0) dropExperience(world, pos, experience)

        // 成長段階を消費
        world.setBlockState(pos, withAge(1), NOTIFY_LISTENERS)

        // エフェクト
        world.playSound(null, pos, soundGroup.breakSound, SoundCategory.NEUTRAL, (soundGroup.volume + 1.0f) / 2.0f * 0.5f, soundGroup.pitch * 0.8f)

        return ActionResult.CONSUME
    }

    override fun onStacksDropped(state: BlockState, world: ServerWorld, pos: BlockPos, stack: ItemStack, dropExperience: Boolean) {
        super.onStacksDropped(state, world, pos, stack, dropExperience)
        if (dropExperience) {
            val experience = world.random.randomInt(0.2)
            if (experience > 0) dropExperience(world, pos, experience)
        }
    }

}

class MirageSeedItem(block: Block, settings: Settings) : AliasedBlockItem(block, settings) {
    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)
        tooltip += text { translate("$translationKey.poem").gray }
    }
}

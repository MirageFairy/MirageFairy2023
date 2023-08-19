package miragefairy2023.modules

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.concat
import miragefairy2023.util.createItemStack
import miragefairy2023.util.datagen.ExactMatchBlockStatePropertyLootCondition
import miragefairy2023.util.datagen.ItemLootPoolEntry
import miragefairy2023.util.datagen.LootPool
import miragefairy2023.util.datagen.LootTable
import miragefairy2023.util.datagen.RangedMatchBlockStatePropertyLootCondition
import miragefairy2023.util.datagen.TextureMap
import miragefairy2023.util.datagen.applyExplosionDecay
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.generateBlockLootTable
import miragefairy2023.util.init.generateBlockState
import miragefairy2023.util.init.registerGrassDrop
import miragefairy2023.util.jsonObjectOf
import miragefairy2023.util.jsonPrimitive
import miragefairy2023.util.randomInt
import miragefairy2023.util.with
import mirrg.kotlin.hydrogen.atLeast
import mirrg.kotlin.hydrogen.atMost
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
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
import net.minecraft.data.client.Models
import net.minecraft.data.client.TextureKey
import net.minecraft.data.client.TextureMap
import net.minecraft.enchantment.Enchantments
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
import net.minecraft.loot.function.SetCountLootFunction
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.sound.SoundCategory
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.JsonSerializer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryEntry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.PlacedFeature
import net.minecraft.world.gen.feature.PlacedFeatures
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier
import net.minecraft.world.gen.stateprovider.BlockStateProvider

object MirageFlower {
    val identifier = Identifier(MirageFairy2023.modId, "mirage_flower")
    val block = MirageFlowerBlock(
        FabricBlockSettings.of(Material.PLANT)
            .nonOpaque()
            .noCollision()
            .ticksRandomly()
            .breakInstantly()
            .sounds(BlockSoundGroup.GLASS)
    )
    val seedIdentifier = Identifier(MirageFairy2023.modId, "mirage_seed")
    val seedItem = AliasedBlockItem(block, FabricItemSettings().group(commonItemGroup))
}


val mirageFlowerModule = module {

    // 登録
    Registry.register(Registry.BLOCK, MirageFlower.identifier, MirageFlower.block)
    Registry.register(Registry.ITEM, MirageFlower.seedIdentifier, MirageFlower.seedItem)


    // モデル

    // BlockState
    generateBlockState(MirageFlower.block) {
        jsonObjectOf(
            "variants" to jsonObjectOf((0..MirageFlowerBlock.MAX_AGE).map { age ->
                "age=$age" to jsonObjectOf(
                    "model" to "${"block/" concat MirageFlower.identifier concat "_age$age"}".jsonPrimitive,
                )
            }),
        )
    }

    // サイズごとのモデル
    onGenerateBlockStateModels { blockStateModelGenerator ->
        (0..MirageFlowerBlock.MAX_AGE).forEach { age ->
            val textureMap = TextureMap(TextureKey.CROSS to TextureMap.getSubId(MirageFlower.block, "_age$age"))
            Models.CROSS.upload(MirageFlower.block, "_age$age", textureMap, blockStateModelGenerator.modelCollector)
        }
    }

    // アイテムモデル
    onGenerateItemModels { it.register(MirageFlower.seedItem, Models.GENERATED) }

    // レンダリング
    onInitializeClient { MirageFairy2023.clientProxy!!.registerCutoutBlockRenderLayer(MirageFlower.block) }


    // 翻訳
    enJa(MirageFlower.seedItem, "Mirage Seed", "ミラージュの球根")
    val poemList = listOf(
        Poem("poem1", "Evolution to escape extermination", "最高に可憐にして益々人畜無害たる怪物。"),
        Poem("poem2", "Order Miragales, family Miragaceae", "妖花目ミラージュ科"),
    )
    generatePoemList(MirageFlower.seedItem, poemList)
    onRegisterItems { registerPoemList(MirageFlower.seedItem, poemList) }


    // 性質
    //onGenerateBlockTags { it(BlockTags.SMALL_FLOWERS).add(feature) } // これをやるとエンダーマンが勝手に引っこ抜いていく


    // レシピ

    // ドロップ
    generateBlockLootTable(MirageFlower.block) {
        val age2Condition = RangedMatchBlockStatePropertyLootCondition(MirageFlower.identifier, MirageFlowerBlock.AGE, 2, 3)
        val age3Condition = ExactMatchBlockStatePropertyLootCondition(MirageFlower.identifier, MirageFlowerBlock.AGE, 3)
        LootTable(
            LootPool(ItemLootPoolEntry(MirageFlower.seedItem)) { // ベース種ドロップ
                conditionally(InvertedLootCondition.builder { PickedUpLootCondition() })
            },
            LootPool(ItemLootPoolEntry(MirageFlower.seedItem) {
                apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(0.0F)))
                apply(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.2F, 1))
            }) { // 追加種ドロップ
                conditionally(age3Condition)
                conditionally(InvertedLootCondition.builder { PickedUpLootCondition() })
            },
            LootPool(ItemLootPoolEntry(DemonItemCard.MIRAGE_STEM.item)) { // 茎ドロップ
                conditionally(age2Condition)
                conditionally(InvertedLootCondition.builder { PickedUpLootCondition() })
            },
            LootPool(ItemLootPoolEntry(DemonItemCard.TINY_MIRAGE_FLOUR.item) {
                apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 6.0F)))
                apply(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 1.0F, 0))
                apply { ApplyLuckBonusLootFunction(0.2) }
            }) { // 花粉ドロップ
                conditionally(age3Condition)
            },
        ) {
            applyExplosionDecay(MirageFlower.seedItem) // 爆発時割合ロスト
        }
    }

    // 種は雑草から得られる
    registerGrassDrop(MirageFlower.seedItem, 0.1)

    // 種はコンポスターに投入可能
    onRegisterRecipes { ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(MirageFlower.seedItem, 0.3F) }

    // ミラージュの小さな塊の地形生成
    onRegisterRecipes {
        val blockStateProvider = BlockStateProvider.of(MirageFlower.block.withAge(MirageFlowerBlock.MAX_AGE))
        val identifier = Identifier(modId, "mirage_flower_cluster")
        val configuredFeature = Feature.FLOWER with RandomPatchFeatureConfig(6, 6, 2, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, SimpleBlockFeatureConfig(blockStateProvider)))
        val placedFeature = PlacedFeature(
            RegistryEntry.of(configuredFeature), listOf(
                RarityFilterPlacementModifier.of(16),
                SquarePlacementModifier.of(),
                PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
                BiomePlacementModifier.of(),
            )
        )

        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, identifier, configuredFeature)
        Registry.register(BuiltinRegistries.PLACED_FEATURE, identifier, placedFeature)
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.VEGETAL_DECORATION, RegistryKey.of(Registry.PLACED_FEATURE_KEY, identifier))
    }

    // ミラージュの大きな塊の地形生成
    onRegisterRecipes {
        val blockStateProvider = BlockStateProvider.of(MirageFlower.block.withAge(MirageFlowerBlock.MAX_AGE))
        val identifier = Identifier(modId, "large_mirage_flower_cluster")
        val configuredFeature = Feature.FLOWER with RandomPatchFeatureConfig(100, 8, 3, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, SimpleBlockFeatureConfig(blockStateProvider)))
        val placedFeature = PlacedFeature(
            RegistryEntry.of(configuredFeature), listOf(
                RarityFilterPlacementModifier.of(600),
                SquarePlacementModifier.of(),
                PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
                BiomePlacementModifier.of(),
            )
        )

        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, identifier, configuredFeature)
        Registry.register(BuiltinRegistries.PLACED_FEATURE, identifier, placedFeature)
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.VEGETAL_DECORATION, RegistryKey.of(Registry.PLACED_FEATURE_KEY, identifier))
    }


    // 右クリック収穫によるドロップか否かを識別するドロップ条件
    onRegisterLootConditionType {
        val serializer = object : JsonSerializer<LootCondition> {
            override fun toJson(json: JsonObject, `object`: LootCondition, context: JsonSerializationContext) = Unit
            override fun fromJson(json: JsonObject, context: JsonDeserializationContext) = PickedUpLootCondition()
        }
        pickedUpLootConditionType = Registry.register(Registry.LOOT_CONDITION_TYPE, Identifier(modId, "picked_up"), LootConditionType(serializer))
    }

}


private lateinit var pickedUpLootConditionType: LootConditionType

private class PickingUpDummyBlockEntity : BlockEntity(BlockEntityType.CHEST, BlockPos.ORIGIN, Blocks.AIR.defaultState)

class PickedUpLootCondition : LootCondition {
    override fun getType() = pickedUpLootConditionType
    override fun test(t: LootContext) = t[LootContextParameters.BLOCK_ENTITY] is PickingUpDummyBlockEntity
}


@Suppress("OVERRIDE_DEPRECATION")
class MirageFlowerBlock(settings: Settings) : PlantBlock(settings), Fertilizable {
    companion object {
        val AGE = Properties.AGE_3!!
        val MAX_AGE = 3
        private val AGE_TO_SHAPE: Array<VoxelShape> = arrayOf(
            createCuboidShape(5.0, 0.0, 5.0, 11.0, 5.0, 11.0),
            createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0),
            createCuboidShape(2.0, 0.0, 2.0, 14.0, 15.0, 14.0),
            createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0),
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
    fun withAge(age: Int): BlockState = defaultState.with(AGE, age atLeast 0 atMost MAX_AGE)


    // 挙動
    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext) = AGE_TO_SHAPE[getAge(state)]
    override fun canPlantOnTop(floor: BlockState, world: BlockView, pos: BlockPos) = world.getBlockState(pos).isSideSolid(world, pos, Direction.UP, SideShapeType.CENTER) || floor.isOf(Blocks.FARMLAND)
    override fun getPickStack(world: BlockView, pos: BlockPos, state: BlockState) = MirageFlower.seedItem.createItemStack()


    // 行動

    fun move(world: ServerWorld, pos: BlockPos, state: BlockState, speed: Double = 1.0) {

        if (isStacked(world, pos)) return // この花の上に別のミラージュの花が存在する場合は阻害により成長しない

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
                    !blockState.isSolidBlock(world, targetPos) -> 1 // 固体ブロックなら1
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

    private fun isStacked(world: BlockView, blockPos: BlockPos): Boolean {
        (blockPos.y + 1 until world.height).forEach { y ->
            if (world.getBlockState(BlockPos(blockPos.x, y, blockPos.z)).block is MirageFlowerBlock) return true
        }
        return false
    }

    override fun hasRandomTicks(state: BlockState) = !isMaxAge(state)
    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) = move(world, pos, state)

    override fun isFertilizable(world: BlockView, pos: BlockPos, state: BlockState, isClient: Boolean) = !isMaxAge(state) && !isStacked(world, pos)
    override fun canGrow(world: World, random: Random, pos: BlockPos, state: BlockState) = true
    override fun grow(world: ServerWorld, random: Random, pos: BlockPos, state: BlockState) = move(world, pos, state, speed = 10.0)

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (getAge(state) != MAX_AGE) return ActionResult.PASS
        if (world.isClient) return ActionResult.SUCCESS

        // 前提条件を計算
        world as ServerWorld
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
        world.playSound(null, pos, soundGroup.breakSound, SoundCategory.BLOCKS, (soundGroup.volume + 1.0F) / 2.0F * 0.5F, soundGroup.pitch * 0.8F)

        return ActionResult.CONSUME
    }

    @Suppress("DEPRECATION")
    override fun onStacksDropped(state: BlockState, world: ServerWorld, pos: BlockPos, stack: ItemStack, dropExperience: Boolean) {
        super.onStacksDropped(state, world, pos, stack, dropExperience)
        if (dropExperience) {
            val experience = world.random.randomInt(0.2)
            if (experience > 0) dropExperience(world, pos, experience)
        }
    }

    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        if (getAge(state) >= 3) {
            if (random.nextInt(20) == 0) {
                world.addParticle(
                    DemonParticleTypeCard.MIRAGE_FLOUR.particleType,
                    pos.x.toDouble() + 8.0 / 16.0,
                    pos.y.toDouble() + 14.0 / 16.0,
                    pos.z.toDouble() + 8.0 / 16.0,
                    random.nextGaussian() * 0.04,
                    random.nextGaussian() * 0.01 + 0.01,
                    random.nextGaussian() * 0.04,
                )
            }
        }
    }

}

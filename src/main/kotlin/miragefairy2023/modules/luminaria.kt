package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.concat
import miragefairy2023.util.init.applyExplosionDecay
import miragefairy2023.util.init.enJaBlock
import miragefairy2023.util.init.generateBlockLootTable
import miragefairy2023.util.init.generateBlockState
import miragefairy2023.util.init.itemEntry
import miragefairy2023.util.init.lootPool
import miragefairy2023.util.init.lootTable
import miragefairy2023.util.jsonObjectOf
import miragefairy2023.util.jsonPrimitive
import miragefairy2023.util.randomInt
import miragefairy2023.util.with
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.Material
import net.minecraft.block.PlantBlock
import net.minecraft.block.ShapeContext
import net.minecraft.block.SideShapeType
import net.minecraft.data.client.Models
import net.minecraft.data.client.TextureKey
import net.minecraft.data.client.TextureMap
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.loot.condition.MatchToolLootCondition
import net.minecraft.loot.function.ApplyBonusLootFunction
import net.minecraft.loot.function.SetCountLootFunction
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.predicate.NumberRange
import net.minecraft.predicate.item.EnchantmentPredicate
import net.minecraft.predicate.item.ItemPredicate
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.tag.BlockTags
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
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

enum class LuminariaCard(
    val path: String,
    val en: String,
    val ja: String,
    val poemList: List<Poem>,
    val spawnCondition: ((BiomeSelectionContext) -> Boolean)?,
    val drop: Pair<() -> Item, Double>?,
    val dropExperience: Boolean,
) {
    LUMINARIA(
        "luminaria", "Luminaria", "ルミナリア",
        listOf(
            Poem("poem1", "Closely related to the dayflower", "多くの者が栽培を試みた、神秘の果実。"),
            Poem("poem2", "Order Miragales, family Luminariaceae", "妖花目ルミナリア科"),
        ),
        null, null, false,
    ),
    DIAMOND_LUMINARIA(
        "diamond_luminaria", "Diamond Luminaria", "ダイヤモンドルミナリア",
        listOf(Poem("Fruits the crystallized carbon", "表土に輝く、凍てつく星。")),
        { it.hasTag(ConventionalBiomeTags.CLIMATE_COLD) }, Pair({ Items.DIAMOND }, 1.0), true,
    ),
    EMERALD_LUMINARIA(
        "emerald_luminaria", "Emerald Luminaria", "エメラルドルミナリア",
        listOf(Poem("Makes Berryllium by unknown means", "幸福もたらす、栄光の樹。")),
        { !it.hasTag(ConventionalBiomeTags.CLIMATE_COLD) }, Pair({ Items.EMERALD }, 1.0), true,
    ),
    LAPIS_LAZULI_LUMINARIA(
        "lapis_lazuli_luminaria", "Lapis Lazuli Luminaria", "ラピスラズリルミナリア",
        listOf(Poem("Contrary to tales, it's just a rock", "虚空を貫く、魔の残光。")),
        { it.hasTag(ConventionalBiomeTags.CLIMATE_WET) }, Pair({ Items.LAPIS_LAZULI }, 4.0), true,
    ),
    REDSTONE_LUMINARIA(
        "redstone_luminaria", "Redstone Luminaria", "レッドストーンルミナリア",
        listOf(Poem("This plant may have \"consciousness\"", "漆黒を照らす、知の波動。")),
        { !it.hasTag(ConventionalBiomeTags.CLIMATE_WET) }, Pair({ Items.REDSTONE }, 4.0), true,
    ),
    ;

    val identifier = Identifier(MirageFairy2023.modId, path)
    val block = LuminariaBlock(this, FabricBlockSettings.of(Material.PLANT)
        .nonOpaque()
        .noCollision()
        .strength(0.2F)
        .luminance(15)
        .emissiveLighting { _, _, _ -> true }
        .sounds(BlockSoundGroup.GRASS))
    val item = BlockItem(block, FabricItemSettings().group(commonItemGroup))
}

val luminariaModule = module {
    LuminariaCard.values().forEach { card ->

        // 登録
        Registry.register(Registry.BLOCK, card.identifier, card.block)
        Registry.register(Registry.ITEM, card.identifier, card.item)


        // 見た目

        // モデル
        generateBlockState(card.block) {
            jsonObjectOf(
                "variants" to jsonObjectOf(
                    "" to jsonObjectOf(
                        "model" to "${"block/" concat card.identifier}".jsonPrimitive,
                    )
                ),
            )
        }
        onGenerateBlockStateModels { blockStateModelGenerator ->
            blockStateModelGenerator.registerItemModel(card.block)
            blockStateModelGenerator.createSubModel(card.block, "", Models.CROSS) { TextureMap.of(TextureKey.CROSS, it) }
        }
        onInitializeClient { MirageFairy2023.clientProxy!!.registerCutoutBlockRenderLayer(card.block) }

        // 翻訳
        enJaBlock({ card.block }, card.en, card.ja)
        generatePoemList(card.item, card.poemList)
        onRegisterItems { registerPoemList(card.item, card.poemList) }


        // 性質
        onGenerateBlockTags { it(BlockTags.AXE_MINEABLE).add(card.block) }
        // onGenerateBlockTags { it(BlockTags.SMALL_FLOWERS).add(card.block) } // これをやるとエンダーマンが勝手に引っこ抜いていく


        // レシピ

        // 地形生成
        if (card.spawnCondition != null) {
            onRegisterRecipes {
                val blockStateProvider = BlockStateProvider.of(card.block)
                val identifier = card.identifier concat "_cluster"
                val configuredFeature = Feature.FLOWER with RandomPatchFeatureConfig(6, 6, 2, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, SimpleBlockFeatureConfig(blockStateProvider)))
                val placedFeature = PlacedFeature(
                    RegistryEntry.of(configuredFeature), listOf(
                        RarityFilterPlacementModifier.of(100),
                        SquarePlacementModifier.of(),
                        PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
                        BiomePlacementModifier.of(),
                    )
                )

                Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, identifier, configuredFeature)
                Registry.register(BuiltinRegistries.PLACED_FEATURE, identifier, placedFeature)
                BiomeModifications.addFeature(card.spawnCondition, GenerationStep.Feature.VEGETAL_DECORATION, RegistryKey.of(Registry.PLACED_FEATURE_KEY, identifier))
            }
        }

        // ドロップ
        generateBlockLootTable(card.block) {
            lootTable {
                if (card.drop != null) { // 原種の植物と収穫物をドロップする
                    val withSilkTouchCondition = MatchToolLootCondition.builder(ItemPredicate.Builder.create().enchantment(EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(1))))
                    val withoutSilkTouchCondition = withSilkTouchCondition.invert()
                    pool(lootPool {
                        conditionally(withSilkTouchCondition)
                        with(itemEntry(card.block) { // その植物自体
                            applyExplosionDecay(card.block)
                        })
                    })
                    pool(lootPool {
                        conditionally(withoutSilkTouchCondition)
                        with(itemEntry(LuminariaCard.LUMINARIA.item) { // 原種の植物
                            applyExplosionDecay(card.block)
                        })
                    })
                    pool(lootPool {
                        conditionally(withoutSilkTouchCondition)
                        with(itemEntry(card.drop.first) { // 収穫物
                            applyExplosionDecay(card.block)
                            apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(card.drop.second.toFloat())))
                            apply(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5F * card.drop.second.toFloat(), 0))
                            apply { ApplyLuckBonusLootFunction(0.2 * card.drop.second) }
                        })
                    })
                } else { // 常にそれ自体をドロップする
                    pool(lootPool {
                        with(itemEntry(card.block) { // その植物自体
                            applyExplosionDecay(card.block)
                        })
                    })
                }
            }
        }

    }
}

@Suppress("OVERRIDE_DEPRECATION")
class LuminariaBlock(private val card: LuminariaCard, setting: Settings) : PlantBlock(setting) {
    companion object {
        val SHAPE: VoxelShape = createCuboidShape(2.0, 0.0, 2.0, 14.0, 14.0, 14.0)
    }


    // 挙動
    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext) = SHAPE
    override fun canPlantOnTop(floor: BlockState, world: BlockView, pos: BlockPos) = world.getBlockState(pos).isSideSolid(world, pos, Direction.UP, SideShapeType.CENTER) || floor.isOf(Blocks.FARMLAND)


    // イベント
    override fun onStacksDropped(state: BlockState, world: ServerWorld, pos: BlockPos, stack: ItemStack, dropExperience: Boolean) {
        @Suppress("DEPRECATION")
        super.onStacksDropped(state, world, pos, stack, dropExperience)
        if (!dropExperience) return
        if (!card.dropExperience) return
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) != 0) return
        val experience = world.random.randomInt(10.0)
        if (experience <= 0) return
        dropExperience(world, pos, experience)
    }

    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        if (random.nextInt(5) == 0) {
            world.addParticle(
                DemonParticleTypeCard.MISSION.particleType,
                pos.x.toDouble() + 2.0 / 16.0 + random.nextDouble() * 12.0 / 16.0,
                pos.y.toDouble() + 2.0 / 16.0 + random.nextDouble() * 12.0 / 16.0,
                pos.z.toDouble() + 2.0 / 16.0 + random.nextDouble() * 12.0 / 16.0,
                random.nextGaussian() * 0.04,
                random.nextGaussian() * 0.02 + 0.02,
                random.nextGaussian() * 0.04,
            )
        }
    }

}

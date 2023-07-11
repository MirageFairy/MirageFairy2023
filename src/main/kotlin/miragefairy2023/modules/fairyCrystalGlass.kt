package miragefairy2023.modules

import com.google.gson.JsonElement
import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.concat
import miragefairy2023.util.datagen.enJaBlock
import miragefairy2023.util.datagen.generateBlockState
import miragefairy2023.util.identifier
import miragefairy2023.util.init.FeatureSlot
import miragefairy2023.util.init.block
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.generateDefaultBlockLootTable
import miragefairy2023.util.init.group
import miragefairy2023.util.init.item
import miragefairy2023.util.jsonArrayOf
import miragefairy2023.util.jsonObjectOf
import miragefairy2023.util.jsonPrimitive
import miragefairy2023.util.string
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.AbstractGlassBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ConnectingBlock
import net.minecraft.block.Material
import net.minecraft.data.client.Model
import net.minecraft.data.client.TextureKey
import net.minecraft.data.client.TextureMap
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemPlacementContext
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.tag.BlockTags
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.WorldAccess
import java.util.Optional
import java.util.function.BiConsumer
import java.util.function.Supplier


enum class FairyCrystalGlassCard(
    val path: String,
    val gemItemGetter: () -> Item,
    val enName: String,
    val jaName: String,
    val poemList: List<Poem>,
) {
    ARTIFICIAL_FAIRY_CRYSTAL_GLASS(
        "artificial_fairy_crystal_glass", { DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL.item.feature },
        "Artificial Fairy Crystal Glass", "人工フェアリークリスタルガラス",
        listOf(Poem("Fairies fear its distorted molecule", "窓を潤す、模造の美学。")),
    ),
    FAIRY_CRYSTAL_50_GLASS(
        "fairy_crystal_50_glass", { DemonItemCard.FAIRY_CRYSTAL_50.item.feature },
        "50 Minia Crystal Glass", "50ミーニャクリスタルガラス",
        listOf(Poem("Popular among artifact fairies", "家の外を映し出す鏡。")),
    ),
    //FAIRY_CRYSTAL_100_GLASS(
    //    "fairy_crystal_100_glass", { DemonItemCard.FAIRY_CRYSTAL_100.item.feature },
    //    "100 Minia Crystal Glass", "100ミーニャクリスタルガラス",
    //    "", "", // TODO
    //),
    //FAIRY_CRYSTAL_500_GLASS(
    //    "fairy_crystal_500_glass", { DemonItemCard.FAIRY_CRYSTAL_500.item.feature },
    //    "500 Minia Crystal Glass", "500ミーニャクリスタルガラス",
    //    "", "の壁に咲く、", // TODO
    //),
    // TODO 残りのミーニャ系ガラス
    // TODO 名誉系ガラス
    ;

    lateinit var block: FeatureSlot<Block>
    lateinit var item: FeatureSlot<BlockItem>
}


val fairyCrystalGlassModule = module {

    // 各ガラス初期化
    FairyCrystalGlassCard.values().forEach { card ->

        // ブロック
        card.block = block(card.path, {
            FairyCrystalGlassBlock(
                FabricBlockSettings.of(Material.GLASS)
                    .strength(1.5F)
                    .sounds(BlockSoundGroup.GLASS)
                    .nonOpaque()
                    .allowsSpawning { _, _, _, _ -> false }
                    .solidBlock { _, _, _ -> false }
                    .suffocates { _, _, _ -> false }
                    .blockVision { _, _, _ -> false })
        }) {

            // BlockStateファイル
            generateBlockState({ feature }) {
                fun createPart(direction: String, x: Int, y: Int) = jsonObjectOf(
                    "when" to jsonObjectOf(
                        direction to "false".jsonPrimitive,
                    ),
                    "apply" to jsonObjectOf(
                        "model" to "${"block/" concat id concat "_frame"}".jsonPrimitive,
                        "x" to x.jsonPrimitive,
                        "y" to y.jsonPrimitive,
                    ),
                )
                jsonObjectOf(
                    "multipart" to jsonArrayOf(
                        createPart("north", 90, 0),
                        createPart("east", 90, 90),
                        createPart("south", -90, 0),
                        createPart("west", 90, -90),
                        createPart("up", 0, 0),
                        createPart("down", 180, 0),
                    ),
                )
            }

            // インベントリ内のモデル
            onGenerateBlockStateModels { blockStateModelGenerator ->
                FairyCrystalGlassBlockModel().upload(feature, TextureMap().apply {
                    put(TextureKey.TEXTURE, TextureMap.getSubId(feature, "_frame"))
                }, blockStateModelGenerator.modelCollector)
            }

            // 枠パーツモデル
            onGenerateBlockStateModels { blockStateModelGenerator ->
                FairyCrystalGlassFrameBlockModel().upload(feature, "_frame", TextureMap().apply {
                    put(TextureKey.TEXTURE, TextureMap.getSubId(feature, "_frame"))
                }, blockStateModelGenerator.modelCollector)
            }

            // レンダリング関連
            onInitializeClient { MirageFairy2023.clientProxy!!.registerCutoutBlockRenderLayer(feature) }

            // 翻訳
            enJaBlock({ feature }, card.enName, card.jaName)

            // レシピ
            onGenerateBlockTags { it(BlockTags.IMPERMEABLE).add(feature) }
            onGenerateBlockTags { it(BlockTags.PICKAXE_MINEABLE).add(feature) }
            generateDefaultBlockLootTable { feature }

        }

        // アイテム
        card.item = item(card.path, { BlockItem(card.block.feature, FabricItemSettings().group(commonItemGroup)) }) {

            // ポエム
            generatePoemList({ feature }, card.poemList)
            onRegisterItems { registerPoemList(feature, card.poemList) }

        }

        // 変換レシピ
        onGenerateRecipes {

            // 圧縮
            ShapelessRecipeJsonBuilder
                .create(card.item.feature, 1)
                .input(card.gemItemGetter(), 9)
                .criterion(card.gemItemGetter())
                .group(card.item.feature)
                .offerTo(it, card.item.feature.identifier)

            // 分解
            ShapelessRecipeJsonBuilder
                .create(card.gemItemGetter(), 9)
                .input(card.item.feature, 1)
                .criterion(card.item.feature)
                .group(card.gemItemGetter())
                .offerTo(it, card.gemItemGetter().identifier concat "_from_${card.item.feature.identifier.path}")

        }

    }

}


private class FairyCrystalGlassBlock(settings: Settings) : AbstractGlassBlock(settings) {
    init {
        defaultState = defaultState
            .with(Properties.NORTH, false)
            .with(Properties.EAST, false)
            .with(Properties.SOUTH, false)
            .with(Properties.WEST, false)
            .with(Properties.UP, false)
            .with(Properties.DOWN, false)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        return defaultState
            .with(Properties.NORTH, ctx.world.getBlockState(ctx.blockPos.north()).isOf(this))
            .with(Properties.EAST, ctx.world.getBlockState(ctx.blockPos.east()).isOf(this))
            .with(Properties.SOUTH, ctx.world.getBlockState(ctx.blockPos.south()).isOf(this))
            .with(Properties.WEST, ctx.world.getBlockState(ctx.blockPos.west()).isOf(this))
            .with(Properties.UP, ctx.world.getBlockState(ctx.blockPos.up()).isOf(this))
            .with(Properties.DOWN, ctx.world.getBlockState(ctx.blockPos.down()).isOf(this))
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getStateForNeighborUpdate(state: BlockState, direction: Direction, neighborState: BlockState, world: WorldAccess, pos: BlockPos, neighborPos: BlockPos): BlockState {
        return state.with(ConnectingBlock.FACING_PROPERTIES[direction], neighborState.isOf(this))
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(Properties.NORTH, Properties.EAST, Properties.SOUTH, Properties.WEST, Properties.UP, Properties.DOWN)
    }
}

private class FairyCrystalGlassFrameBlockModel : Model(Optional.empty(), Optional.empty()) {
    override fun upload(id: Identifier, textures: TextureMap, modelCollector: BiConsumer<Identifier, Supplier<JsonElement>>): Identifier {
        modelCollector.accept(id) {
            jsonObjectOf(
                "parent" to Identifier("minecraft", "block/block").string.jsonPrimitive,
                "textures" to jsonObjectOf(
                    TextureKey.PARTICLE.name to textures.getTexture(TextureKey.TEXTURE).string.jsonPrimitive,
                    TextureKey.TEXTURE.name to textures.getTexture(TextureKey.TEXTURE).string.jsonPrimitive,
                ),
                "elements" to jsonArrayOf(
                    jsonObjectOf(
                        "from" to jsonArrayOf(0.jsonPrimitive, 0.jsonPrimitive, 0.jsonPrimitive),
                        "to" to jsonArrayOf(16.jsonPrimitive, 16.jsonPrimitive, 16.jsonPrimitive),
                        "faces" to jsonObjectOf(
                            "north" to jsonObjectOf("texture" to TextureKey.TEXTURE.string.jsonPrimitive, "cullface" to "north".jsonPrimitive),
                            "south" to jsonObjectOf("texture" to TextureKey.TEXTURE.string.jsonPrimitive, "cullface" to "south".jsonPrimitive),
                            "west" to jsonObjectOf("texture" to TextureKey.TEXTURE.string.jsonPrimitive, "cullface" to "west".jsonPrimitive),
                            "east" to jsonObjectOf("texture" to TextureKey.TEXTURE.string.jsonPrimitive, "cullface" to "east".jsonPrimitive),
                        ),
                    ),
                ),
            )
        }
        return id
    }
}

class FairyCrystalGlassBlockModel : Model(Optional.empty(), Optional.empty()) {
    override fun upload(id: Identifier, textures: TextureMap, modelCollector: BiConsumer<Identifier, Supplier<JsonElement>>): Identifier {
        modelCollector.accept(id) {
            fun createPart(roration: Int) = jsonObjectOf(
                "from" to jsonArrayOf(0.jsonPrimitive, 0.jsonPrimitive, 0.jsonPrimitive),
                "to" to jsonArrayOf(16.jsonPrimitive, 16.jsonPrimitive, 16.jsonPrimitive),
                "faces" to jsonObjectOf(
                    "north" to jsonObjectOf("texture" to TextureKey.TEXTURE.string.jsonPrimitive, "cullface" to "north".jsonPrimitive, "rotation" to roration.jsonPrimitive),
                    "south" to jsonObjectOf("texture" to TextureKey.TEXTURE.string.jsonPrimitive, "cullface" to "south".jsonPrimitive, "rotation" to roration.jsonPrimitive),
                    "west" to jsonObjectOf("texture" to TextureKey.TEXTURE.string.jsonPrimitive, "cullface" to "west".jsonPrimitive, "rotation" to roration.jsonPrimitive),
                    "east" to jsonObjectOf("texture" to TextureKey.TEXTURE.string.jsonPrimitive, "cullface" to "east".jsonPrimitive, "rotation" to roration.jsonPrimitive),
                    "up" to jsonObjectOf("texture" to TextureKey.TEXTURE.string.jsonPrimitive, "cullface" to "up".jsonPrimitive, "rotation" to roration.jsonPrimitive),
                    "down" to jsonObjectOf("texture" to TextureKey.TEXTURE.string.jsonPrimitive, "cullface" to "down".jsonPrimitive, "rotation" to roration.jsonPrimitive),
                ),
            )
            jsonObjectOf(
                "parent" to Identifier("minecraft", "block/block").string.jsonPrimitive,
                "textures" to jsonObjectOf(
                    TextureKey.PARTICLE.name to textures.getTexture(TextureKey.TEXTURE).string.jsonPrimitive,
                    TextureKey.TEXTURE.name to textures.getTexture(TextureKey.TEXTURE).string.jsonPrimitive,
                ),
                "elements" to jsonArrayOf(
                    createPart(0),
                    createPart(90),
                    createPart(180),
                    createPart(270),
                ),
            )
        }
        return id
    }
}

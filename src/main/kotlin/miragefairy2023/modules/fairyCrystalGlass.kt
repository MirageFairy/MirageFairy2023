package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.concat
import miragefairy2023.util.datagen.Model
import miragefairy2023.util.datagen.TextureMap
import miragefairy2023.util.identifier
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.generateBlockState
import miragefairy2023.util.init.generateDefaultBlockLootTable
import miragefairy2023.util.init.group
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
import net.minecraft.util.registry.Registry
import net.minecraft.world.WorldAccess


enum class FairyCrystalGlassCard(
    val path: String,
    val gemItem: Item,
    val enName: String,
    val jaName: String,
    val poemList: List<Poem>,
) {
    ARTIFICIAL_FAIRY_CRYSTAL_GLASS(
        "artificial_fairy_crystal_glass", DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL.item,
        "Artificial Fairy Crystal Glass", "人工フェアリークリスタルガラス",
        listOf(Poem("Fairies fear its distorted molecule", "窓を潤す、模造の美学。")),
    ),
    FAIRY_CRYSTAL_50_GLASS(
        "fairy_crystal_50_glass", DemonItemCard.FAIRY_CRYSTAL_50.item,
        "50 Minia Crystal Glass", "50ミーニャクリスタルガラス",
        listOf(Poem("Popular among artifact fairies", "家の外を映し出す鏡。")),
    ),
    //FAIRY_CRYSTAL_100_GLASS(
    //    "fairy_crystal_100_glass", DemonItemCard.FAIRY_CRYSTAL_100.item,
    //    "100 Minia Crystal Glass", "100ミーニャクリスタルガラス",
    //    "", "", // TODO
    //),
    //FAIRY_CRYSTAL_500_GLASS(
    //    "fairy_crystal_500_glass", DemonItemCard.FAIRY_CRYSTAL_500.item,
    //    "500 Minia Crystal Glass", "500ミーニャクリスタルガラス",
    //    "", "の壁に咲く、", // TODO
    //),
    // TODO 残りのミーニャ系ガラス
    // TODO 名誉系ガラス
    ;

    val identifier = Identifier(MirageFairy2023.modId, path)
    val block = FairyCrystalGlassBlock(FabricBlockSettings.of(Material.GLASS)
        .strength(1.5F)
        .sounds(BlockSoundGroup.GLASS)
        .nonOpaque()
        .allowsSpawning { _, _, _, _ -> false }
        .solidBlock { _, _, _ -> false }
        .suffocates { _, _, _ -> false }
        .blockVision { _, _, _ -> false })
    val item = BlockItem(block, FabricItemSettings().group(commonItemGroup))
}


val fairyCrystalGlassModule = module {
    FairyCrystalGlassCard.values().forEach { card ->

        // 登録
        Registry.register(Registry.BLOCK, card.identifier, card.block)
        Registry.register(Registry.ITEM, card.identifier, card.item)


        // 見た目

        // blockState
        generateBlockState(card.block) {
            fun createPart(direction: String, x: Int, y: Int) = jsonObjectOf(
                "when" to jsonObjectOf(
                    direction to "false".jsonPrimitive,
                ),
                "apply" to jsonObjectOf(
                    "model" to "${"block/" concat card.identifier concat "_frame"}".jsonPrimitive,
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
            val textureMap = TextureMap(TextureKey.TEXTURE to TextureMap.getSubId(card.block, "_frame"))
            fairyCrystalGlassBlockModel.upload(card.block, textureMap, blockStateModelGenerator.modelCollector)
        }

        // 枠パーツモデル
        onGenerateBlockStateModels { blockStateModelGenerator ->
            val textureMap = TextureMap(TextureKey.TEXTURE to TextureMap.getSubId(card.block, "_frame"))
            fairyCrystalGlassFrameBlockModel.upload(card.block, "_frame", textureMap, blockStateModelGenerator.modelCollector)
        }

        // レンダリング関連
        onInitializeClient { MirageFairy2023.clientProxy!!.registerCutoutBlockRenderLayer(card.block) }


        // 翻訳
        enJa(card.block, card.enName, card.jaName)
        generatePoemList(card.item, card.poemList)
        onRegisterItems { registerPoemList(card.item, card.poemList) }


        // 性質
        onGenerateBlockTags { it(BlockTags.IMPERMEABLE).add(card.block) }
        onGenerateBlockTags { it(BlockTags.PICKAXE_MINEABLE).add(card.block) }


        // レシピ

        // ドロップ
        generateDefaultBlockLootTable(card.block)

        // 圧縮
        onGenerateRecipes {
            ShapelessRecipeJsonBuilder
                .create(card.item, 1)
                .input(card.gemItem, 9)
                .criterion(card.gemItem)
                .group(card.item)
                .offerTo(it, card.item.identifier)
        }

        // 分解
        onGenerateRecipes {
            ShapelessRecipeJsonBuilder
                .create(card.gemItem, 9)
                .input(card.item, 1)
                .criterion(card.item)
                .group(card.gemItem)
                .offerTo(it, card.gemItem.identifier concat "_from_${card.item.identifier.path}")
        }

    }
}


class FairyCrystalGlassBlock(settings: Settings) : AbstractGlassBlock(settings) {
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

val fairyCrystalGlassFrameBlockModel = Model { textures ->
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

val fairyCrystalGlassBlockModel = Model { textures ->
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

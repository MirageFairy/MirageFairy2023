package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.concat
import miragefairy2023.util.datagen.Model
import miragefairy2023.util.datagen.TextureMap
import miragefairy2023.util.identifier
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.generateDefaultBlockLootTable
import miragefairy2023.util.init.generateSimpleCubeAllBlockState
import miragefairy2023.util.init.group
import miragefairy2023.util.jsonArrayOf
import miragefairy2023.util.jsonObjectOf
import miragefairy2023.util.jsonPrimitive
import miragefairy2023.util.string
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.MapColor
import net.minecraft.block.Material
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.TextureKey
import net.minecraft.data.client.TextureMap
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder
import net.minecraft.item.BlockItem
import net.minecraft.tag.BlockTags
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

enum class DemonBlockCard(
    val path: String,
    val en: String,
    val ja: String,
    val poemList: List<Poem>,
    val block: Block,
) {
    CREATIVE_AURA_STONE(
        "creative_aura_stone", "Neutronium Block", "アカーシャの霊氣石",
        listOf(Poem("Hypothetical element with ideal hardness", "終末と創造の波紋。")),
        Block(FabricBlockSettings.of(Material.STONE).strength(-1.0F, 3600000.0F).dropsNothing().allowsSpawning { _, _, _, _ -> false }),
    ),
    LOCAL_VACUUM_DECAY(
        "local_vacuum_decay", "Local Vacuum Decay", "局所真空崩壊",
        listOf(Poem("Stable instability due to anti-entropy", "これが秩序の究極の形だというのか？")),
        Block(FabricBlockSettings.of(Material.STONE).strength(-1.0F, 3600000.0F).dropsNothing().allowsSpawning { _, _, _, _ -> false })
    ),
    MIRANAGITE_BLOCK(
        "miranagite_block", "Miranagite Block", "蒼天石ブロック",
        listOf(Poem("Passivation confines discontinuous space", "虚空に導かれし、霊界との接合点。")),
        Block(FabricBlockSettings.of(Material.METAL, MapColor.LIGHT_BLUE).strength(3.0f, 3.0f).requiresTool())
    ),
    ;

    val identifier = Identifier(MirageFairy2023.modId, path)
    val item = BlockItem(block, FabricItemSettings().group(commonItemGroup))
}

val demonBlockModule = module {

    // 全体
    DemonBlockCard.values().forEach { card ->
        Registry.register(Registry.BLOCK, card.identifier, card.block)
        Registry.register(Registry.ITEM, card.identifier, card.item)
        enJa(card.block, card.en, card.ja)
        generatePoemList(card.item, card.poemList)
        onRegisterItems { registerPoemList(card.item, card.poemList) }
    }

    // アカーシャの霊氣石
    DemonBlockCard.CREATIVE_AURA_STONE.let { card ->
        generateSimpleCubeAllBlockState(card.block)
        onGenerateBlockTags { it(BlockTags.DRAGON_IMMUNE).add(card.block) }
        onGenerateBlockTags { it(BlockTags.WITHER_IMMUNE).add(card.block) }
        onGenerateBlockTags { it(BlockTags.FEATURES_CANNOT_REPLACE).add(card.block) }
        onGenerateBlockTags { it(BlockTags.GEODE_INVALID_BLOCKS).add(card.block) }
    }

    // 局所真空崩壊
    DemonBlockCard.LOCAL_VACUUM_DECAY.let { card ->
        onGenerateBlockStateModels { blockStateModelGenerator ->
            val model = Model { textures ->
                jsonObjectOf(
                    "parent" to Identifier("minecraft", "block/block").string.jsonPrimitive,
                    "textures" to jsonObjectOf(
                        TextureKey.PARTICLE.name to textures.getTexture(TextureKey.BACK).string.jsonPrimitive,
                        TextureKey.BACK.name to textures.getTexture(TextureKey.BACK).string.jsonPrimitive,
                        TextureKey.FRONT.name to textures.getTexture(TextureKey.FRONT).string.jsonPrimitive,
                    ),
                    "elements" to jsonArrayOf(
                        jsonObjectOf(
                            "from" to jsonArrayOf(0.jsonPrimitive, 0.jsonPrimitive, 0.jsonPrimitive),
                            "to" to jsonArrayOf(16.jsonPrimitive, 16.jsonPrimitive, 16.jsonPrimitive),
                            "faces" to jsonObjectOf(
                                "down" to jsonObjectOf("texture" to TextureKey.BACK.string.jsonPrimitive, "cullface" to "down".jsonPrimitive),
                                "up" to jsonObjectOf("texture" to TextureKey.BACK.string.jsonPrimitive, "cullface" to "up".jsonPrimitive),
                                "north" to jsonObjectOf("texture" to TextureKey.BACK.string.jsonPrimitive, "cullface" to "north".jsonPrimitive),
                                "south" to jsonObjectOf("texture" to TextureKey.BACK.string.jsonPrimitive, "cullface" to "south".jsonPrimitive),
                                "west" to jsonObjectOf("texture" to TextureKey.BACK.string.jsonPrimitive, "cullface" to "west".jsonPrimitive),
                                "east" to jsonObjectOf("texture" to TextureKey.BACK.string.jsonPrimitive, "cullface" to "east".jsonPrimitive),
                            ),
                        ),
                        jsonObjectOf(
                            "from" to jsonArrayOf(0.jsonPrimitive, 0.jsonPrimitive, 0.jsonPrimitive),
                            "to" to jsonArrayOf(16.jsonPrimitive, 16.jsonPrimitive, 16.jsonPrimitive),
                            "faces" to jsonObjectOf(
                                "down" to jsonObjectOf("texture" to TextureKey.FRONT.string.jsonPrimitive, "cullface" to "down".jsonPrimitive),
                                "up" to jsonObjectOf("texture" to TextureKey.FRONT.string.jsonPrimitive, "cullface" to "up".jsonPrimitive),
                                "north" to jsonObjectOf("texture" to TextureKey.FRONT.string.jsonPrimitive, "cullface" to "north".jsonPrimitive),
                                "south" to jsonObjectOf("texture" to TextureKey.FRONT.string.jsonPrimitive, "cullface" to "south".jsonPrimitive),
                                "west" to jsonObjectOf("texture" to TextureKey.FRONT.string.jsonPrimitive, "cullface" to "west".jsonPrimitive),
                                "east" to jsonObjectOf("texture" to TextureKey.FRONT.string.jsonPrimitive, "cullface" to "east".jsonPrimitive),
                            ),
                        ),
                    ),
                )
            }
            val textureMap = TextureMap(
                TextureKey.BACK to TextureMap.getSubId(card.block, "_base"),
                TextureKey.FRONT to TextureMap.getSubId(card.block, "_spark"),
            )
            val modelId = model.upload(card.block, textureMap, blockStateModelGenerator.modelCollector)
            blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(card.block, modelId))
        }
        onInitializeClient { MirageFairy2023.clientProxy!!.registerCutoutBlockRenderLayer(card.block) }
        onGenerateBlockTags { it(BlockTags.DRAGON_IMMUNE).add(card.block) }
        onGenerateBlockTags { it(BlockTags.WITHER_IMMUNE).add(card.block) }
        onGenerateBlockTags { it(BlockTags.FEATURES_CANNOT_REPLACE).add(card.block) }
        onGenerateBlockTags { it(BlockTags.GEODE_INVALID_BLOCKS).add(card.block) }
    }

    // 蒼天石ブロック
    DemonBlockCard.MIRANAGITE_BLOCK.let { card ->
        generateSimpleCubeAllBlockState(card.block)
        onGenerateBlockTags { it(BlockTags.PICKAXE_MINEABLE).add(card.block) }
        onGenerateBlockTags { it(BlockTags.NEEDS_STONE_TOOL).add(card.block) }
        generateDefaultBlockLootTable(card.block)
    }


    // 蒼天石⇔蒼天石ブロック
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(DemonBlockCard.MIRANAGITE_BLOCK.item)
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .input('#', DemonItemCard.MIRANAGITE.item)
            .criterion(DemonItemCard.MIRANAGITE.item)
            .group(DemonBlockCard.MIRANAGITE_BLOCK.item)
            .offerTo(it, DemonBlockCard.MIRANAGITE_BLOCK.item.identifier)
    }
    onGenerateRecipes {
        ShapelessRecipeJsonBuilder
            .create(DemonItemCard.MIRANAGITE.item, 9)
            .input(DemonBlockCard.MIRANAGITE_BLOCK.item)
            .criterion(DemonBlockCard.MIRANAGITE_BLOCK.item)
            .group(DemonItemCard.MIRANAGITE.item)
            .offerTo(it, DemonItemCard.MIRANAGITE.item.identifier concat "_from_" concat DemonBlockCard.MIRANAGITE_BLOCK.item.identifier.path)
    }

}

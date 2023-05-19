package miragefairy2023.modules

import com.google.gson.JsonElement
import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.gray
import miragefairy2023.util.init.FeatureSlot
import miragefairy2023.util.init.block
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.enJaBlock
import miragefairy2023.util.init.generateSimpleCubeAllBlockState
import miragefairy2023.util.init.item
import miragefairy2023.util.jsonArrayOf
import miragefairy2023.util.jsonObjectOf
import miragefairy2023.util.jsonPrimitive
import miragefairy2023.util.text
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.Model
import net.minecraft.data.client.TextureKey
import net.minecraft.data.client.TextureMap
import net.minecraft.data.client.TexturedModel
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.tag.BlockTags
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.world.World
import java.util.Optional
import java.util.function.BiConsumer
import java.util.function.Supplier

lateinit var creativeAuraStoneBlock: FeatureSlot<Block>
lateinit var creativeAuraStoneBlockItem: FeatureSlot<BlockItem>

lateinit var localVacuumDecayBlock: FeatureSlot<Block>
lateinit var localVacuumDecayBlockItem: FeatureSlot<BlockItem>

val demonBlockModule = module {

    creativeAuraStoneBlock = block("creative_aura_stone", { Block(FabricBlockSettings.of(Material.STONE).strength(-1.0F, 3600000.0F).dropsNothing().allowsSpawning { _, _, _, _ -> false }) }) {
        generateSimpleCubeAllBlockState()
        enJaBlock({ feature }, "Neutronium Block", "アカーシャの霊氣石")
        enJa({ "${feature.translationKey}.poem" }, "Hypothetical substance with ideal hardness", "終末と創造の波紋。")
        onGenerateBlockTags { it(BlockTags.DRAGON_IMMUNE).add(feature) }
        onGenerateBlockTags { it(BlockTags.WITHER_IMMUNE).add(feature) }
        onGenerateBlockTags { it(BlockTags.FEATURES_CANNOT_REPLACE).add(feature) }
        onGenerateBlockTags { it(BlockTags.GEODE_INVALID_BLOCKS).add(feature) }
    }
    creativeAuraStoneBlockItem = item("creative_aura_stone", {
        object : BlockItem(creativeAuraStoneBlock.feature, FabricItemSettings().group(commonItemGroup)) {
            override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
                super.appendTooltip(stack, world, tooltip, context)
                tooltip += text { translate("$translationKey.poem").gray }
            }
        }
    })

    localVacuumDecayBlock = block("local_vacuum_decay", { Block(FabricBlockSettings.of(Material.STONE).strength(-1.0F, 3600000.0F).dropsNothing().allowsSpawning { _, _, _, _ -> false }) }) {
        initializationScope.onGenerateBlockStateModels { blockStateModelGenerator ->
            val model = object : Model(Optional.empty(), Optional.empty()) {
                override fun upload(id: Identifier, textures: TextureMap, modelCollector: BiConsumer<Identifier, Supplier<JsonElement>>): Identifier {
                    modelCollector.accept(id) {
                        jsonObjectOf(
                            "parent" to Identifier("minecraft", "block/block").toString().jsonPrimitive,
                            "textures" to jsonObjectOf(
                                TextureKey.PARTICLE.name to textures.getTexture(TextureKey.BACK).toString().jsonPrimitive,
                                TextureKey.BACK.name to textures.getTexture(TextureKey.BACK).toString().jsonPrimitive,
                                TextureKey.FRONT.name to textures.getTexture(TextureKey.FRONT).toString().jsonPrimitive,
                            ),
                            "elements" to jsonArrayOf(
                                jsonObjectOf(
                                    "from" to jsonArrayOf(0.jsonPrimitive, 0.jsonPrimitive, 0.jsonPrimitive),
                                    "to" to jsonArrayOf(16.jsonPrimitive, 16.jsonPrimitive, 16.jsonPrimitive),
                                    "faces" to jsonObjectOf(
                                        "down" to jsonObjectOf("texture" to TextureKey.BACK.toString().jsonPrimitive, "cullface" to "down".jsonPrimitive),
                                        "up" to jsonObjectOf("texture" to TextureKey.BACK.toString().jsonPrimitive, "cullface" to "up".jsonPrimitive),
                                        "north" to jsonObjectOf("texture" to TextureKey.BACK.toString().jsonPrimitive, "cullface" to "north".jsonPrimitive),
                                        "south" to jsonObjectOf("texture" to TextureKey.BACK.toString().jsonPrimitive, "cullface" to "south".jsonPrimitive),
                                        "west" to jsonObjectOf("texture" to TextureKey.BACK.toString().jsonPrimitive, "cullface" to "west".jsonPrimitive),
                                        "east" to jsonObjectOf("texture" to TextureKey.BACK.toString().jsonPrimitive, "cullface" to "east".jsonPrimitive),
                                    ),
                                ),
                                jsonObjectOf(
                                    "from" to jsonArrayOf(0.jsonPrimitive, 0.jsonPrimitive, 0.jsonPrimitive),
                                    "to" to jsonArrayOf(16.jsonPrimitive, 16.jsonPrimitive, 16.jsonPrimitive),
                                    "faces" to jsonObjectOf(
                                        "down" to jsonObjectOf("texture" to TextureKey.FRONT.toString().jsonPrimitive, "cullface" to "down".jsonPrimitive),
                                        "up" to jsonObjectOf("texture" to TextureKey.FRONT.toString().jsonPrimitive, "cullface" to "up".jsonPrimitive),
                                        "north" to jsonObjectOf("texture" to TextureKey.FRONT.toString().jsonPrimitive, "cullface" to "north".jsonPrimitive),
                                        "south" to jsonObjectOf("texture" to TextureKey.FRONT.toString().jsonPrimitive, "cullface" to "south".jsonPrimitive),
                                        "west" to jsonObjectOf("texture" to TextureKey.FRONT.toString().jsonPrimitive, "cullface" to "west".jsonPrimitive),
                                        "east" to jsonObjectOf("texture" to TextureKey.FRONT.toString().jsonPrimitive, "cullface" to "east".jsonPrimitive),
                                    ),
                                ),
                            ),
                        )
                    }
                    return id
                }
            }
            val modelFactory = TexturedModel.makeFactory({ block ->
                TextureMap().apply {
                    put(TextureKey.BACK, TextureMap.getSubId(block, "_base"))
                    put(TextureKey.FRONT, TextureMap.getSubId(block, "_spark"))
                }
            }, model)
            val modelId = modelFactory.upload(feature, blockStateModelGenerator.modelCollector)
            blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(feature, modelId))
        }
        onInitializeClient { MirageFairy2023.clientProxy!!.registerBlockRenderLayer(feature) }
        enJaBlock({ feature }, "Local Vacuum Decay", "局所真空崩壊")
        enJa({ "${feature.translationKey}.poem" }, "Stable instability caused by anti-entropy", "これが秩序の究極の形だというのか？")
        onGenerateBlockTags { it(BlockTags.DRAGON_IMMUNE).add(feature) }
        onGenerateBlockTags { it(BlockTags.WITHER_IMMUNE).add(feature) }
        onGenerateBlockTags { it(BlockTags.FEATURES_CANNOT_REPLACE).add(feature) }
        onGenerateBlockTags { it(BlockTags.GEODE_INVALID_BLOCKS).add(feature) }
    }
    localVacuumDecayBlockItem = item("local_vacuum_decay", {
        object : BlockItem(localVacuumDecayBlock.feature, FabricItemSettings().group(commonItemGroup)) {
            override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
                super.appendTooltip(stack, world, tooltip, context)
                tooltip += text { translate("$translationKey.poem").gray }
            }
        }
    })

}

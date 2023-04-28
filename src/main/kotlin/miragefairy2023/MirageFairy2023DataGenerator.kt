package miragefairy2023

import com.google.gson.JsonElement
import com.mojang.logging.LogUtils
import miragefairy2023.MirageFairy2023.initializationScope
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider
import net.minecraft.advancement.Advancement
import net.minecraft.block.Block
import net.minecraft.data.DataGenerator
import net.minecraft.data.DataProvider
import net.minecraft.data.DataWriter
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.item.Item
import net.minecraft.loot.LootTable
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.io.IOException
import java.util.function.BiConsumer
import java.util.function.Consumer

object MirageFairy2023DataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {

        fabricDataGenerator.addProvider(object : FabricLanguageProvider(fabricDataGenerator, "en_us") {
            override fun generateTranslations(translationBuilder: TranslationBuilder) {
                initializationScope.onGenerateEnglishTranslations.fire { it(translationBuilder) }
            }
        })
        fabricDataGenerator.addProvider(object : FabricLanguageProvider(fabricDataGenerator, "ja_jp") {
            override fun generateTranslations(translationBuilder: TranslationBuilder) {
                initializationScope.onGenerateJapaneseTranslations.fire { it(translationBuilder) }
            }
        })

        fabricDataGenerator.addProvider(object : FabricModelProvider(fabricDataGenerator) {
            override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
                initializationScope.onGenerateBlockStateModels.fire { it(blockStateModelGenerator) }
            }

            override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
                initializationScope.onGenerateItemModels.fire { it(itemModelGenerator) }
            }
        })

        fabricDataGenerator.addProvider(object : FabricRecipeProvider(fabricDataGenerator) {
            override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
                initializationScope.onGenerateRecipes.fire { it(exporter) }
            }
        })

        fabricDataGenerator.addProvider(object : SimpleFabricLootTableProvider(fabricDataGenerator, LootContextTypes.ADVANCEMENT_REWARD) {
            override fun accept(t: BiConsumer<Identifier, LootTable.Builder>) {
                initializationScope.onGenerateAdvancementRewardLootTables.fire { it(t) }
            }
        })
        fabricDataGenerator.addProvider(object : FabricBlockLootTableProvider(fabricDataGenerator) {
            override fun generateBlockLootTables() {
                initializationScope.onGenerateBlockLootTables.fire { it(this) }
            }
        })

        fabricDataGenerator.addProvider(object : FabricAdvancementProvider(fabricDataGenerator) {
            override fun generateAdvancement(consumer: Consumer<Advancement>) {
                initializationScope.onGenerateAdvancements.fire { it(consumer) }
            }
        })

        fabricDataGenerator.addProvider(object : FabricTagProvider<Item>(fabricDataGenerator, Registry.ITEM) {
            override fun generateTags() {
                initializationScope.onGenerateItemTags.fire { it { id -> getOrCreateTagBuilder(id) } }
            }
        })
        fabricDataGenerator.addProvider(object : FabricTagProvider<Block>(fabricDataGenerator, Registry.BLOCK) {
            override fun generateTags() {
                initializationScope.onGenerateBlockTags.fire { it { id -> getOrCreateTagBuilder(id) } }
            }
        })

        fabricDataGenerator.addProvider(ParticleProvider(fabricDataGenerator).also { provider ->
            initializationScope.onGenerateParticles.fire { it(provider) }
        })

    }
}

class ParticleProvider(private val dataGenerator: DataGenerator) : DataProvider {

    private val map = mutableMapOf<Identifier, JsonElement>()

    operator fun set(identifier: Identifier, jsonElement: JsonElement) {
        if (identifier in map) throw Exception("Duplicate particle definition for $identifier")
        map[identifier] = jsonElement
    }

    override fun run(writer: DataWriter) {
        val pathResolver = dataGenerator.createPathResolver(DataGenerator.OutputType.RESOURCE_PACK, "particles")
        map.forEach { (identifier, jsonElement) ->
            val path = pathResolver.resolveJson(identifier)
            try {
                DataProvider.writeToPath(writer, jsonElement, path)
            } catch (e: IOException) {
                LogUtils.getLogger().error("Couldn't save data file {}", path, e)
            }
        }
    }

    override fun getName() = "Particles"

}

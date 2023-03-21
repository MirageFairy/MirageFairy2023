package miragefairy2023

import miragefairy2023.core.init.InitializationScope
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.server.recipe.RecipeJsonProvider
import java.util.function.Consumer

object MirageFairy2023DataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val initializationScope = InitializationScope(MirageFairy2023.modId)

        initializationScope.modules()

        fabricDataGenerator.addProvider(object : FabricLanguageProvider(fabricDataGenerator, "en_us") {
            override fun generateTranslations(translationBuilder: TranslationBuilder) {
                initializationScope.englishTranslationGeneration.fire { it(translationBuilder) }
            }
        })
        fabricDataGenerator.addProvider(object : FabricLanguageProvider(fabricDataGenerator, "ja_jp") {
            override fun generateTranslations(translationBuilder: TranslationBuilder) {
                initializationScope.japaneseTranslationGeneration.fire { it(translationBuilder) }
            }
        })

        fabricDataGenerator.addProvider(object : FabricModelProvider(fabricDataGenerator) {
            override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator?) {

            }

            override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
                initializationScope.itemModelGeneration.fire { it(itemModelGenerator) }
            }
        })

        fabricDataGenerator.addProvider(object : FabricRecipeProvider(fabricDataGenerator) {
            override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
                initializationScope.recipeGeneration.fire { it(exporter) }
            }
        })

    }
}

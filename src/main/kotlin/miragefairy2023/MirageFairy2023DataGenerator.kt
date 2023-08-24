package miragefairy2023

import miragefairy2023.datagen.BiomeProvider
import miragefairy2023.datagen.ParticleProvider
import miragefairy2023.datagen.SoundsProvider
import miragefairy2023.datagen.TrinketsEntitiesProvider
import miragefairy2023.datagen.TrinketsSlotProvider
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
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.loot.LootTable
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.function.BiConsumer
import java.util.function.Consumer

object MirageFairy2023DataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {

        fabricDataGenerator.addProvider(object : FabricLanguageProvider(fabricDataGenerator, "en_us") {
            override fun generateTranslations(translationBuilder: TranslationBuilder) {
                InitializationScope.INSTANCE.onGenerateEnglishTranslations.fire { it(translationBuilder) }
            }
        })
        fabricDataGenerator.addProvider(object : FabricLanguageProvider(fabricDataGenerator, "ja_jp") {
            override fun generateTranslations(translationBuilder: TranslationBuilder) {
                InitializationScope.INSTANCE.onGenerateJapaneseTranslations.fire { it(translationBuilder) }
            }
        })

        fabricDataGenerator.addProvider(object : FabricModelProvider(fabricDataGenerator) {
            override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
                InitializationScope.INSTANCE.onGenerateBlockStateModels.fire { it(blockStateModelGenerator) }
            }

            override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
                InitializationScope.INSTANCE.onGenerateItemModels.fire { it(itemModelGenerator) }
            }
        })

        fabricDataGenerator.addProvider(object : FabricRecipeProvider(fabricDataGenerator) {
            override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
                InitializationScope.INSTANCE.onGenerateRecipes.fire { it(exporter) }
            }
        })

        fabricDataGenerator.addProvider(object : SimpleFabricLootTableProvider(fabricDataGenerator, LootContextTypes.ADVANCEMENT_REWARD) {
            override fun accept(t: BiConsumer<Identifier, LootTable.Builder>) {
                InitializationScope.INSTANCE.onGenerateAdvancementRewardLootTables.fire { it(t) }
            }
        })
        fabricDataGenerator.addProvider(object : FabricBlockLootTableProvider(fabricDataGenerator) {
            override fun generateBlockLootTables() {
                InitializationScope.INSTANCE.onGenerateBlockLootTables.fire { it(this) }
            }
        })

        fabricDataGenerator.addProvider(object : FabricAdvancementProvider(fabricDataGenerator) {
            override fun generateAdvancement(consumer: Consumer<Advancement>) {
                InitializationScope.INSTANCE.onGenerateAdvancements.fire { it(consumer) }
            }
        })

        fabricDataGenerator.addProvider(object : FabricTagProvider<Item>(fabricDataGenerator, Registry.ITEM) {
            override fun generateTags() {
                InitializationScope.INSTANCE.onGenerateItemTags.fire { it { id -> getOrCreateTagBuilder(id) } }
            }
        })
        fabricDataGenerator.addProvider(object : FabricTagProvider<Block>(fabricDataGenerator, Registry.BLOCK) {
            override fun generateTags() {
                InitializationScope.INSTANCE.onGenerateBlockTags.fire { it { id -> getOrCreateTagBuilder(id) } }
            }
        })
        fabricDataGenerator.addProvider(object : FabricTagProvider<EntityType<*>>(fabricDataGenerator, Registry.ENTITY_TYPE) {
            override fun generateTags() {
                InitializationScope.INSTANCE.onGenerateEntityTypeTags.fire { it { id -> getOrCreateTagBuilder(id) } }
            }
        })

        fabricDataGenerator.addProvider(ParticleProvider(fabricDataGenerator).also { provider ->
            InitializationScope.INSTANCE.onGenerateParticles.fire { it(provider) }
        })

        fabricDataGenerator.addProvider(TrinketsEntitiesProvider(fabricDataGenerator).also { provider ->
            InitializationScope.INSTANCE.onGenerateTrinketsEntities.fire { it(provider) }
        })
        fabricDataGenerator.addProvider(TrinketsSlotProvider(fabricDataGenerator).also { provider ->
            InitializationScope.INSTANCE.onGenerateTrinketsSlot.fire { it(provider) }
        })

        fabricDataGenerator.addProvider(SoundsProvider(fabricDataGenerator, MirageFairy2023.modId).also { provider ->
            InitializationScope.INSTANCE.onGenerateSounds.fire { it(provider) }
        })

        fabricDataGenerator.addProvider(BiomeProvider(fabricDataGenerator, MirageFairy2023.modId).also { provider ->
            InitializationScope.INSTANCE.onGenerateBiome.fire { it(provider) }
        })

    }
}

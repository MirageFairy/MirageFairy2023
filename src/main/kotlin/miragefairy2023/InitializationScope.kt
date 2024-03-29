package miragefairy2023

import miragefairy2023.datagen.BiomeProvider
import miragefairy2023.datagen.ParticleProvider
import miragefairy2023.datagen.SoundsProvider
import miragefairy2023.datagen.TrinketsEntitiesProvider
import miragefairy2023.datagen.TrinketsSlotProvider
import miragefairy2023.modules.modules
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.advancement.Advancement
import net.minecraft.block.Block
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.loot.LootTable
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.world.biome.Biome
import java.util.function.BiConsumer
import java.util.function.Consumer

object InitializationScope {

    val onGenerateEnglishTranslations = EventBus<(FabricLanguageProvider.TranslationBuilder) -> Unit>()
    val onGenerateJapaneseTranslations = EventBus<(FabricLanguageProvider.TranslationBuilder) -> Unit>()
    val onGenerateBlockStateModels = EventBus<(BlockStateModelGenerator) -> Unit>()
    val onGenerateItemModels = EventBus<(ItemModelGenerator) -> Unit>()
    val onGenerateRecipes = EventBus<(Consumer<RecipeJsonProvider>) -> Unit>()
    val onGenerateAdvancementRewardLootTables = EventBus<(BiConsumer<Identifier, LootTable.Builder>) -> Unit>()
    val onGenerateBlockLootTables = EventBus<FabricBlockLootTableProvider.() -> Unit>()
    val onGenerateAdvancements = EventBus<(Consumer<Advancement>) -> Unit>()
    val onGenerateItemTags = EventBus<((TagKey<Item>) -> FabricTagProvider<Item>.FabricTagBuilder<Item>) -> Unit>()
    val onGenerateBlockTags = EventBus<((TagKey<Block>) -> FabricTagProvider<Block>.FabricTagBuilder<Block>) -> Unit>()
    val onGenerateEntityTypeTags = EventBus<((TagKey<EntityType<*>>) -> FabricTagProvider<EntityType<*>>.FabricTagBuilder<EntityType<*>>) -> Unit>()
    val onGenerateBiomeTags = EventBus<((TagKey<Biome>) -> FabricTagProvider<Biome>.FabricTagBuilder<Biome>) -> Unit>()
    val onGenerateParticles = EventBus<(ParticleProvider) -> Unit>()
    val onGenerateTrinketsEntities = EventBus<(TrinketsEntitiesProvider) -> Unit>()
    val onGenerateTrinketsSlot = EventBus<(TrinketsSlotProvider) -> Unit>()
    val onGenerateSounds = EventBus<(SoundsProvider) -> Unit>()
    val onGenerateBiome = EventBus<(BiomeProvider) -> Unit>()

    val onInitialize = EventBus<() -> Unit>()
    val onInitializeClient = EventBus<() -> Unit>()
    val onTerraBlenderInitialized = EventBus<() -> Unit>()

    init {
        modules()
    }
}

class EventBus<L> {
    private val listeners = mutableListOf<L>()

    operator fun invoke(listener: L) {
        listeners += listener
    }

    fun fire(listenerConsumer: (L) -> Unit) {
        listeners.forEach {
            listenerConsumer(it)
        }
    }
}

fun module(block: InitializationScope.() -> Unit) = block

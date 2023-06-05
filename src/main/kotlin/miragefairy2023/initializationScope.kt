package miragefairy2023

import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.advancement.Advancement
import net.minecraft.block.Block
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.item.Item
import net.minecraft.loot.LootTable
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import java.util.function.BiConsumer
import java.util.function.Consumer

class InitializationScope(val modId: String) {

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
    val onGenerateParticles = EventBus<(ParticleProvider) -> Unit>()
    val onGenerateTrinketsEntities = EventBus<(TrinketsEntitiesProvider) -> Unit>()
    val onGenerateTrinketsSlot = EventBus<(TrinketsSlotProvider) -> Unit>()

    val onInitializeClient = EventBus<() -> Unit>()

    val onRegisterLootConditionType = EventBus<() -> Unit>()
    val onRegisterLootFunctionType = EventBus<() -> Unit>()
    val onRegisterBlocks = EventBus<() -> Unit>()
    val onRegisterBlockEntities = EventBus<() -> Unit>()
    val onRegisterItems = EventBus<() -> Unit>()
    val onRegisterRecipes = EventBus<() -> Unit>()

}

fun module(block: InitializationScope.() -> Unit) = block

package miragefairy2023.core.init

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider

class InitializationScope(val modId: String) {
    val englishTranslationGeneration = EventBus<(FabricLanguageProvider.TranslationBuilder) -> Unit>()
    val japaneseTranslationGeneration = EventBus<(FabricLanguageProvider.TranslationBuilder) -> Unit>()
    val itemRegistration = EventBus<() -> Unit>()
    val recipeRegistration = EventBus<() -> Unit>()
}

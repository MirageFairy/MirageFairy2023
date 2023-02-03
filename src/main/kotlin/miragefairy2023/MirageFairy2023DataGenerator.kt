package miragefairy2023

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.client.Models

object MirageFairy2023DataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        fabricDataGenerator.addProvider(object : FabricModelProvider(fabricDataGenerator) {
            override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator?) {

            }

            override fun generateItemModels(itemModelGenerator: ItemModelGenerator?) {
                itemModelGenerator!!
                DemonItemCard.values().forEach { card ->
                    itemModelGenerator.register(card(), Models.GENERATED)
                }
            }
        })
        fabricDataGenerator.addProvider(object : FabricLanguageProvider(fabricDataGenerator, "en_us") {
            override fun generateTranslations(translationBuilder: TranslationBuilder?) {
                translationBuilder!!
                DemonItemCard.values().forEach { card ->
                    translationBuilder.add(card(), card.enName)
                }
            }
        })
        fabricDataGenerator.addProvider(object : FabricLanguageProvider(fabricDataGenerator, "ja_jp") {
            override fun generateTranslations(translationBuilder: TranslationBuilder?) {
                translationBuilder!!
                DemonItemCard.values().forEach { card ->
                    translationBuilder.add(card(), card.jaName)
                }
            }
        })
    }
}

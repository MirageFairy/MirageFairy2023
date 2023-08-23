package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.init.enJa
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.util.Identifier
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeEffects
import net.minecraft.world.biome.GenerationSettings
import net.minecraft.world.biome.SpawnSettings
import net.minecraft.world.biome.SpawnSettings.SpawnEntry
import net.minecraft.world.gen.feature.DefaultBiomeFeatures

enum class BiomeCard(
    val path: String,
    val en: String,
    val ja: String,
) {
    FAIRY_FOREST("fairy_forest", "Fairy Forest", "妖精の森"),
    ;

    val identifier = Identifier(MirageFairy2023.modId, path)
    val biome = Biome.Builder()
        .precipitation(Biome.Precipitation.RAIN)
        .temperature(0.4F)
        .downfall(0.6F)
        .effects(
            BiomeEffects.Builder()
                .waterColor(0xF3D9FF)
                .waterFogColor(0xF3D9FF)
                .fogColor(0xF7AFFF)
                .skyColor(0xF7AFFF)
                .grassColor(0x82FFBF)
                .foliageColor(0xCDAFFF)
                .build()
        )
        .spawnSettings(SpawnSettings.Builder().also { spawnSettings ->
            DefaultBiomeFeatures.addCaveMobs(spawnSettings)
            spawnSettings.spawn(SpawnGroup.MONSTER, SpawnEntry(EntityType.ENDERMAN, 10, 1, 4))
            spawnSettings.spawn(SpawnGroup.CREATURE, SpawnEntry(EntityType.RABBIT, 4, 2, 3))
            spawnSettings.spawn(SpawnGroup.CREATURE, SpawnEntry(EntityType.FOX, 8, 2, 4))
        }.build())
        .generationSettings(GenerationSettings.Builder().also { generationSettings ->
            DefaultBiomeFeatures.addLandCarvers(generationSettings)
            DefaultBiomeFeatures.addAmethystGeodes(generationSettings)
            DefaultBiomeFeatures.addDungeons(generationSettings)
            DefaultBiomeFeatures.addMineables(generationSettings)
            DefaultBiomeFeatures.addSprings(generationSettings)
            DefaultBiomeFeatures.addFrozenTopLayer(generationSettings)
            DefaultBiomeFeatures.addLargeFerns(generationSettings)
            DefaultBiomeFeatures.addDefaultOres(generationSettings)
            DefaultBiomeFeatures.addDefaultDisks(generationSettings)

            //DefaultBiomeFeatures.addTaigaTrees(generationSettings)
            DefaultBiomeFeatures.addForestTrees(generationSettings)

            //DefaultBiomeFeatures.addDefaultFlowers(generationSettings)
            DefaultBiomeFeatures.addDefaultFlowers(generationSettings)
            DefaultBiomeFeatures.addMeadowFlowers(generationSettings)

            DefaultBiomeFeatures.addTaigaGrass(generationSettings)
            DefaultBiomeFeatures.addDefaultVegetation(generationSettings)
            DefaultBiomeFeatures.addSweetBerryBushes(generationSettings)

        }.build()).build()
}

val biomeModule = module {
    BiomeCard.values().forEach { card ->
        onGenerateBiome {
            it.map[card.identifier] = card.biome
        }
        enJa("biome.${card.identifier.toTranslationKey()}", card.en, card.ja)
    }
}

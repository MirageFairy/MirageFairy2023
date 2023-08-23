package miragefairy2023.datagen

import com.mojang.serialization.JsonOps
import mirrg.kotlin.slf4j.hydrogen.getLogger
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.data.DataGenerator
import net.minecraft.data.DataProvider
import net.minecraft.data.DataWriter
import net.minecraft.util.Identifier
import net.minecraft.util.dynamic.RegistryOps
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.world.biome.Biome
import java.io.IOException

class BiomeProvider(private val dataGenerator: FabricDataGenerator, private val modId: String) : DataProvider {
    val map = mutableMapOf<Identifier, Biome>()

    override fun run(writer: DataWriter) {
        if (map.isEmpty()) return
        val pathResolver = dataGenerator.createPathResolver(DataGenerator.OutputType.DATA_PACK, "worldgen/biome")
        map.forEach { (identifier, biome) ->
            val path = pathResolver.resolveJson(identifier)
            val dataResult = Biome.CODEC.encodeStart(RegistryOps.of(JsonOps.INSTANCE, BuiltinRegistries.DYNAMIC_REGISTRY_MANAGER), biome)
            val jsonElement = dataResult.getOrThrow(false) { throw RuntimeException(it) }
            try {
                DataProvider.writeToPath(writer, jsonElement, path)
            } catch (e: IOException) {
                getLogger().error("Couldn't save data file {}", path, e)
            }
        }
    }

    override fun getName() = "Biome"

}

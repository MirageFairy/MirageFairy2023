package miragefairy2023.datagen

import com.google.gson.JsonElement
import mirrg.kotlin.slf4j.hydrogen.getLogger
import net.minecraft.data.DataGenerator
import net.minecraft.data.DataProvider
import net.minecraft.data.DataWriter
import net.minecraft.util.Identifier
import java.io.IOException

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
                getLogger().error("Couldn't save data file {}", path, e)
            }
        }
    }

    override fun getName() = "Particles"

}

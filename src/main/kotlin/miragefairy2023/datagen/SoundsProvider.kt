package miragefairy2023.datagen

import miragefairy2023.util.jsonArray
import miragefairy2023.util.jsonObject
import miragefairy2023.util.jsonObjectOfNotNull
import miragefairy2023.util.jsonPrimitive
import miragefairy2023.util.string
import mirrg.kotlin.slf4j.hydrogen.getLogger
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.data.DataGenerator
import net.minecraft.data.DataProvider
import net.minecraft.data.DataWriter
import net.minecraft.util.Identifier
import java.io.IOException

class SoundsProvider(private val dataGenerator: FabricDataGenerator, private val modId: String) : DataProvider {

    val map = mutableMapOf<String, SoundEntry>()

    class SoundEntry(
        val subtitle: String?,
        val sounds: List<Identifier>,
    )

    override fun run(writer: DataWriter) {
        if (map.isEmpty()) return
        val path = dataGenerator.resolveRootDirectoryPath(DataGenerator.OutputType.RESOURCE_PACK).resolve("$modId/sounds.json")
        val jsonElement = map.map { (name, entry) ->
            name to jsonObjectOfNotNull(
                entry.subtitle?.let { "subtitle" to it.jsonPrimitive },
                "sounds" to entry.sounds.map { it.string.jsonPrimitive }.jsonArray,
            )
        }.jsonObject
        try {
            DataProvider.writeToPath(writer, jsonElement, path)
        } catch (e: IOException) {
            getLogger().error("Couldn't save data file {}", path, e)
        }
    }

    override fun getName() = "Sounds"

}

package miragefairy2023.datagen

import miragefairy2023.MirageFairy2023
import miragefairy2023.util.jsonArrayOf
import miragefairy2023.util.jsonObjectOf
import miragefairy2023.util.jsonPrimitive
import mirrg.kotlin.slf4j.hydrogen.getLogger
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.data.DataGenerator
import net.minecraft.data.DataProvider
import net.minecraft.data.DataWriter
import net.minecraft.util.Identifier
import java.io.IOException

class TrinketsEntitiesProvider(private val dataGenerator: FabricDataGenerator) : DataProvider {

    val slots = mutableListOf<String>()

    override fun run(writer: DataWriter) {
        val pathResolver = dataGenerator.createPathResolver(DataGenerator.OutputType.DATA_PACK, "entities")
        if (slots.isNotEmpty()) {
            val path = pathResolver.resolveJson(Identifier("trinkets", MirageFairy2023.modId))
            val jsonElement = jsonObjectOf(
                "entities" to jsonArrayOf(
                    "player".jsonPrimitive,
                ),
                "slots" to jsonArrayOf(
                    *slots.map { it.jsonPrimitive }.toTypedArray(),
                ),
            )
            try {
                DataProvider.writeToPath(writer, jsonElement, path)
            } catch (e: IOException) {
                getLogger().error("Couldn't save data file {}", path, e)
            }
        }
    }

    override fun getName() = "Trinkets Entities"

}

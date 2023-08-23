package miragefairy2023.datagen

import miragefairy2023.util.jsonArray
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

class TrinketsSlotProvider(private val dataGenerator: FabricDataGenerator) : DataProvider {

    val slots = mutableListOf<Pair<String, TrinketsSlotEntry>>()

    class TrinketsSlotEntry(
        val icon: Identifier,
        val quickMovePredicates: List<String>? = null,
    )

    override fun run(writer: DataWriter) {
        val pathResolver = dataGenerator.createPathResolver(DataGenerator.OutputType.DATA_PACK, "slots")
        slots.forEach { (name, entry) ->
            val path = pathResolver.resolveJson(Identifier("trinkets", name))
            val jsonElement = jsonObjectOfNotNull(
                "icon" to entry.icon.string.jsonPrimitive,
                entry.quickMovePredicates?.let { "quick_move_predicates" to entry.quickMovePredicates.map { it.jsonPrimitive }.jsonArray },
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

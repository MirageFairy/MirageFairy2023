package miragefairy2023

import miragefairy2023.core.init.EventBus
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

val itemRegistration = EventBus<() -> Unit>()
val recipeRegistration = EventBus<() -> Unit>()

object MirageFairy2023 : ModInitializer {
    val modId = "miragefairy2023"
    val logger = LoggerFactory.getLogger("miragefairy2023")

    override fun onInitialize() {

        initDemonItem()

        itemRegistration.fire { it() }
        recipeRegistration.fire { it() }

    }
}

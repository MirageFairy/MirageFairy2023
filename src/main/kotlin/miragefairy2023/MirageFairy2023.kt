package miragefairy2023

import miragefairy2023.core.init.InitializationScope
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object MirageFairy2023 : ModInitializer {
    val modId = "miragefairy2023"
    val logger = LoggerFactory.getLogger("miragefairy2023")

    override fun onInitialize() {
        InitializationScope(modId).run {

            initDemonItem()

            itemRegistration.fire { it() }
            recipeRegistration.fire { it() }

        }
    }
}

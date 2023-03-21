package miragefairy2023

import miragefairy2023.core.init.InitializationScope
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object MirageFairy2023 : ModInitializer {
    val modId = "miragefairy2023"
    val logger = LoggerFactory.getLogger("miragefairy2023")

    override fun onInitialize() {
        val initializationScope = InitializationScope(modId)

        initializationScope.modules()

        initializationScope.blockRegistration.fire { it() }
        initializationScope.itemRegistration.fire { it() }
        initializationScope.recipeRegistration.fire { it() }

    }
}

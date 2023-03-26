package miragefairy2023

import net.fabricmc.api.ModInitializer
import net.minecraft.entity.player.PlayerEntity
import org.slf4j.LoggerFactory

object MirageFairy2023 : ModInitializer {
    val modId = "miragefairy2023"
    val logger = LoggerFactory.getLogger("miragefairy2023")
    var proxy: Proxy? = null

    lateinit var initializationScope: InitializationScope

    override fun onInitialize() {
        initializationScope = InitializationScope(modId)

        initializationScope.modules()

        initializationScope.onRegisterBlocks.fire { it() }
        initializationScope.onRegisterItems.fire { it() }
        initializationScope.onRegisterRecipes.fire { it() }

    }
}

interface Proxy {
    fun getClientPlayer(): PlayerEntity?
}

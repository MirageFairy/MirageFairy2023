package miragefairy2023

import terrablender.api.TerraBlenderApi

class MirageFairy2023TerraBlenderPlugin : TerraBlenderApi {
    override fun onTerraBlenderInitialized() {
        InitializationScope.INSTANCE.onTerraBlenderInitialized.fire { it() }
    }
}

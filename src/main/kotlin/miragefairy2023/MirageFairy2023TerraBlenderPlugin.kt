package miragefairy2023

import terrablender.api.TerraBlenderApi

class MirageFairy2023TerraBlenderPlugin : TerraBlenderApi {
    override fun onTerraBlenderInitialized() {
        MirageFairy2023.initializationScope.onTerraBlenderInitialized.fire { it() }
    }
}

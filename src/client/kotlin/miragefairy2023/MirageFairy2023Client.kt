package miragefairy2023

import miragefairy2023.MirageFairy2023.initializationScope
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.render.RenderLayer

object MirageFairy2023Client : ClientModInitializer {
    override fun onInitializeClient() {

        initializationScope.renderLayerRegistration.fire {
            it { block, layerName ->
                val layer = when (layerName) {
                    Unit -> RenderLayer.getCutout()
                    else -> throw AssertionError()
                }
                BlockRenderLayerMap.INSTANCE.putBlocks(layer, block);
            }
        }

    }
}

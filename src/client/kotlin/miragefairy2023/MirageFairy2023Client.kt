package miragefairy2023

import miragefairy2023.MirageFairy2023.initializationScope
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.client.render.RenderLayer
import net.minecraft.entity.player.PlayerEntity

object MirageFairy2023Client : ClientModInitializer {
    override fun onInitializeClient() {

        MirageFairy2023.proxy = object : Proxy {
            override fun getClientPlayer(): PlayerEntity? = miragefairy2023.util.getClientPlayer()
        }

        initializationScope.onRegisterRenderLayers.fire {
            it { block, layerName ->
                val layer = when (layerName) {
                    Unit -> RenderLayer.getCutout()
                    else -> throw AssertionError()
                }
                BlockRenderLayerMap.INSTANCE.putBlocks(layer, block)
            }
        }

        initializationScope.onRegisterColorProvider.fire {
            it { item, colorFunction ->
                ColorProviderRegistry.ITEM.register(ItemColorProvider { stack, tintIndex -> colorFunction(stack, tintIndex) }, item)
            }
        }

    }
}

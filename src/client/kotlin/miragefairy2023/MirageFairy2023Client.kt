package miragefairy2023

import miragefairy2023.MirageFairy2023.initializationScope
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.client.particle.SuspendParticle
import net.minecraft.client.render.RenderLayer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.DefaultParticleType
import net.minecraft.util.Identifier

object MirageFairy2023Client : ClientModInitializer {
    init {
        MirageFairy2023.clientProxy = object : ClientProxy {
            override fun getClientPlayer(): PlayerEntity? = MinecraftClient.getInstance().player

            override fun registerClientPacketReceiver(identifier: Identifier, packetReceiver: ClientPacketReceiver<*>) {
                fun <T> registerPacketReceiver(packetReceiver: ClientPacketReceiver<T>) {
                    ClientPlayNetworking.registerGlobalReceiver(identifier) { client, _, buf, _ ->
                        val data = packetReceiver.read(buf)
                        client.execute {
                            packetReceiver.receive(data)
                        }
                    }
                }
                registerPacketReceiver(packetReceiver)
            }

            override fun registerParticleFactory(particleType: DefaultParticleType) {
                val pendingParticleFactory = SuspendParticle::HappyVillagerFactory
                ParticleFactoryRegistry.getInstance().register(particleType, pendingParticleFactory)
            }
        }
    }

    override fun onInitializeClient() {

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

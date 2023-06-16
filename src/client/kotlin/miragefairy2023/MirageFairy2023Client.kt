package miragefairy2023

import miragefairy2023.MirageFairy2023.initializationScope
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.MinecraftClient
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.client.model.ModelData
import net.minecraft.client.model.ModelPartData
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.particle.EnchantGlyphParticle
import net.minecraft.client.particle.EndRodParticle
import net.minecraft.client.particle.SuspendParticle
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.particle.DefaultParticleType
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object MirageFairy2023Client : ClientModInitializer {
    override fun onInitializeClient() {

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

            override fun registerParticleFactory(particleType: DefaultParticleType, demonParticleBehaviour: DemonParticleBehaviour) {
                val pendingParticleFactory = when (demonParticleBehaviour) {
                    DemonParticleBehaviour.HAPPY -> SuspendParticle::HappyVillagerFactory
                    DemonParticleBehaviour.ENCHANT -> EnchantGlyphParticle::EnchantFactory
                    DemonParticleBehaviour.END_ROD -> EndRodParticle::Factory
                    DemonParticleBehaviour.ATTRACTING -> AttractingParticle::Factory
                }
                ParticleFactoryRegistry.getInstance().register(particleType, pendingParticleFactory)
            }

            override fun registerCutoutBlockRenderLayer(block: Block) {
                BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), block)
            }

            override fun registerItemColorProvider(item: Item, colorFunction: (stack: ItemStack, tintIndex: Int) -> Int) {
                ColorProviderRegistry.ITEM.register(ItemColorProvider { stack, tintIndex -> colorFunction(stack, tintIndex) }, item)
            }

            override fun <T> registerRenderingProxyBlockEntityRendererFactory(blockEntityType: BlockEntityType<T>) where T : BlockEntity, T : RenderingProxyBlockEntity {
                BlockEntityRendererFactories.register(blockEntityType, ::RenderingProxyBlockEntityRenderer)
            }

            override fun registerItemTooltipCallback(block: (stack: ItemStack, lines: MutableList<Text>) -> Unit) {
                ItemTooltipCallback.EVENT.register { stack, _, lines ->
                    block(stack, lines)
                }
            }

            override fun registerEntityRendererFactory(entityType: EntityType<*>, type: String) {
                when (type) {
                    "antimatter_bolt" -> EntityRendererRegistry.register(entityType, ::AntimatterBoltEntityRenderer)
                }
            }
        }

        initializationScope.onInitializeClient.fire { it() }

        EntityModelLayerRegistry.registerModelLayer(AntimatterBoltEntityRenderer.MAIN.entityModelLayer, AntimatterBoltEntityRenderer.MAIN.provider)

    }
}

class EntityModelLayerCard(
    path: String,
    layerName: String,
    private val textureWidth: Int,
    private val textureHeight: Int,
    private val configurator: (ModelPartData) -> Unit,
) {
    val entityModelLayer = EntityModelLayer(Identifier(MirageFairy2023.modId, path), layerName)
    val provider = EntityModelLayerRegistry.TexturedModelDataProvider {
        val modelData = ModelData()
        configurator(modelData.root)
        TexturedModelData.of(modelData, textureWidth, textureHeight)
    }
}

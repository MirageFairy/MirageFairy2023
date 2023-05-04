package miragefairy2023

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.particle.DefaultParticleType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object MirageFairy2023 : ModInitializer {
    val modId = "miragefairy2023"
    val logger = LoggerFactory.getLogger(modId)
    var clientProxy: ClientProxy? = null
    var serverProxy: ServerProxy = object : ServerProxy {
        override fun registerServerPacketReceiver(identifier: Identifier, packetReceiver: ServerPacketReceiver<*>) {
            fun <T> registerPacketReceiver(packetReceiver: ServerPacketReceiver<T>) {
                ServerPlayNetworking.registerGlobalReceiver(identifier) { server, player, _, buf, _ ->
                    val data = packetReceiver.read(buf)
                    server.execute {
                        packetReceiver.receive(data, player)
                    }
                }
            }
            registerPacketReceiver(packetReceiver)
        }
    }

    lateinit var initializationScope: InitializationScope

    override fun onInitialize() {
        initializationScope = InitializationScope(modId)

        initializationScope.modules()

        initializationScope.onRegisterLootConditionType.fire { it() }
        initializationScope.onRegisterLootFunctionType.fire { it() }
        initializationScope.onRegisterBlocks.fire { it() }
        initializationScope.onRegisterBlockEntities.fire { it() }
        initializationScope.onRegisterItems.fire { it() }
        initializationScope.onRegisterRecipes.fire { it() }

    }
}

interface ClientProxy {
    fun getClientPlayer(): PlayerEntity?
    fun registerClientPacketReceiver(identifier: Identifier, packetReceiver: ClientPacketReceiver<*>)
    fun registerParticleFactory(particleType: DefaultParticleType, demonParticleBehaviour: DemonParticleBehaviour)
    fun registerBlockRenderLayer(block: Block)
    fun registerItemColorProvider(item: Item, colorFunction: (stack: ItemStack, tintIndex: Int) -> Int)
    fun <T> registerRenderingProxyBlockEntityRendererFactory(blockEntityType: BlockEntityType<T>) where T : BlockEntity, T : RenderingProxyBlockEntity
}

enum class DemonParticleBehaviour {
    HAPPY,
    ENCHANT,
    END_ROD,
}

interface RenderingProxy {
    fun stack(block: () -> Unit)

    fun translate(x: Double, y: Double, z: Double)
    fun scale(x: Float, y: Float, z: Float)
    fun rotateX(degrees: Float)
    fun rotateY(degrees: Float)
    fun rotateZ(degrees: Float)

    fun renderItem(itemStack: ItemStack)
}

interface RenderingProxyBlockEntity {
    fun render(renderingProxy: RenderingProxy, tickDelta: Float, light: Int, overlay: Int)
}

interface ServerProxy {
    fun registerServerPacketReceiver(identifier: Identifier, packetReceiver: ServerPacketReceiver<*>)
}

interface ClientPacketReceiver<P> {
    fun read(buf: PacketByteBuf): P
    fun receive(packet: P)
}

interface ServerPacketReceiver<P> {
    fun read(buf: PacketByteBuf): P
    fun receive(packet: P, player: ServerPlayerEntity)
}

package miragefairy2023.modules

import com.faux.customentitydata.api.CustomDataHelper
import miragefairy2023.ClientPacketReceiver
import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

private var clientCustomData: NbtCompound? = null

var PlayerEntity.customData: NbtCompound
    get() {
        return when (this) {
            is ServerPlayerEntity -> CustomDataHelper.getPersistentData(this)
            else -> clientCustomData ?: NbtCompound()
        }
    }
    set(it) {
        when (this) {
            is ServerPlayerEntity -> CustomDataHelper.setPersistentData(this, it)
            else -> clientCustomData = it
        }
    }

private val CUSTOM_DATA_PACKET_ID = Identifier(MirageFairy2023.modId, "custom_data_sync")

val customDataModule = module {

    // 受信
    onInitializeClient {
        MirageFairy2023.clientProxy!!.registerClientPacketReceiver(CUSTOM_DATA_PACKET_ID, object : ClientPacketReceiver<NbtCompound> {
            override fun read(buf: PacketByteBuf) = buf.readNbt()!!
            override fun receive(packet: NbtCompound) {
                clientCustomData = packet
            }
        })
    }

    // ログイン時自動同期
    onInitialize {
        ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
            handler.player.syncCustomData()
        }
    }

}

// 送信
fun ServerPlayerEntity.syncCustomData() {
    ServerPlayNetworking.send(this, CUSTOM_DATA_PACKET_ID, PacketByteBufs.create().also {
        it.writeNbt(this.customData)
    })
}

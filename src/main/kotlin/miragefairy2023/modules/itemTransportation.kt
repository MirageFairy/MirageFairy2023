package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.Translation
import miragefairy2023.util.get
import miragefairy2023.util.init.enJa
import miragefairy2023.util.list
import miragefairy2023.util.toItemStack
import miragefairy2023.util.toNbt
import miragefairy2023.util.wrapper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.server.network.ServerPlayerEntity
import org.slf4j.LoggerFactory

val ITEM_TRANSPORTATION_LIMIT = 2000

val ITEM_TRANSPORTATION_COUNT_KEY = Translation("${MirageFairy2023.modId}.item_transportation.count", "Items in transport: %s / Max %s", "転送中のアイテム: %s / 最大 %s")
val ITEM_TRANSPORTATION_OVERFLOWED_KEY = Translation("${MirageFairy2023.modId}.item_transportation.overflowed", "Transport limit reached!", "転送の上限に達しました！")

val itemTransportationModule = module {
    enJa(ITEM_TRANSPORTATION_COUNT_KEY)
    enJa(ITEM_TRANSPORTATION_OVERFLOWED_KEY)
}


val <T : PlayerEntity> T.itemTransportation get() = ItemTransportation(this)

class ItemTransportation<T : PlayerEntity>(val player: T) {
    companion object {
        private val logger = LoggerFactory.getLogger(ItemTransportation::class.java)
    }

    private fun getRootNbt() = player.customData.wrapper[MirageFairy2023.modId]["item_transportation"]

    fun size(): Int {
        val root = getRootNbt()
        val list = root.list.get() ?: return 0
        return list.size
    }

    /** @return メソッド呼出し後は安全に改変することができます。 */
    fun removeRandom(): ItemStack? {
        val root = getRootNbt()
        val list = root.list.get() ?: return null
        if (list.isEmpty()) return null
        val nbt = list.removeAt(player.random.nextInt(list.size))
        if (nbt !is NbtCompound) {
            logger.error("Invalid item tag: $nbt")
            return null
        }
        return nbt.toItemStack()
    }

    /** @param itemStack メソッド呼出し後は安全に改変することができます。 */
    fun add(itemStack: ItemStack) {
        val root = getRootNbt()
        val list = root.list.get() ?: run {
            val newList = NbtList()
            root.list.set(newList)
            newList
        }
        list.add(itemStack.toNbt())
    }
}

fun ItemTransportation<ServerPlayerEntity>.sync() = player.syncCustomData()
fun ItemTransportation<*>.isEmpty() = this.size() == 0
fun ItemTransportation<*>.isNotEmpty() = !this.isEmpty()

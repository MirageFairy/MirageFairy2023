package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.mixins.api.EatFoodCallback
import miragefairy2023.module
import miragefairy2023.util.NbtProperty
import miragefairy2023.util.get
import miragefairy2023.util.toItemStack
import miragefairy2023.util.toNbt
import miragefairy2023.util.wrapper
import mirrg.kotlin.hydrogen.castOrNull
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity

val lastFoodModule = module {
    EatFoodCallback.EVENT.register { entity, world, stack ->
        if (world.isClient) return@register
        if (entity !is PlayerEntity) return@register
        if (!stack.isFood) return@register
        entity.lastFoodProperty.set(stack)
        syncCustomData(entity as ServerPlayerEntity)
    }
}

// TODO 一旦NBTにするのではなくItemStackをそのまま格納する
val PlayerEntity.lastFoodProperty
    get() = this.customData.wrapper[MirageFairy2023.modId]["last_food"].let { parent ->
        NbtProperty<ItemStack?, ItemStack?>({ parent.get()?.castOrNull<NbtCompound>()?.toItemStack() }, { parent.set(it?.toNbt()) })
    }

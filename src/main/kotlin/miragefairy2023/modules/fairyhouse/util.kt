package miragefairy2023.modules.fairyhouse

import miragefairy2023.RenderingProxy
import miragefairy2023.modules.DemonParticleTypeCard
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d

fun ServerWorld.spawnCraftingCompletionParticles(pos: Vec3d) {
    this.spawnParticles(DemonParticleTypeCard.DESCENDING_MAGIC.particleType, pos.x, pos.y, pos.z, 5, 0.0, 0.0, 0.0, 0.02)
}

fun RenderingProxy.renderItemStack(itemStack: ItemStack, dotX: Double, dotY: Double, dotZ: Double, scale: Float = 1.0F, rotate: Float = 0.0F) {
    this.stack {
        this.translate(dotX / 16.0, dotY / 16.0, dotZ / 16.0)
        this.scale(scale, scale, scale)
        this.rotateY(rotate)
        this.renderItem(itemStack)
    }
}

package miragefairy2023.modules.toolitem

import miragefairy2023.modules.DemonSoundEventCard
import miragefairy2023.util.createItemStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterial
import net.minecraft.sound.SoundCategory
import net.minecraft.stat.Stats
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class StaffItem(toolMaterial: ToolMaterial, settings: Settings) : ToolItem(toolMaterial, settings) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)
        if (world.isClient) return TypedActionResult.success(itemStack)

        // TODO 魔法弾を発射するようにする
        // TODO 魔法弾のダメージ
        // TODO Luckによって魔法弾のダメージ丞相
        // TODO 魔法攻撃力Attribute
        // 生成
        val entity = ArrowEntity(world, user)
        entity.initFromStack(Items.ARROW.createItemStack())
        entity.setVelocity(user, user.pitch, user.yaw, 0.0F, 5.0F, 1.0F)
        entity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY
        world.spawnEntity(entity)

        // 消費
        itemStack.damage(1, user) {
            it.sendToolBreakStatus(hand)
        }

        // 統計
        user.incrementStat(Stats.USED.getOrCreateStat(this))

        // エフェクト
        world.playSound(null, user.x, user.y, user.z, DemonSoundEventCard.MAGIC1.soundEvent, SoundCategory.PLAYERS, 1.0F, 0.80F + (world.random.nextFloat() - 0.5F) * 0.3F)

        return TypedActionResult.success(itemStack, world.isClient)
    }

    // TODO エンチャント関連
    // TODO 修理
}

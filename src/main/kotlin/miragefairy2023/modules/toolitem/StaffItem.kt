package miragefairy2023.modules.toolitem

import miragefairy2023.modules.DemonPlayerAttributeCard
import miragefairy2023.modules.DemonSoundEventCard
import miragefairy2023.modules.entity.AntimatterBoltEntity
import miragefairy2023.util.text
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
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

        if (!user.isCreative) {
            if (user.totalExperience < 1) {
                user.sendMessage(text { NOT_ENOUGH_EXPERIENCE_KEY() }, true)
                return TypedActionResult.consume(itemStack)
            }
        }

        // TODO 属性
        // 生成
        val damage = 5.0F + user.getAttributeValue(DemonPlayerAttributeCard.MAGIC_DAMAGE.entityAttribute).toFloat()
        val entity = AntimatterBoltEntity(world, damage, 16.0) // TODO 射程増加エンチャント
        entity.setPosition(user.x, user.eyeY - 0.3, user.z)
        entity.setVelocity(user, user.pitch, user.yaw, 0.0F, 2.0F, 1.0F)
        entity.owner = user
        world.spawnEntity(entity)

        // 消費
        itemStack.damage(1, user) {
            it.sendToolBreakStatus(hand)
        }
        if (!user.isCreative) user.addExperience(-1)

        user.itemCooldownManager.set(this, 10) // TODO クールタイムの軽減エンチャント

        // 統計
        user.incrementStat(Stats.USED.getOrCreateStat(this))

        // エフェクト
        world.playSound(null, user.x, user.y, user.z, DemonSoundEventCard.MAGIC2.soundEvent, SoundCategory.PLAYERS, 1.0F, 0.90F + (world.random.nextFloat() - 0.5F) * 0.3F)

        return TypedActionResult.consume(itemStack)
    }
}

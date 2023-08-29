package miragefairy2023.modules.toolitem

import miragefairy2023.InitializationScope
import miragefairy2023.modules.DemonEnchantmentCard
import miragefairy2023.modules.DemonPlayerAttributeCard
import miragefairy2023.modules.DemonSoundEventCard
import miragefairy2023.modules.ToolMaterialCard
import miragefairy2023.modules.commonItemGroup
import miragefairy2023.modules.entity.AntimatterBoltEntity
import miragefairy2023.util.getRate
import miragefairy2023.util.init.generateItemTag
import miragefairy2023.util.randomInt
import miragefairy2023.util.text
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.data.client.Models
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterial
import net.minecraft.sound.SoundCategory
import net.minecraft.stat.Stats
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class StaffType(
    private val toolMaterialCard: ToolMaterialCard,
) : ToolItemCardType<StaffItem>(Models.HANDHELD) {
    override fun createItem() = StaffItem(toolMaterialCard.toolMaterial, FabricItemSettings().group(commonItemGroup))
    override fun init(scope: InitializationScope, card: ToolItemCard<StaffItem>) = scope.run {
        generateItemTag(toolMaterialCard.tag, card.item)
    }
}

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

        val damage = 5.0F +
            10.0F * DemonEnchantmentCard.MAGIC_DAMAGE.enchantment.getRate(itemStack).toFloat() +
            user.getAttributeValue(DemonPlayerAttributeCard.MAGIC_DAMAGE.entityAttribute).toFloat()
        val limitDistance = 16.0 +
            16.0 * DemonEnchantmentCard.MAGIC_REACH.enchantment.getRate(itemStack)
        val speed = 2.0F +
            2.0F * DemonEnchantmentCard.MAGIC_REACH.enchantment.getRate(itemStack).toFloat()
        val frequency = 1.0 +
            2.0 * DemonEnchantmentCard.MAGIC_FREQUENCY.enchantment.getRate(itemStack)

        // TODO 属性
        // 生成
        val entity = AntimatterBoltEntity(world, damage, limitDistance)
        entity.setPosition(user.x, user.eyeY - 0.3, user.z)
        entity.setVelocity(user, user.pitch, user.yaw, 0.0F, speed, 1.0F)
        entity.owner = user
        world.spawnEntity(entity)

        // 消費
        itemStack.damage(1, user) {
            it.sendToolBreakStatus(hand)
        }
        if (!user.isCreative) user.addExperience(-1)

        user.itemCooldownManager.set(this, world.random.randomInt(10.0 / frequency))

        // 統計
        user.incrementStat(Stats.USED.getOrCreateStat(this))

        // エフェクト
        world.playSound(null, user.x, user.y, user.z, DemonSoundEventCard.MAGIC2.soundEvent, SoundCategory.PLAYERS, 1.0F, 0.90F + (world.random.nextFloat() - 0.5F) * 0.3F)

        return TypedActionResult.consume(itemStack)
    }
}

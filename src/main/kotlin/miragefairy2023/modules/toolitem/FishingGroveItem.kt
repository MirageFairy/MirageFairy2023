package miragefairy2023.modules.toolitem

import dev.emi.trinkets.api.SlotReference
import dev.emi.trinkets.api.Trinket
import dev.emi.trinkets.api.TrinketItem
import miragefairy2023.mixins.api.FishingBobberEntityHelper
import miragefairy2023.modules.ToolMaterialCard
import miragefairy2023.util.randomInt
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.FishingBobberEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

class FishingGroveItem(settings: Settings) : Item(settings.maxDamageIfAbsent(ToolMaterialCard.CHAOS_STONE.toolMaterial.durability)), Trinket {
    override fun tick(stack: ItemStack, slot: SlotReference, entity: LivingEntity) {
        val world = entity.world
        if (entity !is PlayerEntity) return // プレイヤーじゃない
        val fishHook = entity.fishHook ?: return // 釣り糸を垂らしていない
        val (hand, fishingRodItemStack) = when {
            entity.mainHandStack.isOf(Items.FISHING_ROD) -> Pair(Hand.MAIN_HAND, entity.mainHandStack)
            entity.offHandStack.isOf(Items.FISHING_ROD) -> Pair(Hand.OFF_HAND, entity.offHandStack)
            else -> return // 釣り竿が見つからない
        }
        if (FishingBobberEntityHelper.getHookCountdown(fishHook) <= 0) return // 発動タイミングじゃない

        // 成立

        // 消費
        if (!world.isClient) {
            stack.damage(1, entity) {

            }
        }

        // 引っ張る
        if (!world.isClient) {
            val damage = fishHook.use(fishingRodItemStack) + 1 // 自動釣りによるペナルティ
            fishingRodItemStack.damage(damage, entity) {
                it.sendToolBreakStatus(hand)
            }
        }
        world.playSound(null, entity.x, entity.y, entity.z, SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F))
        entity.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH)

        if (fishingRodItemStack.isEmpty) return // 壊れたので抜ける

        // 再び投げる
        world.playSound(null, entity.x, entity.y, entity.z, SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F))
        if (!world.isClient) {
            val lure = EnchantmentHelper.getLure(fishingRodItemStack) / 2.0 // 自動釣りによるペナルティ
            val luckOfTheSea = EnchantmentHelper.getLuckOfTheSea(fishingRodItemStack) / 2.0 // 自動釣りによるペナルティ
            world.spawnEntity(FishingBobberEntity(entity, world, world.random.randomInt(luckOfTheSea), world.random.randomInt(lure)))
        }
        entity.incrementStat(Stats.USED.getOrCreateStat(fishingRodItemStack.item))
        entity.emitGameEvent(GameEvent.ITEM_INTERACT_START)

    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)
        if (TrinketItem.equipItem(user, itemStack)) {
            return TypedActionResult.success(itemStack, world.isClient)
        }
        return super.use(world, user, hand)
    }

    override fun getEquipSound(): SoundEvent = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC
}

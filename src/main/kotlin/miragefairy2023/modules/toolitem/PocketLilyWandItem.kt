package miragefairy2023.modules.toolitem

import miragefairy2023.InitializationScope
import miragefairy2023.modules.DemonSoundEventCard
import miragefairy2023.modules.ITEM_TRANSPORTATION_COUNT_KEY
import miragefairy2023.modules.ITEM_TRANSPORTATION_LIMIT
import miragefairy2023.modules.ITEM_TRANSPORTATION_OVERFLOWED_KEY
import miragefairy2023.modules.ToolMaterialCard
import miragefairy2023.modules.commonItemGroup
import miragefairy2023.modules.itemTransportation
import miragefairy2023.modules.sync
import miragefairy2023.util.EMPTY_ITEM_STACK
import miragefairy2023.util.init.generateItemTag
import miragefairy2023.util.text
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.data.client.Models
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterial
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class PocketLilyWandType(
    private val toolMaterialCard: ToolMaterialCard,
) : ToolItemCardType<PocketLilyWandItem>(Models.HANDHELD) {
    override fun createItem() = PocketLilyWandItem(toolMaterialCard.toolMaterial, FabricItemSettings().group(commonItemGroup))
    override fun init(scope: InitializationScope, card: ToolItemCard<PocketLilyWandItem>) = scope.run {
        generateItemTag(toolMaterialCard.tag, card.item)
    }
}

class PocketLilyWandItem(toolMaterial: ToolMaterial, settings: Settings) : ToolItem(toolMaterial, settings) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)
        if (world.isClient) return TypedActionResult.success(itemStack)
        if (user !is ServerPlayerEntity) return TypedActionResult.consume(itemStack) // プレイヤーのみが実行可能

        if (user.itemTransportation.size() >= ITEM_TRANSPORTATION_LIMIT) {
            user.sendMessage(text { ITEM_TRANSPORTATION_OVERFLOWED_KEY() }, true)
            return TypedActionResult.consume(itemStack) // 上限に到達
        }

        val (fairyHand, targetHand) = when {
            user.mainHandStack === itemStack -> Pair(Hand.MAIN_HAND, Hand.OFF_HAND)
            user.offHandStack === itemStack -> Pair(Hand.OFF_HAND, Hand.MAIN_HAND)
            else -> return TypedActionResult.consume(itemStack) // 異常な選択状態
        }
        val targetItemStack = user.getStackInHand(targetHand)
        if (targetItemStack.isEmpty) return TypedActionResult.consume(itemStack) // 対象アイテムが無い

        // 成立

        // 消費
        itemStack.damage(4, user) {
            it.sendToolBreakStatus(hand)
        }

        // アイテムを減らす
        user.setStackInHand(targetHand, EMPTY_ITEM_STACK)

        // 格納
        user.itemTransportation.add(targetItemStack)
        user.itemTransportation.sync()
        user.sendMessage(text { ITEM_TRANSPORTATION_COUNT_KEY(user.itemTransportation.size(), ITEM_TRANSPORTATION_LIMIT) }, true)

        // エフェクト
        world.playSound(null, user.x, user.y, user.z, DemonSoundEventCard.MAGIC3.soundEvent, SoundCategory.PLAYERS, 0.5F, 1.0F + (user.random.nextFloat() - 0.5F) * 2F * 0.2F)

        return TypedActionResult.consume(itemStack)
    }
}

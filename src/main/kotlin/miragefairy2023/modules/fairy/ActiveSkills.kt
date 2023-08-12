package miragefairy2023.modules.fairy

import miragefairy2023.MirageFairy2023
import miragefairy2023.modules.DemonSoundEventCard
import miragefairy2023.modules.ITEM_TRANSPORTATION_COUNT_KEY
import miragefairy2023.modules.ITEM_TRANSPORTATION_LIMIT
import miragefairy2023.modules.ITEM_TRANSPORTATION_OVERFLOWED_KEY
import miragefairy2023.modules.itemTransportation
import miragefairy2023.modules.sync
import miragefairy2023.util.EMPTY_ITEM_STACK
import miragefairy2023.util.Translation
import miragefairy2023.util.text
import miragefairy2023.util.yellow
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.world.World

interface ActiveSkill {
    fun getTooltip(): List<Text>

    /** 論理クライアントと論理サーバーの両方で呼び出されます。 */
    fun action(itemStack: ItemStack, world: World, user: LivingEntity, useTicks: Int)
}

val ITEM_TRANSPORTATION_DESCRIPTION_KEY = Translation("${MirageFairy2023.modId}.active_skill.itemTransportation.description", "Use to transfer the offhand item", "使用時、オフハンドのアイテムを転送")

fun itemTransportation(): ActiveSkill = object : ActiveSkill {
    override fun getTooltip() = listOf(text { ITEM_TRANSPORTATION_DESCRIPTION_KEY().yellow })
    override fun action(itemStack: ItemStack, world: World, user: LivingEntity, useTicks: Int) {
        if (world.isClient) return
        if (useTicks >= 20) {
            if (user !is ServerPlayerEntity) return // プレイヤーのみが実行可能

            if (user.itemTransportation.size() >= ITEM_TRANSPORTATION_LIMIT) {
                user.sendMessage(text { ITEM_TRANSPORTATION_OVERFLOWED_KEY() }, true)
                return // 上限に到達
            }

            val (fairyHand, targetHand) = when {
                user.mainHandStack === itemStack -> Pair(Hand.MAIN_HAND, Hand.OFF_HAND)
                user.offHandStack === itemStack -> Pair(Hand.OFF_HAND, Hand.MAIN_HAND)
                else -> return // 異常な選択状態
            }
            val targetItemStack = user.getStackInHand(targetHand)
            if (targetItemStack.isEmpty) return // 対象アイテムが無い

            // 成立

            // 消費
            val removedFairyItemStack = itemStack.split(1)
            if (itemStack.isEmpty) user.setStackInHand(fairyHand, EMPTY_ITEM_STACK)

            // アイテムを減らす
            user.setStackInHand(targetHand, EMPTY_ITEM_STACK)

            // 格納
            user.itemTransportation.add(removedFairyItemStack)
            user.itemTransportation.add(targetItemStack)
            user.itemTransportation.sync()
            user.sendMessage(text { ITEM_TRANSPORTATION_COUNT_KEY(user.itemTransportation.size(), ITEM_TRANSPORTATION_LIMIT) }, true)

            // エフェクト
            world.playSound(null, user.x, user.y, user.z, DemonSoundEventCard.MAGIC3.soundEvent, SoundCategory.PLAYERS, 0.5F, 1.0F + (user.random.nextFloat() - 0.5F) * 2F * 0.2F)
            // TODO 飛んでいく妖精エンティティ

        }
    }
}

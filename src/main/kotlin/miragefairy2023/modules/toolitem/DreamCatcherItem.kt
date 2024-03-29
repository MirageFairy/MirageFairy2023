package miragefairy2023.modules.toolitem

import miragefairy2023.InitializationScope
import miragefairy2023.MirageFairy2023
import miragefairy2023.api.Fairy
import miragefairy2023.modules.ToolMaterialCard
import miragefairy2023.modules.commonItemGroup
import miragefairy2023.modules.fairy.BLOCK_FAIRY_RELATION_LIST
import miragefairy2023.modules.fairy.ENTITY_TYPE_FAIRY_RELATION_LIST
import miragefairy2023.modules.foundFairies
import miragefairy2023.modules.syncCustomData
import miragefairy2023.util.Translation
import miragefairy2023.util.init.generateItemTag
import miragefairy2023.util.text
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.BlockState
import net.minecraft.block.FluidBlock
import net.minecraft.data.client.Models
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterial
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.World
import kotlin.math.roundToInt

class DreamCatcherType(
    private val toolMaterialCard: ToolMaterialCard,
    private val maxDamage: Int,
) : ToolItemCardType<DreamCatcherItem>(Models.HANDHELD) {
    override fun createItem() = DreamCatcherItem(toolMaterialCard.toolMaterial, maxDamage, FabricItemSettings().group(commonItemGroup))
    override fun init(scope: InitializationScope, card: ToolItemCard<DreamCatcherItem>) = scope.run {
        generateItemTag(toolMaterialCard.tag, card.item)
        generateItemTag(DREAM_CATCHERS, card.item)
    }
}

class DreamCatcherItem(material: ToolMaterial, maxDamage: Int, settings: Settings) : ToolItem(material, settings.maxDamage(maxDamage)) {
    companion object {
        val knownKey = Translation("item.${MirageFairy2023.modId}.dream_catcher.known_message", "Already have memory of %s", "%s の記憶は既に持っている")
        val successKey = Translation("item.${MirageFairy2023.modId}.dream_catcher.success_message", "I dreamed of %s!", "%s の夢を見た！")
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val player = context.player ?: return super.useOnBlock(context)
        if (context.world.isClient) return ActionResult.SUCCESS
        player as ServerPlayerEntity

        swing(player, context.stack, context.hand) {
            val block = context.world.getBlockState(context.blockPos).block
            BLOCK_FAIRY_RELATION_LIST.filter { it.block === block }.map { it.fairy }
        }

        return ActionResult.CONSUME
    }

    override fun useOnEntity(stack: ItemStack, user: PlayerEntity, entity: LivingEntity, hand: Hand): ActionResult {
        if (user.world.isClient) return ActionResult.SUCCESS
        user as ServerPlayerEntity

        swing(user, stack, hand) {
            val entityType = entity.type
            ENTITY_TYPE_FAIRY_RELATION_LIST.filter { it.entityType == entityType }.map { it.fairy }
        }

        return ActionResult.CONSUME
    }

    private fun swing(player: ServerPlayerEntity, itemStack: ItemStack, hand: Hand, fairyListGetter: () -> List<Fairy>) {

        // 消費
        itemStack.damage(1, player) {
            it.sendToolBreakStatus(hand)
        }

        // 入手済み妖精計算
        val foundFairies = player.foundFairies.getList()

        // 妖精判定
        val fairy = run found@{
            fairyListGetter().forEach { fairy ->
                if (fairy.motif in foundFairies) {
                    player.sendMessage(text { knownKey(fairy.item.name) }, true)
                } else {
                    return@found fairy
                }
            }
            return // 該当する未入手妖精が居ないので終了
        }

        // ----- 結果の成立 -----

        // 生産
        player.foundFairies.add(fairy.motif)
        player.syncCustomData()

        // エフェクト
        player.world.playSound(null, player.x, player.y, player.z, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 0.5F, 1.0F)

        player.sendMessage(text { successKey(fairy.item.name) })

    }

    override fun postHit(stack: ItemStack, target: LivingEntity, attacker: LivingEntity): Boolean {
        stack.damage(1, attacker) {
            it.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND)
        }
        return true
    }

    override fun postMine(stack: ItemStack, world: World, state: BlockState, pos: BlockPos, miner: LivingEntity): Boolean {
        if (state.getHardness(world, pos) != 0.0F) {
            stack.damage(1, miner) {
                it.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND)
            }
        }
        return true
    }

    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        if (!world.isClient) return
        if (entity !is PlayerEntity) return

        if (world.random.nextInt(40) != 0) return // 平均して2秒に1回

        // インベントリチェック
        run ok@{
            val inventory = entity.inventory.main + entity.inventory.armor + entity.inventory.offHand
            inventory.forEach {
                if (it.item is DreamCatcherItem) {
                    if (it === stack) {
                        return@ok // インベントリ内に見つかった最初のドリームキャッチャーがこれだった
                    } else {
                        return // インベントリ内に別のドリームキャッチャーが存在する
                    }
                }
            }
            return
        }

        // 入手済み妖精計算
        val foundFairies = entity.foundFairies.getList()

        // ブロック判定
        run {
            val playerBlockPos = entity.blockPos
            val a = BlockPos.iterate(
                playerBlockPos.x - 4,
                playerBlockPos.y - 4,
                playerBlockPos.z - 4,
                playerBlockPos.x + 4,
                playerBlockPos.y + 4,
                playerBlockPos.z + 4,
            ).asSequence()
            val b = (0 until 500).map {
                playerBlockPos.add(
                    (world.random.nextGaussian() * 10.0).roundToInt(),
                    (world.random.nextGaussian() * 10.0).roundToInt(),
                    (world.random.nextGaussian() * 10.0).roundToInt(),
                )
            }.asSequence()
            (a + b).forEach { blockPos ->

                val blockState = world.getBlockState(blockPos)
                val block = blockState.block

                // TODO
                if (blockState.isAir) return@forEach // 空気は除く
                if (block is FluidBlock) return@forEach // 流体は除く

                // 未知の妖精が入手可能か
                val hasUnknownFairy = BLOCK_FAIRY_RELATION_LIST
                    .filter { it.block === block }
                    .map { it.fairy }
                    .any { it.motif !in foundFairies }
                if (!hasUnknownFairy) return@forEach

                // 演出
                repeat(5) {
                    world.addParticle(
                        ParticleTypes.ENCHANT,
                        blockPos.x.toDouble() + 0.5, blockPos.y.toDouble() + 0.5 + 1.0, blockPos.z.toDouble() + 0.5,
                        world.random.nextGaussian() * 2.00,
                        world.random.nextGaussian() * 2.00,
                        world.random.nextGaussian() * 2.00,
                    )
                }

            }
        }

        // エンティティ判定
        world.getNonSpectatingEntities(LivingEntity::class.java, Box.of(entity.pos, 32.0, 32.0, 32.0)).forEach { targetEntity ->
            val entityType = targetEntity.type

            // TODO
            if (targetEntity === entity) return@forEach // 自分は除く

            // 未知の妖精が入手可能か
            val hasUnknownFairy = ENTITY_TYPE_FAIRY_RELATION_LIST
                .filter { it.entityType == entityType }
                .map { it.fairy }
                .any { it.motif !in foundFairies }
            if (!hasUnknownFairy) return@forEach

            // 演出
            repeat(10) {
                world.addParticle(
                    ParticleTypes.ENCHANT,
                    targetEntity.x, targetEntity.y + targetEntity.height / 2.0 + 1.0, targetEntity.z,
                    world.random.nextGaussian() * 2.00,
                    world.random.nextGaussian() * 2.00,
                    world.random.nextGaussian() * 2.00,
                )
            }

        }

    }
}

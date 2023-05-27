package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.api.Fairy
import miragefairy2023.module
import miragefairy2023.util.get
import miragefairy2023.util.gray
import miragefairy2023.util.identifier
import miragefairy2023.util.init.FeatureSlot
import miragefairy2023.util.init.Translation
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.enJaItem
import miragefairy2023.util.init.group
import miragefairy2023.util.init.item
import miragefairy2023.util.init.translation
import miragefairy2023.util.int
import miragefairy2023.util.map
import miragefairy2023.util.text
import miragefairy2023.util.wrapper
import mirrg.kotlin.hydrogen.castOrNull
import mirrg.kotlin.hydrogen.or
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.FluidBlock
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.client.Models
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.Items
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterial
import net.minecraft.nbt.AbstractNbtNumber
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.World
import kotlin.math.roundToInt

lateinit var dreamCatcherItem: FeatureSlot<DreamCatcherItem>
lateinit var blueDreamCatcherItem: FeatureSlot<DreamCatcherItem>

val dreamCatcherModule = module {

    translation(DreamCatcherItem.knownKey)
    translation(DreamCatcherItem.successKey)

    // ドリームキャッチャー
    dreamCatcherItem = item("dream_catcher", { DreamCatcherItem(DemonToolMaterials.MIRAGE, 20, FabricItemSettings().group(commonItemGroup)) }) {
        onGenerateItemModels { it.register(feature, Models.HANDHELD) }
        enJaItem({ feature }, "Dream Catcher", "ドリームキャッチャー")
        enJa({ "${feature.translationKey}.poem" }, "Tool to capture the free astral vortices", "未知なる記憶が、ほらそこに。")
    }
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(dreamCatcherItem.feature)
            .pattern("FSS")
            .pattern("FSS")
            .pattern("RFF")
            .input('F', Items.FEATHER)
            .input('S', Items.STRING)
            .input('R', DemonItemCard.MIRAGE_STEM())
            .criterion(DemonItemCard.MIRAGE_STEM())
            .group(dreamCatcherItem.feature)
            .offerTo(it, dreamCatcherItem.feature.identifier)
    }

    // 蒼天のドリームキャッチャー
    blueDreamCatcherItem = item("blue_dream_catcher", { DreamCatcherItem(DemonToolMaterials.CHAOS_STONE, 400, FabricItemSettings().group(commonItemGroup)) }) {
        onGenerateItemModels { it.register(feature, Models.HANDHELD) }
        enJaItem({ feature }, "Blue Dream Catcher", "蒼天のドリームキャッチャー")
        enJa({ "${feature.translationKey}.poem" }, "What are good memories for you?", "信愛、悲哀、混沌の果て。")
    }
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(blueDreamCatcherItem.feature)
            .pattern("GII")
            .pattern("G#I")
            .pattern("IGG")
            .input('#', dreamCatcherItem.feature)
            .input('G', DemonItemCard.MIRANAGITE())
            .input('I', DemonItemCard.CHAOS_STONE())
            .criterion(dreamCatcherItem.feature)
            .group(blueDreamCatcherItem.feature)
            .offerTo(it, blueDreamCatcherItem.feature.identifier)
    }

}

class BlockFairyRelation(val block: Block, val fairy: Fairy)
class EntityTypeFairyRelation(val entityType: EntityType<*>, val fairy: Fairy)

class DreamCatcherItem(material: ToolMaterial, maxDamage: Int, settings: Settings) : ToolItem(material, settings.maxDamage(maxDamage)) {
    companion object {
        val knownKey = Translation("item.${MirageFairy2023.modId}.dream_catcher.known_message", "Already have memory of %s", "%s の記憶は既に持っている")
        val successKey = Translation("item.${MirageFairy2023.modId}.dream_catcher.success_message", "I dreamed of %s!", "%s の夢を見た！")
        val BLOCK_FAIRY_RELATION_LIST = mutableListOf<BlockFairyRelation>()
        val ENTITY_TYPE_FAIRY_RELATION_LIST = mutableListOf<EntityTypeFairyRelation>()
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)
        tooltip += text { translate("$translationKey.poem").gray }
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

    private fun swing(player: ServerPlayerEntity, itemStack: ItemStack, hand: Hand, fairyListSupplier: () -> List<Fairy>) {

        // 消費
        itemStack.damage(1, player) {
            it.sendToolBreakStatus(hand)
        }

        // ストレージ
        val nbt = player.customData

        // 入手済み妖精計算
        val foundFairies = nbt.wrapper[MirageFairy2023.modId]["found_motifs"].map.get().or { mapOf() }.entries
            .filter { it.value.castOrNull<AbstractNbtNumber>()?.intValue() != 0 }
            .map { Identifier(it.key) }
            .toSet()

        // 妖精判定
        val fairy = run found@{
            fairyListSupplier().forEach { fairy ->
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
        nbt.wrapper[MirageFairy2023.modId]["found_motifs"][fairy.motif.toString()].int.set(1)
        syncCustomData(player)

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
        if (state.getHardness(world, pos) != 0.0f) {
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
        val nbt = entity.customData
        val foundFairies = nbt.wrapper[MirageFairy2023.modId]["found_motifs"].map.get().or { mapOf() }.entries
            .filter { it.value.castOrNull<AbstractNbtNumber>()?.intValue() != 0 }
            .map { Identifier(it.key) }
            .toSet()

        // ブロック判定
        run {
            val playerBlockPos = entity.blockPos
            val a = BlockPos.iterate(
                playerBlockPos.x - 4,
                playerBlockPos.y - 4,
                playerBlockPos.z - 4,
                playerBlockPos.x + 4,
                playerBlockPos.y + 4,
                playerBlockPos.z + 4
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

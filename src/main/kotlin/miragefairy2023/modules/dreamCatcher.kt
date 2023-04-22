package miragefairy2023.modules

import com.faux.customentitydata.api.CustomDataHelper
import miragefairy2023.MirageFairy2023
import miragefairy2023.api.Fairy
import miragefairy2023.module
import miragefairy2023.util.get
import miragefairy2023.util.gray
import miragefairy2023.util.init.FeatureSlot
import miragefairy2023.util.init.Translation
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.enJaItem
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
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.client.Models
import net.minecraft.data.server.RecipeProvider
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.Items
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterial
import net.minecraft.item.ToolMaterials
import net.minecraft.nbt.AbstractNbtNumber
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

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
            .criterion("has_feather", RecipeProvider.conditionsFromItem(Items.FEATHER))
            .criterion("has_string", RecipeProvider.conditionsFromItem(Items.STRING))
            .criterion("has_mirage_stem", RecipeProvider.conditionsFromItem(DemonItemCard.MIRAGE_STEM()))
            .offerTo(it, Identifier.of(modId, "dream_catcher"))
    }

    // 蒼天のドリームキャッチャー
    blueDreamCatcherItem = item("blue_dream_catcher", { DreamCatcherItem(ToolMaterials.NETHERITE, 400, FabricItemSettings().group(commonItemGroup)) }) {
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
            .input('I', Items.NETHERITE_INGOT) // TODO 緩和
            .criterion("has_dream_catcher", RecipeProvider.conditionsFromItem(dreamCatcherItem.feature))
            .criterion("has_miranagite", RecipeProvider.conditionsFromItem(DemonItemCard.MIRANAGITE()))
            .criterion("has_netherite_ingot", RecipeProvider.conditionsFromItem(Items.NETHERITE_INGOT))
            .offerTo(it, Identifier.of(modId, "blue_dream_catcher"))
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
        val nbt = CustomDataHelper.getPersistentData(player)

        // 入手済み妖精計算
        val foundFairies = nbt.wrapper[MirageFairy2023.modId]["found_motifs"].map.get().or { mapOf() }.entries
            .filter { it.value.castOrNull<AbstractNbtNumber>()?.intValue() != 0 }
            .map { Identifier(it.key) }
            .toSet()

        // 妖精判定
        val fairy = run found@{
            fairyListSupplier().forEach { fairy ->
                if (fairy.getIdentifier() in foundFairies) {
                    player.sendMessage(text { knownKey(fairy.getItem().name) }, true)
                } else {
                    return@found fairy
                }
            }
            return // 該当する未入手妖精が居ないので終了
        }

        // ----- 結果の成立 -----

        // 生産
        nbt.wrapper[MirageFairy2023.modId]["found_motifs"][fairy.getIdentifier().toString()].int.set(1)

        // エフェクト
        player.world.playSound(null, player.x, player.y, player.z, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 0.5F, 1.0F)

        player.sendMessage(text { successKey(fairy.getItem().name) })

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
}

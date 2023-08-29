package miragefairy2023.modules.toolitem

import dev.emi.trinkets.api.TrinketItem
import miragefairy2023.InitializationScope
import miragefairy2023.api.PassiveSkill
import miragefairy2023.api.PassiveSkillItem
import miragefairy2023.api.PassiveSkillProvider
import miragefairy2023.modules.TrinketsSlotCard
import miragefairy2023.modules.commonItemGroup
import miragefairy2023.modules.passiveskill.getPassiveSkillTooltip
import miragefairy2023.util.identifier
import miragefairy2023.util.init.generateItemTag
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.client.Models
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Vanishable
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class PassiveSkillAccessoryType(
    private val trinketsSlotCards: List<TrinketsSlotCard>,
    private val mana: Double,
    private val passiveSkills: List<PassiveSkill>,
) : ToolItemCardType<PassiveSkillAccessoryItem>(Models.GENERATED) {
    override fun createItem() = PassiveSkillAccessoryItem(mana, passiveSkills, FabricItemSettings().maxCount(1).group(commonItemGroup))
    override fun init(scope: InitializationScope, card: ToolItemCard<PassiveSkillAccessoryItem>) = scope.run {
        trinketsSlotCards.forEach { trinketsSlotCard ->
            generateItemTag(trinketsSlotCard.tag, card.item)
        }
    }
}

class PassiveSkillAccessoryItem(private val mana: Double, private val passiveSkills: List<PassiveSkill>, settings: Settings) : Item(settings), PassiveSkillItem, Vanishable {
    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)
        tooltip += getPassiveSkillTooltip(stack, 0.0, passiveSkills)
    }

    override val passiveSkillProvider: PassiveSkillProvider
        get() = object : PassiveSkillProvider {
            override val identifier get() = this@PassiveSkillAccessoryItem.identifier
            override val mana get() = this@PassiveSkillAccessoryItem.mana
            override fun getPassiveSkills(player: PlayerEntity, itemStack: ItemStack) = passiveSkills
        }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)
        if (TrinketItem.equipItem(user, itemStack)) {
            return TypedActionResult.success(itemStack, world.isClient)
        }
        return super.use(world, user, hand)
    }

    override fun getEquipSound(): SoundEvent = SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND
}

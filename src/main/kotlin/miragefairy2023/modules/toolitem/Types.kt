package miragefairy2023.modules.toolitem

import dev.emi.trinkets.api.Trinket
import dev.emi.trinkets.api.TrinketsApi
import miragefairy2023.InitializationScope
import miragefairy2023.api.PassiveSkill
import miragefairy2023.modules.ToolMaterialCard
import miragefairy2023.modules.TrinketsSlotCard
import miragefairy2023.modules.commonItemGroup
import miragefairy2023.util.init.generateItemTag
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.block.Block
import net.minecraft.data.client.Model
import net.minecraft.data.client.Models
import net.minecraft.item.Item
import net.minecraft.tag.BlockTags
import net.minecraft.tag.ItemTags
import net.minecraft.tag.TagKey

abstract class ToolItemCardType<T : Item>(val model: Model) {
    abstract fun createItem(): T
    abstract fun init(scope: InitializationScope, card: ToolItemCard<T>)
}

fun <T : Item> ToolItemCard<T>.init(scope: InitializationScope) = type.init(scope, this)


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

class KnifeType(
    private val toolMaterialCard: ToolMaterialCard,
    private val silkTouch: Boolean = false,
) : ToolItemCardType<DemonKnifeItem>(Models.HANDHELD) {
    override fun createItem() = DemonKnifeItem(toolMaterialCard.toolMaterial, silkTouch, FabricItemSettings().group(commonItemGroup))
    override fun init(scope: InitializationScope, card: ToolItemCard<DemonKnifeItem>) = scope.run {
        generateItemTag(toolMaterialCard.tag, card.item)
    }
}

class PickaxeType(
    private val toolMaterialCard: ToolMaterialCard,
    private val additionalEffectiveBlockTags: List<TagKey<Block>> = listOf(),
    private val silkTouch: Boolean = false,
    private val mineAll: Boolean = false,
    private val cutAll: Boolean = false,
) : ToolItemCardType<DemonMiningToolItem>(Models.HANDHELD) {
    override fun createItem() = DemonMiningToolItem(
        toolMaterialCard.toolMaterial,
        1F,
        -2.8F,
        listOf(BlockTags.PICKAXE_MINEABLE) + additionalEffectiveBlockTags,
        silkTouch,
        mineAll,
        cutAll,
        FabricItemSettings().group(commonItemGroup),
    )

    override fun init(scope: InitializationScope, card: ToolItemCard<DemonMiningToolItem>) = scope.run {
        generateItemTag(toolMaterialCard.tag, card.item)
        generateItemTag(ItemTags.CLUSTER_MAX_HARVESTABLES, card.item)
        generateItemTag(ConventionalItemTags.PICKAXES, card.item)
    }
}

class AxeType(
    private val toolMaterialCard: ToolMaterialCard,
    private val additionalEffectiveBlockTags: List<TagKey<Block>> = listOf(),
    private val silkTouch: Boolean = false,
    private val mineAll: Boolean = false,
    private val cutAll: Boolean = false,
) : ToolItemCardType<DemonMiningToolItem>(Models.HANDHELD) {
    override fun createItem() = DemonMiningToolItem(
        toolMaterialCard.toolMaterial,
        6F,
        -3.1F,
        listOf(BlockTags.AXE_MINEABLE) + additionalEffectiveBlockTags,
        silkTouch,
        mineAll,
        cutAll,
        FabricItemSettings().group(commonItemGroup),
    )

    override fun init(scope: InitializationScope, card: ToolItemCard<DemonMiningToolItem>) = scope.run {
        generateItemTag(toolMaterialCard.tag, card.item)
        generateItemTag(ConventionalItemTags.AXES, card.item)
    }
}

class StaffType(
    private val toolMaterialCard: ToolMaterialCard,
) : ToolItemCardType<StaffItem>(Models.HANDHELD) {
    override fun createItem() = StaffItem(toolMaterialCard.toolMaterial, FabricItemSettings().group(commonItemGroup))
    override fun init(scope: InitializationScope, card: ToolItemCard<StaffItem>) = scope.run {
        generateItemTag(toolMaterialCard.tag, card.item)
    }
}

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

class TrinketAccessoryType<I>(
    private val trinketsSlotCards: List<TrinketsSlotCard>,
    private val itemCreator: (Item.Settings) -> I,
) : ToolItemCardType<I>(Models.GENERATED) where I : Item, I : Trinket {
    override fun createItem() = itemCreator(FabricItemSettings().maxCount(1).group(commonItemGroup))
    override fun init(scope: InitializationScope, card: ToolItemCard<I>) = scope.run {
        trinketsSlotCards.forEach { trinketsSlotCard ->
            generateItemTag(trinketsSlotCard.tag, card.item)
        }
        onInitialize { TrinketsApi.registerTrinket(card.item, card.item) }
    }
}

class PocketLilyWandType(
    private val toolMaterialCard: ToolMaterialCard,
) : ToolItemCardType<PocketLilyWandItem>(Models.HANDHELD) {
    override fun createItem() = PocketLilyWandItem(toolMaterialCard.toolMaterial, FabricItemSettings().group(commonItemGroup))
    override fun init(scope: InitializationScope, card: ToolItemCard<PocketLilyWandItem>) = scope.run {
        generateItemTag(toolMaterialCard.tag, card.item)
    }
}

package miragefairy2023.modules

import dev.emi.trinkets.api.TrinketItem
import miragefairy2023.InitializationScope
import miragefairy2023.MirageFairy2023
import miragefairy2023.api.PassiveSkill
import miragefairy2023.api.PassiveSkillItem
import miragefairy2023.api.PassiveSkillProvider
import miragefairy2023.module
import miragefairy2023.modules.passiveskill.PassiveSkillLevelPassiveSkillEffect
import miragefairy2023.modules.passiveskill.getPassiveSkillTooltip
import miragefairy2023.util.gray
import miragefairy2023.util.identifier
import miragefairy2023.util.init.FeatureSlot
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.enJaItem
import miragefairy2023.util.init.group
import miragefairy2023.util.init.item
import miragefairy2023.util.text
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.client.Models
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.PickaxeItem
import net.minecraft.item.ToolMaterial
import net.minecraft.item.Vanishable
import net.minecraft.sound.SoundEvents
import net.minecraft.tag.ItemTags
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

enum class ToolItemCard(
    val path: String,
    val enName: String,
    val jaName: String,
    val enPoem: String,
    val jaPoem: String,
    val initializer: InitializationScope.(ToolItemCard) -> Unit,
) {
    ARTIFICIAL_FAIRY_CRYSTAL_PICKAXE(
        "artificial_fairy_crystal_pickaxe", "Crystal Pickaxe", "クリスタルのつるはし",
        "Amorphous mental body of fairies", "妖精さえ怖れる、技術の結晶。",
        pickaxe(DemonToolMaterials.ARTIFICIAL_FAIRY_CRYSTAL, 1, -2.8F),
    ),
    ARTIFICIAL_FAIRY_CRYSTAL_PENDANT(
        "artificial_fairy_crystal_pendant", "Crystal Pendant", "クリスタルのペンダント",
        "Object that makes Mirage fairies fairies", "『妖精』だったあのころ――",
        accessory(TrinketsSlotCard.CHEST_NECKLACE, 5.0, buildList {
            this += PassiveSkill(listOf(), PassiveSkillLevelPassiveSkillEffect(0.25))
        }),
    ),
    MIRANAGITE_PICKAXE(
        "miranagite_pickaxe", "Miranagi Pickaxe", "蒼天のつるはし",
        "Promotes ore recrystallization", "凝集する秩序、蒼穹彩煌が如く。",
        pickaxe(DemonToolMaterials.MIRANAGITE, 1, -2.8F),
    ),
    CHAOS_STONE_PICKAXE(
        "chaos_stone_pickaxe", "Chaos Pickaxe", "混沌のつるはし",
        "Is this made of metal? Or clay?", "時空結晶の交点に、古代の産業が芽吹く。",
        pickaxe(DemonToolMaterials.CHAOS_STONE, 1, -2.8F),
    ),
    ;

    val identifier = Identifier(MirageFairy2023.modId, path)
    lateinit var item: FeatureSlot<Item>
}

val toolItemModule = module {

    // 全体
    ToolItemCard.values().forEach { card ->
        card.initializer(this, card)
    }

    // クリスタルのつるはし
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PICKAXE.item.feature)
            .pattern("GGG")
            .pattern(" S ")
            .pattern(" S ")
            .input('G', DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL())
            .input('S', Items.STICK)
            .criterion(DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL())
            .group(ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PICKAXE.item.feature)
            .offerTo(it, ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PICKAXE.item.feature.identifier)
    }

    // クリスタルのペンダント
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PENDANT.item.feature)
            .pattern(" s ")
            .pattern("s s")
            .pattern(" G ")
            .input('G', DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL())
            .input('s', Items.STRING)
            .criterion(DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL())
            .group(ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PENDANT.item.feature)
            .offerTo(it, ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PENDANT.item.feature.identifier)
    }

    // 蒼天のつるはし
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.MIRANAGITE_PICKAXE.item.feature)
            .pattern("GGG")
            .pattern(" S ")
            .pattern(" S ")
            .input('G', DemonItemCard.MIRANAGITE())
            .input('S', Items.STICK)
            .criterion(DemonItemCard.MIRANAGITE())
            .group(ToolItemCard.MIRANAGITE_PICKAXE.item.feature)
            .offerTo(it, ToolItemCard.MIRANAGITE_PICKAXE.item.feature.identifier)
    }

    // 混沌のつるはし
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.CHAOS_STONE_PICKAXE.item.feature)
            .pattern("GGG")
            .pattern(" S ")
            .pattern(" S ")
            .input('G', DemonItemCard.CHAOS_STONE())
            .input('S', Items.STICK)
            .criterion(DemonItemCard.CHAOS_STONE())
            .group(ToolItemCard.CHAOS_STONE_PICKAXE.item.feature)
            .offerTo(it, ToolItemCard.CHAOS_STONE_PICKAXE.item.feature.identifier)
    }

}


private fun pickaxe(toolMaterial: ToolMaterial, attackDamage: Int, attackSpeed: Float): InitializationScope.(ToolItemCard) -> Unit = { card ->
    card.item = item(card.path, {
        object : PickaxeItem(toolMaterial, attackDamage, attackSpeed, FabricItemSettings().group(commonItemGroup)) {
            override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
                super.appendTooltip(stack, world, tooltip, context)
                tooltip += text { translate("$translationKey.poem").gray }
            }
        }
    }) {
        onGenerateItemModels { it.register(feature, Models.HANDHELD) }
        enJaItem({ feature }, card.enName, card.jaName)
        enJa({ "${feature.translationKey}.poem" }, card.enPoem, card.jaPoem)
        onGenerateItemTags { it(ItemTags.CLUSTER_MAX_HARVESTABLES).add(feature) }
        onGenerateItemTags { it(ConventionalItemTags.PICKAXES).add(feature) }
    }
}

private fun accessory(trinketsSlotCard: TrinketsSlotCard, passiveSkillLevel: Double, passiveSkills: List<PassiveSkill>): InitializationScope.(ToolItemCard) -> Unit = { card ->
    card.item = item(card.path, {
        class AccessoryItem : Item(FabricItemSettings().group(commonItemGroup).maxCount(1)), PassiveSkillItem, Vanishable {
            override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
                super.appendTooltip(stack, world, tooltip, context)
                tooltip += text { translate("$translationKey.poem").gray }
                tooltip += getPassiveSkillTooltip(stack, passiveSkillLevel, passiveSkills)
            }

            override val passiveSkillProvider: PassiveSkillProvider
                get() = object : PassiveSkillProvider {
                    override val identifier get() = card.identifier
                    override val mana get() = passiveSkillLevel
                    override fun getPassiveSkills(player: PlayerEntity, itemStack: ItemStack) = passiveSkills
                }

            override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
                val itemStack = user.getStackInHand(hand)
                if (TrinketItem.equipItem(user, itemStack)) {
                    return TypedActionResult.success(itemStack, world.isClient)
                }
                return super.use(world, user, hand)
            }

            override fun getEquipSound() = SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND
        }
        AccessoryItem()
    }) {
        onGenerateItemModels { it.register(feature, Models.GENERATED) }
        enJaItem({ feature }, card.enName, card.jaName)
        enJa({ "${feature.translationKey}.poem" }, card.enPoem, card.jaPoem)
        onGenerateItemTags { it(trinketsSlotCard.tag).add(feature) }
    }
}

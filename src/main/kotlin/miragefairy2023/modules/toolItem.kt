package miragefairy2023.modules

import miragefairy2023.InitializationScope
import miragefairy2023.MirageFairy2023
import miragefairy2023.api.PassiveSkill
import miragefairy2023.api.PassiveSkillItem
import miragefairy2023.module
import miragefairy2023.modules.passiveskill.MovementSpeedPassiveSkillEffect
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
import net.minecraft.tag.ItemTags
import net.minecraft.tag.TagKey
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
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
        accessory(TrinketsSlot.CHEST_NECKLACE, buildList {
            this += PassiveSkill(listOf(), MovementSpeedPassiveSkillEffect(0.20 * 0.5)) // TODO レベル制、すべての妖精のレベルUP
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

enum class TrinketsSlot(val groupName: String, val slotName: String) {
    CHEST_NECKLACE("chest", "necklace"),
    ;

    val path = "$groupName/$slotName"
    val tag: TagKey<Item> = TagKey.of(Registry.ITEM_KEY, Identifier("trinkets", path))
}

val toolItemModule = module {

    // Trinkets
    onGenerateTrinketsEntities {
        it.slots += TrinketsSlot.CHEST_NECKLACE.path
    }

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

private fun accessory(trinketsSlot: TrinketsSlot, passiveSkills: List<PassiveSkill>): InitializationScope.(ToolItemCard) -> Unit = { card ->
    card.item = item(card.path, {
        class AccessoryItem : Item(FabricItemSettings().group(commonItemGroup).maxCount(1)), PassiveSkillItem, Vanishable {
            override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
                super.appendTooltip(stack, world, tooltip, context)
                tooltip += text { translate("$translationKey.poem").gray }
                tooltip += getPassiveSkillTooltip(stack, passiveSkills)
            }

            override fun getPassiveSkillIdentifier() = card.identifier
            override fun getPassiveSkills(player: PlayerEntity, itemStack: ItemStack) = passiveSkills
        }
        AccessoryItem()
    }) {
        onGenerateItemModels { it.register(feature, Models.GENERATED) }
        enJaItem({ feature }, card.enName, card.jaName)
        enJa({ "${feature.translationKey}.poem" }, card.enPoem, card.jaPoem)
        onGenerateItemTags { it(trinketsSlot.tag).add(feature) }
    }
}

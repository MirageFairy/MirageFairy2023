package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.toIngredient
import net.fabricmc.yarn.constants.MiningLevels
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.item.ToolMaterial
import net.minecraft.recipe.Ingredient
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object ToolMaterialTags {

    val WOOD: TagKey<Item> = register("wooden_tools")
    val STONE: TagKey<Item> = register("stone_tools")
    val GOLD: TagKey<Item> = register("golden_tools")
    val IRON: TagKey<Item> = register("iron_tools")
    val DIAMOND: TagKey<Item> = register("diamond_tools")
    val NETHERITE: TagKey<Item> = register("netherite_tools")

    val MIRAGE: TagKey<Item> = register("mirage_tools")
    val ARTIFICIAL_FAIRY_CRYSTAL: TagKey<Item> = register("artificial_fairy_crystal_tools")
    val MIRANAGITE: TagKey<Item> = register("miranagite_tools")
    val CHAOS_STONE: TagKey<Item> = register("chaos_stone_tools")

    private fun register(path: String) = TagKey.of(Registry.ITEM_KEY, Identifier(MirageFairy2023.modId, path))
}

val toolMaterialModule = module {

    fun register(tag: TagKey<Item>, item: () -> Item) = onGenerateItemTags { it(tag).add(item()) }

    // WOOD
    register(ToolMaterialTags.WOOD) { Items.WOODEN_SWORD }
    register(ToolMaterialTags.WOOD) { Items.WOODEN_SHOVEL }
    register(ToolMaterialTags.WOOD) { Items.WOODEN_PICKAXE }
    register(ToolMaterialTags.WOOD) { Items.WOODEN_AXE }
    register(ToolMaterialTags.WOOD) { Items.WOODEN_HOE }
    register(ToolMaterialTags.WOOD) { Items.BOW }
    register(ToolMaterialTags.WOOD) { Items.CROSSBOW }
    register(ToolMaterialTags.WOOD) { Items.FISHING_ROD }
    register(ToolMaterialTags.WOOD) { Items.CARROT_ON_A_STICK }
    register(ToolMaterialTags.WOOD) { Items.WARPED_FUNGUS_ON_A_STICK }

    // STONE
    register(ToolMaterialTags.STONE) { Items.STONE_SWORD }
    register(ToolMaterialTags.STONE) { Items.STONE_SHOVEL }
    register(ToolMaterialTags.STONE) { Items.STONE_PICKAXE }
    register(ToolMaterialTags.STONE) { Items.STONE_AXE }
    register(ToolMaterialTags.STONE) { Items.STONE_HOE }

    // GOLD
    register(ToolMaterialTags.GOLD) { Items.GOLDEN_SWORD }
    register(ToolMaterialTags.GOLD) { Items.GOLDEN_SHOVEL }
    register(ToolMaterialTags.GOLD) { Items.GOLDEN_PICKAXE }
    register(ToolMaterialTags.GOLD) { Items.GOLDEN_AXE }
    register(ToolMaterialTags.GOLD) { Items.GOLDEN_HOE }

    // IRON
    register(ToolMaterialTags.IRON) { Items.IRON_SWORD }
    register(ToolMaterialTags.IRON) { Items.IRON_SHOVEL }
    register(ToolMaterialTags.IRON) { Items.IRON_PICKAXE }
    register(ToolMaterialTags.IRON) { Items.IRON_AXE }
    register(ToolMaterialTags.IRON) { Items.IRON_HOE }
    register(ToolMaterialTags.IRON) { Items.FLINT_AND_STEEL }
    register(ToolMaterialTags.IRON) { Items.SHEARS }

    // DIAMOND
    register(ToolMaterialTags.DIAMOND) { Items.DIAMOND_SWORD }
    register(ToolMaterialTags.DIAMOND) { Items.DIAMOND_SHOVEL }
    register(ToolMaterialTags.DIAMOND) { Items.DIAMOND_PICKAXE }
    register(ToolMaterialTags.DIAMOND) { Items.DIAMOND_AXE }
    register(ToolMaterialTags.DIAMOND) { Items.DIAMOND_HOE }

    // NETHERITE
    register(ToolMaterialTags.NETHERITE) { Items.NETHERITE_SWORD }
    register(ToolMaterialTags.NETHERITE) { Items.NETHERITE_SHOVEL }
    register(ToolMaterialTags.NETHERITE) { Items.NETHERITE_PICKAXE }
    register(ToolMaterialTags.NETHERITE) { Items.NETHERITE_AXE }
    register(ToolMaterialTags.NETHERITE) { Items.NETHERITE_HOE }

    // MIRAGE
    register(ToolMaterialTags.MIRAGE) { dreamCatcherItem.feature }

    // ARTIFICIAL_FAIRY_CRYSTAL
    register(ToolMaterialTags.ARTIFICIAL_FAIRY_CRYSTAL) { ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PICKAXE.item.feature }

    // MIRANAGITE
    register(ToolMaterialTags.MIRANAGITE) { ToolItemCard.MIRANAGITE_PICKAXE.item.feature }

    // CHAOS_STONE
    register(ToolMaterialTags.CHAOS_STONE) { blueDreamCatcherItem.feature }
    register(ToolMaterialTags.CHAOS_STONE) { ToolItemCard.CHAOS_STONE_PICKAXE.item.feature }

}

enum class DemonToolMaterials(
    private val durability: Int,
    private val miningSpeedMultiplier: Float,
    private val attackDamage: Float,
    private val miningLevel: Int,
    private val enchantability: Int,
    private val repairIngredient: () -> Ingredient,
) : ToolMaterial {
    MIRAGE(48, 1.6F, 0.0F, MiningLevels.WOOD, 17, { DemonItemCard.MIRAGE_STEM().toIngredient() }),
    ARTIFICIAL_FAIRY_CRYSTAL(235, 5.0F, 1.5F, MiningLevels.IRON, 7, { DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL().toIngredient() }),
    MIRANAGITE(256, 6.5F, 2.0F, MiningLevels.IRON, 24, { DemonItemCard.MIRANAGITE().toIngredient() }),
    CHAOS_STONE(666, 9.0F, 2.0F, MiningLevels.NETHERITE, 15, { DemonItemCard.CHAOS_STONE().toIngredient() }),
    ;

    override fun getDurability() = durability
    override fun getMiningSpeedMultiplier() = miningSpeedMultiplier
    override fun getAttackDamage() = attackDamage
    override fun getMiningLevel() = miningLevel
    override fun getEnchantability() = enchantability
    override fun getRepairIngredient() = repairIngredient()
}

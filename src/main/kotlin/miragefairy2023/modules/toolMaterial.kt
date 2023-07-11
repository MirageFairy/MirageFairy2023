package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.Translation
import miragefairy2023.util.datagen.translation
import miragefairy2023.util.toIngredient
import net.fabricmc.yarn.constants.MiningLevels
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.item.ToolMaterial
import net.minecraft.item.ToolMaterials
import net.minecraft.recipe.Ingredient
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

enum class DemonToolMaterials(
    private val durability: Int,
    private val miningSpeedMultiplier: Float,
    private val attackDamage: Float,
    private val miningLevel: Int,
    private val enchantability: Int,
    private val repairIngredient: () -> Ingredient,
) : ToolMaterial {
    MIRAGE(48, 1.6F, 0.0F, MiningLevels.WOOD, 17, { DemonItemCard.MIRAGE_STEM.item.feature.toIngredient() }),
    ARTIFICIAL_FAIRY_CRYSTAL(235, 5.0F, 1.5F, MiningLevels.IRON, 7, { DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL.item.feature.toIngredient() }),
    MIRANAGITE(256, 6.5F, 2.0F, MiningLevels.IRON, 24, { DemonItemCard.MIRANAGITE.item.feature.toIngredient() }),
    CHAOS_STONE(666, 9.0F, 2.0F, MiningLevels.NETHERITE, 15, { DemonItemCard.CHAOS_STONE.item.feature.toIngredient() }),
    ;

    override fun getDurability() = durability
    override fun getMiningSpeedMultiplier() = miningSpeedMultiplier
    override fun getAttackDamage() = attackDamage
    override fun getMiningLevel() = miningLevel
    override fun getEnchantability() = enchantability
    override fun getRepairIngredient() = repairIngredient()
}

enum class ToolMaterialCard(val toolMaterial: ToolMaterial, val path: String, en: String, ja: String) {

    WOOD(ToolMaterials.WOOD, "wooden_tools", "Wooden Tool", "木ツール"),
    STONE(ToolMaterials.STONE, "stone_tools", "Stone Tool", "石ツール"),
    GOLD(ToolMaterials.GOLD, "golden_tools", "Golden Tool", "金ツール"),
    IRON(ToolMaterials.IRON, "iron_tools", "Iron Tool", "鉄ツール"),
    DIAMOND(ToolMaterials.DIAMOND, "diamond_tools", "Diamond Tool", "ダイヤモンドツール"),
    NETHERITE(ToolMaterials.NETHERITE, "netherite_tools", "Netherite Tool", "ネザライトツール"),

    MIRAGE(DemonToolMaterials.MIRAGE, "mirage_tools", "Mirage Tool", "ミラージュツール"),
    ARTIFICIAL_FAIRY_CRYSTAL(DemonToolMaterials.ARTIFICIAL_FAIRY_CRYSTAL, "artificial_fairy_crystal_tools", "Crystal Tool", "クリスタルツール"),
    MIRANAGITE(DemonToolMaterials.MIRANAGITE, "miranagite_tools", "Miranagi Tool", "蒼天のツール"),
    CHAOS_STONE(DemonToolMaterials.CHAOS_STONE, "chaos_stone_tools", "Chaos Tool", "混沌のツール"),

    ;

    val tag: TagKey<Item> = TagKey.of(Registry.ITEM_KEY, Identifier(MirageFairy2023.modId, path))
    val key = "${MirageFairy2023.modId}.tool_material.$path"
    val translation = Translation(key, en, ja)

}

val toolMaterialModule = module {

    ToolMaterialCard.values().forEach { card ->
        translation(card.translation)
    }


    fun register(card: ToolMaterialCard, item: () -> Item) = onGenerateItemTags { it(card.tag).add(item()) }

    // WOOD
    register(ToolMaterialCard.WOOD) { Items.WOODEN_SWORD }
    register(ToolMaterialCard.WOOD) { Items.WOODEN_SHOVEL }
    register(ToolMaterialCard.WOOD) { Items.WOODEN_PICKAXE }
    register(ToolMaterialCard.WOOD) { Items.WOODEN_AXE }
    register(ToolMaterialCard.WOOD) { Items.WOODEN_HOE }
    register(ToolMaterialCard.WOOD) { Items.BOW }
    register(ToolMaterialCard.WOOD) { Items.CROSSBOW }
    register(ToolMaterialCard.WOOD) { Items.FISHING_ROD }
    register(ToolMaterialCard.WOOD) { Items.CARROT_ON_A_STICK }
    register(ToolMaterialCard.WOOD) { Items.WARPED_FUNGUS_ON_A_STICK }

    // STONE
    register(ToolMaterialCard.STONE) { Items.STONE_SWORD }
    register(ToolMaterialCard.STONE) { Items.STONE_SHOVEL }
    register(ToolMaterialCard.STONE) { Items.STONE_PICKAXE }
    register(ToolMaterialCard.STONE) { Items.STONE_AXE }
    register(ToolMaterialCard.STONE) { Items.STONE_HOE }

    // GOLD
    register(ToolMaterialCard.GOLD) { Items.GOLDEN_SWORD }
    register(ToolMaterialCard.GOLD) { Items.GOLDEN_SHOVEL }
    register(ToolMaterialCard.GOLD) { Items.GOLDEN_PICKAXE }
    register(ToolMaterialCard.GOLD) { Items.GOLDEN_AXE }
    register(ToolMaterialCard.GOLD) { Items.GOLDEN_HOE }

    // IRON
    register(ToolMaterialCard.IRON) { Items.IRON_SWORD }
    register(ToolMaterialCard.IRON) { Items.IRON_SHOVEL }
    register(ToolMaterialCard.IRON) { Items.IRON_PICKAXE }
    register(ToolMaterialCard.IRON) { Items.IRON_AXE }
    register(ToolMaterialCard.IRON) { Items.IRON_HOE }
    register(ToolMaterialCard.IRON) { Items.FLINT_AND_STEEL }
    register(ToolMaterialCard.IRON) { Items.SHEARS }

    // DIAMOND
    register(ToolMaterialCard.DIAMOND) { Items.DIAMOND_SWORD }
    register(ToolMaterialCard.DIAMOND) { Items.DIAMOND_SHOVEL }
    register(ToolMaterialCard.DIAMOND) { Items.DIAMOND_PICKAXE }
    register(ToolMaterialCard.DIAMOND) { Items.DIAMOND_AXE }
    register(ToolMaterialCard.DIAMOND) { Items.DIAMOND_HOE }

    // NETHERITE
    register(ToolMaterialCard.NETHERITE) { Items.NETHERITE_SWORD }
    register(ToolMaterialCard.NETHERITE) { Items.NETHERITE_SHOVEL }
    register(ToolMaterialCard.NETHERITE) { Items.NETHERITE_PICKAXE }
    register(ToolMaterialCard.NETHERITE) { Items.NETHERITE_AXE }
    register(ToolMaterialCard.NETHERITE) { Items.NETHERITE_HOE }

}

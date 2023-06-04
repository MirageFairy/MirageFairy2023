package miragefairy2023.modules

import miragefairy2023.util.toIngredient
import net.fabricmc.yarn.constants.MiningLevels
import net.minecraft.item.ToolMaterial
import net.minecraft.recipe.Ingredient

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

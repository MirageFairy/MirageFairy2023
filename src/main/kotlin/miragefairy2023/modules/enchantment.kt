package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.mixins.api.ItemFilteringEnchantment
import miragefairy2023.mixins.api.LootingEnchantmentRegistry
import miragefairy2023.module
import miragefairy2023.modules.toolitem.StaffItem
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.register
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

enum class DemonEnchantmentCard(
    val path: String,
    val enchantment: Enchantment,
    val en: String,
    val ja: String,
) {
    MAGIC_DAMAGE("magic_damage", DemonEnchantment(7, 1, 14, 40) { it.item is StaffItem }, "Almagest", "アルマゲスト"),
    MAGIC_REACH("magic_reach", DemonEnchantment(7, 1, 14, 40) { it.item is StaffItem }, "Shooting Star", "流星"),
    MAGIC_FREQUENCY("magic_frequency", DemonEnchantment(7, 1, 14, 40) { it.item is StaffItem }, "Tempest", "暴風雨"),
    MAGIC_LOOTING("magic_looting", DemonEnchantment(3, 15, 9, 50) { it.item is StaffItem }, "Greed", "強欲"),
    ;

    val identifier = Identifier(MirageFairy2023.modId, path)
}

val enchantmentModule = module {

    DemonEnchantmentCard.values().forEach { card ->
        register(Registry.ENCHANTMENT, card.identifier, card.enchantment)
        enJa({ card.enchantment.translationKey }, card.en, card.ja)
    }

    onInitialize { LootingEnchantmentRegistry.register(DemonEnchantmentCard.MAGIC_LOOTING.enchantment) }

}

class DemonEnchantment(
    private val maxLevel: Int,
    private val basePower: Int,
    private val powerPerLevel: Int,
    private val powerRange: Int,
    private val itemStackFilter: (ItemStack) -> Boolean,
) : Enchantment(Rarity.COMMON, EnchantmentTarget.VANISHABLE, arrayOf(EquipmentSlot.MAINHAND)), ItemFilteringEnchantment {
    override fun getMaxLevel() = maxLevel
    override fun getMinPower(level: Int) = basePower + (level - 1) * powerPerLevel
    override fun getMaxPower(level: Int) = getMinPower(level) + powerRange
    override fun isAcceptableItem(stack: ItemStack) = itemStackFilter(stack)
    override fun isAcceptableItemOnEnchanting(itemStack: ItemStack) = isAcceptableItem(itemStack)
}

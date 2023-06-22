package miragefairy2023.mixins.mixin;

import miragefairy2023.mixins.api.ItemFilteringEnchantment;
import miragefairy2023.mixins.api.LootingEnchantmentRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "getPossibleEntries", at = @At("RETURN"))
    private static void getPossibleEntries(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> info) {
        info.getReturnValue().removeIf(entry -> entry.enchantment instanceof ItemFilteringEnchantment && !((ItemFilteringEnchantment) entry.enchantment).isAcceptableItemOnEnchanting(stack));
    }

    @Inject(method = "getLooting", at = @At("RETURN"), cancellable = true)
    private static void getLooting(LivingEntity entity, CallbackInfoReturnable<Integer> info) {
        int value = info.getReturnValue();
        for (Enchantment enchantment : LootingEnchantmentRegistry.getEnchantments()) {
            value = Math.max(value, EnchantmentHelper.getEquipmentLevel(enchantment, entity));
        }
        info.setReturnValue(value);
    }
}

package miragefairy2023.mixins.api;

import com.google.common.collect.ImmutableList;
import net.minecraft.enchantment.Enchantment;

import java.util.List;

public final class LootingEnchantmentRegistry {
    private LootingEnchantmentRegistry() {

    }

    private static List<Enchantment> enchantments = ImmutableList.of();

    public static void register(Enchantment enchantment) {
        enchantments = ImmutableList.<Enchantment>builder().addAll(enchantments).add(enchantment).build();
    }

    public static List<Enchantment> getEnchantments() {
        return enchantments;
    }
}

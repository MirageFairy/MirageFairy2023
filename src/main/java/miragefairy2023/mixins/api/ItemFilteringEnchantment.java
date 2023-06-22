package miragefairy2023.mixins.api;

import net.minecraft.item.ItemStack;

public interface ItemFilteringEnchantment {
    public boolean isAcceptableItemOnEnchanting(ItemStack itemStack);
}

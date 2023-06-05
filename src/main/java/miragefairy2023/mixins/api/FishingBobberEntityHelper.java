package miragefairy2023.mixins.api;

import miragefairy2023.mixins.mixin.FishingBobberEntityAccessor;
import net.minecraft.entity.projectile.FishingBobberEntity;

public class FishingBobberEntityHelper {
    public static int getHookCountdown(FishingBobberEntity entity) {
        return ((FishingBobberEntityAccessor) entity).getHookCountdown();
    }
}

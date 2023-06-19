package miragefairy2023.mixins.api;

import miragefairy2023.mixins.mixin.FishingBobberEntityAccessor;
import net.minecraft.entity.projectile.FishingBobberEntity;

public class FishingBobberEntityHelper {
    /**
     * 魚が出現するまでのカウントダウンです。
     * 魚が出現している場合は0になります。
     */
    public static int getWaitCountdown(FishingBobberEntity entity) {
        return ((FishingBobberEntityAccessor) entity).getWaitCountdown();
    }

    /**
     * 魚が糸に到達するまでのカウントダウンです。
     * 魚が移動中でない場合は0になります。
     */
    public static int getFishTravelCountdown(FishingBobberEntity entity) {
        return ((FishingBobberEntityAccessor) entity).getFishTravelCountdown();
    }

    /**
     * 魚が糸から離れるまでのカウントダウンです。
     * 魚が糸にかかっていない間は0になります。
     */
    public static int getHookCountdown(FishingBobberEntity entity) {
        return ((FishingBobberEntityAccessor) entity).getHookCountdown();
    }
}

package miragefairy2023.mixins.mixin;

import net.minecraft.entity.projectile.FishingBobberEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FishingBobberEntity.class)
public interface FishingBobberEntityAccessor {
    @Accessor
    public int getWaitCountdown();

    @Accessor
    public int getFishTravelCountdown();

    @Accessor
    public int getHookCountdown();
}
